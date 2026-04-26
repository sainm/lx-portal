# lx-portal

心理咨询公司门户网站 MVP。

## 技术栈

- Java 21
- Gradle Wrapper
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JPA
- Flyway
- PostgreSQL
- Thymeleaf

## 已实现功能

### 前台门户

- 首页
- 服务项目列表与详情
- 咨询师列表与详情
- 预约表单
- 预约成功页
- 咨询流程
- FAQ
- 文章列表与详情
- 关于我们
- 隐私政策
- 咨询须知
- Cookie / 统计提示

### 后台管理

- 后台登录
- 预约列表、详情、状态更新、备注
- 预约筛选：状态、时间、咨询方向
- 预约 CSV 导出
- 咨询师管理接口与页面草案
- 服务项目管理接口与页面草案
- 文章管理接口与页面草案
- 文章分类管理接口
- 基础配置管理接口
- 文件上传接口
- 操作日志
- 访问统计与转化统计

### 安全与合规

- 后台接口需要登录
- 后台密码使用 BCrypt 加密
- 预约表单包含隐私政策确认
- 预约表单包含非医疗诊断与非急救服务确认
- 预约列表默认不展示完整问题简述
- 预约详情页展示完整问题简述
- 文件上传限制类型和大小

## 本地数据库

项目默认使用本机 PostgreSQL：

- Host：`127.0.0.1`
- Port：`5432`
- Database：`lx_portal`
- Username：`lx_portal`
- Password：`lh`

如需手动创建：

```sql
create role lx_portal login password 'lh';
create database lx_portal owner lx_portal;
```

启动时 Flyway 会自动执行 `src/main/resources/db/migration` 下的迁移脚本。

## 启动

```powershell
.\gradlew.bat bootRun
```

默认地址：

- 前台首页：http://127.0.0.1:8080/
- 预约页：http://127.0.0.1:8080/appointment
- 后台登录：http://127.0.0.1:8080/login
- 后台首页：http://127.0.0.1:8080/admin/dashboard

后台初始账号：

- Username：`admin`
- Password：`admin123`

## 构建与测试

```powershell
.\gradlew.bat build
.\gradlew.bat test
```

## 主要目录

```text
doc/                                  产品与技术文档
src/main/java/com/lx/portal           后端代码
src/main/resources/db/migration        Flyway 数据库迁移
src/main/resources/templates           Thymeleaf 页面模板
src/main/resources/static              CSS / JS 静态资源
src/test                               测试代码
```

## 文档

- [设计文档](doc/design.md)
- [实施计划](doc/plan.md)
- [任务清单](doc/task.md)
- [技术架构](doc/technical-architecture.md)
- [本地开发说明](doc/development-setup.md)

## 说明

当前版本是 MVP 骨架，重点完成门户展示、预约线索收集、后台基础管理和统计闭环。

暂未纳入 MVP 的功能包括：在线支付、用户中心、心理测评、视频咨询、复杂排班、自动匹配咨询师、企业后台和小程序同步。

