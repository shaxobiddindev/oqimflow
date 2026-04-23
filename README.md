# 🚌 Transport Monitoring System

**Elektron transport yo'lovchi oqimini monitoring tizimi**

---

## 📋 Texnologiyalar

| Stack | Versiya |
|-------|---------|
| Java | 17 |
| Spring Boot | 3.2.3 |
| PostgreSQL | 15+ |
| JWT (jjwt) | 0.12.3 |
| WebSocket (STOMP) | Spring |
| Swagger/OpenAPI | 2.3.0 |
| Lombok | latest |

---

## 🚀 Ishga tushirish

### 1. PostgreSQL bazasini yaratish

```sql
CREATE DATABASE transport_db;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE transport_db TO postgres;
```

### 2. `application.yml` ni sozlash

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/transport_db
    username: postgres
    password: postgres
```

### 3. Loyihani build qilish va ishga tushirish

```bash
mvn clean install -DskipTests
mvn spring-boot:run
```

Yoki JAR bilan:
```bash
mvn clean package -DskipTests
java -jar target/monitoring-1.0.0.jar
```

---

## 📖 Swagger UI

Ishga tushirgandan keyin:

```
http://localhost:8080/swagger-ui.html
```

---

## 👤 Default foydalanuvchilar

Birinchi ishga tushirishda avtomatik yaratiladi:

| Username | Password | Role |
|----------|----------|------|
| `admin` | `admin123` | ADMIN |
| `operator` | `operator123` | OPERATOR |
| `viewer` | `viewer123` | VIEWER |

---

## 🔑 JWT Authentication

```
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

Response:
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOi...",
    "refreshToken": "eyJhbGciOi...",
    "tokenType": "Bearer",
    "expiresIn": 86400000
  }
}
```

Keyingi so'rovlarda:
```
Authorization: Bearer <accessToken>
```

---

## 📡 API Endpointlar

### Auth
| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/v1/auth/login` | Login |
| POST | `/api/v1/auth/register` | Yangi user (ADMIN only) |
| POST | `/api/v1/auth/refresh` | Token yangilash |

### Transport
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/v1/transports` | Barcha transportlar |
| GET | `/api/v1/transports?status=ACTIVE` | Status bo'yicha filter |
| GET | `/api/v1/transports?routeId=1` | Marshrut bo'yicha filter |
| GET | `/api/v1/transports/{id}` | ID bo'yicha |
| POST | `/api/v1/transports` | Yangi transport |
| PATCH | `/api/v1/transports/{id}` | Yangilash |
| PATCH | `/api/v1/transports/{id}/status` | Holat o'zgartirish |
| DELETE | `/api/v1/transports/{id}` | O'chirish |

### GPS / Location
| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/v1/locations` | GPS yangilash |
| GET | `/api/v1/locations/current` | Hozirgi joylashuvlar |
| GET | `/api/v1/locations/{id}/latest` | So'ngi joylashuv |
| GET | `/api/v1/locations/{id}/history` | Tarix (from/to) |

### Yo'lovchilar
| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/v1/passengers` | Yo'lovchi yangilash |
| GET | `/api/v1/passengers/{id}/history` | Tarix |

### Marshrutlar
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/v1/routes` | Barcha marshrutlar |
| POST | `/api/v1/routes` | Yangi marshrut |
| PATCH | `/api/v1/routes/{id}` | Yangilash |
| DELETE | `/api/v1/routes/{id}` | O'chirish |

### Bekatlar
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/v1/stations` | Barcha bekatlar |
| GET | `/api/v1/stations/nearby?lat=41.3&lng=69.2&radius=1.0` | Yaqin bekatlar |
| POST | `/api/v1/stations` | Yangi bekat |

### Analitika
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/v1/analytics/dashboard` | Dashboard statistikasi |

---

## 🔌 WebSocket (Real-time)

