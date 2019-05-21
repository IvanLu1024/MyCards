# 一、存储模式

## 1.1 行式存储&列式存储

### 定义

以行为存储基准的存储方式称为行式存储，一行的数据聚合存储在一块；

以列为存储基准的存储方式称为列式存储，保证每一列的数据存储在一块。

### 特点

- 行式存储：维护大量的索引，存储的成本比较高。不能够做到线性扩展，由于维护的大量索引使得其随机读的效率很高。另外，对于事务的处理有很好的支持。
- 列式存储：根据同一列数据的相似性原理，有利于对数据进行压缩，其压缩效率远高于行式存储，存储成本比较低。另外，对于查询多个列的数据，可以利用并行计算提高效率。

### 应用场景

对于单列或者相对比例较少的列并且获取频率较高，特别对于大数据的环境，需要使用到数据压缩和并行计算，就可以选择列式存储；

当表和表之间存在很多关联关系并且数据量不大，可以选择使用行式存储，其最大优势就是其事务处理的能力。

## 1.2 HBase列族式存储

列族就是指多个数据列的组合，HBase中的每个列都归属于一个列族，列族是表schema的一部分，但列并不是的。访问控制、磁盘和内存的使用统计都是在列族层面进行的。每个列族中的列是经常需要一起访问的，这样才会使得数据存取的最优性。

HBase Table的组成：

Table=RowKey（行键）+Family（列族）+Column（列）+Timestamp（版本或时间戳）+Value（值）

数据存储模式(K-V)：

（RowKey，Family，Column，Timestamp） -> Value

列数据属性：

HBase中默认一列数据可以保存三个版本，特别对于聊天数据，标记已读、未读等属性。

<div align="center">
   <img src="https://gitee.com/IvanLu1024/picts/raw/master/blog/hbase/b7cbe24fly1g34acdhpv0j20ub0hstfa.jpg"/>
</div>


数据存储原型：

按照RowKey、Column、Value、Timestamp的顺序按字典序排序：

<div align="center">
  <img src="https://gitee.com/IvanLu1024/picts/raw/master/blog/hbase/b7cbe24fly1g33hh3jrgkj20p40dc0ul.jpg"/>
</div>

# 二、数据表解析

## 2.1 建表语句解析

示例建表语句：

```
create 'demo:user',
{NAME=>'b',VERSIONS=>'3',COMPERSSION=>'SNAPPY',COMPRESSION_COMPACT=>'SNAPPY',
REPLICATION_SCOPE=>1},
{NAME=>'o',REPLICATION_SCOPE=>1,COMPERSSION=>'SNAPPY',COMPRESSION_COMPACT=>'SNAPPY'}
```

- NAME：列族名，必填项；
- VERSION：数据版本，设置一列的数据版本数量；
- REPLICATION_SCOPE：复制机制，主从复制。通过预写日志和Hlog实现的，当请求发送给Master的时候，log日志放入hdfs的同时，会进入REPLICATION这个队列中，通由Slave通过Zookeeper去获取，并写入Slave中的表；
- COMPERSSION：数据压缩的配置。

> Snappy：是一种压缩特性，其编解码速率更高

## 2.2 数据存储目录解析

在hbase-site.xml文件中设置数据存储目录:

```xml
<property>
	<name>hbase.rootdir</name>
    <value>/home/hbase_data</value>
</property>
```

- .tmp：当对表进行创建和删除操作的时候，会将表移动到该目录下，然后再进行操作。它是一个临时存储当前需要修改的数据结构。
- WALs：存储预写日志。
- archive：存储表的归档和快照，由Master上的一个任务定时进行处理。
- corrupt：用于存储损坏的日志文件，一般为空。
- data：存储数据的核心目录，系统表和用户表均存储在这个目录下。
- hbase.id：hbase：集群中的唯一id，用于标识hbase进程。
- hbase.version：表明了文件版本信息。
- oldWALs：当log已经持久化以后，WALs中的日志文件会移动到该目录下。

