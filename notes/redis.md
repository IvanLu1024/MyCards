# 1.  Redis为什么这么快？

1. 完全基于内存，绝大部分请求是纯粹的内存操作，执行效率非常高。数据存在内存中，类似于HashMap，HashMap的优势就是查找和操作的时间复杂度都是O(1)；
2. 数据结构简单，对数据操作也简单，Redis中的数据结构是专门进行设计的；
3. 采用单线程（该单线程指的是处理网络请求的线程），避免了不必要的上下文切换和竞争条件，也不存在多进程或者多线程导致的切换而消耗 CPU，不用去考虑各种锁的问题，不存在加锁释放锁操作，没有因为可能出现死锁而导致的性能消耗；
4. 使用[多路I/O复用模型](https://www.jianshu.com/p/45c694e7abbb)，非阻塞IO；

# 2.  Redis中常用数据类型

[查看详细](https://github.com/DuHouAn/Java-Notes/blob/master/DataBase/17%E6%95%B0%E6%8D%AE%E7%B1%BB%E5%9E%8B.md)

# 3.  从海量Key里查询出某一固定前缀的Key

留意细节：

- 摸清数据规模，即问清楚边界；

使用keys指令来扫出指定模式的key列表，

## 使用keys对线上的业务的影响：

KEYS pattern：查找所有符合给定模式pattern的key

缺点：

- KEYS指令一次性返回所有匹配的key；
- 键的数量过大会使得服务卡顿；

这时可以使用SCAN指令：

SCAN cursor [MATCH pattern] [COUNT count]

- 基于游标的迭代器，需要基于上一次的游标延续之前的迭代过程；
- 以0作为游标开始一次新的迭代，直到命令返回游标0完成一次遍历；
- 不保证每次执行都返回某个给定数量的元素，支持模糊查询；
- 对于增量式迭代命令，一次返回的数量不可控，只能是大概率符合count参数；

```
>scan 0 match k1* count 10
```

# 4.  如何通过Redis实现分布式锁

分布式锁需要解决的问题：

- 互斥性
- 安全性
- 死锁：一个持有锁的客户端宕机而导致其他客户端再也无法获得锁，从而导致的死锁；
- 容错

如何实现：

SETNX（Set if not exsist） key value：如果key不存在，则创建并赋值。因为SETNX有上述功能，并且操作都是原子的，因此在初期的时候可以用来实现分布式锁。

- 时间复杂度：O(1)
- 返回值：设置成功，返回1，表明此时没有其他线程占用该资源；设置失败，返回0，表示此时有别的线程正在占用该资源。

使用EXPIRE key seconds来解决SETNX长期有效的问题：

- 设置key的生存时间，当key过期时（生存时间为0），会被自动删除；

```java
RedisService redisService = SpringUtils.getBean(RedisService.class);
long status = redisService.setnx(key,"1");

if(status == 1){
    redisService.expire(key,expire);
    //执行独占资源逻辑
    doOcuppiedWork();
}
```

- 以上方法的缺点：原子性无法得到满足

从Redis 2.1.6 以后，原子操作set：

SET key value [EX seconds] [PX milliseconds] [NX|XX]

- EX seconds：设置键的过期时间为second（秒）
- PX milliseconds：设置键的过期时间为millisecond（毫秒）
- NX：只在键不存在的时候，才对键进行设置操作，效果等同于setnx
- XX：只在键已存在的时候，才对键进行设置操作
- SET操作成功完成时，返回OK，否则则返回nil

```
> set lock 123 ex 10 nx
OK
> set lock 122 ex 10 nx
(nil)
```

代码实现例如：

```java
RedisService redisService = SpringUtils.getBean(RedisService.class);
String result = redisService.set(lockKey,requestId,SET_IF_NOT_EXIST,
                                 SET_WITH_WITH_EXPIRE_TIME,expireTime);
if("OK".equals(result)){
    //执行独占资源逻辑
    doOcuppiedWork();
}
```

大量key同时过期的注意事项：

集中过期，由于清楚大量key很耗时，会出现短暂的卡顿现象。

解决方法：在设置key的过期时间的时候，给每个key加上一个随机值。

# 5. 如何使用Redis做异步队列

使用List作为队列，RPUSH生产消息，LPOP消费消息。

- 缺点：没有等待队列中有值就直接消费；
- 弥补：可以在应用层引入Sleep机制去调用LPOP重试

BLPOP key [key ...] timeout：阻塞直到队列有消息就能够返回或超时

- 缺点：只能供一个消费者消费

pub/sub：主题发布-订阅模式

- 发送者（publish）发送消息，订阅者（subscribe）接收消息；
- 订阅者可以订阅任意数量的频道（Topic）；

<div align="center">
    <img src="https://gitee.com/IvanLu1024/picts/raw/34211971280f693ed4d2e2c08e00368feb0beb27/blog/redis/20190519213206.png"/>
</div>

- 缺点：消息的发布是无状态的，无法保证可达性，即发送完该消息无法保证该消息被接收到。若想解决该问题需要使用专业的消息队列，例如Kafka等。

# 6. Redis如何做持久化

Redis有三种持久化的方式：

1. RDB(快照)持久化：保存某个时间点的全量数据快照；

缺点：

- 内存数据的全量同步，数据量大会由于I/O而严重影响性能；
- 可能会因为Redis挂掉而丢失从当前至最近一次快照期间的数据；

redis.conf文件中：

```
save 900 1 #900秒之内如果有1条写入指令就触发一次快照
save 300 10
save 60 10000

stop-writes-on-bgsave-error yes #表示备份进程出错的时候，主进程就停止接收新的写入操作，是为了保护持久化数据的一致性

rdbcompression no #RDB的压缩设置为no，因为压缩会占用更多的CPU资源
```

手动触发：

- SAVE：阻塞Redis的服务器进程，知道RDB文件被创建完毕，很少被使用；
- **BGSAVE：**Fork出一个子进程来创建RDB文件，不会阻塞服务器主进程。

自动触发：

- 根据redis.conf配置里的Save m n 定时触发（用的是BGSAVE）
- 主从复制，主节点自动触发。从节点全量复制时，主节点发送RDB文件给从节点完成复制操作，主节点这时候会触发BGSAVE；
- 执行Debug Reload；
- 执行Shutdown且没有开启AOF持久化

## BGSAVE原理：

<div align="center">
    <img src="https://gitee.com/IvanLu1024/picts/raw/48fec4280eddb08553eccfe0d17dbc7bf83b866d/blog/redis/3084708676-5b70e0fd04072_articlex.png"/>
</div>

- 检查子进程的目的是：为了防止子进程之间的竞争；
- 系统（Linux）调用fork()：创建进程，实现Copy-on-write（写时复制）。传统方式下，在fork进程时直接把所有资源全部复制给子进程，这种实现方式简单但是效率低下。Linux为了降低创建子进程的成本，改进fork实现，当父进程创建子进程时，内核只为子进程创建虚拟空间，父子两个进程使用的是相同的物理空间，只有子进程发生更改的时候，才会为子进程分配独立的物理空间。

Copy-on-write：

如果有多个调用者同时要求相同资源(如内存或磁盘上的数据存储)，他们会共同获取相同的指针指向相同的资源，直到某个调用者试图修改资源的内容时，系统才会真正复制一份专用副本给该调用者，而其他调用者所见到的最初的资源仍然保持不变。

