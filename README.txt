# 🚗 Star Protect Vehicle Insurance

A full-stack **Vehicle Insurance Management System** built using **Java, Spring Boot, Angular, TypeScript, JDBC, and H2 Database**. The application provides role-based access for **Administrators and Underwriters**, enabling secure authentication, underwriter management, and vehicle insurance policy management through dedicated dashboards.

---

## 📌 Project Overview

**Star Protect Vehicle Insurance** is a web-based insurance management application designed to streamline the administration of vehicle insurance policies.

The system follows a **frontend-backend architecture**, with Angular providing the user interface and Spring Boot exposing RESTful APIs for authentication, underwriter management, and insurance policy operations.

### 👥 User Roles

* **Administrator**

  * Authenticate into the system
  * Register underwriters
  * Search underwriters
  * Update underwriter details
  * Delete underwriters
  * View registered underwriters

* **Underwriter**

  * Authenticate into the system
  * Create vehicle insurance policies
  * View insurance policies
  * Manage policy-related information

---

## ✨ Key Features

### 🔐 Authentication & Authorization

* Role-based login for Administrators and Underwriters
* Protected Angular routes using route guards
* Authentication state management
* Backend authentication APIs

### 👨‍💼 Administrator Dashboard

* Register new underwriters
* Search underwriters by ID
* Update underwriter information
* Delete underwriters
* View all registered underwriters

### 📋 Underwriter Dashboard

* Create new vehicle insurance policies
* View existing insurance policies
* Manage policy information

### 🔗 RESTful Backend

* Spring Boot REST controllers
* Structured service and repository layers
* JDBC-based database operations
* JSON-based API communication

### 🗄️ Database

* H2 in-memory relational database
* SQL schema initialization
* JDBC Template for database operations
* Automatic database initialization on application startup

### 🎨 Responsive Frontend

* Angular-based single-page application
* TypeScript
* Component-based architecture
* Responsive UI
* Dedicated dashboards for different user roles

---

## 🛠️ Technologies Used

### Backend

| Technology    | Purpose                        |
| ------------- | ------------------------------ |
| Java          | Backend programming language   |
| Spring Boot   | Backend framework              |
| Spring JDBC   | Database connectivity          |
| JDBC Template | Database operations            |
| H2 Database   | Relational database            |
| Maven         | Dependency management & build  |
| REST API      | Frontend-backend communication |

### Frontend

| Technology     | Purpose                          |
| -------------- | -------------------------------- |
| Angular        | Frontend framework               |
| TypeScript     | Application programming language |
| HTML5          | UI structure                     |
| CSS3           | Styling                          |
| Angular Router | Client-side navigation           |

### Development Tools

* Git
* GitHub
* IntelliJ IDEA / VS Code
* Maven
* npm

---

## 📂 Project Structure

```text
Star-Protect-Vehicle-Insurance/
│
├── backend/
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/
│           │       └── starprotect/
│           │           ├── StarProtectApplication.java
│           │           │
│           │           ├── config/
│           │           │   ├── DatabaseConfig.java
│           │           │   └── WebConfig.java
│           │           │
│           │           ├── controller/
│           │           │   ├── AdminController.java
│           │           │   ├── AuthController.java
│           │           │   └── UnderwriterController.java
│           │           │
│           │           ├── dto/
│           │           │   ├── LoginRequest.java
│           │           │   ├── LoginResponse.java
│           │           │   └── PolicyRequest.java
│           │           │
│           │           ├── model/
│           │           │   ├── InsurancePolicy.java
│           │           │   └── Underwriter.java
│           │           │
│           │           ├── repo/
│           │           │   ├── InsurancePolicyRepository.java
│           │           │   └── UnderwriterRepository.java
│           │           │
│           │           └── service/
│           │               ├── InsurancePolicyService.java
│           │               └── UnderwriterService.java
│           │
│           └── resources/
│               ├── application.properties
│               └── schema.sql
│
├── frontend/
│   ├── angular.json
│   ├── package.json
│   ├── package-lock.json
│   ├── tsconfig.json
│   └── src/
│       ├── app/
│       │   ├── components/
│       │   │   ├── admin-dashboard/
│       │   │   ├── landing/
│       │   │   ├── login/
│       │   │   └── underwriter-dashboard/
│       │   │
│       │   ├── models/
│       │   ├── services/
│       │   ├── app-routing.module.ts
│       │   ├── app.component.ts
│       │   └── app.module.ts
│       │
│       ├── environments/
│       ├── index.html
│       ├── main.ts
│       └── styles.css
│
├── .gitignore
├── logo.png
└── README.md
```