<div align="center">
 <img src="https://gitee.com/IvanLu1024/picts/raw/master/blog/hbase/b7cbe24fly1g340yylnn4j217l0eq0zt.jpg"/>
</div>

## 2.3 元信息表（系统表）

元信息表同样是一张hbase表，同样拥有RowKey和列族这样的概念。存储在region server上，位于zookeeper上，用户查询数据的时候，需要先到zookeeper上获取到meta表的region server的地址再到到相应region server上进行查找，如下图所示：

<div align="center">
  <img src="https://gitee.com/IvanLu1024/picts/raw/master/blog/hbase/b7cbe24fly1g341g2emeyj21c30jc7ep.jpg"/>
</div>

RowKey：格式化的region key

value：保存着region server的地址，其中最重要的一个列族就是info，其中最重要的数据列是server，包含region server的地址和端口号。

元信息表的值当region进行分割、disable、enable、drop或balance等操作，或region server挂掉都会导致元信息表值的变化，Master就需要重新分配region，元信息表就会及时更新。元信息表相当于hbase的第一级索引，是hbase中最重要的系统表。








# 三、存储设计

## 3.1 HBase中的LSM存储思想

LSM树（Log-Structured Merge-Trees）：日志结构合并树，由两个或两个以上存储数据的结构组成的，每一个数据结构对应着自己的存储介质。

<div align="center">
  <img src="https://gitee.com/IvanLu1024/picts/raw/master/blog/hbase/b7cbe24fly1g343l9btcvj20x00ct0v4.jpg"/>
</div>

由两个树状结构组成，

- C0：所有数据均存储在内存
- C1：所有数据均存储在磁盘之上

当一条新的记录插入的时候，先从C0中插入，当达到C0的阈值以后，就将C0中的某些数据片段迁移到C1中并合并到C1树上。当C1层级达到阈值时，合并到C2层级。由于合并排序算法是批量的、并且是顺序存储的，所以写的速度十分快。这样层层合并，文件数量越来越少，文件变得越来越大。每个层级的数据都是排序的，合并的时候通过类似归并排序的算法实现快速排序。对于删除操作，仅仅将数据标记为已删除，合并的时候跳过已标记为删除的数据，达到物理删除的效果。

LSM思想在HBase中的实现（三层存储结构）：

<div align="center">
  <img src="https://gitee.com/IvanLu1024/picts/raw/master/blog/hbase/b7cbe24fly1g343s2kwutj20rb0e4acs.jpg"/>
</div>

- Level 0：日志/内存，为了加速随机写的速度，先写入日志和内存中，其中日志是为了保障高可用。
- Level 1：日志/内存，当达到阈值，会有异步线程将部分数据刷写到硬盘上；；
- Level 2：合并，由于不断地刷写会产生大量小文件，这样不利于管理和查询，需要在合适的时机启动一个异步线程进行合并操作会生成一个大文件（多路归并算法）。

## 3.2 数据存储模块简介

RegionServer = Region + Store  + MemStore + StoreFile + HFile + HLog

<div align="center">
  <img src="https://gitee.com/IvanLu1024/picts/raw/master/blog/hbase/b7cbe24fly1g34acdhpv0j20ub0hstfa.jpg"/>
</div>

- Region ：分布式存储数据和负载均衡的最小单元，对于一个 RegionServer 可以包含很多Region，并且**每一个Region 包含的数据都是互斥的**，存储有用户各个行的数据。
- Store  ：对应表中的列族。
- MemStore ：是一个内存式的数据结构，用户数据进入Region 之后会先写入MemStore当满了之后 再刷写到StoreFile 中，在StoreFile 中将数据封装成HFile再刷写到HDFS上 。
- HLog：对于一个RegionServer 只有一个HLog实例。

HLog和MemStore 构成了Level 0，保证了数据的高可用和性能的低延迟。

StoreFile 和HFile 构成了Level 1，实现了不可靠数据的持久化，真正地将HBase变成了高可用的数据库系统。

