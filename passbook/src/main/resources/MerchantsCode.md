需要启动的服务：

   - HBase
   - MySql
   - Kafka
   - Redis
   
需要清空的数据：
 1. HBase的四张表:truncate 'pb:passtemplate' 等
 2. MySQL商户数据:delete from merchants;
 3. token数据:删除/tmp/token下的所有文件
 4. redis 的数据:flushall
 
## 商户子系统的测试 
 1. 创建商户
    POST: 127.0.0.1:9527/merchants/create
    header: token/ivan-passbook-merchants
    {
        "name":"Ivan",
        "logUrl":""
    }
 
    