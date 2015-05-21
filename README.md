java分布式爬虫，主机和从机控制的机制, ConsistentHash分发Url，维持负载均衡
==================

### 说明
    本文使用redis来保存url, 请自己安装redis，否则无法运行爬虫，

### 目录结构
    CrawlerMaster
    |—— 爬虫主机端，进行url分发， 运行main.Index.java即可
    |—— 加入从机之后，按照以下4个步骤点击按钮：
    |—— 1.分发url
    |—— 2.保存url
    |—— 3.开始爬取
    
    CrawlerSlave 
    |—— 爬虫从机端，具体进行爬取，如果想要和主机连接，进行分布式爬取，运行main.Index.java，
    |—— 如果在一台电脑上开启多个从机，需要自己修改网页的保存目录，默认为./web
    
    