**Endpoint:** `ws://localhost:8080/ws`  
**Protocol:** STOMP over SockJS

### Subscribe qilish

```javascript
// Barcha transport joylashuvlari (5 sekundda yangilanadi)
stompClient.subscribe('/topic/locations', callback);

// Bitta transport
stompClient.subscribe('/topic/transport/1', callback);

// Dashboard statistikasi (15 sekundda yangilanadi)
stompClient.subscribe('/topic/dashboard', callback);
```

### Response format (`/topic/locations`)

```json
[
  {
    "transportId": 1,
    "plateNumber": "01 A 777 AA",
    "routeNumber": "27",
    "latitude": 41.3002,
    "longitude": 69.2450,
    "speed": 35.5,
    "passengerCount": 45,
    "capacity": 80,
    "loadPercent": 56.3,
    "status": "ACTIVE",
    "recordedAt": "2024-04-22T09:15:30"
  }
]
```

---

## 🗺️ GPS Simulyatsiya

Tizim avtomatik ravishda **Toshkent koordinatalari** asosida GPS simulyatsiya qiladi:
- Har **5 sekund**da transport harakatlanadi
- **Vaqtga qarab** yo'lovchi soni o'zgaradi:
  - 🌅 07:00–09:00 → 70–100% (Tong chog'i)
  - 🌞 10:00–16:00 → 30–70% (Kunduz)
  - 🌆 17:00–19:00 → 60–100% (Kechki rush)
  - 🌙 22:00–06:00 → 5–20% (Tun)

---

## 🔐 Role huquqlari

| Amal | ADMIN | OPERATOR | VIEWER |
|------|-------|----------|--------|
| Ko'rish | ✅ | ✅ | ✅ |
| Yaratish/Yangilash | ✅ | ✅ | ❌ |
| O'chirish | ✅ | ❌ | ❌ |
| Foydalanuvchi yaratish | ✅ | ❌ | ❌ |
| Analitika | ✅ | ✅ | ❌ |

---

## 🏗️ Arxitektura

```
Client (Browser / Mobile)
    │
    ├── REST API  ──→  Controllers  ──→  Services  ──→  Repositories  ──→  PostgreSQL
    │
    └── WebSocket ──→  STOMP Broker ←── LocationPublisher ←── GpsSimulatorScheduler
```

---

## 📁 Loyiha tuzilmasi

```
src/main/java/uz/transport/monitoring/
├── config/
│   ├── filter/JwtAuthFilter.java
│   ├── DataInitializer.java
│   ├── JpaConfig.java
│   ├── OpenApiConfig.java
│   ├── SecurityConfig.java
│   └── WebSocketConfig.java
├── controller/
│   ├── AnalyticsController.java
│   ├── AuthController.java
│   ├── LocationController.java
│   ├── PassengerController.java
│   ├── RouteController.java
│   ├── StationController.java
│   └── TransportController.java
├── dto/
├── entity/
├── enums/
├── exception/
├── repository/
├── scheduler/
│   ├── DashboardBroadcastScheduler.java
│   └── GpsSimulatorScheduler.java
├── service/
│   ├── AnalyticsService.java
│   ├── AuthService.java
│   ├── CustomUserDetailsService.java
│   ├── LocationService.java
│   ├── PassengerService.java
│   ├── RouteService.java
│   ├── StationService.java
│   └── TransportService.java
├── util/JwtUtil.java
├── websocket/LocationPublisher.java
└── TransportMonitoringApplication.java
```

---

## ⚙️ Konfiguratsiya parametrlari

```yaml
app:
  jwt:
    secret: "..."          # JWT secret key
    expiration: 86400000   # Access token: 24 soat
    refresh-expiration: 604800000  # Refresh token: 7 kun

  scheduler:
    gps-interval: 5000     # GPS simulyatsiya: har 5 sekund
    passenger-interval: 10000  # Yo'lovchi yangilash: har 10 sekund
```
