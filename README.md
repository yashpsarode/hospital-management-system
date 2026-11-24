# 🏥 Hospital Management System (HMS)

[![GitHub Repo](https://img.shields.io/badge/GitHub-Repository-181717?style=for-the-badge&logo=github)](https://github.com/yashpsarode/hospital-management-system)
[![Author Profile](https://img.shields.io/badge/Developer-Yash%20Sarode-2563eb?style=for-the-badge&logo=github)](https://github.com/yashpsarode)
[![Related Project](https://img.shields.io/badge/Companion%20Project-Software%20Asset%20Management-10b981?style=for-the-badge&logo=git)](https://github.com/yashpsarode/software-asset-management)

> A modern, secure, and production-ready **Full-Stack Hospital Management System** featuring a responsive **Frontend Web Portal**, **Spring Boot 3.2 Backend**, **Spring Security 6**, **JWT Authentication**, **Role-Based Access Control (RBAC)**, and **Interactive Swagger UI**.

---

## 🔗 Quick Redirection Links & Access Points

| Resource | Redirection Link | Description |
|---|---|---|
| 🌐 **Frontend Web Portal** | [`http://localhost:8080/`](http://localhost:8080/) | Complete single-page healthcare dashboard (Patients, Doctors, Appointments, Pharmacy, Billing) |
| 📖 **Interactive Swagger UI** | [`http://localhost:8080/swagger-ui/index.html`](http://localhost:8080/swagger-ui/index.html) | OpenAPI 3.0 interactive REST API explorer with JWT authorization |
| 📄 **OpenAPI Schema (JSON)** | [`http://localhost:8080/v3/api-docs`](http://localhost:8080/v3/api-docs) | Raw OpenAPI 3.0 specification |
| 🗄️ **H2 In-Memory DB Console** | [`http://localhost:8080/h2-console`](http://localhost:8080/h2-console) | In-memory database explorer *(when running with `--spring.profiles.active=h2`)* |
| 👨‍💻 **GitHub Profile** | [`https://github.com/yashpsarode`](https://github.com/yashpsarode) | Yash Sarode's GitHub Profile |
| 💼 **Software Asset Management** | [`https://github.com/yashpsarode/software-asset-management`](https://github.com/yashpsarode/software-asset-management) | Enterprise software asset & license tracking platform |

---

## 🌟 Key Highlights & Full-Stack Capabilities

### 💻 Frontend Web Portal (`/src/main/resources/static`)
- **Unified Single-Application Execution**: The frontend is embedded directly within Spring Boot and served out-of-the-box at `http://localhost:8080/` without needing Node.js or a separate dev server.
- **Role Switcher & Authentication**: Instant 1-click demo login buttons for Administrator, Doctor, Pharmacist, and Receptionist, plus self-registration.
- **Real-Time KPIs**: Live dashboard metrics for total doctors, active patients, scheduled appointments, and pharmacy inventory levels.
- **Patient Directory & Registration**: Register new patients, view emergency contacts, medical records, and blood groups.
- **Doctor Consultation & Availability**: View department specialists, fees, and toggle availability.
- **Appointments Scheduling**: Book appointments, filter by date/doctor, and mark visits as completed.
- **Clinical Prescriptions**: Issue diagnosis, medicine dosages, courses, and dispatch pharmacy orders.
- **Pharmacy Inventory & Low-Stock Alerts**: Medicine catalog, batch numbers, expiration monitoring, and automated low-stock warnings.
- **Billing & Invoices**: Itemized medical invoices, payment status tracking (`PAID`, `PENDING`), and instant card/cash payment settlement.

### ⚙️ Backend Architecture
- 🔐 **Role-Based Access Control (RBAC)**: 5 roles (`ROLE_ADMIN`, `ROLE_DOCTOR`, `ROLE_NURSE`, `ROLE_PHARMACIST`, `ROLE_RECEPTIONIST`).
- 🛡️ **Stateless Security**: Spring Security 6 with JJWT 0.12.3 and BCrypt password encryption.
- 🗄️ **Dual Database Support**: Production MySQL with automated schema/data migration, plus instant zero-setup in-memory H2 fallback profile.
- 🤖 **Automated Data Seeder**: Pre-seeds all departments, roles, demo users, sample patients, doctors, appointments, medicines, and invoices on boot.

---

## 👥 Pre-seeded Demo Accounts

| Role | Username | Password | Access Privileges |
|---|---|---|---|
| **Administrator** | `admin` | `admin123` | Full administrative control across all hospital modules |
| **Doctor** | `dr.john` | `doctor123` | Patient records, clinical appointments, prescription issuance |
| **Pharmacist** | `pharmacist` | `pharmacy123` | Pharmacy inventory, stock replenishment, prescription fulfillment |
| **Receptionist** | `receptionist` | `reception123` | Patient check-in, registration, appointment scheduling, billing |

---

## 🏗️ Architecture & Technology Stack

| Layer | Technology | Details |
|---|---|---|
| **Frontend** | HTML5, CSS3, Modern JavaScript | Responsive dashboard UI served directly from Spring Boot static resources |
| **Backend Framework** | Spring Boot 3.2.0 | Java 17 enterprise application framework |
| **Security** | Spring Security 6 & JJWT 0.12.3 | HMAC-SHA256 tokens, stateless sessions, method-level security (`@PreAuthorize`) |
| **Persistence / ORM** | Spring Data JPA / Hibernate 6 | Automated schema migrations, connection pooling, transactional integrity |
| **Databases** | MySQL 8.x / H2 In-Memory | Production MySQL support with fallback H2 profile for zero-friction local run |
| **Documentation** | SpringDoc OpenAPI 2.3.0 | Swagger UI interactive test explorer |
| **Boilerplate Reduction**| Project Lombok | Clean models with `@Data`, `@Builder`, `@RequiredArgsConstructor` |

---

## 🚀 Running the Project (Backend + Frontend Together)

### Option A: Standard Run (with MySQL)
1. Ensure MySQL is running on `localhost:3306` with database credentials in `application.properties` (`root`/`root`).
2. Run the application:
   ```bash
   mvn spring-boot:run
   ```
3. Open your browser and navigate to:
   - **Frontend Portal**: [`http://localhost:8080/`](http://localhost:8080/)
   - **Swagger UI**: [`http://localhost:8080/swagger-ui/index.html`](http://localhost:8080/swagger-ui/index.html)

### Option B: Zero-Friction Run (In-Memory H2 — No MySQL needed)
If you don't have MySQL installed, run with the embedded H2 profile:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```
The entire application (backend + in-memory database + frontend UI) will boot instantly!

---

## 📁 Repository Structure

```
hospital-management-system/
├── pom.xml
├── README.md
├── .gitignore
└── src/
    └── main/
        ├── java/com/hospital/management/
        │   ├── HospitalManagementApplication.java
        │   ├── config/
        │   │   ├── DatabaseSeeder.java         # Auto-populates sample data on boot
        │   │   ├── OpenApiConfig.java          # Swagger OpenAPI JWT configuration
        │   │   └── SecurityConfig.java         # Spring Security 6 filter chain & static paths
        │   ├── controller/                     # REST controllers for all modules
        │   ├── dto/                            # Data Transfer Objects with validation
        │   ├── entity/                         # JPA entities
        │   ├── enums/                          # System enums (AppointmentStatus, BillStatus, etc.)
        │   ├── exception/                      # Global exception handlers
        │   ├── repository/                     # Spring Data JPA repositories
        │   ├── security/                       # JWT token provider and authentication filters
        │   └── service/                        # Business logic layer
        └── resources/
            ├── application.properties          # Default MySQL configuration
            ├── application-h2.properties       # Zero-dependency embedded H2 profile
            ├── schema.sql                      # SQL DDL script
            ├── data.sql                        # SQL seed script
            └── static/                         # 🌐 Full-Stack Frontend Web Portal
                ├── index.html                  # Responsive Healthcare Dashboard UI
                ├── css/
                │   └── style.css               # Modern glassmorphism & medical theme styling
                └── js/
                    └── app.js                  # Frontend state, JWT handling, and API integration
```
