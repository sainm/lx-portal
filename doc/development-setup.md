# 本地开发启动说明

## 技术栈

- Java 21
- Gradle Wrapper
- Spring Boot
- PostgreSQL

## PostgreSQL 本地配置

本项目默认连接本机 PostgreSQL：

- Host：`127.0.0.1`
- Port：`5432`
- Database：`lx_portal`
- Username：`lx_portal`
- Password：`lh`

已使用 `postgres` 管理账号创建：

```sql
create role lx_portal login password 'lh';
create database lx_portal owner lx_portal;
```

## 启动项目

```powershell
.\gradlew.bat bootRun
```

默认访问地址：

- 前台首页：`http://127.0.0.1:8080/`
- 预约页：`http://127.0.0.1:8080/appointment`
- 后台登录：`http://127.0.0.1:8080/login`
- 后台首页：`http://127.0.0.1:8080/admin/dashboard`

## 后台初始账号

- Username：`admin`
- Password：`admin123`

密码会在应用首次启动时使用 BCrypt 写入数据库。

## 常用命令

```powershell
.\gradlew.bat build
.\gradlew.bat test
.\gradlew.bat bootRun
```

## 已验证接口

预约提交接口：

```http
POST /api/public/appointments
Content-Type: application/json
```

示例请求：

```json
{
  "nickname": "测试用户",
  "contact": "13800000000",
  "city": "上海",
  "ageRange": "26-35 岁",
  "consultationTarget": "本人",
  "concernDirection": "压力",
  "consultationMethod": "线上",
  "preferredTime": "周末",
  "preferredCounselorId": null,
  "acceptsRecommendation": true,
  "problemSummary": "用于联调的预约。",
  "privacyAgreed": true,
  "emergencyAcknowledged": true
}
```

