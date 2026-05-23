# 🔍 FindIt — Lost & Found System

> A smart Lost & Found management system that reunites people with their belongings through intelligent item matching, real-time notifications, and a seamless claims process.

---

## 📌 Project Description

FindIt is a full-stack RESTful web application built as a capstone project for the **Java Spring Boot Bootcamp at General Assembly (2026)**. The platform solves a real-world problem: when people lose their belongings, there is rarely a centralized, smart system to help reconnect them with whoever found their items.

FindIt bridges that gap by allowing users to post lost or found items, automatically matching them using a scoring algorithm based on category, location, and date. Once a match is suggested, admins review and confirm it, users can file claims with proof, and the entire process is tracked with email notifications and an in-app notification center.

The backend is built entirely with **Java Spring Boot**, exposing a secure REST API with JWT-based authentication and role-based access control. The system supports two roles — regular users and admins — each with their own set of permissions and workflows.

---

## 🛠 Tools & Technologies

### Backend
| Technology | Version | Purpose |
|---|---|---|
| Java | 17 | Core programming language |
| Spring Boot | 3.x | Backend framework |
| Spring Security | 3.x | Authentication & Authorization |
| JWT (jjwt) | 0.11+ | Stateless token-based auth |
| Spring Data JPA | 3.x | ORM & database access |
| Hibernate | 6.x | JPA implementation |
| PostgreSQL | 16 | Relational database |
| JavaMailSender | - | Email notifications via Gmail SMTP |
| Lombok | 1.18+ | Boilerplate reduction |
| Maven | 3.9+ | Build & dependency management |

### DevOps
| Technology | Purpose |
|---|---|
| Docker | Application containerization |
| Docker Compose | Multi-container orchestration |

### Testing & Development Tools
| Tool | Purpose |
|---|---|
| Postman | API testing & documentation |
| IntelliJ IDEA | IDE |
| pgAdmin / DBeaver | Database management |
| Git & GitHub | Version control |

---

## 💡 General Approach

### Planning & Design
The project began with defining the core problem: most lost and found systems are either physical bulletin boards or basic listing apps with no intelligence. The goal was to build something smarter — a system that actively tries to match lost items with found items automatically, without requiring users to manually search through hundreds of listings.

The data model was designed first using an ERD, identifying five core entities: **Users, Items, Matches, Claims, and Notifications**. Each entity was carefully mapped with its relationships before writing a single line of code. The item entity was designed with two key fields — `type` (LOST or FOUND) and `status` (OPEN, MATCHED, CLAIMED, CLOSED) — to track the full lifecycle of an item through the system.

### Development Approach
Development followed a layered architecture pattern, building each feature vertically from the database up:

```
Entity → Repository → Service → DTO → Mapper → Controller
```

This approach was followed consistently for all five features: Auth, Items, Matching, Claims, and Notifications. Each feature was developed on its own Git branch and merged via pull requests, maintaining a clean commit history throughout.

Authentication was implemented first using JWT tokens and Spring Security, including email verification and password reset flows. Once auth was stable, the core item management CRUD was built, followed by the matching engine, claims system, and finally the notification layer.

### Matching Algorithm
The matching engine runs automatically every time a new item is posted. It searches for items of the opposite type (LOST searches FOUND, FOUND searches LOST) that are still OPEN, then calculates a match score:

```
Category match  → +0.5  (most important signal)
Location match  → +0.3  (same area)
Date valid      → +0.2  (found date ≥ lost date)
─────────────────────────
Threshold       → 0.5   (minimum to create a match)
```

Any pair scoring 0.5 or above triggers a Match record and notifies both users by email and in-app notification.

---

## ⚠️ Unsolved Problems & Known Issues

### 1. Image Upload (Partial Solution)
Currently, item images and profile pictures are stored as **base64 strings** in the database (`TEXT` column). This works for small images in a demo environment but is not production-ready. The proper solution would be to integrate a cloud storage service such as **AWS S3** or **Cloudinary**, upload the file there, and store only the returned URL in the database. This was identified as a known limitation but was not implemented within the capstone timeline.

### 2. Frontend Integration (In Progress)
The Angular frontend was partially built with full pages for auth, items, matches, claims, and notifications. However, due to time constraints and several Angular 19 compatibility issues (component naming conventions, CORS configuration, JWT interceptor setup), the frontend was not fully polished and is not included in the final submission. The backend REST API is fully functional and tested via Postman.

