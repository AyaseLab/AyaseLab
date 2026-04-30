# Linux虚拟机服务环境搭建
由于部署Apollo服务在windows系统上存在兼容问题，启动命令会报错，所以建Linux虚拟机环境，用于运行mysql、apollo、java服务
## 一.下载安装VMWare workstation pro
下载地址：https://www.vmware.com/products/desktop-hypervisor/workstation-and-fusion  
本次使用的版本是25H2u1，安装前注意关闭Hyper-V，安装在非系统盘上即可
## 二.下载并安装ubuntu server
下载地址：https://cn.ubuntu.com/download#server  
本次使用的ubuntu server版本是ubuntu-24.04.3-live-server-amd64.iso，服务器不需要桌面操作环境所以安装server版就足够用了  

下载完成后在VMWare workstation里创建虚拟机，选择Linux的Ubuntu 64-bit，位置放在磁盘充裕的非系统盘上  
分配磁盘空间60G，内存4G，网络NAT模式，其余选择默认即可，后续不够用可以调整  
在虚拟机设置里选择本地下载好的镜像文件，进入安装程序，类似于安装windows系统时插入的系统安装包的U盘
基本可以一路默认，磁盘空间处可以设置swap，这也可以等到系统安装完成后再设置。选择安装openssh server。一路Enter到最后reboot就可以了。

迁移这台Linux机器的时候只需要copy走虚拟机安装目录下全部文件就可以了，注意在windows上运行的Linux虚拟机一般是x86_64架构的，可以迁移到同样CPU架构的机器上，但是不支持arm架构，无法迁移到mac。
## 三.系统初始化
1.更新系统
``` 
sudo apt update
sudo apt upgrade -y
sudo reboot
```
2.确认系统时间和修改时区
```
cat /etc/os-release
timedatectl
```
```
sudo timedatectl set-timezone Asia/Shanghai
```
3.安装常用工具
```
sudo apt install -y curl wget vim unzip zip net-tools lsof tree htop openssh-server ca-certificates gnupg
```
4.确认ssh状态
```
// 查看ssh状态
systemctl status ssh

// 如果ssh没有启动
sudo systemctl enable --now ssh

// 获取linux机器ip地址
ip addr
ifcongig
```
5.ufw防火墙配置
```
sudo ufw allow OpenSSH
sudo ufw allow 8070/tcp
sudo ufw allow 8080/tcp
sudo ufw allow 8090/tcp
sudo ufw enable
sudo ufw status
```


## 四.linux常用命令
前面提到的就不再重复了，下面是一些在日常管理机器过程中，或者监控服务状态过程中会使用到的命令  
1.vim命令
```
// 进入编辑
i        当前光标前插入
a        当前光标后插入
o        下一行新建并插入
Esc      退出编辑模式，回到命令模式
 
// 保存退出
:w       保存
:q       退出
:wq      保存并退出
:x       保存并退出
:q!      不保存强制退出

// 移动光标
h        左
j        下
k        上
l        右
gg       到文件开头
G        到文件结尾
数字G    跳到指定行，比如 20G

// 删除/复制/粘贴
x        删除当前字符
dd       删除当前行
数字dd   删除多行，比如 5dd
yy       复制当前行
数字yy   复制多行，比如 3yy
p        粘贴到下一行/光标后
P        粘贴到上一行/光标前

// 查找
/关键字   向下查找
?关键字   向上查找
n        下一个匹配
N        上一个匹配

// 撤销/重做
u        撤销
Ctrl + r 重做

// 显示行号
:set number
```
2.ufw命令
```

```




