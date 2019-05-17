# :credit_card:MyCards

这是一款仿小米卡包的个人项目，用于个人学习交流分享

## 一、技术栈

存储：MySQL+Redis+HBase

中间件：Kafka

框架：SpringBoot

## 二、系统架构

该应用分为**商户子投放系统**和**用户应用子系统**两个部分，

- 商户投放子系统（商户开放平台）：提供给商户注册和投放优惠券的功能，并且需要对商户进行校验以免非法攻击；
- 用户应用子系统：提供给用户领取优惠券、使用优惠券、查看优惠券和反馈用户意见等功能。

代码结构：

- [商户投放子系统（merchants）](https://github.com/IvanLu1024/MyCards/blob/master/merchants/)
- [用户应用子系统（passbook）](https://github.com/IvanLu1024/MyCards/blob/master/passbook/)

架构图如下图所示：

<div align="center">
    <img src="https://ws1.sinaimg.cn/mw690/b7cbe24fgy1g322bq7kpgj20sj0lrmyx.jpg"/>
</div>

## 三、开发环境

- 开发工具：Intellij IDEA
- 框架：SpringBoot 1.5.3
- 存储：MySQL 5.1.30，Redis 3.0.0，HBase 1.2.6
- 中间件：Kafka 2.11-1.1.1
- 测试工具：[Postman](<https://www.getpostman.com/>)

## 四、环境配置教程

[查看详细](https://github.com/IvanLu1024/MyCards/blob/master/notes/环境配置.md)

## 五、测试教程

[查看详细]()

## 六、涉及技术和原理讲解

- [MySQL]()
- [SpringBoot]()
- [Redis]()
- [HBase](https://github.com/IvanLu1024/MyCards/blob/master/notes/HBase.md)

## 七、踩坑记录

[查看详细](https://github.com/IvanLu1024/MyCards/blob/master/notes/踩坑记录.md)

> 继续完善中……

## 参考资料

- [慕课网-Java分布式优惠券系统后台](<https://coding.imooc.com/class/254.html>)