## 环境准备
Centos7.x 两台，分别安装tracker与storage.  
下载安装包：
- libfatscommon：FastDFS分离出的一些公用函数包
- FastDFS：FastDFS本体
- fastdfs-nginx-module：FastDFS和nginx的关联模块
- nginx：发布访问服务
## 安装步骤 (tracker与storage都要执行)
- 安装基础环境
> yum install -y gcc gcc-c++  
  yum -y install libevent
- 安装libfatscommon函数库
```shell
tar -zxvf libfastcommon-1.0.42.tar.gz

进入libfastcommon文件夹，编译并且安装

./make.sh
./make.sh install
```
`注意控制台输出的安装的目录。`

- 安装fastdfs主程序文件
```shell
# 解压
tar -zxvf fastdfs-6.04.tar.gz
进入到fastdfs目录，查看fastdfs安装配置
cd fastdfs-6.04/
vim make.sh
# 安装fastdfs
./make.sh
./make.sh install  
```

- 拷贝配置文件：
```jshelllanguage
cp /home/software/FastDFS/fastdfs-6.04/conf/* /etc/fdfs/
```

## 使用手册
### 配置 tracker
- 编辑 tracker.conf
```properties
# 只需要修改配置文件的基础目录就行
base_path=/usr/local/fastdfs/tracker
```
-p 表示递归创建目录
> mkdir /usr/local/fastdfs/tracker -p
- 启动 tracker
> /usr/bin/fdfs_trackerd /etc/fdfs/tracker.conf
- 停止 tracker
> /usr/bin/stop.sh /etc/fdfs/tracker.conf

### 配置 storage
- 修改 storage.cond 配置文件
```properties
# 修改组名
group_name=sxzhongf
# 修改storage的工作空间
base_path=/usr/local/fastdfs/storage
# 修改storage的存储空间
store_path0=/usr/local/fastdfs/storage
# 修改tracker的地址和端口号，用于心跳
tracker_server=192.168.1.200:22122
# nginx的一个对外服务端口号
http.server_port=8888
```
> mkdir /usr/local/fastdfs/storage -p

- 启动 storage (先启动 tracker )
> /usr/bin/fdfs_storaged /etc/fdfs/storage.conf

## 参考文献
https://github.com/happyfish100/
https://github.com/happyfish100/fastdfs/wiki
https://www.cnblogs.com/leechenxiang/p/5406548.html
https://www.cnblogs.com/leechenxiang/p/7089778.html