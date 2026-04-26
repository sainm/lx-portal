# 心理咨询门户网站 MVP 技术架构设计

## 技术栈

### 后端

- Java 21
- Gradle
- Spring Boot
- Spring Web
- Spring Validation
- Spring Security
- Spring Data JPA 或 MyBatis Plus
- PostgreSQL 或 MySQL
- Flyway 或 Liquibase

### 前端

前端技术可根据现有团队能力确认，MVP 建议选择一种开发效率高、生态成熟的方案：

- Vue 3 + TypeScript + Vite
- 或 React + TypeScript + Vite

后台管理端可以与前台共用同一前端项目，也可以独立为 admin 项目。MVP 建议先共用同一仓库，降低部署和维护成本。

### 基础设施

- Nginx：静态资源、反向代理、HTTPS
- Linux 云服务器或容器化部署环境
- PostgreSQL 或 MySQL 数据库
- 对象存储或本地文件存储：咨询师头像、文章封面、微信二维码
- 企业微信或飞书 webhook：预约通知
- 网站统计工具：百度统计、Google Analytics 或自建埋点

## 总体架构

MVP 采用单体应用架构，优先保证交付速度、维护简单和数据一致性。

```text
用户浏览器
  |
  | HTTPS
  v
Nginx
  |
  | 反向代理 / 静态资源
  v
Spring Boot 应用
  |
  | JPA/MyBatis
  v
数据库

Spring Boot 应用
  |
  | Webhook
  v
企业微信 / 飞书

Spring Boot 应用
  |
  | 文件上传
  v
本地存储 / 对象存储
```

## 应用分层

后端建议采用清晰的三层结构。

```text
controller
  接收 HTTP 请求，处理参数校验和响应包装

service
  编排业务逻辑，处理预约、咨询师、文章、配置等业务规则

repository / mapper
  负责数据访问

domain / entity
  定义数据库实体和核心业务对象

dto
  定义请求与响应对象，避免直接暴露实体

config
  安全、跨域、文件上传、审计、异常处理等配置
```

## 模块划分

### 前台门户模块

负责公开页面所需的数据接口。

- 首页配置查询
- 服务项目查询
- 咨询师列表查询
- 咨询师详情查询
- 文章列表查询
- 文章详情查询
- FAQ 查询
- 关于我们与基础配置查询

### 预约模块

MVP 核心模块。

功能：

- 提交预约
- 预约表单校验
- 危机提示文案返回
- 预约成功页数据
- 预约通知运营人员
- 后台预约列表与详情
- 预约状态流转
- 内部备注

预约状态：

- 待联系
- 已联系
- 已预约
- 已完成
- 已取消

### 咨询师模块

功能：

- 咨询师列表
- 咨询师详情
- 新增咨询师
- 编辑咨询师
- 上架/下架咨询师
- 上传头像

核心字段：

- 姓名
- 头像
- 简介
- 擅长方向
- 服务人群
- 咨询方式
- 咨询风格
- 价格与时长
- 资质与受训经历
- 展示排序
- 上架状态

### 服务项目模块

功能：

- 服务项目列表
- 服务项目详情
- 新增服务项目
- 编辑服务项目
- 上架/下架服务项目
- 展示排序
- SEO 配置

### 内容模块

功能：

- 文章列表
- 文章详情
- 新增文章
- 编辑文章
- 草稿/发布
- 分类管理
- SEO 配置

### 基础配置模块

功能：

- 公司名称
- 联系电话
- 微信二维码
- 地址
- 营业时间
- 首页核心文案
- 首页 SEO 信息
- 隐私政策
- 咨询须知
- Cookie 或统计授权提示

### 后台认证模块

MVP 只做后台运营账号，不做前台用户注册登录。

功能：

- 后台登录
- 后台退出
- 当前用户信息
- 密码加密存储
- 登录失败限制或验证码预留

## API 设计原则

- 前台公开接口以 `/api/public/**` 开头
- 后台管理接口以 `/api/admin/**` 开头
- 后台接口必须登录后访问
- 请求参数使用 DTO 承接，并使用 Bean Validation 校验
- 响应格式统一
- 分页接口统一分页参数
- 后端不直接返回数据库实体

建议响应结构：

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