---

## ⚙️ Getting Started

### Prerequisites

Make sure the following are installed:

* **Java JDK 17+**
* **Maven**
* **Node.js**
* **npm**
* **Angular CLI**

Verify the installations:

```bash
java -version
mvn -version
node -version
npm -version
ng version
```

---

# 🚀 Running the Backend

Navigate to the backend directory:

```bash
cd backend
```

Run the Spring Boot application using Maven:

```bash
mvn spring-boot:run
```

The backend will start on:

```text
http://localhost:8080
```

The application uses the `/api` context path, so REST endpoints are available under:

```text
http://localhost:8080/api
```

### H2 Database Console

The H2 console is enabled for development.

```text
http://localhost:8080/api/h2-console
```

The database is configured as an in-memory H2 database and is initialized automatically using `schema.sql`.

---

# 🎨 Running the Angular Frontend

Open a new terminal and navigate to the frontend directory:

```bash
cd frontend
```

Install dependencies:

```bash
npm install
```

Start the Angular development server:

```bash
ng serve
```

The frontend will normally be available at:

```text
http://localhost:4200
```

The Angular application communicates with the Spring Boot backend through:

```text
http://localhost:8080/api
```

---

## 🔑 Application Roles

The application supports two primary roles:

### Administrator

Provides access to:

* Underwriter registration
* Underwriter search
* Underwriter updates
* Underwriter deletion
* Underwriter listing

### Underwriter

Provides access to:

* Vehicle insurance policy creation
* Policy listing
* Policy management

> **Note:** Credentials should be configured according to the application's database seed/configuration rather than hardcoded in this README.

---

## 🔌 Backend Architecture

The backend follows a layered architecture:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
H2 Database
```

### Controller Layer

Handles HTTP requests and exposes REST APIs.

### Service Layer

Contains application and business logic.

### Repository Layer

Handles database operations using JDBC.

### Database Layer

Uses H2 with SQL-based schema initialization.

---

## 🔄 Application Flow

```text
                 ┌─────────────────────┐
                 │      Angular UI     │
                 │   TypeScript / HTML │
                 └──────────┬──────────┘
                            │
                            │ REST / JSON
                            ▼
                 ┌─────────────────────┐
                 │   Spring Boot API   │
                 │    Controllers      │
                 └──────────┬──────────┘
                            │
                            ▼
                 ┌─────────────────────┐
                 │      Services       │
                 │   Business Logic    │
                 └──────────┬──────────┘
                            │
                            ▼
                 ┌─────────────────────┐
                 │    Repositories     │
                 │   JDBC Template     │
                 └──────────┬──────────┘
                            │
                            ▼
                 ┌─────────────────────┐
                 │    H2 Database      │
                 └─────────────────────┘
```

---

## 🧪 Development & Testing

The project can be tested locally by running both applications:

**Terminal 1 — Backend**

```bash
cd backend
mvn spring-boot:run
```

**Terminal 2 — Frontend**

```bash
cd frontend
npm install
ng serve
```

Then open:

```text
http://localhost:4200
```

---

## 🔒 Security Considerations

This project is intended as a **development/portfolio application**.

For production deployment, the following improvements would be recommended:

* JWT-based authentication
* Password hashing using BCrypt
* Role-based authorization at the API level
* Externalized environment variables
* Production-grade database such as PostgreSQL/MySQL
* HTTPS
* Input validation and sanitization
* Centralized exception handling
* API documentation using Swagger/OpenAPI

---

## 🚀 Future Enhancements

Potential improvements include:

* JWT authentication and refresh tokens
* Advanced role-based authorization
* PostgreSQL/MySQL integration
* Docker containerization
* Swagger/OpenAPI documentation
* Automated unit and integration testing
* CI/CD pipeline using GitHub Actions
* Policy search and filtering
* Policy renewal and expiration tracking
* Claims management
* Production deployment

---

## 📸 Screenshots

Screenshots of the application can be added here to showcase:

* Landing Page
* Login Page
* Administrator Dashboard
* Underwriter Dashboard
* Policy Management

Example:

```text
screenshots/
├── landing-page.png
├── login.png
├── admin-dashboard.png
└── underwriter-dashboard.png
```

---

## 👨‍💻 Author

**Varun Saxena**

Computer Science Engineer | Java | Spring Boot | Angular | Python | AI/ML

GitHub: [VarunSaxena123](https://github.com/VarunSaxena123)

---

## ⭐ Project

If you find this project useful or interesting, consider giving it a ⭐ on GitHub.