## 3.3 Region 解析

每个一个Region 都会存储在一个确定的Region Server上，数据之间是互斥的关系。HBase表在行键上分割为多个Region，**它是HBae中分布式存储和负载均衡的最小单元，但不是存储的最小单元。**Region 按照大小切分，每个表一行只有一个Region ，一行数据不可能分布在多个Region 上。当不断插入导致列族到达阈值之后，Region 会被水平拆分为两个Region 。Region 在Region Server的运行过程中可能出现移动，这是Master的负载均衡策略或者因为出现宕机。Region 是用户和Region Server交互的实际载体，每个Region 都有三个信息来标识：

- Table Name（表名）
- Start RowKey（起始的RowKey）
- Create Time（创建时间）

<div align="center">
  <img src="https://gitee.com/IvanLu1024/picts/raw/master/blog/hbase/b7cbe24fly1g35hmy8xkjj20l70dpmzz.jpg"/>
</div>

Region 的拆分过程是：先该当前Region 下线，然后对其进行拆分，接着将子Region 加入到Meta的元信息中，再加入原Region Server上，最后同步到Master上。

## 3.4 HFile 解析

HBase实际以HFile的形式保存在HDFS上。

HFile文件是HBase存储数据的最基础形式，它的底层是Hadoop二进制文件，是用户数据的实际载体，存储着K-V这样的数据。

<div align="center">
  <img src="https://gitee.com/IvanLu1024/picts/raw/master/blog/hbase/b7cbe24fly1g34aux2wiqj20pj0it7be.jpg"/>
</div>

- Scanned block section：在顺序扫描HFile的时候，这个部分的数据块将会被读取，**用户数据存储于该部分**。
- Nonscanned block section：在顺序扫描HFile的时候，这个部分的数据块将不会被读取。主要包含一些元数据，在访问用户数据的时候，该部分不会被扫描到。
- Load-on-open section：当RegionServer 启动的时候，该部分数据会被加载到内存中，主要是HFile的一些元数据信息。
- Trailer：记录了HFile的基本信息，各个部分的偏移量和寻址信息。

<div align="center">
  <img src="https://gitee.com/IvanLu1024/picts/raw/master/blog/hbase/b7cbe24fly1g34bzmz8bqj219j0id49b.jpg"/>
</div>

Data Block：是HBase中最基础的存储单元，是实际存储用户的数据结构，**存储的是K-V数据**。

KeyType的作用：对于HBase中的删除操作，由于HFile一旦刷写成功就不可做修改，正常插入是Put，Delete表示删除整行，DeleteColumn表示删除某一列，DeleteFamily表示删除某个列族。这就是给数据打上一个标记，称为“墓碑标记”，实际上标识数据被删除掉了。当再次扫描的时候，发现有“墓碑标记”的时候，就会**在以后的某个时间点将对应的数据删除，并不是在插入的过程中就将其进行删除，这也是为了删除性能的高效性**。


## 3.5 WAL 解析

HBase的日志系统，WAL即预写日志：

<div align="center">
  <img src="https://gitee.com/IvanLu1024/picts/raw/master/blog/hbase/b7cbe24fly1g34cc4fqqej20r50fcjyj.jpg"/>
</div>

其最重要的功能就是灾难恢复，解决了高可用，解决了远程备份。

WAL是通过HLog实例进行实现的，HLog是使用append方法，对日志进行追加写的功能。WAL通过序列化的number去追踪数据的改变，其内部实现使用了AtimoicLong来保证线程安全。

<div align="center">
  <img src="https://gitee.com/IvanLu1024/picts/raw/master/blog/hbase/b7cbe24fly1g34cf313slj20ne0g6gqi.jpg"/>
</div>

- HLogKey：

<div align="center">
  <img src="https://gitee.com/IvanLu1024/picts/raw/master/blog/hbase/b7cbe24fly1g34ckpm0abj219g0d5dki.jpg"/>
</div>

