chao-cloud: springboot 拓展工具包
=====

<p>
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
		│  ├─core 		//核心
		│  ├─extra 		//拓展
		│  └─config		//配置
		│
		└─......
		
------  

#### chao-cloud 提供以下功能

   * [package-包结构](#>-package-%E5%8C%85%E7%BB%93%E6%9E%84)
   * chao-cloud-common-core  
       * [递归树](#>-%E9%80%92%E5%BD%92%E6%A0%91)
       * [透明背景验证码](#>-%E9%80%8F%E6%98%8E%E8%83%8C%E6%99%AF%E9%AA%8C%E8%AF%81%E7%A0%81)
       * [Spring-核心配置](#>-Spring-%E6%A0%B8%E5%BF%83%E9%85%8D%E7%BD%AE)
       * [全局异常处理](#>-%E5%85%A8%E5%B1%80%E5%BC%82%E5%B8%B8%E5%A4%84%E7%90%86)
       * [全局参数校验](#>-%E5%85%A8%E5%B1%80%E5%8F%82%E6%95%B0%E6%A0%A1%E9%AA%8C)
       * [web](#>-web)
       * [新建一个web项目](#>-%E6%96%B0%E5%BB%BA%E4%B8%80%E4%B8%AAweb%E9%A1%B9%E7%9B%AE)
   * chao-cloud-common-extra  
       * [接口访问控制](#>-%E6%8E%A5%E5%8F%A3%E8%AE%BF%E9%97%AE%E6%8E%A7%E5%88%B6)
       * [表情过滤](#>-%E8%A1%A8%E6%83%85%E8%BF%87%E6%BB%A4)
       * [feign微服务接口调用](#>-feign%E5%BE%AE%E6%9C%8D%E5%8A%A1%E6%8E%A5%E5%8F%A3%E8%B0%83%E7%94%A8)
       * [ftp连接池](#>-ftp%E8%BF%9E%E6%8E%A5%E6%B1%A0)
       * [地图解析](#>-%E5%9C%B0%E5%9B%BE%E8%A7%A3%E6%9E%90)
       * [mybatis-plus](#>-mybatis-plus)
       * [mybatis-plus-generator](#>-mybatis-plus-generator)
       * [防止表单重复提交](#>-%E9%98%B2%E6%AD%A2%E8%A1%A8%E5%8D%95%E9%87%8D%E5%A4%8D%E6%8F%90%E4%BA%A4)
       * [seata-分布式事务](#>-seata-%E5%88%86%E5%B8%83%E5%BC%8F%E4%BA%8B%E5%8A%A1)
       * [语音识别-百度AI](#>-%E8%AF%AD%E9%9F%B3%E8%AF%86%E5%88%AB-%E7%99%BE%E5%BA%A6AI)
       * [微信小程序](#>-%E5%BE%AE%E4%BF%A1%E5%B0%8F%E7%A8%8B%E5%BA%8F)
       * [微信支付](#>-%E5%BE%AE%E4%BF%A1%E6%94%AF%E4%BB%98)
   * chao-cloud-common-config  
       * [接口权限校验](#>-%E6%8E%A5%E5%8F%A3%E6%9D%83%E9%99%90%E6%A0%A1%E9%AA%8C)
       * [跨域访问](#>-%E8%B7%A8%E5%9F%9F%E8%AE%BF%E9%97%AE)
       * [cron-定时器](#>-cron-%E5%AE%9A%E6%97%B6%E5%99%A8)
       * [redis](#>-redis)
       * [敏感词过滤](#>-%E6%95%8F%E6%84%9F%E8%AF%8D%E8%BF%87%E6%BB%A4)
       * [线程池](#>-%E7%BA%BF%E7%A8%8B%E6%B1%A0)
       * [分词器](#>-%E5%88%86%E8%AF%8D%E5%99%A8)
   	 
#### maven 安装（以下为逐级依赖） 一般直接依赖 [config] 即可
**parent（pom）**  

```
<dependency>
	<groupId>com.github.chaojunzi</groupId>
	<artifactId>chao-cloud-parent</artifactId>
	<version>1.0.7</version>
</dependency>
```

**core核心↑** 

```
<dependency>
	<groupId>com.github.chaojunzi</groupId>
	<artifactId>chao-cloud-common-core</artifactId>
	<version>1.0.7</version>
</dependency>
```

**extra 第三方依赖↑**  

```
<dependency>
	<groupId>com.github.chaojunzi</groupId>
	<artifactId>chao-cloud-common-extra</artifactId>
	<version>1.0.7</version>
</dependency>
```
**config 注解和配置↑** 

```
<dependency>
	<groupId>com.github.chaojunzi</groupId>
	<artifactId>chao-cloud-common-config</artifactId>
	<version>1.0.7</version>
</dependency>
```
	
## 调用方法

chao-cloud 几乎所有功能都采取插件化处理，以注解和配置文件（yaml语法），在启动类配置即可

### >. package-包结构

	1.chao-cloud-common-core
		│
		└─com.chao.cloud.common   		 
		 	├─annotation 		//注解，递归树，参数解析
		 	├─base 	 		//接口工具，一些默认的公共方法
		 	├─constant 		//常量和枚举，返回码，错误信息
		 	├─core 			//核心包，Application，SpringUtil
		 	├─entity 		//公共实体类，Response，Tree
		 	├─exception 		//异常类，BusinessException
		 	├─util			//工具类，透明背景验证码，权限（2的权的和）校验，EntityUtil递归树，list转换
		 	└─web			//web（全局参数校验，controller拦截，全局异常，核心，健康检查，资源访问，参数解析等）
		 	  ├─annotation		//注解 application exception valid web
		 	  ├─config 		//配置aop application valid web
		 	  ├─convert 		//rest 参数拦截返回值转换
		 	  └─HealthController	//健康监测API
		 
	2.chao-cloud-common-extra 
		│
		└─com.chao.cloud.common.extra    		 
		 	├─access 		//接口访问控制（错误次数和超时时间）
		 	├─emoji 	 	//表情过滤（接口层）
		 	├─feign 		//spring-cloud 微服务间接口调用（可传递文件）
		 	├─ftp 			//ftp连接池
		 	├─map 			//地图解析（地址转坐标，距离计算一对多）
		 	├─mybatis 		//mybatis 日志，代码自动生成，乐观锁，分页
		 	├─token 		//拦截表单重复提交
		 	├─tx 			//微服务分布式事务（springcloud+seata+feign）
		 	├─voice 		//百度AI-语音转文字
		 	└─wx			//微信支付，微信小程序（单例）
		 	
	3.chao-cloud-common-config 
		│
		└─com.chao.cloud.common.config    		 
		 	├─auth 			//接口权限校验
		 	├─cors 			//跨域访问
		 	├─cron 			//cron 定时器
		 	├─redis 		//redis 缓存
		 	├─sensitive 		//敏感词过滤
		 	├─thread 		//线程池-ThreadPoolTaskExecutor
		 	└─tokenizer 		//分词器（可自定义词库）
		 
### > 递归树

```java
//方法一：（接口法）
@Data
@Accessors(chain = true)
@NoArgsConstructor(staticName = "of")
public class TreeDTO implements TreeEntity<TreeDTO> {

	private Integer id; // id
	private Integer parentId;// 父id
	private List<TreeDTO> subList;// 子集

	@Override
	public Serializable getId() {
		return id;
	}

	@Override
	public Serializable getParentId() {
		return parentId;
	}

	@Override
	public void setSubList(List<TreeDTO> subList) {
		this.subList = subList;
	}
}
//递归处理
	public static void main(String[] args) {
		List<TreeDTO> list = new ArrayList<>();
		list.add(TreeDTO.of().setId(1).setParentId(0));
		list.add(TreeDTO.of().setId(2).setParentId(0));
		list.add(TreeDTO.of().setId(3).setParentId(1));
		list.add(TreeDTO.of().setId(4).setParentId(2));
		list.add(TreeDTO.of().setId(5).setParentId(2));
		List<TreeDTO> treeList = EntityUtil.toTreeList(list, 0);
		Console.log(JSONUtil.toJsonPrettyStr(treeList));
	}

//方法二：（注解法）
@Data
@Accessors(chain = true)
@NoArgsConstructor(staticName = "of")
public class TreeDTO {

	@TreeAnnotation(TreeEnum.ID)
	private Integer id; // id
	@TreeAnnotation(TreeEnum.PARENT_ID)
	private Integer parentId;// 父id
	@TreeAnnotation(TreeEnum.SUB_LIST)
	private List<TreeDTO> subList;// 子集

}
//递归处理
	public static void main(String[] args) {
		List<TreeDTO> list = new ArrayList<>();
		list.add(TreeDTO.of().setId(1).setParentId(0));
		list.add(TreeDTO.of().setId(2).setParentId(0));
		list.add(TreeDTO.of().setId(3).setParentId(1));
		list.add(TreeDTO.of().setId(4).setParentId(2));
		list.add(TreeDTO.of().setId(5).setParentId(2));
		List<TreeDTO> treeList = EntityUtil.toTreeAnnoList(list, 0);
		Console.log(JSONUtil.toJsonPrettyStr(treeList));
	}

//结果
[
    {
        "subList": [
            {
                "subList": [
                ],
                "id": 3,
                "parentId": 1
            }
        ],
        "id": 1,
        "parentId": 0
    },
    {
        "subList": [
            {
                "subList": [
                ],
                "id": 4,
                "parentId": 2
            },
            {
                "subList": [
                ],
                "id": 5,
                "parentId": 2
            }
        ],
        "id": 2,
        "parentId": 0
    }
]
```
	
### >. 透明背景验证码

```java
HyalineCircleCaptcha captcha = HyalineCaptchaUtil.createCircleCaptcha(100, 42, 4, 3);
String code = captcha.getCode();
log.info("[验证码: {}]", code);
```
### >. Spring-核心配置

```java
@EnableCore
```
- 说明
  * 在启动类增加@EnableCore  
  * 可使用全局 SpringContextUtil 处理相关bean 
  * 可实现接口 [IApplicationRestart] 做一些容器启动后的相关操作 
      
### >. 全局异常处理

```java
@EnableGlobalException
```
- 说明
  * 在启动类增加@EnableGlobalException  

### >. 全局参数校验

```java
@EnableValidator

//简单用法
@Null   被注释的元素必须为 null    
@NotNull    被注释的元素必须不为 null    
@AssertTrue     被注释的元素必须为 true    
@AssertFalse    被注释的元素必须为 false    
@Min(value)     被注释的元素必须是一个数字，其值必须大于等于指定的最小值    
@Max(value)     被注释的元素必须是一个数字，其值必须小于等于指定的最大值    
@DecimalMin(value)  被注释的元素必须是一个数字，其值必须大于等于指定的最小值    
@DecimalMax(value)  被注释的元素必须是一个数字，其值必须小于等于指定的最大值    
@Size(max=, min=)   被注释的元素的大小必须在指定的范围内    
@Digits (integer, fraction)     被注释的元素必须是一个数字，其值必须在可接受的范围内    
@Past   被注释的元素必须是一个过去的日期    
@Future     被注释的元素必须是一个将来的日期    
@Pattern(regex=,flag=)  被注释的元素必须符合指定的正则表达式    
    
Hibernate Validator 附加的 constraint    
@NotBlank(message =)   验证字符串非null，且长度必须大于0    
@Email  被注释的元素必须是电子邮箱地址    
@Length(min=,max=)  被注释的字符串的大小必须在指定的范围内    
@NotEmpty   被注释的字符串的必须非空    
@Range(min=,max=,message=)  被注释的元素必须在合适的范围内 
```
- 说明
  * 在启动类增加@EnableValidator  
      - 国际化
  * 使用方式：  
      - 在Controller 添加注解 @Validated
      - 在method-> 基本类型  前添加注解 @NotNull
      - 在method-> String 前添加注解 @NotBlank
      - 在method-> 对象  前添加注解 @Valid
  * 详情请点击[@参考1-普通版](https://www.jianshu.com/p/0bfe2318814f)      
  * 详情请点击[@参考2-springboot版](https://cloud.tencent.com/developer/article/1054194)      
  * 详情请点击[github@hibernate-validator](https://github.com/hibernate/hibernate-validator)      
      
### >. web

```java
@EnableWeb

//yaml 配置
 spring:
  resources:  #这里必须使用字符串 后面的 / 必须有，多个路径逗号分割(越靠前，优先级越高；默认包含下面4个路径，且高于下面的优先级)
    static-locations: file:/path/static/

```
- 说明
  * 在启动类增加@EnableWeb  
      - 默认集成@EnableValidator
      - 默认集成@EnableCore
      - 默认集成AOP->参数拦截器->ConvertInterceptor
      - 默认集成健康检查：HealthController [/health/core]
          - ip: 主机地址
          - macAddress:  物理地址
      	  - threadCount: 线程数
          - freeMemory: 剩余可用内存
          - totalMemory: 总内存
          - useMemory: 已经使用内存
          - useRate: 使用比率
      - 默认集成静态资源访问：spring.resources.static-locations 
          - classpath:/META-INF/resources/
          - classpath:/resources/
          - classpath:/static/
          - classpath:/public/
 
### >. 新建一个web项目

```java
//1.创建启动类
@SpringBootApplication
@EnableWeb // web
@EnableGlobalException // 全局异常处理
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}

//2.yaml 配置
logging.config: classpath:chao-logback.xml
log.path: /logs/${spring.application.name}
log.maxHistory: 30
server:
  port: 8080 #端口
spring:
  application:
    name: ApplicationName
  output:
    ansi: #彩色日志
      enabled: always 

//3.maven-pom
<parent>
	<groupId>com.github.chaojunzi</groupId>
	<artifactId>chao-cloud-parent</artifactId>
	<version>1.0.7</version>
</parent>
<dependencies>
	<dependency>
		<groupId>com.github.chaojunzi</groupId>
		<artifactId>chao-cloud-common-config</artifactId>
		<version>${chao.cloud.version}</version>
	</dependency>
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-web</artifactId>
	</dependency>
</dependencies>
```
- 说明
  * 在启动类增加@SpringBootApplication  
  * 在启动类增加@EnableWeb  
  * 在启动类增加@EnableGlobalException  
  * 自定义接口->end  

### >. 接口访问控制

```java
@EnableAccessLimit
```
- 说明
  * 在启动类增加@EnableAccessLimit  
  * 方法method增加@AccessLimit（Controller）  
      - timeout： 拦截持续时间
      - count： 最大出错次数
      - enable： 是否可用，false，此注解将无效。
  	
### >. 表情过滤

```java
@EnableEmojiFilter
```
- 说明
  * 在启动类增加@EnableEmojiFilter  
  * 方法method增加@EmojiFilter（Controller）  
      - value： 是否可用，false，将不参与拦截。

### >. feign微服务接口调用

```java
@EnableFeign

//yaml 配置文件
chao:
  cloud: 
    feign:
      http:
        connect-timeout:  #连接时间
        socket-timeout: #超时时间
        idle-timeout:  #超时时间
        max-per-route: #同路由的并发数
        max-total: #总连接数
        time-to-live: #持续时间 
        delay:  #定时器延迟时间
        period: #周期
```
- 说明
  * 在启动类增加@EnableFeign  
  * 方法method增加@FeignFallback（Controller 接口格式请自行查阅官方文档）  
  * 文件传输MultipartFile::name 数组必须为files  
      
### >. ftp连接池

```java
@EnableFtp

//业务类注入
@Autowired
private IFileOperation fileOperation;

//yaml 配置文件
chao:
  cloud: 
    ftp: 
      config:
        username: #用户名
        password: #密码
        host: #主机
        port: #端口
        path: #文件夹根目录
        logo: #文字水印
```
- 说明
  * 在启动类增加@EnableFtp  
  * 在调用类注入 IFileOperation
      
### >. 地图解析

```java
@EnableMapAnalysis

//业务类注入
@Autowired
private MapService mapService;

//yaml 配置文件
chao:
  cloud: 
    map: 
      key: #腾讯地图申请key
```
- 说明
  * 在启动类增加@EnableMapAnalysis  
  * 在调用类注入 MapService
      
### >. mybatis-plus

```java
//maven
<dependency>
	<groupId>com.alibaba</groupId>
	<artifactId>druid-spring-boot-starter</artifactId>
</dependency>
<dependency>
	<groupId>com.baomidou</groupId>
	<artifactId>mybatis-plus-boot-starter</artifactId>
</dependency>

@MybatisPlusConfig

//yaml 配置文件
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true #sql 实体类驼峰下划线转换
    log-impl:  com.chao.cloud.common.extra.mybatis.log.Slf4jLogImpl #日志打印
```
- 说明
  * 在启动类增加@MybatisPlusConfig  
  * 默认集成乐观锁和分页插件
  * 详情请点击[原创@mybatis-plus](https://mybatis.plus/guide/)
  
### >. mybatis-plus-generator

```java
//maven
<dependency>
	<groupId>com.baomidou</groupId>
	<artifactId>mybatis-plus-generator</artifactId>
</dependency>

@EnableMybatisGenerator

//业务类注入
@Autowired
private ZipAutoGenerator autoGenerator;

//yaml 配置文件
#默认使用本地数据源-代码自动生成
chao:
  cloud:
    codegen: #代码自动生成
        before:
          template-style: rest #rest 风格 
        datasource:
          url: ${spring.datasource.url}
          username: ${spring.datasource.username}
          password: ${spring.datasource.password}
          driver-name: ${spring.datasource.driver-class-name}
        package:
          parent: com.chao.cloud.generator #上级包名
        global:
         author: #作者
        strategy:
         logic-delete-field-name: deleted #逻辑删除
```
- 说明
  * 在启动类增加@EnableMybatisGenerator  
  * 在调用类注入 ZipAutoGenerator
  * 使用：autoGenerator.execute(out); //out 为输出流
  * 案例[@chao-cloud-generator](https://github.com/chaojunzi/chao-cloud-generator)
  * 详情请点击[原创@mybatis-plus-generator](https://mybatis.plus/guide/generator.html)
  
### >. 防止表单重复提交

```java
@EnableFormToken
```
- 说明
  * 在启动类增加@EnableFormToken  
  * 方法method增加@FormToken（Controller）  
      - save： 添加session[uuid]。
      - remove： 删除session[uuid]。
  * 表单  input name="formToken"
  * 目前只支持session，后续将支持redis等

### >. seata-分布式事务

```java
@EnableTxSeata
```
- 说明
  * 在启动类增加@EnableTxSeata  
  * 目前只支持 nacos+feign+seata
  * demo->[chaojunzi@分布式事务微服务](https://github.com/chaojunzi/chao-cloud-micro)
  
### >. 语音识别-百度AI
```java
@EnableVoiceAI

//业务类注入
@Autowired
private SpeechRecognitionService speechRecognitionService;

//yaml 配置
chao:
  cloud:
    ai:
      voice:
        client:
          app-id: #百度语音ai申请
          secret-key: #百度语音ai申请
          api-key: #百度语音ai申请
          timeout-connection: #连接时间
          timeout-socket: #超时时间
```
- 说明
  * 在启动类增加@EnableVoiceAI  
  * 在调用类注入 SpeechRecognitionService
  * 详情请点击[@百度ai-语音识别](https://ai.baidu.com/docs#/ASR-API/top)
  
### >. 微信小程序
```java
@EnableWxMaSingleton

//业务类注入
@Autowired
private WxAppUserInfoApi wxAppUserInfoApi;

//yaml 配置
chao:
  cloud:
    wx:
      ma:
        config:
          appid: #微信平台申请
          secret: #微信平台申请
```
- 说明
  * 在启动类增加@EnableWxMaSingleton  
  * 在调用类注入 WxAppUserInfoApi
  * 详情请点击[@WxJava-sdk](https://github.com/Wechat-Group/WxJava)
  * 详情请点击[@小程序开发文档](https://github.com/Wechat-Group/WxJava/wiki/%E5%B0%8F%E7%A8%8B%E5%BA%8F%E5%BC%80%E5%8F%91%E6%96%87%E6%A1%A3)
  
### >. 微信支付
```java
@EnableWxPaySingleton

//业务类注入
@Autowired
private WxPayService wxPayService;

//yaml 配置
chao:
  cloud:
    wx:
      pay:
        config:
          app-id: #微信平台申请
          mch-id: #微信平台申请
          mch-key: #微信平台设置
          notify-url: #支付结果回调地址
```
- 说明
  * 在启动类增加@EnableWxPaySingleton  
  * 在调用类注入 WxPayService
  * 详情请点击[@WxJava-sdk](https://github.com/Wechat-Group/WxJava)
  * 详情请点击[@微信支付开发文档](https://github.com/Wechat-Group/WxJava/wiki/%E5%BE%AE%E4%BF%A1%E6%94%AF%E4%BB%98%E5%BC%80%E5%8F%91%E6%96%87%E6%A1%A3)
  
### >. 接口权限校验
```java
@EnableAuthUser
//格式（自定义）
//1.UserTypeEnum
public enum UserTypeEnum {
	STUDENT(1), // 学生
	VISITOR(0); // 游客
	Integer type;

	UserTypeEnum(Integer type) {
		this.type = type;
	}

	public Integer getType() {
		return type;
	}
}
//2.UserStatusEnum
public enum UserStatusEnum {
	FREEZE(0),//冻结
	PASS(1),//正常
	NOT_PERFECT(2);//未完善

	public Integer status;

	UserStatusEnum(Integer status) {
		this.status = status;
	}
}
//3.PermConstant
/**
 *  命名规范：
 *    -	[Set<Integer> ERROR_PERM] 必须存在 
 *    - TYPE_STATUS 用户类型_用户状态
 *    - int 值不可以重复
 */
public interface PermConstant {
	/**
	 * 错误权限
	 */
	Set<Integer> ERROR_PERM = CollUtil.newHashSet(PermConstant.FREEZE);
	/**
	 * 冻结用户-（不可访问） 
	 */
	int FREEZE = -1;
	/**
	 * 游客 （必须进行转型-只有查看功能）
	 */
	int VISITOR = 0;
	/**
	 * 学生 
	 */
	int STUDENT_PASS = 1;
	/**
	 *学生-未完善
	 */
	int STUDENT_NOT_PERFECT = 2;

}
//yaml 配置
chao:
  cloud: 
    auth:  
      type: UserTypeEnum #枚举
      status: UserStatusEnum #枚举
      perm: PermConstant #常量
```
- 说明
  * 在启动类增加@EnableAuthUser  
  * 方法method增加@Permission（Controller）  
      - hasPerm： 权限列表：PermConstant（数组）
      - retCode： 返回值错误码 默认 0403
      - retMsg： 返回值信息
  *  注：UserTypeEnum  
      - 必须存在 type 属性 类型为Integer
  *  注：UserStatusEnum  
      - 必须存在 status 属性 类型为Integer
  *  注：PermConstant  
      - 必须存在 [Set<Integer> ERROR_PERM]，且有初始化赋值

### >. 跨域访问

```java
@EnableCors
```

- 说明
  * 在启动类增加@EnableCors  
      - 允许任何域名
      - 允许任何头
      - 允许任何方法
      
### >. cron-定时器

```java
@EnableCron
```

- 说明
  * 在启动类增加@EnableCron  
      - 实现接口  com.chao.cloud.common.config.cron.task.CronTask
      - 方法一：注入Spring 容器即可. 如 @Component
      - 方法二：CronService.schedule(CronTask task);
      - 删除：CronService.remove(String id);
      - 列表：CronService.list();
     
### >. redis

```java
//maven
<dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

@EnableRedisCache

//业务类注入
@Autowired
private IRedisService redisService;

//yaml 配置文件
spring:
  redis:
    host: 127.0.0.1
    port: 6379
    password:   #密码
         
```

- 说明
  * 在启动类增加@EnableRedisCache  
  * 在调用类注入 IRedisService
  * 详情请点击[原创@whvcse/RedisUtil](https://github.com/whvcse/RedisUtil)
        
### >. 敏感词过滤

```java
@EnableSensitiveWord
```
- 说明
  * 在启动类增加@EnableSensitiveWord  
      - 增加资源文件 config/SensitiveWord.txt
      - 每一行为一个词  
      
### >. 线程池

```java
@EnableThreadPool

//yaml 配置
thread:
  pool:
    async:
      core-pool-size: #核心线程数
      max-pool-size: #最大线程
      keep-alive-seconds: #持续时间
      thread-name-prefix: #线程名称前缀
```
- 说明
  * 在启动类增加@EnableThreadPool  
      
### >. 分词器

```java
@EnableTokenizer

//yaml 配置
chao.cloud.tokenizer.word: #词库以逗号分割 [,]
```
- 说明
  * 在启动类增加@EnableTokenizer  
      - 词库以逗号分割 [,]
      
------  	
## 环境依赖

##### jdk1.8+↑   
##### Eclipse 4.7（Oxygen[氧气]）+↑  
##### maven阿里云仓库   			
##### 开发工具请安装lombok
* [eclipse@lombok](https://www.jianshu.com/p/6825d8116006)   			
* [idea@lombok](https://www.jianshu.com/p/3ce2f0a39df4)  			

------

## 版权

### Apache License Version 2.0  

- 如不特殊注明，所有模块都以此协议授权使用。
- 任何使用了chao-cloud的全部或部分功能的项目、产品或文章等形式的成果必须显式注明chao-cloud。

### NPL (The 996 Prohibited License)

- 不允许 996 工作制度企业使用该开源软件

### 其他版权方
- 实施上由个人维护，欢迎任何人与任何公司向本项目开源模块。
- 充分尊重所有版权方的贡献，本项目不占有用户贡献模块的版权。

### 鸣谢
感谢下列优秀开源项目：
- [hutool-超级工具类](https://github.com/looly/hutool)  
- [lombok](https://github.com/rzwitserloot/lombok)  
- [微信-sdk](https://github.com/Wechat-Group/WxJava)  
- [mybatis-plus](https://github.com/baomidou/mybatis-plus)  
- [layui](https://github.com/sentsin/layui/)  
- [echarts](https://github.com/apache/incubator-echarts)  
- [......](https://github.com/)  

感谢诸位用户的关注和使用，chao-cloud并不完善，未来还恳求各位开源爱好者多多关照，提出宝贵意见。

作者 [@chaojunzi 1521515935@qq.com]

2019年8月13日
