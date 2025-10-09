# 📊 FM Buyer backup project

### Overview 
This backup project is designed to save all buyer data, ensuring that full details, similar to those of the old buyer, are stored in the database.


### ✅ Prerequisites

Ensure the following are installed on your machine:

- **Java 17**
  
---

### Technology Stack
- Java 17
- Spring Boot 3.x
- Spring Data JPA (Hibernate)
- MySQL 
- Maven
- TestNG for unit and integration testing


### 📦 Installation

Follow these steps to set up and run the project locally:

1. **Clone the repository**:
   ```bash
   git clone https://github.com/AdminFreshMenu/fm-new-buyer-service.git
   ```

2. **Navigate to the project directory**:

   ```bash
   cd fm-buyer
   ```

3. **Install dependencies**:

   ```bash
   mvn clean install
   ```

4. **Configure environment** :

  The project utilises Spring Boot’s application.properties file to manage environment-specific configurations, including database connections, API URLs, and custom application settings.


---

## 🚀 Getting Started

### 🧪 Running the Development Server

To start the application in default mode:

```bash
mvn spring-boot:run
```
```
Server Port: 8080
Base host url: localhost:8080
```
---

## Tests
For the test it is using the testng internally

```bash
Step 1: Clean and run all tests
mvn clean test
```
```bash
Step 2: Open detailed test reports
start target\surefire-reports\index.html          # Main detailed report
start target\surefire-reports\emailable-report.html  # Compact summary
```

### 🧠 Notes

Environment-specific configurations can be added in:

Need to create the application.properties file accordingly.


### 🗂️ Project Structure
```
fm-buyer/
│
├── src/
│   ├── main/
│   │   ├── java/com/freshmenu/buyer/   # Source code
│   │   └── resources/                  # Config files (application.properties)
│   └── test/
│       └── java/com/freshmenu/buyer/   # Test cases (TestNG)
│
├── pom.xml
└── README.md
```
