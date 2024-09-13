# Mamlaka Payment System

This repository contains the backend for the Mamlaka Payment System, a Spring Boot application that integrates with a MySQL database and serves payment-related API endpoints. The frontend is hosted separately at [Mamlaka Dashboard](https://mamlaka.web.app/dashboard).

## Project Overview

The backend is responsible for handling payment transactions, retrieving transaction statuses, and managing user interactions. It is designed to integrate with a mock payment gateway and provides endpoints to process payments, retrieve transaction details, and list all transactions.

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

## Requirements

To run this project, ensure you have the following installed:

- Java 17 or later
- Maven
- Docker and Docker Compose

---

## Setup Instructions

### Running Locally

To run the Spring Boot application locally without Docker, you need to have mysql configured:

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

1. **Build and Run with Docker Compose**:
   Use the following command to build the Docker image and start the containers:
   ```bash
   docker-compose up --build
   ```

2. **Accessing the Application**:
   The Spring Boot application will be accessible at `http://localhost:8080`, and MySQL will be running on port `3306`.

### Stopping the Containers

To stop the containers, simply run:
```bash
docker-compose down
```

---

## API Documentation

The API documentation can be accessed via Swagger at `http://localhost:8080/swagger-ui.html` once the application is running.

**Endpoints**:
- `POST /payment`: Process a new payment
- `GET /payment/{id}`: Retrieve the status of a specific payment
- `GET /transactions`: List all transactions (with pagination)

Each endpoint requires JWT authentication, and you need to include the token in the request header as `Authorization: Bearer <token>`.

---

## Frontend

The frontend of this project is hosted separately and can be accessed at [Mamlaka Dashboard](https://mamlaka.web.app/dashboard).
- Git Repository: [Frontend Repo](https:github.com/Shivere/mamlaka-frontend)
---

## Conclusion

This project demonstrates a complete backend setup for a payment gateway API, including Docker support for containerization and JWT for security. You can integrate this backend with any frontend or use the provided frontend at the Mamlaka Dashboard.

For further inquiries or support, feel free to raise an issue or contribute to the repository!

--- 

Feel free to customize any sections of the README as needed!
