Chao-cloud: springboot 拓展工具包
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
		│	├─core 		//核心
		│	├─extra 	//拓展
		│	└─config	//配置
		│
		└─......
		
------  


#### chao-cloud 提供以下功能

   * [package-包结构](https://github.com/chaojunzi/chao-cloud#1-package-%E5%8C%85%E7%BB%93%E6%9E%84)
   * chao-cloud-common-core  
       * [递归树](https://github.com/chaojunzi/chao-cloud#2-%E9%80%92%E5%BD%92%E6%A0%91)
       * [透明背景验证码](https://github.com/chaojunzi/chao-cloud#3-%E9%80%8F%E6%98%8E%E8%83%8C%E6%99%AF%E9%AA%8C%E8%AF%81%E7%A0%81)
   * chao-cloud-common-extra  
       * [接口访问控制](https://github.com/chaojunzi/chao-cloud#4-%E6%8E%A5%E5%8F%A3%E8%AE%BF%E9%97%AE%E6%8E%A7%E5%88%B6)
       * [表情过滤](https://github.com/chaojunzi/chao-cloud#5-%E8%A1%A8%E6%83%85%E8%BF%87%E6%BB%A4)
       	  

   	 
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

### 1. package-包结构

	1.chao-cloud-common-core
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
		 
	2.chao-cloud-common-extra 
		│
		└─com.chao.cloud.common.extra    		 
		 	├─access 		//接口访问控制（错误次数和超时时间）
		 	├─emoji 	 	//表情过滤（接口层）
		 	├─feign 		//spring-cloud 微服务间接口调用（可传递文件）
		 	├─ftp 			//ftp连接池
		 	├─map 			//地图解析（地址转坐标，距离计算一对多）
		 	├─mybatis 		//mybatis 日志，代码自动生成，乐观锁，分页
		 	├─redis 		//redis 缓存
		 	├─token 		//拦截表单重复提交
		 	├─voice 		//百度AI-语音转文字
		 	└─wx			//微信支付，微信小程序（单例）
		 	
	3.chao-cloud-common-config 
		│
		└─com.chao.cloud.common.config    		 
		 	├─auth 			//接口权限校验
		 	├─core 	 		//spring核心配置（包含容器启动后的自定义处理）
		 	├─cors 			//跨域访问
		 	├─exception 		//全局异常处理
		 	├─sensitive 		//敏感词过滤
		 	├─thread 		//线程池-ThreadPoolTaskExecutor
		 	├─tokenizer 		//分词器（可自定义词库）
		 	├─valid 		//全局参数校验
		 	└─web			//web（全局参数校验，controller拦截，异常，核心，健康检查，资源访问，参数解析等）
		 
### 2. 递归树

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
	
### 3. 透明背景验证码

```java
HyalineCircleCaptcha captcha = HyalineCaptchaUtil.createCircleCaptcha(100, 42, 4, 3);
String code = captcha.getCode();
log.info("[验证码: {}]", code);
```
	
### 4. 接口访问控制

```java
@EnableAccessLimit
```
- 说明
  * 在启动类增加@EnableAccessLimit  
  * 方法method增加@AccessLimit  
      - timeout： 拦截持续时间
      * count： 最大出错次数
      * enable： 是否可用，false，此注解将无效。
  	
### 5. 表情过滤

```java
@EnableEmojiFilter
```
- 说明
  * 在启动类增加@EnableEmojiFilter  
  * 方法method增加@EmojiFilter  
      * value： 是否可用，false，将不参与拦截。
  	
### 环境依赖
##### jdk1.8+↑ 
##### Eclipse 4.7（Oxygen[氧气]）+↑
##### maven阿里云仓库   			

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