### 3. Match Scoring — Location Matching
The current location matching uses exact string comparison (`equalsIgnoreCase`). This means "City Centre Mall" and "city centre" would not match. A more robust solution would use partial string matching or integrate a geolocation API to match items within a certain radius. This is noted as a future enhancement.

### 4. No Pagination
All list endpoints currently return the full dataset. For a production system with thousands of items, pagination should be implemented using Spring Data's `Pageable` interface. This was scoped out for the capstone but is a clear next step.

---

## 👤 User Stories

### Regular User
```
As a user, I want to register and verify my email
so that I can securely access the system.

As a user, I want to post a lost item with details and an image
so that others can identify it if they find it.

As a user, I want to post a found item
so that the rightful owner can be notified and claim it.

As a user, I want to search and filter items by type, category, and location
so that I can find items relevant to me quickly.

As a user, I want to be automatically notified when a match is found for my item
so that I don't have to keep checking manually.

As a user, I want to file a claim on a found item with a message and proof
so that I can prove the item belongs to me.

As a user, I want to receive an email when my claim is approved or rejected
so that I know the outcome without logging in.

As a user, I want to view all my notifications in one place
so that I can track all activity related to my items.
```

### Admin
```
As an admin, I want to log in and be redirected to the admin dashboard
so that I can immediately see the system overview.

As an admin, I want to review all auto-generated matches
so that I can confirm or reject them based on accuracy.

As an admin, I want to review all pending claims
so that I can approve or reject them based on the user's proof.

As an admin, I want to update any item's status manually
so that I can manage edge cases in the system.

As an admin, I want to delete items when necessary
so that I can keep the system clean and accurate.
```

---

## 🗄 ERD (Entity Relationship Diagram)

> 📎 [View ERD on dbdiagram.io](docs/ERD.png)

```
users ──────────────────────────── items
  │                                  │
  │  (one user posts many items)      │
  │                                  │
  └── notifications            matches
        (user receives          ├── lost_item_id → items
         notifications)         └── found_item_id → items

                              claims
                               ├── item_id → items
                               └── claimant_id → users
```

### Tables Summary
```
users         → id, name, email, password, role, profile_picture_url,
                is_verified, status, created_at, updated_at

items         → id, user_id, title, description, category, location,
                date, item_image_url, type, status, created_at, updated_at

matches       → id, lost_item_id, found_item_id, match_score,
                status, created_at, updated_at

claims        → id, item_id, claimant_id, message,
                proof_attachment_url, status, created_at, updated_at

notifications → id, user_id, message, type, is_read,
                related_item_id, created_at
```

---

## 📅 Planning Documentation

