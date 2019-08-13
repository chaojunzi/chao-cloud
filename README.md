Chao-cloud: springboot 拓展工具包
=====

<p align="center">
  <a href="https://search.maven.org/search?q=chaojunzi">
    <img alt="maven" src="https://img.shields.io/maven-central/v/com.github.chaojunzi/chao-cloud-parent.svg?style=flat-square">
  </a>

  <a href="https://github.com/996icu/996.ICU/blob/master/LICENSE">
    <img alt="996icu" src="https://img.shields.io/badge/license-NPL%20(The%20996%20Prohibited%20License)-blue.svg">
  </a>

  <a href="https://www.apache.org/licenses/LICENSE-2.0">
    <img alt="code style" src="https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square">
  </a>
</p>

------

以 spring-boot 为基础，集成feign，微信sdk，百度ai等第三方工具，以及自定义接口权限拦截，表情处理，ftp连接池，地图坐标解析，mybatis-plus 代码自动生成，token防止表单重复提交，语音处理，敏感词过滤，分词器，参数校验等

	chao-cloud
		│
		├─common   		//工具包
		│	├─core 		//核心
		│	├─extra 	//拓展
		│	└─config	//配置
		│
		└─......
		
------  


#### chao-cloud 提供以下功能

   * chao-cloud-common-core  
         *  [package-包结构](https://github.com/chaojunzi/chao-cloud#1. package-包结构（core）)
   * chao-cloud-common-extra  
         *  access 接口限流
       	  

   	 
#### maven 安装（以下为逐级依赖） 一般直接依赖 [config] 即可
**parent（pom）**  

```
<dependency>
	<groupId>com.github.chaojunzi</groupId>
	<artifactId>chao-cloud-parent</artifactId>
	<version>1.0.6</version>
</dependency>
```

**core核心↑** 

```
<dependency>
	<groupId>com.github.chaojunzi</groupId>
	<artifactId>chao-cloud-common-core</artifactId>
	<version>1.0.6</version>
</dependency>
```

**extra 第三方依赖↑**  

```
<dependency>
	<groupId>com.github.chaojunzi</groupId>
	<artifactId>chao-cloud-common-extra</artifactId>
	<version>1.0.6</version>
</dependency>
```
**config 注解和配置↑** 

```
<dependency>
	<groupId>com.github.chaojunzi</groupId>
	<artifactId>chao-cloud-common-config</artifactId>
	<version>1.0.6</version>
</dependency>
```
	
## 调用方法

chao-cloud 几乎所有功能都采取插件化处理，以注解和配置文件（yaml语法），在启动类配置即可

### 1. package-包结构（core）

	chao-cloud-common-core
			│
			└─com.chao.cloud.common   		 
				 	├─annotation 		//注解，递归树，参数解析
				 	├─base 	 		//接口工具，一些默认的公共方法
				 	├─constant 		//常量和枚举，返回码，错误信息
				 	├─convert 		//参数转换，拦截器，返回值处理
				 	├─core 			//核心包，Application，SpringUtil
				 	├─entity 		//公共实体类，Response，Tree
				 	├─exception 		//异常类，BusinessException
				 	└─util			//工具类，透明背景验证码，权限（2的权的和）校验，EntityUtil递归树，list转换
		 
		
### 环境依赖
##### jdk1.8+↑ 
##### Eclipse 4.7（Oxygen[氧气]）+↑
##### maven阿里云仓库   			

### 鸣谢：  
[hutool-超级工具类](https://github.com/looly/hutool)  
[lombok](https://github.com/rzwitserloot/lombok)  
[微信-sdk](https://github.com/Wechat-Group/WxJava)  
[mybatis-plus](https://github.com/baomidou/mybatis-plus)  
[layui](https://github.com/sentsin/layui/)  
[echarts](https://github.com/apache/incubator-echarts)  
[......](https://github.com/)  

