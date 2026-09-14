# Ticket Booking System

A microservice-based ticket booking system built to explore backend architecture, distributed systems, security, containerization, and service communication using the Java and Spring ecosystem.

> **Project Status:** 🚧 In Active Development

This project is being built incrementally. The architecture and documentation will evolve as new services and infrastructure components are added.

---

# Table of Contents

* [Overview](#overview)
* [Goals](#goals)
* [Architecture](#architecture)
* [Current Services](#current-services)
* [Technology Stack](#technology-stack)
* [API Gateway](#api-gateway)
* [Authentication and Security](#authentication-and-security)
* [Identity Service](#identity-service)
* [Refresh Token Architecture](#refresh-token-architecture)
* [Database Architecture](#database-architecture)
* [Docker Architecture](#docker-architecture)
* [API Endpoints](#api-endpoints)
* [Project Structure](#project-structure)
* [Challenges and Lessons Learned](#challenges-and-lessons-learned)
* [Learning Journey](#learning-journey)
* [Current Progress](#current-progress)
* [Planned Features](#planned-features)
* [Running the Project](#running-the-project)

---

# Overview

The Ticket Booking Platform is a backend-focused microservice project designed to simulate a real-world ticket booking system.

The system will eventually support:

* User authentication and account management
* Event management
* Venues and event schedules
* Ticket types and availability
* Ticket booking
* Payment processing
* Notifications
* Authentication and authorization
* Session management
* Service-to-service communication

The project is intentionally being developed as a learning-oriented architecture project rather than simply building all features as quickly as possible.

The primary focus is understanding:

* Microservice architecture
* Service boundaries
* API gateways
* Authentication and authorization
* JWT security
* Refresh token management
* Docker networking
* Database design
* Synchronous communication
* Asynchronous messaging
* Scalability and system design

---

# Goals

The main goals of this project are:

## Backend Architecture

* Build independent microservices
* Define clear service responsibilities
* Avoid tight coupling between services
* Apply SOLID principles where appropriate
* Design services that can evolve independently

## Security

* JWT-based authentication
* RSA public/private key signing
* Short-lived access tokens
* Refresh token rotation
* Secure token storage
* Gateway-level authentication
* Header spoofing prevention
* Defense-in-depth security

## Infrastructure

* Dockerized services
* Docker Compose orchestration
* PostgreSQL persistence
* Redis for token/session management
* Health checks
* Service networking
* Persistent volumes

## Distributed Systems

Planned exploration includes:

* gRPC for synchronous service communication
* RabbitMQ for asynchronous communication
* Event-driven architecture
* Distributed consistency challenges
* Service discovery
* Observability

---

# Architecture

The system currently follows a microservice-oriented architecture.

```text
                              Client
                                │
                                │ HTTP Request
                                ▼
                      ┌──────────────────────┐
                      │     API Gateway      │
                      │                      │
                      │ Spring Cloud Gateway │
                      │   Authentication     │
                      │   JWT Validation     │
                      └──────────┬───────────┘
                                 │
              ┌──────────────────┼──────────────────┐
              │                  │                  │
              ▼                  ▼                  ▼

      ┌───────────────┐   ┌───────────────┐   ┌───────────────┐
      │   Identity    │   │     Event     │   │    Booking    │
      │   Service     │   │    Service    │   │    Service    │
      └───────┬───────┘   └───────────────┘   └───────────────┘
              │
              │
       ┌──────┴──────┐
       │             │
       ▼             ▼

  PostgreSQL        Redis
   User Data     Refresh Tokens
```

Additional services planned:

```text
Payment Service
Notification Service
```

---

# Current Services

## API Gateway

Status: ✅ Implemented

Responsibilities:

* Route incoming requests
* Authenticate protected requests
* Validate JWT access tokens
* Verify JWT signatures using RSA public keys
* Remove untrusted identity headers
* Add trusted identity headers
* Forward requests to internal services
* Protect internal endpoints

---

## Identity Service

Status: ✅ Core Features Implemented

Responsibilities:

* User registration
* User login
* JWT generation
* Refresh token generation
* Refresh token rotation
* Current user retrieval
* Password change
* Logout
* Logout from all devices
* User authentication validation

---

## Event Service

Status: 🚧 In Progress

Current state:

* Basic service setup
* API Gateway routing
* Protected gateway access
* Test endpoint

Planned functionality:

* Event creation
* Event management
* Categories
* Venues
* Event schedules
* Ticket types
* Ticket availability

---

## Booking Service

Status: 📋 Planned

Responsibilities will include:

* Ticket reservation
* Booking creation
* Booking expiration
* Ticket inventory coordination
* Booking lifecycle management

---

## Payment Service

Status: 📋 Planned

Responsibilities will include:

* Payment processing
* Payment status
* Payment events
* Booking confirmation coordination

---

## Notification Service

Status: 📋 Planned

Responsibilities will include:

* Email notifications
* Password reset emails
* Booking confirmations
* Payment notifications
* Event notifications

---

# Technology Stack

| Technology           | Purpose                                   |
| -------------------- | ----------------------------------------- |
| Java 21              | Primary programming language              |
| Spring Boot          | Backend framework                         |
| Spring Security      | Authentication and authorization          |
| Spring Cloud Gateway | API Gateway                               |
| Spring WebFlux       | Reactive gateway stack                    |
| PostgreSQL           | Relational database                       |
| Redis                | Refresh token storage                     |
| JWT                  | Access token authentication               |
| RSA                  | JWT signing and verification              |
| Docker               | Containerization                          |
| Docker Compose       | Service orchestration                     |
| Flyway               | Database migrations                       |
| gRPC                 | Planned synchronous service communication |
| RabbitMQ             | Planned asynchronous messaging            |

---

# API Gateway

The API Gateway acts as the single entry point for external clients.

```text
Client
  │
  │ HTTP Request
  ▼
API Gateway
  │
  ├── Route Request
  │
  ├── Validate JWT
  │
  ├── Extract Identity
  │
  ├── Remove Untrusted Headers
  │
  └── Add Trusted Headers
           │
           ▼
     Internal Service
```

---

## Current Routes

### Identity Service

```text
/api/auth/**
```

Forwarded to:

```text
identity-service:8081
```

---

### Event Service

```text
/api/events/**
```

Forwarded to:

```text
event-service:8082
```

---

### Planned Routes

```text
/api/bookings/**
/api/payments/**
/api/notifications/**
```

---

# Authentication and Security

The system uses JWT-based authentication.

Two types of tokens are used:

```text
Access Token
Refresh Token
```

---

## Access Token

The access token is:

* A JWT
* Short-lived
* Signed using an RSA private key
* Verified using an RSA public key
* Sent using the Authorization header

Example:

```http
Authorization: Bearer <access-token>
```

The token contains identity information such as:

```text
Subject → User ID
Role    → User Role
Issued At
Expiration
```

---

## JWT Architecture

```text
                     Identity Service
                            │
                            │
                     RSA Private Key
                            │
                            ▼
                     Sign JWT Token
                            │
                            ▼
                          Client
                            │
                            │ Authorization Header
                            ▼
                       API Gateway
                            │
                            │
                     RSA Public Key
                            │
                            ▼
                      Verify JWT
                            │
                            ▼
                     Internal Service
```

The private key remains with the Identity Service.

The API Gateway only needs the public key to verify access tokens.

---

# Gateway Header Security

A client must never be trusted to provide identity headers directly.

For example, a malicious client could attempt:

```http
X-User-Id: fake-admin-id
X-User-Role: ADMIN
```

Therefore, the API Gateway removes client-supplied identity headers.

```text
Incoming Request

Authorization: Bearer JWT

X-User-Id: FAKE-ID
X-User-Role: ADMIN
```

The Gateway performs:

```text
1. Remove X-User-Id
2. Remove X-User-Role
3. Validate JWT
4. Extract actual user identity
5. Add trusted headers
```

The forwarded request becomes:

```http
X-User-Id: actual-user-id
X-User-Role: USER
```

---

## Header Sanitization Flow

```text
                 Client Request
                       │
                       ▼

            ┌─────────────────────┐
            │ Remove User Headers │
            └──────────┬──────────┘
                       │
                       ▼

            ┌─────────────────────┐
            │    Validate JWT     │
            └──────────┬──────────┘
                       │
                       ▼

            ┌─────────────────────┐
            │ Extract User ID     │
            │ Extract User Role   │
            └──────────┬──────────┘
                       │
                       ▼

            ┌─────────────────────┐
            │ Add Trusted Headers │
            └──────────┬──────────┘
                       │
                       ▼

                Internal Service
```

---

# Identity Service

The Identity Service manages authentication and user session functionality.

---

## Registration

A new user can register with:

```text
Email
Password
First Name
Last Name
```

The service:

```text
Validate Request
      │
      ▼
Normalize Email
      │
      ▼
Check Existing User
      │
      ▼
Hash Password
      │
      ▼
Create User
      │
      ▼
Save to Database
```

Passwords are never stored in plain text.

---

## Login

Login flow:

```text
Client
  │
  │ Email + Password
  ▼

Identity Service
  │
  ├── Find User
  │
  ├── Verify Password
  │
  ├── Verify User Status
  │
  ├── Generate Access Token
  │
  └── Generate Refresh Token
          │
          ▼
        Response
```

The response contains:

```json
{
  "accessToken": "...",
  "refreshToken": "...",
  "tokenType": "Bearer"
}
```

---

# Refresh Token Architecture

Refresh tokens are used to obtain new access tokens.

```text
Client
  │
  │ Refresh Token
  ▼
Identity Service
  │
  ▼
Validate Token
  │
  ▼
Rotate Token
  │
  ▼
Generate New Access Token
```

---

## Redis Storage

Refresh tokens are not stored directly.

Instead:

```text
Refresh Token
      │
      ▼
Hash Token
      │
      ▼
Redis
```

Conceptually:

```text
token:{tokenHash}
        │
        ▼
      userId
```

A reverse mapping is also maintained:

```text
user:{userId}
        │
        ▼
Set of Token Hashes
```

This allows efficient operations.

---

## Logout

Single device logout:

```text
Authenticated User
        │
        ▼
Refresh Token
        │
        ▼
Hash Token
        │
        ▼
Find Token Owner
        │
        ▼

Does Token User ID
Match Authenticated User ID?

        │
     ┌──┴──┐
     │     │
    Yes    No
     │     │
     ▼     ▼

  Revoke   Reject
  Token
```

This prevents a user from revoking another user's refresh token.

---

## Logout All Devices

The user-to-token mapping allows all refresh tokens belonging to a user to be revoked.

```text
User ID
   │
   ▼

user:{userId}
   │
   ▼

Token Hashes

Token A
Token B
Token C
   │
   ▼

Revoke All
```

---

## Password Change Security

When a password is changed:

```text
Validate Current Password
          │
          ▼
Validate New Password
          │
          ▼
Hash New Password
          │
          ▼
Update Password
          │
          ▼
Revoke All Refresh Tokens
```

This invalidates existing sessions.

---

# Defense in Depth

Security is implemented across multiple layers.

```text
Layer 1

Client Authentication

        │
        ▼

Layer 2

API Gateway

JWT Validation

        │
        ▼

Layer 3

Header Sanitization

        │
        ▼

Layer 4

Trusted Identity Headers

        │
        ▼

Layer 5

Internal Service Security Filter

        │
        ▼

Layer 6

Security Context

        │
        ▼

Layer 7

Service-Level Ownership Validation
```

This prevents the system from relying on a single security mechanism.

---

# Database Architecture

The project follows a database-per-service approach conceptually.

```text
PostgreSQL
│
├── identity_db
│
├── event_db
│
├── booking_db
│
└── payment_db
```

Each service owns its own data.

```text
Identity Service
      │
      ▼
 identity_db


Event Service
      │
      ▼
  event_db
```

Services should not directly access another service's database.

---

# Docker Architecture

The application runs using Docker Compose.

Current containers:

```text
ticket-api-gateway

ticket-identity-service

ticket-event-service

ticket-postgres

ticket-redis
```

All services communicate through:

```text
ticket-network
```

---

## Docker Network

Inside Docker:

```text
Identity Service
       │
       ▼
postgres:5432


Identity Service
       │
       ▼
redis:6379
```

Services use Docker service names for internal communication.

---

## Host vs Container Ports

One important concept learned during development:

```text
Host Machine

localhost:6381
       │
       ▼

Docker Port Mapping

6381 → 6379
```

Inside the Docker network, services communicate using:

```text
redis:6379
```

Not:

```text
redis:6381
```

The external port mapping is for communication from the host machine.

---

# API Endpoints

## Authentication

| Method | Endpoint                    | Authentication |
| ------ | --------------------------- | -------------- |
| POST   | `/api/auth/register`        | Public         |
| POST   | `/api/auth/login`           | Public         |
| POST   | `/api/auth/refresh`         | Public         |
| POST   | `/api/auth/forgot-password` | Planned        |
| POST   | `/api/auth/reset-password`  | Planned        |

---

## User

| Method | Endpoint                     | Authentication |
| ------ | ---------------------------- | -------------- |
| GET    | `/api/users/me`              | Required       |
| POST   | `/api/users/change-password` | Required       |
| POST   | `/api/users/logout`          | Required       |
| POST   | `/api/users/logout-all`      | Required       |

---

## Event

| Method | Endpoint           | Authentication |
| ------ | ------------------ | -------------- |
| GET    | `/api/events/test` | Required       |

More endpoints will be added as the Event Service is implemented.

---

# Project Structure

```text
ticket-booking-platform
│
├── api-gateway
│
├── identity-service
│
├── event-service
│
├── booking-service
│
├── payment-service
│
├── notification-service
│
├── infrastructure
│   │
│   ├── keys
│   │   ├── private.pem
│   │   └── public.pem
│   │
│   └── postgres
│       └── init
│
├── docker-compose.yml
│
├── .env
│
└── README.md
```


---

# Challenges and Lessons Learned

This project is also a record of the engineering problems encountered during development.

---

## 1. Choosing Service Communication

Several approaches were explored:

```text
REST
RestTemplate
RestClient
Feign Client
WebClient
gRPC
RabbitMQ
```

The planned architecture separates communication styles by purpose.

```text
External Clients
       │
       ▼
REST / HTTP
       │
       ▼
API Gateway
```

For synchronous internal communication:

```text
Service
   │
   ▼
gRPC
```

For asynchronous communication:

```text
Service Event
      │
      ▼
   RabbitMQ
      │
      ▼
Other Services
```

---

## 2. Understanding WebMVC and WebFlux

The project required understanding the difference between:

```text
Spring MVC
```

and:

```text
Spring WebFlux
```

The API Gateway uses the reactive stack.

This introduced concepts such as:

```text
Servlet Filter
       │
       ▼
WebFilter


SecurityFilterChain
       │
       ▼
SecurityWebFilterChain
```

Understanding these differences was necessary to correctly configure authentication and filters in the gateway.

---

## 3. Preventing Header Spoofing

A major security consideration was preventing clients from sending fake identity headers.

Problem:

```http
X-User-Id: FAKE-ADMIN-ID
X-User-Role: ADMIN
```

Solution:

```text
Remove Client Headers
        │
        ▼
Validate JWT
        │
        ▼
Extract Real Identity
        │
        ▼
Add Trusted Headers
```

This allows downstream services to receive verified identity information.

---

## 4. Redis Docker Networking

A configuration issue occurred because the host port was confused with the container port.

Incorrect inside Docker:

```text
redis:6381
```

Correct:

```text
redis:6379
```

The lesson:

```text
Host Port

Used outside Docker


Container Port

Used inside Docker Network
```

---

## 5. Refresh Token Ownership

The initial logout logic only accepted:

```text
Refresh Token
```

The improved design accepts:

```text
Authenticated User ID

+

Refresh Token
```

Then verifies:

```text
Refresh Token Owner

==

Authenticated User
```

before revoking the token.

This adds another layer of security.

---

# Learning Journey

This project is being developed in phases.

---

## Phase 1 — Microservice Fundamentals

Topics explored:

* Monolith vs microservices
* Service boundaries
* Decoupling
* Service discovery
* Synchronous communication
* Asynchronous communication

---

## Phase 2 — Docker

Topics learned:

* Docker images
* Containers
* Dockerfiles
* Docker Compose
* Networks
* Volumes
* Port mapping
* Health checks
* Service dependencies

---

## Phase 3 — API Gateway

Topics learned:

* Reverse proxy architecture
* Request routing
* Route predicates
* Gateway filters
* Request headers
* Response headers
* Public routes
* Protected routes

---

## Phase 4 — Spring Security

Topics learned:

* Authentication
* Authorization
* Security filters
* Security context
* Stateless security
* JWT authentication

---

## Phase 5 — JWT Security

Topics learned:

* JWT structure
* Access tokens
* Refresh tokens
* RSA signing
* Public/private keys
* Token expiration
* Token validation

---

## Phase 6 — Session Management

Topics learned:

* Refresh token storage
* Redis
* Token hashing
* Token rotation
* Logout
* Logout all devices
* Session invalidation

---

## Phase 7 — Identity Service

Implemented functionality:

```text
Register

Login

Refresh Token

Current User

Change Password

Logout

Logout All
```

Planned functionality:

```text
Forgot Password

Reset Password

Email Notifications
```

---

# Current Progress

## Infrastructure

| Component          | Status |
| ------------------ | ------ |
| Docker             | ✅      |
| Docker Compose     | ✅      |
| PostgreSQL         | ✅      |
| Redis              | ✅      |
| Docker Network     | ✅      |
| Persistent Volumes | ✅      |
| Health Checks      | ✅      |

---

## API Gateway

| Feature                  | Status |
| ------------------------ | ------ |
| Request Routing          | ✅      |
| JWT Validation           | ✅      |
| Public Routes            | ✅      |
| Protected Routes         | ✅      |
| Header Sanitization      | ✅      |
| Trusted Identity Headers | ✅      |

---

## Identity Service

| Feature         | Status |
| --------------- | ------ |
| Registration    | ✅      |
| Login           | ✅      |
| Access Token    | ✅      |
| Refresh Token   | ✅      |
| Token Rotation  | ✅      |
| Current User    | ✅      |
| Change Password | ✅      |
| Logout          | ✅      |
| Logout All      | ✅      |
| Forgot Password | 🚧     |
| Reset Password  | 🚧     |

---

## Event Service

| Feature          | Status |
| ---------------- | ------ |
| Service Setup    | ✅      |
| Gateway Route    | ✅      |
| Authentication   | ✅      |
| Event Management | 🚧     |
| Venue Management | 📋     |
| Categories       | 📋     |
| Ticket Types     | 📋     |

---

# Planned Features

The following features are planned.

---

## Event Service

```text
Event CRUD

Event Categories

Venues

Event Schedules

Ticket Types

Ticket Availability

Event Publishing
```

---

## Booking Service

```text
Create Booking

Reserve Tickets

Booking Expiration

Booking Confirmation

Inventory Coordination
```

---

## Payment Service

```text
Payment Processing

Payment Status

Payment Events

Booking Confirmation
```

---

## Notification Service

```text
Email Notifications

Password Reset Email

Booking Confirmation

Payment Confirmation
```

---

## Infrastructure

```text
RabbitMQ

gRPC

Service Discovery

Centralized Configuration

Distributed Tracing

Logging

Metrics

Monitoring

Rate Limiting

Circuit Breakers
```

---

# Future Architecture

The architecture is expected to evolve toward:

```text
                              Client
                                │
                                ▼
                         API Gateway
                                │
                                ▼
                       Authentication
                                │
                ┌───────────────┼───────────────┐
                │               │               │
                ▼               ▼               ▼

           Identity          Event          Booking
           Service           Service         Service

                │               │               │
                └───────────────┼───────────────┘
                                │
                                ▼

                             RabbitMQ

                                │

                ┌───────────────┼───────────────┐
                │                               │
                ▼                               ▼

            Payment                       Notification
            Service                        Service
```

Synchronous communication may use:

```text
gRPC
```

Asynchronous communication may use:

```text
RabbitMQ
```

---

# Running the Project

## Requirements

Install:

```text
Java 21

Docker

Docker Compose
```

---

## Environment Variables

Create a `.env` file.

Example values:

```text
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your-password

POSTGRES_PORT=5433

REDIS_PORT=6381

API_GATEWAY_PORT=8080

IDENTITY_SERVICE_PORT=8081

EVENT_SERVICE_PORT=8082

IDENTITY_DB_USER=postgres
IDENTITY_DB_PASSWORD=your-password

EVENT_DB_USER=postgres
EVENT_DB_PASSWORD=your-password
```


---

## Start the Application

From the project root:

```bash
docker compose up --build
```

Run in detached mode:

```bash
docker compose up --build -d
```

---

## Check Running Services

```bash
docker compose ps
```

---

## View Logs

All services:

```bash
docker compose logs
```

Specific service:

```bash
docker compose logs api-gateway
```

Follow logs:

```bash
docker compose logs -f identity-service
```

---

## Stop the Application

```bash
docker compose down
```

---

# Development Philosophy

The goal of this project is not simply to add more technologies.

Each component should answer a specific architectural question.

Examples:

```text
Why does this need to be a separate service?

Why does this service own this data?

Why should this communication be synchronous?

Why should this event be asynchronous?

Why is Redis used here?

Why is authentication handled at the gateway?

Why does this service need additional validation?

What happens if this service fails?
```

The project will evolve as these questions are explored.

---

# Project Roadmap

```text
Phase 1

Infrastructure
      │
      ▼

Phase 2

API Gateway
      │
      ▼

Phase 3

Identity Service
      │
      ▼

Phase 4

Event Service
      │
      ▼

Phase 5

Booking Service
      │
      ▼

Phase 6

Payment Service
      │
      ▼

Phase 7

Notification Service
      │
      ▼

Phase 8

RabbitMQ
      │
      ▼

Phase 9

gRPC
      │
      ▼

Phase 10

Observability and Production Architecture
```

---

# Status

🚧 **This project is actively under development.**

The current implementation focuses on building a solid foundation before introducing more complex distributed system features.

The README will be updated as:

* New services are implemented
* New endpoints are added
* Infrastructure evolves
* Messaging is introduced
* gRPC communication is added
* Production concerns are addressed


