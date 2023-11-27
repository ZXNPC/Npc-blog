# NPC Blog

---

- [Git 相关知识](#git-相关知识)
- [BootStrap v3 相关知识](#bootstrap-v3-相关知识)
- [GitHub OAuth 使用指南](#github-oauth-使用指南)
- [application.properties 配置（本地运行）](#applicationproperties-配置本地运行)
- [MyBatis 相关知识](#mybatis-相关知识)
- [PageHelper 实现分页功能](#pagehelper-实现分页功能)
- [Session 拦截器](#session-拦截器)
- [ControllerAdvice 和 ErrorViewResolver 全局处理异常](#controlleradvice-和-errorviewresolver-全局处理异常)
- [AJAX 实现异步刷新](#ajax-实现异步刷新)
- [MarkDown Editor](#markdown-editor)
- [腾讯云 COS 图床](#腾讯云-cos-图床)
- [服务器项目部署](#服务器项目部署)
- [日志记录](#日志记录)
- [MyBatis Plus 相关知识](#MyBatis-Plus-相关知识)
- [发送邮件](#发送邮件)
- [登录注册功能实现](#登录注册功能实现)

---

## Git 相关知识

### Git 是什么

- Git 是一个开源的分布式版本控制系统，用于敏捷高效地处理任何或小或大的项目。
- Git 是 Linus Torvalds 为了帮助管理 Linux 内核开发而开发的一个开放源码的版本控制软件。
- Git 与常用的版本控制工具 CVS, Subversion 等不同，它采用了分布式版本库的方式，不必服务器端软件支持。

### Git 的三大储存区

- 工作区：存储于本地的项目文件
- 暂存区：存放在 `.git` 目录下的index文件
- 版本库 | 本地仓库：`.git` 目录本身

### Git 的运作方式

- 工作区 &rarr; 暂存区：`git add <file>` | `git rm --cached <file>`
- 暂存区 &rarr; 本地仓库：`git commit -m <comment>` | `git rm <file>`
- 本地仓库 &rarr; 远端 GitHub 仓库：`git push` | `git pull`

### Git 设置全局用户信息

`$ git config --global user.name "runoob"`

`$ git config --global user.email test@runoob.com`

### Git 克隆仓库

`$ git clone https://github.com/ZXNPC/community.git`

### Git 查看提交历史

`$ git log`

### Git 回溯版本

`$ git reset --hard <version>`

---

## BootStrap v3 相关知识

### BootStrap 是什么

- Bootstrap 是基于 HTML、CSS、JAVASCRIPT 的前端框架，它简洁灵活，使得 Web 开发更加快捷。

### 快速上手

- 访问官方网站 https://v3.bootcss.com/
- 下载 BootStrap 包解压并保存到 `src/main/resources/static` 路径下
- 在 html 文件头部引入相应的文件，如下( .min.css 相比于 .css 的执行效率更高但可读性更差)

```html

<link rel="stylesheet" href="/css/bootstrap.min.css"/>
<link rel="stylesheet" href="/css/bootstrap-theme.min.css"/>
<link rel="stylesheet" href="/css/community.css"/>
<script src="/js/bootstrap.min.js" type="application/javascript"></script>
```

- 也可以采用 BootStrapCDN 引入，此种方式不需要下载 BootStrap 文件

```html
<!-- 最新版本的 Bootstrap 核心 CSS 文件 -->
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css"
      integrity="sha384-HSMxcRTRxnN+Bdg0JdbxYKrThecOKuH5zCYotlSAcp1+c8xmyTe9GYg1l9a69psu" crossorigin="anonymous">

<!-- 可选的 Bootstrap 主题文件（一般不用引入） -->
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap-theme.min.css"
      integrity="sha384-6pzBo3FDv/PJ8r2KRkGHifhEocL+1X2rVCTTkUfGk7/0pbek5mMa1upzvWbrUbOZ" crossorigin="anonymous">

<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"
        integrity="sha384-aJ21OjlMXNL5UyIl/XNwTMqvzeRMZH2w8c5cRVpzpU8Y5bApTppSuUkhZXN0VxHd"
        crossorigin="anonymous"></script>
```

---

## GitHub OAuth 使用指南

### GitHub OAuth 是什么

GitHub 官方提供的用于第三方网站获取用户信息的 API ，开发者可以用此 API 来实现 GitHub 用户登录功能。

### 官方文档

https://docs.github.com/cn/developers/apps/building-oauth-apps

### 创建 OAuth 应用程序

<ol>
<li>
<p>在任何页面的右上角，单击您的个人资料照片，然后单击 <strong>Settings（设置）</strong>。</p>
</li>
<li>
<p>在左侧栏中，单击 <strong><svg version="1.1" width="16" height="16" viewBox="0 0 16 16" class="octicon octicon-code" aria-label="The code icon" role="img"><path fill-rule="evenodd" d="M4.72 3.22a.75.75 0 011.06 1.06L2.06 8l3.72 3.72a.75.75 0 11-1.06 1.06L.47 8.53a.75.75 0 010-1.06l4.25-4.25zm6.56 0a.75.75 0 10-1.06 1.06L13.94 8l-3.72 3.72a.75.75 0 101.06 1.06l4.25-4.25a.75.75 0 000-1.06l-4.25-4.25z"></path></svg> 开发者设置</strong>。</p>
</li>
<li>
<p>在左侧边栏中，单击 <strong>OAuth 应用程序</strong>。 </p>
</li>
<li>
<p>单击 <strong>New OAuth App（新建 OAuth 应用程序）</strong>。 </p>
<strong>注：</strong>如果您以前没有创建过应用程序，该按钮将显示 <strong>Register a new application（注册新应用程序）</strong>。
</li>
<li>
<p>在“Application name（应用程序名称）”中，输入应用程序的名称。 </p>
<p><strong>警告：</strong>仅在 OAuth 应用程序中使用您认为公开的信息。 创建 OAuth 应用程序时，应避免使用敏感数据（如内部 URL）。</p>
</li>
<li>
<p>在“Homepage URL（主页 URL）”中，输入应用程序网站的完整 URL。 </p>
</li>
<li>
<p>（可选）在“Application description（应用程序说明）”中，输入用户将看到的应用程序说明。 </p>
</li>
<li>
<p>在“Authorization callback URL（授权回调 URL）”中，输入应用程序的回调 URL。 </p>
<p><strong>注：</strong>与 GitHub 应用程序 不同，OAuth 应用程序不能有多个回调 URL。</p>
</li>
<li>
<p>如果您的 OAuth 应用将使用设备流来识别和授权用户，请单击 <strong>Enable Device Flow（启用设备流）</strong>。 有关设备流的更多信息，请参阅“<a href="https://docs.github.com/cn/developers/apps/building-oauth-apps/authorizing-oauth-apps#device-flow">授权 OAuth 应用程序</a>”。 </p>
</li>
<li>
<p>单击 <strong>Register application（注册应用程序）</strong>。 </p>
</li>
</ol>

<span id="github"></span>

### 授权 OAuth 应用程序（简要流程，完整参考官方文档）

1. 在 **OAuth 应用程序** 自己创建的项目中记录下 `Client ID` ，选择 **Generate a new client secret** 并记录下 `Client secret` 确认 **Authorization
   callback URL** ，建议设置为 `<your homepage>/callback`
2. 为网页创建登录按钮，跳转 `http://github.com/login/oauth/authorize <client_id> <redirect_uri> <scope> <user>`
3. GitHub 将返回 `code`, `state` 参数，使用 controller 处理信息，将 `client_id` `client_secret` `code` `redirect_uri` 作为参数进行 POST<br>
   **注：**
   - 使用 `okhttp` 进行 POST ，使用方法参考官方网站 https://square.github.io/okhttp/
   - 安装参考 https://mvnrepository.com/artifact/com.squareup.okhttp/okhttp
4. GitHub 返回令牌 `access_token` 一个长字符串，从中提取出主要内容，例子如下<br>
   `access_token=<font color=Red>gho_16C7e42F292c6912E7710c838347Ae178B4a</font>&scope=repo%2Cgist&token_type=bearer
5. 将 **token** 拼接上令牌作为 `Authorization` 添加到 `header` 之中，对 `https://api.github.com/user` 进行 GET，例子如下<br>
   Authorization: token gho_16C7e42F292c6912E7710c838347Ae178B4a
6. GitHub 将返回用户的相关信息，参考格式如下：

```json
{
  "login": "your_login",
  "id": "your_id",
  "node_id": "your_node_id",
  "avatar_url": "your_avatar_url",
  "gravatar_id": "your_gravatar_id",
  "url": "your_url",
  "html_url": "your_html_url",
  "followers_url": "your_followers_url",
  "following_url": "your_following_url",
  "gists_url": "your_gists_url",
  "starred_url": "your_starred_url",
  "subscriptions_url": "your_subscriptions_url",
  "organizations_url": "your_organizations_url",
  "repos_url": "your_repos_url",
  "events_url": "your_events_url",
  "received_events_url": "your_received_events_url",
  "type": "User",
  "site_admin": false,
  "name": "your_name",
  "company": "your_company",
  "blog": "your_blog",
  "location": "your_location",
  "email": "your_email",
  "hireable": "your_hireable",
  "bio": "your_bio",
  "twitter_username": "your_twitter_username",
  "public_repos": 0,
  "public_gists": 0,
  "followers": 0,
  "following": 0,
  "created_at": "your_create_time",
  "updated_at": "your_update_time",
  "private_gists": 0,
  "total_private_repos": 0,
  "owned_private_repos": 0,
  "disk_usage": 0,
  "collaborators": 0,
  "two_factor_authentication": false,
  "plan": {
    "name": "free",
    "space": 0,
    "collaborators": 0,
    "private_repos": 0
  }
}
```

---

## application.properties 配置（本地运行）

### 端口配置

`server.port=8887`

### GitHub 登录相关配置

<span id="github-oauth"></span>

```
github.client.id=your_github_client_id
github.client.secret=your_github_client_server
github.redirect.uri=http://localhost:8887/callback
```

### 腾讯云 COS 图床配置

<span id="cos"></span>

```
cloud.tencent.cam.capi.app-id=your_cloud_tencent_cam_capi_app-id
cloud.tencent.cam.capi.secret-id=your_cloud_tencent_cam_capi_secret-id
cloud.tencent.cam.capi.secret-key=your_cloud_tencent_cam_capi_secret-key
cloud.tencent.cos.bucket.bucket-name=your_cloud_tencent_cos_bucket_bucket-name
cloud.tencent.cos.bucket.region=your_cloud_tencent_cos_bucket_region
cloud.tencent.cos.bucket.expires=315360000
```

### MySQL 数据库和 MyBatis 配置

<span id="mybatis"></span>

```
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/community
spring.datasource.username=your_datasource_username
spring.datasource.password=your_datasource_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
mybatis.configuration.mapUnderscoreToCamelCase=true
mybatis.type-aliases-package=your_projetc_mapper_path
mybatis.mapper-locations=classpath:mapper/*.xml
```

### 日志配置

<span id="log"></span>

```
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=100MB
logging.file.name=logs/community.log
logging.level.root=info
logging.logback.rollingpolicy.max-file-size=100MB
logging.logback.rollingpolicy.max-history=15
```

---

## MyBatis 相关知识

### MyBatis 是什么

MyBatis 是一款优秀的持久层框架，它支持自定义 SQL、存储过程以及高级映射。MyBatis 免除了几乎所有的 JDBC 代码以及设置参数和获取结果集的工作。MyBatis 可以通过简单的 XML 或注解来配置和映射原始类型、接口和
Java POJO（Plain Old Java Objects，普通老式 Java 对象）为数据库中的记录。

### MyBatis Generator 是什么

MyBatis Generator (MBG) is a code generator for MyBatis. It will generate code for all versions of MyBatis. It will
introspect a database table (or many tables) and will generate artifacts that can be used to access the table(s). This
lessens the initial nuisance of setting up objects and configuration files to interact with database tables. MBG seeks
to make a major impact on the large percentage of database operations that are simple CRUD (Create, Retrieve, Update,
Delete). You will still need to hand code SQL and objects for join queries, or stored procedures.

简单来说， MyBatis Generator 是用于自动生成 MySQL 语句，使得程序员可以方便地通过函数调用的方式来完成数据库增删改查一系列操作。

### 相关网站

MyBatis 官方网站：https://mybatis.net.cn/

教程网站：https://mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/

MyBatis Generator 官方文档：https://mybatis.org/generator/

### 快速上手

1. 在 `pom.xml` 中引入依赖
   ```xml
   <dependency>
      <groupId>org.mybatis</groupId>
      <artifactId>mybatis</artifactId>
      <version>x.x.x</version>
   </dependency>
   ```

   ```xml
   <plugin>
       <groupId>org.mybatis.generator</groupId>
       <artifactId>mybatis-generator-maven-plugin</artifactId>
       <version>1.4.1</version>
       <!--引入 MySQL 驱动包-->
       <dependencies>
           <dependency>
               <groupId>mysql</groupId>
               <artifactId>mysql-connector-java</artifactId>
               <version>8.0.28</version>
           </dependency>
       </dependencies>
   </plugin>
   ```

2. 在 application.properties 中配置 MyBatis [点击跳转](#mybatis)
3. 直接使用 MyBatis 进行数据库操作：在 `model` 中创建相应的类，在 `mapper` 中创建相关 mysql 语句方法，例子如下<br>
   ```java
   @Select("SELECT * FROM CITY WHERE state = #{state}")
   City findByState(@Param("state") String state);
   ```
4. 使用 MyBatis Generator 生成相关方法：
   1. 仿照以下[官方提供的参考文件](https://mybatis.org/generator/configreference/xmlconfig.html)创建 `generatorConfig.xml`
      并放置于 `application.properties` 同目录下
   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <!DOCTYPE generatorConfiguration
           PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
           "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">
   
   <generatorConfiguration>
   
      <context id="DB2Tables" targetRuntime="MyBatis3">
         <!--连接数据库-->
         <jdbcConnection driverClass="com.mysql.cj.jdbc.Driver"
                         connectionURL="jdbc:mysql://127.0.0.1:3306/community"
                         userId="your_user_id"
                         password="your_password">
         </jdbcConnection>
   
         <!--配置 model 文件位置-->
         <javaModelGenerator targetPackage="com.example.community.model" targetProject="src\main\java">
            <property name="enableSubPackages" value="true"/>
            <property name="trimStrings" value="true"/>
         </javaModelGenerator>
   
         <!--配置 mapper 文件位置-->
         <sqlMapGenerator targetPackage="mapper" targetProject="src\main\resources">
            <property name="enableSubPackages" value="true"/>
         </sqlMapGenerator>
   
         <!--配置 xmlmapper 文件位置-->
         <javaClientGenerator type="XMLMAPPER" targetPackage="com.example.community.mapper"
                              targetProject="src\main\java">
            <property name="enableSubPackages" value="true"/>
         </javaClientGenerator>
   
         <!--配置数据库中各表属性-->
         <table tableName="user" domainObjectName="User">
         </table>
         <table tableName="question" domainObjectName="Question">
         </table>
         <table tableName="comment" domainObjectName="Comment">
         </table>
         <table tableName="notification" domainObjectName="Notification">
         </table>
   
      </context>
   </generatorConfiguration>
   ```

   2. 在 idea 终端运行脚本
   ```bash
   mvn -Dmybatis'.generator.overwrite=true' mybatis-generator:generate
   ```
   3. MyBatis Generator 自动生成 model, mapper, xmlmapper。
   4. 使用 `mapper` 中生成的方法，例子参考 https://mybatis.org/generator/generatedobjects/legacy.html

---

## PageHelper 实现分页功能

### PageHelper 是什么

PageHelper 是基于 MyBatis 实现的分页插件，支持任何复杂的单表、多表分页。

官方网站及教程： https://pagehelper.github.io/

### 快速入门

在任何需要类似 `limit` 语句的查询语句之前使用以下代码

```java
PageHelper.offsetPage(offset,size);
```

例如

```java
QuestionExample questionExample=new QuestionExample();
        questionExample.createCriteria().andCreatorEqualTo(userId);
        PageHelper.offsetPage(offset,size);
        List<Question> questions=questionMapper.selectByExampleWithBLOBs(questionExample);
```

等同于 mysql 语句

```mysql
select *
from question
where creator = userId
limit offset, size;
```

### 其它

- Q1: 为什么要用 PageHelper 分页而不用 MyBatis Generator 自动生成的分页相关的方法 <br>
  A1: MyBatis Generator 确实提供了分页相关的插件功能，如下：
   ```xml
   <plugin type="org.mybatis.generator.plugins.RowBoundsPlugin"></plugin>
   ```
  然而，引入此插件并使用 `mapper.selectByExampleWithRowbounds` 方法会使得项目报错，报错原因为 xmlmapper
  中的方法重复。而如果删除其中重复的方法，一来违背了不修改源码的程序员准则，二来程序仍然不能正常工作。因此与其使用 `mapper.selectByExampleWithRowbounds` 方法，不如手写 MySQL 语句。~~
  所以最后偷懒直接用了第三方插件。~~

---

## Session 拦截器

### 什么是拦截器

拦截器是基于 AOP (面向切面编程) 思想所提出来的一种方式，主要在以下两种场景中使用：

- 在将请求发送到控制器之前
- 在将响应发送给客户端之前

拦截器常常被用来进行网站用户的登录状态检测

### 快速上手

1. 在 `/interceptor/SessionInterceptor` 中实现 `HandlerInterceptor` 方法，在 `preHandle` 中检测用户当前的 `cookies` 是否包含登录信息并与数据库进行校对
2. 在 `/interceptor/WebConfig` 中实现 `WebMvcConfigurer` 方法，将上述 `SessionInterceptor` 应用于所有链接

---

## ControllerAdvice 和 ErrorViewResolver 全局处理异常

### 快速上手

~~说实话我自己也没想清楚是怎么个过程，总之代码在下面~~

```java

@ControllerAdvice
public class CustomizeExceptionResolver implements ErrorViewResolver {

    // 处理 Controller 层抛出来的异常
    @ExceptionHandler(Exception.class)
    ModelAndView handle(HttpServletRequest request, Throwable e, Model model, HttpServletResponse response) {
        String contentType = request.getContentType();
        if ("application/json".equals(contentType)) {
            ResultDTO resultDTO;
            // 返回json
            if (e instanceof CustomizeException) {
                resultDTO = ResultDTO.errorOf((CustomizeException) e);
            } else {
                resultDTO = ResultDTO.errorOf(CustomizeErrorCode.SYSTEM_ERROR);
            }
            try {
                response.setContentType("application/json");
                response.setStatus(200);
                response.setCharacterEncoding("UTF-8");
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(resultDTO));
                writer.close();
            } catch (IOException ioe) {
            }
            return null;
        } else {
            // 错误页面跳转
            String message;
            if (e instanceof CustomizeException) {
                message = e.getMessage();
            } else {
                message = CustomizeErrorCode.SYSTEM_ERROR.getMessage();
            }
            model.addAttribute("message", message);
            return new ModelAndView("error");
        }
    }

    // 处理客户端和服务器的异常
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView errorHtml(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        ModelAndView modelAndView = new ModelAndView("error");
        if (status.is4xxClientError()) {
            modelAndView.addObject("message", "请求出错，换一个呗？");
        }
        if (status.is5xxServerError()) {
            modelAndView.addObject("message", "服务器冒烟了。");
        }
        return modelAndView;
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null)
            return HttpStatus.INTERNAL_SERVER_ERROR;
        try {
            return HttpStatus.valueOf(statusCode);
        } catch (Exception ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    // 处理访问不存在页面异常
    @Override
    public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", "请求页面出错了！");
        return modelAndView;
    }
}
```

其中定义了许多错误代码，如下：

```java
public enum CustomizeErrorCode implements ICustomizeErrorCode {

    QUESTION_NOT_FOUND(2001, "问题不存在，换一个试试？"),
    TARGET_PARENT_NOT_FOUND(2002, "未选中任何问题或评论进行回复"),
    NO_LOGIN(2003, "当前操作需要登录，请登录后重试"),
    SYSTEM_ERROR(2004, "服务器出错力"),
    TYPE_PARAM_WRONG(2005, "评论类型错误或不存在"),
    COMMENT_NOT_FOUND(2006, "回复的评论不存在"),
    ACCOUNT_ERROR(2007, "当前登录用户错误"),
    CONTENT_IS_EMPTY(2008, "评论为空"),
    READ_NOTIFICATION_FAIL(2009, "提示出错"),
    NOTIFICATION_NOT_FOUND(2010, "提示不存在"),
    FILE_UPLOAD_FAIL(2011, "文件上传错误");

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    private String message;
    private Integer code;

    CustomizeErrorCode(Integer code, String message) {
        this.message = message;
        this.code = code;
    }
}
```

---

## AJAX 实现异步刷新

### 什么是 AJAX

- AJAX = Asynchronous JavaScript and XML（异步的 JavaScript 和 XML）。
- AJAX 不是新的编程语言，而是一种使用现有标准的新方法。
- AJAX 最大的优点是在不重新加载整个页面的情况下，可以与服务器交换数据并更新部分网页内容。
- AJAX 不需要任何浏览器插件，但需要用户允许JavaScript在浏览器上执行。

### 官方教程

JQuery.post() 相关教程： https://api.jquery.com/jQuery.post/

### 快速上手

1. 在 `html` 中创建一个 `button` 并将其 `onclick` 事件设置为 `post()`
2. 在 `js` 中创建 `post()` 方法，获取页面元素并使用 `ajax` 生成 `POST` 信息，例子如下：
    ```javascript
    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: 'application/json',
        data: JSON.stringify({
            "parentId": targetId,
            "content": content,
            "type": type
        }),
        success: function (response) {
            if (response.code == 200) {
                window.location.reload();
            } else {
                if (response.code == 2003) {
                    var isAccepted = confirm(response.message);
                    if (isAccepted) {
                        window.open("https://github.com/login/oauth/authorize?client_id=Iv1.0a840547ff21ff68&redirect_uri=http://localhost:8887/callback&scope=user&state=123");
                        window.localStorage.setItem("closable", "true");
                    }
                } else {
                    alert(response.message);
                }
            }
        },
        dataType: "json"
    });
    ```
3. 在后端使用对应的 `controller` 对 `POST` 进行捕获并进行相应处理，例子如下：
    ```java
    @ResponseBody
    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public Object post(@RequestBody CommentCreateDTO commentDTO,
                       HttpServletRequest request) {
    
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }
    
        if(StringUtils.isBlank(commentDTO.getContent()) || commentDTO == null ) {
            throw new CustomizeException(CustomizeErrorCode.CONTENT_IS_EMPTY);
        }
    
        Comment comment = new Comment();
        comment.setParentId(commentDTO.getParentId());
        comment.setContent(commentDTO.getContent());
        comment.setType(commentDTO.getType());
        comment.setCommentator(user.getId());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(comment.getGmtCreate());
        comment.setCommentator(user.getId());
        comment.setLikeCount(0L);
        comment.setCommentCount(0L);
        commentService.insert(comment, user);
    
        return ResultDTO.okOf();
    }
    ```

---

## MarkDown Editor

### 相关网站

- MarkDown 语法: https://markdown.com.cn/
- Editor.md 官网: https://pandao.github.io/editor.md/

### 引入第三方 MarkDown 编辑器

1. 将输入框的 html 修改成以下样式：
   ```html
   <link rel="stylesheet" href="editor.md/css/editormd.min.css" />
   <div id="editor">
       <!-- Tips: Editor.md can auto append a `<textarea>` tag -->
       <textarea style="display:none;">### Hello Editor.md !</textarea>
   </div>
   <script src="jquery.min.js"></script>
   <script src="editor.md/editormd.min.js"></script>
   <script type="text/javascript">
       $(function() {
           var editor = editormd("editor", {
               // width: "100%",
               // height: "100%",
               // markdown: "xxxx",     // dynamic set Markdown text
               path : "editor.md/lib/"  // Autoload modules mode, codemirror, marked... dependents libs path
           });
       });
   </script>
   ```
2. 将展示内容的 html 修改成以下样式：
   ```html
   <link rel="stylesheet" href="editormd/css/editormd.preview.css" />
   <div id="test-markdown-view">
       <!-- Server-side output Markdown text -->
       <textarea style="display:none;">### Hello world!</textarea>             
   </div>
   <script src="jquery.min.js"></script>
   <script src="editormd/editormd.js"></script>
   <script src="editormd/lib/marked.min.js"></script>
   <script src="editormd/lib/prettify.min.js"></script>
   <script type="text/javascript">
       $(function() {
           var testView = editormd.markdownToHTML("test-markdown-view", {
               // markdown : "[TOC]\n### Hello world!\n## Heading 2", // Also, you can dynamic set Markdown text
               // htmlDecode : true,  // Enable / disable HTML tag encode.
               // htmlDecode : "style,script,iframe",  // Note: If enabled, you should filter some dangerous HTML tags for website security.
           });
       });
   </script>    
   ```
3. 其中相关设置选项参考 https://github.com/pandao/editor.md
4. 引入相关 `js`, `css` 等文件，可以让项目运行后查看浏览器 `console` 提示来一步步引入
5. `Editor.md` 并不提供现成的图床，因此需要开发者自行配装图床 [点击跳转](#sdk)

---

<span id="sdk"></span>

## 腾讯云 COS 图床

### 图床推荐

微博图床插件: https://chrome.google.com/webstore/detail/%E5%BE%AE%E5%8D%9A%E5%9B%BE%E5%BA%8A/pinjkilghdfhnkibhcangnpmcpdpmehk

~~其实小型网站的图床完全可以用微博图床，但是我没有研究出来微博图床的 API 怎么用，所以还是自己建了一个~~

### 官方网站及文档

https://cloud.tencent.com/document/product/436

### 快速上手

官方教程 https://cloud.tencent.com/document/product/436/10199

1. 在腾讯云上创建存储桶
2. 在 [访问密钥](https://console.cloud.tencent.com/cam/capi) 中新建密钥并记录下 `SecretId` 和 `SecretKey` ，记录到 `applicatiob.properties`
   之中 [点击跳转](#cos)
3. 同时在 `application.properties` 中记录下存储桶的名称，区域等信息
4. 通过以下方法使用 `InputStream` 来完成图片的上传
   ```java
   @Service
   @Slf4j
   public class TencentCloudProvider {
   
      @Value("${cloud.tencent.cam.capi.app-id}")
      private String appId;
   
      @Value("${cloud.tencent.cam.capi.secret-id}")
      private String secretId;
   
      @Value("${cloud.tencent.cam.capi.secret-key}")
      private String secretKey;
   
      @Value("${cloud.tencent.cos.bucket.bucket-name}")
      private String bucketName;
   
      @Value("${cloud.tencent.cos.bucket.region}")
      private String region;
   
      @Value("${cloud.tencent.cos.bucket.expires}")
      private Integer expires;
   
   
   
      private COSClient createCOSClient() {
         COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
   
         ClientConfig clientConfig = new ClientConfig();
   
         clientConfig.setRegion(new Region(region));
   
         clientConfig.setHttpProtocol(HttpProtocol.https);
   
         return new COSClient(cred, clientConfig);
      }
   
      private TransferManager createTransferManager(COSClient cosClient) {
         ExecutorService threadPool = Executors.newFixedThreadPool(32);
   
         TransferManager transferManager = new TransferManager(cosClient, threadPool);
   
         TransferManagerConfiguration transferManagerConfiguration = new TransferManagerConfiguration();
         transferManagerConfiguration.setMultipartUploadThreshold(5 * 1024 * 1024);
         transferManagerConfiguration.setMinimumUploadPartSize(1 * 1024 * 1024);
         transferManager.setConfiguration(transferManagerConfiguration);
   
         return transferManager;
      }
   
      private void shutdownTransferManager(TransferManager transferManager) {
         transferManager.shutdownNow(true);
      }
   
      public String upload(InputStream fileStream, String fileName) {
         String generatedFileName;
         String[] fileNames = fileName.split("\\.");
         if (fileNames.length > 1) {
            generatedFileName = UUID.randomUUID() + "." + fileNames[fileNames.length - 1];
         } else {
            return null;
         }
   
         COSClient cosClient = createCOSClient();
   
         TransferManager transferManager = createTransferManager(cosClient);
   
         String key = generatedFileName;
   
         ObjectMetadata objectMetadata = new ObjectMetadata();
   
         PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, fileStream, objectMetadata);
   
         try {
            Upload upload = transferManager.upload(putObjectRequest);
            UploadResult uploadResult = upload.waitForUploadResult();
         } catch (CosServiceException e) {
            log.error("file upload fail, {}", key);
            throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
         } catch (CosClientException e) {
            log.error("file upload fail, {}", key);
            throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
         } catch (InterruptedException e) {
            log.error("file upload fail, {}", key);
            throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
         }
         shutdownTransferManager(transferManager);
   
         Date expirationDate = new Date(System.currentTimeMillis() + expires);
         HttpMethodName method = HttpMethodName.GET;
   
         URL url = cosClient.generatePresignedUrl(bucketName, key, expirationDate, method);
   
         return url.toString();
      }
   }
   ```
5. 对于 `Editor.md` ，采用以下方式获取图片流
   ```java
   @Controller
   public class FileController {
   
       @Autowired
       private TencentCloudProvider tencentCloudProvider;
   
       @ResponseBody
       @RequestMapping("/file/upload")
       public FileDTO upload(HttpServletRequest request) throws IOException {
           MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
           MultipartFile file = multipartRequest.getFile("editormd-image-file");
           String fileName = tencentCloudProvider.upload(file.getInputStream(), file.getOriginalFilename());
           FileDTO fileDTO = new FileDTO();
           fileDTO.setSuccess(1);
           fileDTO.setUrl(fileName);
           return fileDTO;
       }
   }
   ```

---

## 服务器项目部署

### 服务器信息

- 腾讯云轻量应用服务器 [官方网站](https://cloud.tencent.com/)
- CPU: 2核
- 内存: 4GB
- 系统盘: 60GB
- 操作系统: CentOS 8.2 64bit

### 项目配置及运行过程

1. 获取 `root` 管理员权限
    ```bash
    sudo su root
    ```
2. 使用 `yum` 集成安装 `git` 服务
    ```bash
    yum -y install git
    ```
   `-y` 表示自动安装而无需交互确认
3. 安装 'maven' 服务<br>
    ```bash
    yum -y install maven
    ```
4. 进入相关目录并执行 `git clone` 来下载项目
   ```bash
   mkdir App
   cd App
   git clone your_project.git
   ```
5. 进入 `src/main/resources` 目录，上传并修改 `application.properties` 文件
   ```bash
   cd src/main/resources
   rz
   vim application.properties
   ```
6. 执行 `mvn` 来安装相关 package
   ```bash
   mvn package
   ```
   注意，每次修改文件或者执行 `git pull` 更新文件后都要执行一次 `maven` 脚本
7. 运行项目
   ```bash
   java -jar -Dspring.profiles.active=production target/community-0.0.1-SNAPSHOT.jar
   ```

通过以上步骤，则完成了基本的项目运行，但需要注意的是，MySQL 数据库和 GitHub 登录仍然不可用，接下来将继续配置。

### GitHub 登录配置

1. 新建一个 **OAuth** 应用程序或者修改当前的应用程序 [点击跳转](#github)
2. 修改 `src/main/resources/application.properties` 中与 GitHub 登录相关的关键字 [点击跳转](#github-oauth)

### MySQL 数据库配置

1. 使用 `yum` 下载 `mysql-server`
   ```bash
   yum -y install mysql-server
   ```
2. 开启 `mysql` 服务
   ```bash
   service mysqld restart
   ```
3. 登入数据库，初始密码为空
   ```bash
   mysql -uroot -p
   ```
4. 修改用户密码
   ```mysql
   ALTER USER 'root'@'localhost' IDENTIFIED BY 'new password';
   ```
5. 根据需要为项目创建相应表
   ```mysql
   create database community;
   use community;
   
   create table user
   (
      id           bigint auto_increment
         primary key,
      account_id   varchar(100) null,
      name         varchar(50)  null,
      token        char(36)     null,
      gmt_create   bigint       null,
      gmt_modified bigint       null,
      bio          varchar(256) null,
      avatar_url   varchar(100) null
   );
   
   create table question
   (
      id            bigint auto_increment
         primary key,
      title         varchar(50)   null,
      description   text          null,
      gmt_create    bigint        null,
      gmt_modified  bigint        null,
      creator       bigint        null,
      comment_count int default 0 null,
      view_count    int default 0 null,
      like_count    int default 0 null,
      tag           varchar(256)  null,
      constraint question_user_id_fk
         foreign key (creator) references user (id)
   );
   
   create table comment
   (
      id            bigint auto_increment
         primary key,
      parent_id     bigint           not null comment '父类id',
      type          int              not null comment '父类类型',
      commentator   bigint           not null comment '评论人id',
      gmt_create    bigint           not null,
      gmt_modified  bigint           not null,
      like_count    bigint default 0 null,
      content       text             null,
      comment_count bigint default 0 null,
      constraint comment_user_id_fk
         foreign key (commentator) references user (id)
   );
   
   create table notification
   (
      id            bigint auto_increment
         primary key,
      notifier      bigint        not null,
      receiver      bigint        not null,
      outer_id      bigint        not null,
      type          int           not null,
      gmt_create    bigint        not null,
      status        int default 0 not null,
      notifier_name varchar(100)  not null,
      outer_title   varchar(256)  not null
   );
   ```
6. 修改数据库访问权限，设置为任意 host 访问，方便开发人员在本地通过 `DataGrip` 之类的 IDE 来进行管理
   ```mysql
   use mysql;
   update user set host = '%' where user = 'root';
   flush privileges;
   ```
7. 在服务器防火墙中添加 MySQL(3306) 端口
8. 在本地 `DataGrip` 中使用服务器地址和账号密码登录数据库
9. 修改 `src/main/resources/application.properties` 中与数据库相关的关键字 [点击跳转](#mybatis)
10. （重启后）启动 `mysql`<br>
    `service mysqld restart`

### 后台持续运行

`screen` 菜鸟教程： https://www.runoob.com/linux/linux-comm-screen.html

1. 安装 `screen`<br>
   `yum install -y screen`
2. 创建新视窗<br>
   `screen -S newShell`
3. 退出该视窗（后台运行）<br>
   `ctrl + A , ctrl + D`
4. 删除视窗<br>
   视窗内输入 `exit`

---

## 日志记录

### 官方文档

https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#features.logging

~~没什么说的，就跟着官方文档走就行~~

### 配置

[点击跳转](#log)

---

## MyBatis Plus 相关知识

### 相关网站

官方网站及教程： https://baomidou.com/

### 分页实现

```java
QueryWrapper<Question> queryWrapper=new QueryWrapper<Question>()
        .orderByDesc("id")
        .last(String.format("limit %d, %d",offset,size));
List<Question> questions=questionMapper.selectList(queryWrapper);
```
等同于：`select * from question order by id desc limit #{offset}, #{size}`

### Generator 使用

*建议使用 mybatis-plus-generato 3.5.1*

```java
public class MybatisPlusGenerator {


    public static void main(String[] args) {
        String url = "jdbc:mysql://127.0.0.1:3306/blog";
        String username = "username";
        String password = "password";

        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> {
                    builder.author("NPC")                       // 作者名
                            .disableOpenDir()                   // 完成后不打开文件夹
                            .enableSwagger()                    // 开启 swagger 模式
                            .fileOverride()                     // 覆盖源文件（注意检查有无同名文件或需要保留源文件）
                            .outputDir("src\\main\\java");      // 文件生成地址
                })
                .packageConfig(builder -> {
                    builder.parent("com.example.blognpc")       // 父 package
                            .entity("model")                    // 实体文件夹名，默认为 entity
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, "src\\main\\resources\\mapper"));
                                                                // mapperXml 文件生成地址（我在项目里面根本没用到 xml）
                })
                .strategyConfig(builder -> {
                    builder.addInclude("user")                  // 添加需要生成代码的数据库表
                            .addInclude("question")             // 同上
                            .addInclude("comment")              // 同上
                            .mapperBuilder()                    
                            .enableMapperAnnotation();          // 为 mapper 文件加上 @mapper 标注
                })
                .templateConfig(builder -> {
                                                        
                    builder.service("")                         // 不生成 service 文件
                            .serviceImpl("")                    // 不生成 serviceImp 文件
                            .controller("");                    // 不生成 controller 文件
                })
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
```

代码生成器配置： https://baomidou.com/pages/981406/

---

## 发送邮件

### 相关教程

~~并没有找到较为官方的教程，下面一个是我参考的教程~~

知乎教程： https://zhuanlan.zhihu.com/p/234380542

### 快速上手

1. 在 qq 邮箱中开启 **IMAP/SMTP 服务**，并生成授权码，记录在 `application.properties` 中，如下
    ```
    spring.mail.host=smtp.qq.com
    spring.mail.username=youremail@qq.com
    spring.mail.password=authorizepassword
    spring.mail.port=465
    spring.mail.properties.mail.smtp.socketFactory.class=javax.net.ssl.SSLSocketFactory
    spring.mail.properties.mail.smtp.auth=true
    spring.mail.properties.mail.smtp.starttls.enable=true
    spring.mail.properties.mail.smtp.starttls.required=true
    ```

2. 在 `test` 中进行测试

    ```java
    @Autowired
    private JavaMailSender mailSender;
    
    @Test
    public void emailTest() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("from@email.com");
        message.setTo("to@email.com");
        message.setSubject("it is a test for spring boot");
        message.setText("测试发送邮件。");
    
        try {
            mailSender.send(message);
        } catch (MailException e) {
            e.printStackTrace();
        }
    }
    ```

### 邮件错误代码总表

CSDN： https://blog.csdn.net/qq_43442102/article/details/102743947

---

## 登录注册功能实现

### 总体逻辑

<img src="images-for-README/图片1.png" width="3156" alt="登录界面" title="登录界面">

*------我是分割线------*

<img src="images-for-README/图片2.png" width="500" alt="验证界面" title="验证界面" style="margin-left: 200px">

## 参考资料

- [moment format](http://momentjs.cn/docs/displaying/format.html)