- HLogSyncer：日志同步刷写类，有定时刷写和内存溢值两种工作方式。
- HLogRoller：Log的大小可以通过配置文件进行设置，默认是一个小时，每经过一个小时生成一个新的Log文件。当一定时间后，就有大量的日志文件。HLogRoller是在特定的时间滚动日志生成新的日志文件，避免了单个日志文件过大。根据序列化的number（Sequence Number）对比时间，删除旧的不需要的日志文件。

## 3.6 Compaction 解析

Compaction 会从Region Store中选择一些HFile文件进行合并，合并就是指将一些待合并的文件中的K-V对进行排序重新生成一个新的文件取代原来的待合并的文件。**由于系统不断地进行刷写会产生大量小文件，这样不利于数据查找。那么将这些小文件合并成一些大文件，这样使得查找过程的I/O次数减少，提高了查找效率**。其中可以分为以下两类：

<div align="center">
  <img src="https://gitee.com/IvanLu1024/picts/raw/master/blog/hbase/b7cbe24fly1g34l80ooxgj20og0h2n2o.jpg"/>
</div>

- MinorCompaction：选取一些小的相邻的Store File进行合并一个更大的Store File，生成多个较大的Store File。
- MajorCompaction：将所有的Store File合并成一个Store File，这个过程中将清理被删除的数据、TTL过期的数据、版本号超过设定版本号的数据。操作过程的时间比较长，消耗的资源也比较多。

HBase中Compaction的触发时机的因素很多，最主要的有三种：

- MemStore Flush ：每次执行完Flush 操作以后，都会进行判断当超过阈值就会触发一个合并。
- 后台线程周期性的检查：Compaction Checker会定期触发检查是否需要合并，这个线程会优先检查文件数是否大于阈值，一旦大于就会触发合并，若不满足会继续检查是否满足MajorCompaction。简单来说，就是如果当前Store中最早更新时间早于某个值，这个值成为mc time，就会触发大的合并，HBase通过这种方式来删除过期的数据，其浮动区间为[7-7 * 0.2, 7+7 * 0.2]。默认在七天会进行一次大合并（MajorCompaction）。
- 手动触发：通常是为了执行MajorCompaction，因为很多业务担心自动的MajorCompaction会影响性能，会选择在低峰期手动触发；还可能用户在进行完alter操作以后，希望立即生效；管理员在发现硬盘容量不够的时候会手动触发，这样可以删除大量的过期数据。

# 四、数据存取解析

## 4.1 数据存取流程解析

数据存储(客户端)：

提交之前会先请求Zookeeper来确定Meta表（元数据表）所在的Region Server的地址，再根据RowKey确定归属的Region Server，之后用户提交Put/Delete这样的请求，HBase客户端会将Put请求添加到本地的buffer中，符合一定的条件就会通过异步批量提交。

HBase默认设置auto flush（自动刷写）为true，表示put请求会直接提交给服务器进行处理，用户可以设置auto flush为false，这样put请求会首先放入本地的buffer中，等到buffer的大小达到一定阈值（默认是2M）后再提交。

<div align="center">
  <img src="https://gitee.com/IvanLu1024/picts/raw/master/blog/hbase/b7cbe24fly1g35hw8y2p1j20kb0ckq4j.jpg"/>
</div>

数据存储（服务器）：

当数据到达Region Server的某个Region后，首先获取RowLock（行锁），之后再写入日志和缓存，**持有行锁的时候并不会同步日志**，操作完释放RowLock（行锁），随后再将同步（sync）到HDFS上，如果同步失败进行回滚操作将缓存中已写入的数据删除掉，代表插入失败。当缓存达到一定阈值（默认是64M）后，启动异步线程将数据刷写到硬盘上形成多个StoreFile，当StoreFile数量达到一定阈值后会触发合并操作。当单个StoreFile的大小达到一定大小的时候会触发一个split操作，将当前的Region切分为两个Region，再同步到Master上，原有Region会下线，子Region会被Master分配到相应的Region Server上。

