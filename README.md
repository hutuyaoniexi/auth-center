# Auth Center Demo

A **Spring Boot 3.x authentication starter demo** based on **JWT + RBAC**,
designed to provide a **reusable and pluggable unified authentication & authorization module** across multiple business systems.

This project demonstrates how to **modularize authentication and authorization capabilities** into a reusable infrastructure component and integrate it into different business applications with minimal effort.

The focus is on **engineering design and boundary definition**, rather than building a full-featured Identity Provider (IdP).

---

## 🎯 Project Goals

- Decouple **authentication / authorization logic** from business systems
- Provide unified security capabilities via a **Spring Boot Starter**
- Integrate with business systems through **minimal SPI interfaces**
- Keep the implementation **minimal, understandable, and extensible**

### Non-goals

- Not intended to replace a full user center or IAM platform
- Does not include complex account systems, organization models, or approval workflows

---

## ✨ Core Features

- Unified authentication module packaged as a Spring Boot Starter
- JWT-based authentication (Access Token / Refresh Token)
- Role-Based Access Control (RBAC)
- Fine-grained permission checks
- Method-level permission annotation `@RequirePerm`
- Unified JSON error responses for 401 / 403
- Token blacklist mechanism  
  (via pluggable `TokenStore`, in-memory implementation by default)
- Business user system integration via SPI (`AuthUserService`)

---

## 📦 Project Structure

```
auth-center-demo
├─ auth-center-spring-boot-starter   # Reusable authentication starter
└─ auth-center-example-app           # Example business application
```

- **starter**: contains only generic authentication logic, independent of any business domain
- **example-app**: demonstrates how a business system integrates the starter with minimal configuration

---

## 🧩 Starter Module Structure

```
auth-center-spring-boot-starter
└─ com.demo.authcenter
   ├─ autoconfig        # Auto-configuration entry
   ├─ filter            # JWT authentication filter
   ├─ security          # JWT & token core logic
   ├─ spi               # SPI for business integration
   ├─ permission        # Permission model & validators
   ├─ annotation        # @RequirePerm annotation
   ├─ aop               # Authorization AOP
   ├─ web               # Unified 401 / 403 responses
   └─ store             # TokenStore implementations
```

Auto-configuration registration:

```
src/main/resources/META-INF/spring/
└─ org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

---

## 🚀 Quick Start

### 1️⃣ Add Starter Dependency

```xml
<dependency>
    <groupId>com.demo</groupId>
    <artifactId>auth-center-spring-boot-starter</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

---

### 2️⃣ Configure `application.yml`

```yaml
auth-center:
  enabled: true   # optional, default true

jwt:
  secret: demo-jwt-secret-key-demo-jwt-secret-key   # required
  issuer: auth-center                               # optional
  expire-minutes: 30
  refresh-expire-minutes: 1440
```

---

### 3️⃣ Implement SPI in Business System

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

### 4️⃣ Use Permission Annotation

```java
@RequirePerm("ORDER:CREATE")
@PostMapping("/order")
public String createOrder() {
    return "ok";
}
```

---

## 🌐 Example Application APIs

### Authentication

```
POST /auth/login
POST /auth/refresh
POST /auth/logout
```

---

### Business APIs

```
GET /api/user/me
POST /api/admin/task
```

---

## ❌ Error Response Specification

### 401 Unauthorized

```json
{
  "code": 401,
  "message": "Unauthorized",
  "path": "/api/user/me"
}
```

### 403 Forbidden

```json
{
  "code": 403,
  "message": "Access Denied",
  "path": "/api/admin/task"
}
```

---

## 🔐 Security Notes

- JWT secret should be at least **256 bits**
- Access tokens should be short-lived (≤ 30 minutes)
- Refresh tokens are recommended to be stored in Redis with TTL
- HTTPS is mandatory in production environments

---

## 🛣 Roadmap (Non-binding)

- Redis-based `TokenStore`
- Multi-device login & forced logout
- OAuth2 / SSO integration
- Multi-tenant support

---

## 📌 Summary

An **engineering-oriented, low-intrusion, reusable** authentication design for Spring Boot,
suitable for **small to medium-sized systems or internal platforms**.