> 📎 [View Project Board on Trello / GitHub Projects](https://trello.com/b/Rlz1mVVX/findit-lost-found-system)

### Development Phases

```
Phase 1 — Setup & Auth
  ✅ Spring Boot project setup
  ✅ PostgreSQL database connection
  ✅ User entity & repository
  ✅ JWT authentication filter
  ✅ Register, Login, Email verification
  ✅ Forgot password & reset

Phase 2 — Item Management
  ✅ Item entity with type & status enums
  ✅ CRUD endpoints
  ✅ Search & filter by type/category/location
  ✅ Role-based access (admin vs user)

Phase 3 — Matching Engine
  ✅ Match entity & repository
  ✅ Auto-matching service (triggers on item post)
  ✅ Match scoring algorithm
  ✅ Admin confirm/reject endpoints

Phase 4 — Claims
  ✅ Claim entity & repository
  ✅ File claim with validation rules
  ✅ Admin approve/reject
  ✅ Item status auto-update on approval

Phase 5 — Notifications
  ✅ Notification entity & repository
  ✅ In-app notification center
  ✅ Email notifications for all key events
  ✅ Mark as read / mark all as read

Phase 6 — DevOps
  ✅ Dockerfile (multi-stage build)
  ✅ Docker Compose (app + PostgreSQL)
  ✅ Environment variable configuration
```

---

## ⚙️ Installation Instructions

### Prerequisites

Make sure you have the following installed:

```
✅ Java 21+          → https://adoptium.net
✅ Maven 3.9+        → https://maven.apache.org
✅ PostgreSQL 16+    → https://www.postgresql.org
✅ Docker Desktop    → https://www.docker.com/products/docker-desktop
✅ Git               → https://git-scm.com
```

---

### Option A — Run Locally (Without Docker)

**1. Clone the repository**
```bash
git clone https://github.com/yourusername/findit-lostfound.git
cd findit-lostfound
```

**2. Create the database**
```bash
psql -U postgres
CREATE DATABASE lostfounddb;
\q
```

**3. Create application.properties**

Navigate to `src/main/resources/application.properties` and configure:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/lostfounddb
spring.datasource.username=your_postgres_username
spring.datasource.password=your_postgres_password

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT
jwt.secret=yourSecretKey
jwt.expiration=86400000

# Email (Gmail)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-gmail-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

**4. Run the application**
```bash
./mvnw spring-boot:run
```

**5. Verify it's running**
```
http://localhost:8080/api/auth/login
```

---

### Option B — Run with Docker Compose

**1. Clone the repository**
```bash
git clone https://github.com/zainabalzaimoor/findit-lostfound.git
cd findit-lostfound
```

**2. Create .env file** in the project root:
```env
JWT_SECRET=yourSuperSecretKeyThatIsAtLeast256BitsLong
JWT_EXPIRATION=86400000
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-gmail-app-password
```

**3. Build and run**
```bash
docker-compose up --build
```

**4. Verify it's running**
```
http://localhost:8080/api/auth/login
```

**5. Stop the containers**
```bash
docker-compose down
```

---

### Gmail App Password Setup

The app uses Gmail SMTP for email notifications. To get your app password:

```
1. Go to myaccount.google.com
2. Security → 2-Step Verification (enable if not already)
3. Security → App Passwords
4. Select "Mail" and generate
5. Copy the 16-character password
6. Use it as MAIL_PASSWORD in your config
```

---

### Testing the API (Postman)

Import the following base URL into Postman:
```
http://localhost:8080
```

**Quick test — Register a user:**
```
POST /api/auth/register
Content-Type: application/json

{
  "name": "Test User",
  "email": "test@example.com",
  "password": "123456789"
}
```

**Login:**
```
POST /api/auth/login
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "123456789"
}
```

Copy the token from the response and use it as:
```
Authorization: Bearer <your_token>
```

---

## 📡 API Endpoints Summary

### Authentication
| Method | Endpoint | Access |
|--------|----------|--------|
| POST | `/api/auth/register` | Public |
| POST | `/api/auth/login` | Public |
| GET | `/api/auth/verify` | Public |
| POST | `/api/auth/forgot-password` | Public |
| POST | `/api/auth/reset-password` | Public |

### Items
| Method | Endpoint | Access |
|--------|----------|--------|
| POST | `/api/items` | User |
| GET | `/api/items` | User |
| GET | `/api/items/{id}` | User |
| GET | `/api/items/my-items` | User |
| GET | `/api/items/search` | User |
| PUT | `/api/items/{id}` | User |
| DELETE | `/api/items/{id}` | Admin |
| PATCH | `/api/items/{id}/status` | Admin |

### Matches
| Method | Endpoint | Access |
|--------|----------|--------|
| GET | `/api/matches` | Admin |
| GET | `/api/matches/pending` | Admin |
| PATCH | `/api/matches/{id}/status` | Admin |

### Claims
| Method | Endpoint | Access |
|--------|----------|--------|
| POST | `/api/claims` | User |
| GET | `/api/claims/my-claims` | User |
| GET | `/api/claims` | Admin |
| PATCH | `/api/claims/{id}/status` | Admin |

### Notifications
| Method | Endpoint | Access |
|--------|----------|--------|
| GET | `/api/notifications` | User |
| GET | `/api/notifications/unread/count` | User |
| PATCH | `/api/notifications/{id}/read` | User |
| PATCH | `/api/notifications/read-all` | User |

---

## 👨‍💻 Author

**Zainab Alzaimoor**
Java Spring Boot Bootcamp
General Assembly — 2026

---

## 📄 License

This project was developed as a capstone project for educational purposes at General Assembly.

---

> 🔍 FindIt — Built with Java Spring Boot | Reuniting people with their belongings