<div align="center">
  <img src="https://gitee.com/IvanLu1024/picts/raw/master/blog/hbase/b7cbe24fly1g35igfraixj20ux0auwit.jpg"/>
</div>

数据获取（客户端）：

这里同数据存储的过程是类似的。

<div align="center">
  <img src="https://gitee.com/IvanLu1024/picts/raw/master/blog/hbase/b7cbe24fly1g35iiaibggj20kl0c275w.jpg"/>
</div>

数据获取（服务器）：

Region Server在接收到客户端的Get/Scan请求之后，首先HBase在确定的Region Server上构造一个RegionScanner准备为当前定位的Scan做检索，RegionScanner会根据列族构建StoreScanner，有多少个列族就会构建多少个StoreScanner。每个StoreScanner会为当前Store中的每个HFile构建一个StoreFileScanner，用于实际执行对应的文件检索。同时会对对应的Mem构建对应的MemStoreScanner，用于检索MemStore中的数据。构建两类Scanner的原因在于，数据可能还没有完全刷写到硬盘上，部分数据还存储于内存之中。检索完之后，就能够找到对应的K-V，再经过简单地封装就形成了ResultSet，就可以直接返回给客户端。

<div align="center">
  <img src="https://gitee.com/IvanLu1024/picts/raw/master/blog/hbase/b7cbe24fly1g35iv23cpfj20yc0fewjs.jpg"/>
</div>

## 4.2 数据存取优化

存储优化：

HBase通过MemStore和WAL两种机制，实现数据顺序快速的插入，极大降低了数据存储的延迟。

检索（获取）优化：

HBase使用[布隆过滤器](https://zh.wikipedia.org/wiki/%E5%B8%83%E9%9A%86%E8%BF%87%E6%BB%A4%E5%99%A8)来提高**随机读**的性能，布隆过滤器是列族级别的配置。HBase中的每个HFile都有对应的位数组，K-V在写入HFile时，会经过几个哈希函数的映射并写入对应的位数组里面。HFile中的位数组，就是布隆过滤器中存储的值。HFile越大位数组也会越大，太大就不适合放入内存中了，因此HFile将位数组以RowKey进行了拆分，一部分连续的RowKey使用一个位数组。因此HFile会有多个位数组，在查询的时候，首先会定位到某个位数组再将该位数组加载到内存中进行过滤就行，这样减少了内存的开支。

HBase中存在两种布隆过滤器：

- Row：根据RowKey来过滤StoreFile，这种情况可以针对列族和列都相同，只有RowKey不同的情况；
- RowCol：根据RowKey+ColumnQualifier（列描述符）来过滤StoreFile，这种情况是针对列族相同，列和RowKey不同的情况。

# 五、特点

- 容量大：HBase单表可以有百亿行、百万列，数据矩阵横向和纵向两个纬度所支持的数据量级都非常具有弹性；
- 面向列：列是可以动态增加的，不需要指定列，面向列的存储和权限控制，并支持独立检索；
- 多版本：每一个列的数据存储有个多Version；
- 稀疏性：为空的列不占用存储空间；
- 扩展性：底层依赖于HDFS，空间不够的时候只需要横向扩展即可；
- 高可靠性：副本机制保证了数据的可靠性；
- 高性能：写入性能高，底层使用LSM数据结构和RowKey有序排序等架构上的独特设计；读性能高，使用region切分、主键索引和缓存机制使得具备随机读取性能高。

<div align="center">
  <img src="https://gitee.com/IvanLu1024/picts/raw/master/blog/hbase/b7cbe24fly1g35ju61jgaj20r00butcu.jpg"/>
</div>

# 六、和关系型数据库的对比

区别：

- 列动态增加；
- 数据自动切分；
- 高并发读写；
- 不支持条件查询

[HBase shell命令](https://www.cnblogs.com/ityouknow/p/7344001.html)

# 参考资料

<https://www.imooc.com/learn/996>