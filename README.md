# HDBox_Android
### 申明

由于黑龙江大学校方将黑大盒子用于获取学生信息使用的模拟登录校园网功能定义为 **钓鱼网站行为** ，要求我们关闭服务器并停止相关应用开发，目前项目处于停滞状态，代码不再继续维护。如果你对项目中使用到的相关技术感兴趣，可以免费获取源代码并作修改，同时也欢迎对已有代码提出意见或建议。如果你需要在已有项目基础上再开发并使用，**请务必注明**你的行为属于个人行为，与 classTC 及其团队无关，一切后果自负

### 简介

黑大盒子，一款为黑龙江大学在校学生提供查课表，查考试安排，查成绩，查学分绩点以及校内咨询等功能的应用，截至项目停滞时，Android 端与 iOS 端累积下载量超过一万次，注册用户超过五千人。



### 服务器端爬虫

项目前期采用 C/S 架构，服务器端(Server)采用 Flask + uWsgi + Nginx 搭建的小型 Python Web 应用，提供爬虫，数据解析，数据持久化存储等功能，提供接口供客户端调用，服务器部署在阿里云 ECS 上，客户端(Client)分为 Android 客户端应用和 iOS 客户端应用，其中 Android 端应用采用 RxJava + Retrofit + Glide + Realm 等框架开发

登录流程如下图所示



获取到登录后返回的 token，以后对校园网的访问就可以使用 token 而避免使用密码，但是，token 是有有效期的，因此在客户端会有个过期检测，过期自动执行登录，因此在用户设备上始终是要缓存用户密码的，为了安全起见，可以对用户密码做一定的加密(当前版本还没有实现这个)



获取用户课表数据，考试安排数据，成绩数据，以及用户个人信息数据的流程大致相同，如下图所示





### 客户端爬虫

由于黑大盒子用户数量急剧上升，导致服务器对黑龙江大学校园网访问量剧增，致使黑龙江大学校园网管理人员发现异常并将黑大盒子服务器 IP 拉入黑名单，使得查课表，查考试安排，查成绩，查学分绩点等功能失效。因此我们决定将爬虫功能放在客户端，通过测试验证，这是可行的，这也是目前最新版的实现方案

登录流程如下所示

