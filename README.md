# NPC Blog

---

## ~~同步数据库~~ 配置多数据源

### Why?
~~我想要让两个网站的用户信息同步，但是又不想用同一个 scheme 中的表，于是重新创建了一个 scheme 并且使用触发器使得两个表数据同步。~~

把数据同步的事情交给后端来做，因此需要连接两个不同的数据库。

### How? （同步数据库，已弃用）
使用触发器同步 **（实测出错，不要使用这种方式，除非只是单向同步）**
1. 对 `community` 中的 `user` 表
    ```mysql
    use community;
    
    create trigger trigger_user_insert after insert on user for each row
        insert into blog.user(id, account_id, name, token, gmt_create, gmt_modified, bio, avatar_url)
        VALUES (new.id, new.account_id, new.name, new.token, new.gmt_create, new.gmt_modified, bio, avatar_url);
    
    create trigger trigger_user_delete after delete on user for each row
        delete from blog.user where id = old.id;
    
    create trigger trigger_user_update after update on user for each row
        update blog.user set account_id = new.account_id, name = new.name, token = new.token, gmt_create = new.gmt_create,
                             gmt_modified = new.gmt_modified, bio = new.bio, avatar_url = new.avatar_url where id = new.id;
    ```

2. 对 `blog` 中的 `user` 表·
    ```mysql
    use blog;
    
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
    
    create trigger trigger_user_insert after insert on user for each row
        insert into community.user(id, account_id, name, token, gmt_create, gmt_modified, bio, avatar_url)
        VALUES (new.id, new.account_id, new.name, new.token, new.gmt_create, new.gmt_modified, bio, avatar_url);
    
    create trigger trigger_user_delete after delete on user for each row
        delete from community.user where id = old.id;
    
    create trigger trigger_user_update after update on user for each row
        update community.user set account_id = new.account_id, name = new.name, token = new.token, gmt_create = new.gmt_create,
                                  gmt_modified = new.gmt_modified, bio = new.bio, avatar_url = new.avatar_url where id = new.id;
    ```

3. 结果 mysql 报错 `Can't update table 'user' in stored function/trigger because it is already used by statement which invoked this stored function/trigger.` 因为两张表中的触发器递归触发。

## How? （连接多数据源，仅适用于手写语句）
参考网站： https://zhuanlan.zhihu.com/p/436075500

---

## MyBatis Plus 使用

### 相关网站

官方网站及教程： https://www.mybatis-plus.com/

### 

---

## 发送邮件

### 相关教程

- ~~官方文档： https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#io.email~~
- ~~官方教程： https://docs.spring.io/spring-framework/docs/5.3.22/reference/html/integration.html#mail~~
- (以上方法都不好使)
- 



