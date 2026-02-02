
# Auth Center Demo

一个基于 **JWT + RBAC** 的 **Spring Boot 3.x 鉴权 Starter 示例项目**，  
用于在多个业务系统之间实现 **可复用、可插拔的统一鉴权能力**。

用于演示如何将认证与鉴权能力 **模块化封装为可复用、可插拔的基础组件**，并在多个业务系统中统一接入。

该项目关注的是 **工程化设计与边界划分**，而非完整身份认证平台（IdP）的功能堆叠。

---
## 🎯 项目目标

- 将 **认证 / 鉴权逻辑** 从具体业务系统中剥离
- 以 **Spring Boot Starter** 的形式提供统一能力
- 通过 **最小 SPI 接口** 与业务系统解耦
- 保持实现 **克制、可理解、可二次扩展**

非目标：
- 不试图替代完整用户中心或 IAM 系统
- 不包含复杂账号体系、组织架构、审批流等能力

---

## ✨ 核心特性

- Spring Boot Starter 形式的统一鉴权模块
- JWT 认证（Access Token / Refresh Token）
- 基于角色的访问控制（RBAC）
- 细粒度权限校验（Permission）
- 方法级权限控制注解 `@RequirePerm`
- 统一的 401 / 403 JSON 错误响应
- Token 拉黑机制（基于可插拔 `TokenStore`，默认内存实现
- 通过 SPI（`AuthUserService`）接入业务用户体系

---

## 📦 项目结构

```
auth-center-demo
├─ auth-center-spring-boot-starter   # 可复用的鉴权 Starter
└─ auth-center-example-app           # 示例业务系统
```

- **starter**：仅包含通用鉴权能力，不依赖任何具体业务
- **example-app**：演示业务系统如何以最小成本接入 Starter

---

## 🧩 Starter 模块结构

```
auth-center-spring-boot-starter
└─ com.demo.authcenter
   ├─ autoconfig        # 自动装配入口
   ├─ filter            # JWT 认证过滤器
   ├─ security          # JWT 与 Token 核心逻辑
   ├─ spi               # 业务系统接入 SPI
   ├─ permission        # 权限模型与校验器
   ├─ annotation        # @RequirePerm 注解
   ├─ aop               # 鉴权 AOP
   ├─ web               # 统一 401 / 403 响应
   └─ store             # TokenStore 实现
```

自动装配注册：

```
src/main/resources/META-INF/spring/
└─ org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

---

## 🚀 快速开始

### 1️⃣ 引入 Starter 依赖

```xml
<dependency>
    <groupId>com.demo</groupId>
    <artifactId>auth-center-spring-boot-starter</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

---

### 2️⃣ 配置 application.yml

```yaml
auth-center:
  enabled: true   # 可选，默认 true

jwt:
  secret: demo-jwt-secret-key-demo-jwt-secret-key   # 必填
  issuer: auth-center                               # 可选
  expire-minutes: 30
  refresh-expire-minutes: 1440
```

---

### 3️⃣ 业务系统实现 SPI

业务系统通过实现 `AuthUserService` 提供用户身份、角色与权限信息。

```java
@Component
public class DemoAuthUserService implements AuthUserService {

    @Override
    public AuthUser loadByUsername(String username) {
        return new AuthUser(
            "1",
            username,
            List.of("ROLE_USER"),
            List.of("ORDER:CREATE", "ORDER:VIEW")
        );
    }
}
```

---

### 4️⃣ 使用权限注解

```java
@RequirePerm("ORDER:CREATE")
@PostMapping("/order")
public String createOrder() {
    return "ok";
}
```

---

## 🌐 示例系统接口

以下接口由 **示例业务系统（example-app）** 提供。

### 鉴权相关

```
POST /auth/login
```
返回 `accessToken` 与 `refreshToken`。

```
POST /auth/refresh
```
使用 refresh token 刷新 access token。

```
POST /auth/logout
```
使当前 access token 失效。

---

### 业务接口

登录即可访问：

```
GET /api/user/me
```

仅 ADMIN 可访问：

```
POST /api/admin/task
```

---

## ❌ 错误返回规范

所有鉴权相关错误均以统一 JSON 结构返回，
以简化前端与调用方的处理逻辑。

### 401 未认证

```json
{
  "code": 401,
  "message": "Unauthorized",
  "path": "/api/user/me"
}
```

### 403 无权限

```json
{
  "code": 403,
  "message": "Access Denied",
  "path": "/api/admin/task"
}
```

---

## 🔐 安全说明

- JWT Secret 建议不少于 256 bit
- Access Token 建议短时有效（≤ 30 分钟）
- Refresh Token 推荐存储于 Redis 并设置 TTL
- 生产环境必须使用 HTTPS

本项目 **不以替代完整身份认证中心（IdP）为目标**。  
在生产系统中，应额外考虑密钥轮换、多端登录控制、
统一用户中心等问题。

---

## 🛣 Roadmap（非承诺）

- 基于 Redis 的 `TokenStore`
- 多端登录与强制下线
- OAuth2 / SSO 集成
- 多租户支持

> Roadmap 仅表示潜在演进方向，不构成实现承诺。

---

## 📌 总结

本项目展示了一种 **工程化、低侵入、可复用** 的 Spring Boot 鉴权设计方式：

- 鉴权能力封装在独立 Starter 中
- 业务系统通过最小 SPI 接入
- 权限规则清晰、可测试、低耦合

适用于 **中小规模系统或内部平台** 的统一鉴权需求。
