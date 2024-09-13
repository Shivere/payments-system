# Mamlaka Payment System

This repository contains the backend for the Mamlaka Payment System, a Spring Boot application that integrates with a MySQL database and serves payment-related API endpoints. The frontend is hosted separately at [Mamlaka Dashboard](https://mamlaka.web.app/dashboard).

## Project Overview

The backend is responsible for handling payment transactions, retrieving transaction statuses, and managing user interactions. It is designed to integrate with a mock payment gateway and provides endpoints to process payments, retrieve transaction details, and list all transactions.

 **Core Endpoints include**: 
- `POST /api/register`: Registers the user.
- `GET  /api/login`: Login using specifi user credentials.
- `POST /api/payment`: Creates a payment request.
- `GET  /payment/{id}`: Retrieves transaction by ID.
- `GET  /transactions`: Lists all payment transactions with pagination support.

**Functionality**
- **Payment Methods**: Supports credit cards and Mpesa, simulating payment processing for both.
- **Security**: Implements JWT-based authentication and tokenization for sensitive payment data. Also uses roles
- **Idempotency**: Ensures POST `/payment` transactions are not duplicated.
- **Error Handling**: Provides detailed error messages for invalid requests and system errors.
- **Database**: Uses a relational database to store payment transactions with proper indexing for optimized reads and writes.
- **Performance**: Includes caching on GET `/payment/{id}` to improve retrieval speed for frequently accessed transactions.
- **Webhooks** (Bonus): Webhooks are triggered when payment status changes (e.g., from pending to successful).


The backend is built using Spring Boot and runs inside a Docker container alongside a MySQL database. 

---

## Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Requirements](#requirements)
- [Setup Instructions](#setup-instructions)
  - [Running Locally](#running-locally)
  - [Docker Setup](#docker-setup)
- [API Documentation](#api-documentation)
- [Frontend](#frontend)
- [Testing](#testing)

---

## Features

- Payment processing for different payment types (Credit Card, E-Wallet, Mpesa)
- JWT-based token authentication for securing API endpoints
- Pagination for listing transactions
- Error handling and status management
- Support for idempotent payment requests

---

## Technologies Used

- **Java 17** with **Spring Boot**
- **MySQL** for the relational database
- **Docker** for containerizing the application
- **JWT** for authentication and authorization
- **Swagger** for API documentation

---

## Performance Considerations and Enhancements

To ensure the performance and scalability of the payment gateway API, the following design choices were made:

1. **Role-based User Access**:
   - Each user is tied to a specific role (e.g., `ADMIN`, `USER`). This role determines their access to certain API endpoints.

2. **User-based Payment Transactions**:
   - Each payment transaction is linked to a user in the system. This ensures that users can only query transactions that they own, reinforcing security and data isolation.

3. **Indexing on `transaction_id`**:
   - The `transaction_id` field is indexed in the database. This supplements caching strategies and ensures that querying transactions by ID is both fast and efficient, especially in high-traffic environments.

4. **Indexing on `created_at`**:
   - The `created_at` field is indexed for the paginated endpoint `GET /transactions`. This ensures that when sorting transactions by date (in descending order), the query will execute efficiently even with large datasets.

These enhancements are key to supporting a scalable API that can handle high transaction volumes while maintaining quick response times.

## Design Choices and Trade-offs

### 1. **Token-based Authentication**
   - **Why JWT?**: We chose JWT for its stateless nature and scalability, allowing us to handle user authentication without storing session data on the server.
   - **Trade-off**: JWTs, once issued, are valid until they expire, which means revoking access (e.g., for a compromised token) requires additional mechanisms like token blacklisting.

### 2. **Idempotency Implementation**
   - **Why Idempotency?**: The idempotency key ensures that even if the client sends duplicate requests (due to network retries), only one transaction is processed.
   - **Trade-off**: Implementing idempotency adds complexity and requires storage of the idempotency keys for a certain duration, potentially increasing storage needs.

### 3. **Error Handling Strategy**
   - **Why Clear Error Messages?**: Users should be provided with helpful, consistent error messages. HTTP status codes are used appropriately (e.g., 400 for client errors, 500 for server errors).
   - **Trade-off**: More complex error handling increases code complexity but improves user experience and debugging.

### 4. **Database Choice**
   - **Why Relational Database?**: We use MySQL/PostgreSQL for storing transactions because relational databases provide ACID compliance, ensuring transaction integrity.
   - **Trade-off**: While relational databases are reliable, they may not be as scalable as NoSQL alternatives in certain high-throughput scenarios.

### 5. **Webhooks**
   - **Why Webhooks?**: Webhooks notify external systems when the status of a payment changes. This is useful for real-time updates without needing to poll the API.
   - **Trade-off**: Webhooks require more setup and error handling (e.g., retry mechanisms for failed webhook deliveries).


---

## Requirements

To run this project, ensure you have the following installed:

- Java 17 or later
- Maven
- Docker and Docker Compose

---

## Setup Instructions

### Running Locally

To run the Spring Boot application locally without Docker, you need to also have mysql configured and edit the properties file:

1. **Clone the repository**:
   ```bash
   git clone https://github.com/Shivere/payment_system.git
   cd payment_system
   ```

2. **Build the application**:
   ```bash
   mvn clean package
   ```

3. **Run the application**:
   After building, the JAR file can be executed using:
   ```bash
   java -jar target/mamlaka-0.0.1-SNAPSHOT.jar
   ```

   The application will start on `http://localhost:8080`.

### Docker Setup

You can also run the application and its MySQL database using Docker.

1. **Build the jar file**: Using -DskipTests to avoid database connection test failures since it's running on a docker env

    ```bash
    mvn clean package -DskipTests
    ```

2. **Build and Run with Docker Compose**:
   Use the following command to build the Docker image and start the containers:
   ```bash
   docker compose up --build
   ```

3. **Accessing the Application**:
   The Spring Boot application will be accessible at `http://localhost:8080`, and MySQL will be running on port `3306`.

### Stopping the Containers

To stop the containers, simply run:
```bash
docker compose down
```

---

## API Documentation

The API documentation can be accessed via Swagger at `http://localhost:8080/swagger-ui.html` once the application is running.

The postman documentation can also be found using this link `https://www.postman.com/solar-comet-140492/workspace/public-work-space/collection/4366083-7e23550d-b46b-4878-96a5-7aae3e962bd2?action=share&creator=4366083`

Each endpoint listed above requires JWT authentication, and you need to include the token in the request header as `Authorization: Bearer <token>`.

---

## Frontend

The frontend of this project is hosted separately and can be accessed at [Mamlaka Dashboard](https://mamlaka.web.app/dashboard).
- Git Repository: [Frontend Repo](https:github.com/Shivere/mamlaka-frontend)
---

## Conclusion

This project demonstrates a complete backend setup for a payment gateway API, including Docker support for containerization and JWT for security. You can integrate this backend with any frontend or use the provided frontend at the Mamlaka Dashboard.

For further inquiries or support, feel free to raise an issue or contribute to the repository!

--- 