分页响应结构：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "items": [],
    "page": 1,
    "pageSize": 20,
    "total": 100
  }
}
```

## 数据库设计原则

- 所有表使用统一主键 `id`
- 所有业务表包含 `created_at`、`updated_at`
- 需要软删除的内容表包含 `deleted_at` 或 `deleted`
- 后台可排序内容包含 `sort_order`
- 前台展示内容包含 `status`
- 预约数据属于敏感数据，避免无必要冗余
- 重要状态变更建议记录操作时间

建议核心表：

- `admin_user`
- `counselor`
- `service_item`
- `article`
- `article_category`
- `appointment`
- `site_config`
- `upload_file`
- `operation_log`

## 安全设计

### 传输安全

- 生产环境必须启用 HTTPS
- 后台和预约提交接口只允许 HTTPS 访问
- Nginx 配置 HTTP 跳转 HTTPS

### 后台安全

- 密码使用 BCrypt 或 Argon2 加密
- 后台接口需要认证
- 后台登录失败次数限制或验证码预留
- 后台账号不开放自助注册
- 后台操作预约敏感数据需要记录操作日志

### 数据安全

- 预约信息属于敏感数据，后台列表默认只展示必要字段
- 预约详情页再展示完整问题简述
- 数据库账号使用最小权限
- 生产数据库定期备份
- 导出预约数据需要限制权限

### 隐私合规

- 预约表单必须勾选隐私政策
- 非医疗诊断与非急救服务确认必须明确
- 如接入第三方统计工具，需要隐私政策说明
- 如使用 Cookie 或统计授权提示，需要允许用户关闭提示

## 文件上传设计

上传场景：

- 咨询师头像
- 文章封面
- 微信二维码

MVP 可先使用服务器本地存储，后续再迁移对象存储。

上传限制：

- 限制文件类型：jpg、jpeg、png、webp
- 限制文件大小
- 文件名使用随机名称
- 不使用用户原始文件名作为最终路径
- 上传文件访问路径由后端统一生成

## 通知设计

MVP 推荐企业微信或飞书 webhook。

触发时机：

- 用户成功提交预约后

通知内容：

- 称呼
- 联系方式
- 咨询方向
- 咨询方式
- 偏好时间
- 是否指定咨询师
- 后台详情链接

注意事项：

- 通知中不要包含过长的问题简述
- 避免在群通知中暴露过多敏感信息
- 完整信息应回到后台预约详情查看

## 埋点与统计

关键事件：

- 首页访问
- 点击预约按钮
- 打开预约表单
- 提交预约表单
- 查看咨询师详情
- 查看服务项目详情
- 查看文章详情

核心指标：

- 预约提交量 / 预约表单打开量
- 预约提交量 / 首页访问量
- 咨询师详情访问量
- 服务项目访问量
- 不同咨询方向的预约占比

如使用第三方统计工具，应同步更新隐私政策和 Cookie 或统计授权提示。

## 部署方案

### MVP 推荐部署

```text
Nginx
  - HTTPS
  - 静态资源
  - 反向代理 API

Spring Boot
  - 单体后端服务
  - systemd 或 Docker 运行

Database
  - PostgreSQL 或 MySQL
```

### 环境划分

- `dev`：本地开发环境
- `test`：测试环境
- `prod`：生产环境

### 配置管理

- 使用 Spring Profile 区分环境
- 敏感配置通过环境变量或独立配置文件管理
- 不将数据库密码、webhook 地址、密钥提交到代码仓库

## Gradle 项目建议

MVP 可以采用单模块项目，后续复杂后再拆分多模块。

建议结构：

```text
src/main/java
  com.company.portal
    common
    config
    security
    appointment
    counselor
    serviceitem
    article
    siteconfig
    upload
    notification

src/main/resources
  application.yml
  application-dev.yml
  application-test.yml
  application-prod.yml
  db/migration

src/test/java
```

建议 Gradle 依赖：

- `spring-boot-starter-web`
- `spring-boot-starter-validation`
- `spring-boot-starter-security`
- `spring-boot-starter-data-jpa` 或 MyBatis Plus 相关依赖
- 数据库驱动：PostgreSQL 或 MySQL
- `flyway-core` 或 `liquibase-core`
- `spring-boot-starter-test`

## 测试策略

### 单元测试

- 预约表单校验
- 预约状态流转
- 咨询师上架/下架逻辑
- 文章发布状态逻辑

### 接口测试

- 提交预约
- 后台登录
- 预约列表和详情
- 咨询师管理
- 文章管理

### 安全测试

- 未登录不能访问后台接口
- 登录失败限制生效
- 预约提交接口参数校验生效
- 敏感字段不在列表接口中过度暴露

### 上线验收

- HTTPS 正常
- 静态资源可访问
- 预约提交成功
- 企业微信或飞书通知成功
- 后台可以查看预约
- 数据库备份可用
- 关键页面移动端正常

## 后续扩展方向

MVP 后如预约量提升，可扩展：

- 在线支付
- 咨询师排班
- 用户中心
- 心理测评
- 智能咨询师匹配
- 企业 EAP 后台
- 多角色权限
- 对象存储
- 更完整的操作审计

