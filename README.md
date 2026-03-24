# 🚀 Release Tracker

This project is a Spring Boot application designed with a focus on clean architecture, containerization, and automated CI/CD workflows.

---

## 🛠️ Getting Started

To ensure a consistent environment across different machines, the project is fully containerized using Docker.

### Prerequisites
* **Docker & Docker Compose**
* **Maven** (Optional, for local development outside of Docker)

---

### 📦 Option 1: Full Docker Launch (Recommended)
This is the fastest way to run the entire stack (Application + PostgreSQL) without manually installing Java or Maven.

1. **Prepare the environment file:**
   ```bash
   cp .env.example .env

*(Note: .env.example contains pre-configured defaults for an immediate "out-of-the-box" experience.)*

2. **Launch the services:**
   ```bash
   docker compose up --build -d

**The application will be accessible at:** http://localhost:8080

---

### 💻 Option 2: Local Development
If you prefer running the application locally using your installed Maven while using the Dockerized database:

1. **Start the database only:**
   ```bash
   docker compose up postgres -d
2. **Run the application:**
      ```bash
   mvn spring-boot:run
