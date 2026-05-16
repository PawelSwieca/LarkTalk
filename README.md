
# LarkTalk

**LarkTalk** is a full-stack chat room application built with a Java Spring Boot backend and a modern React frontend.  
The project focuses on user authentication, channel-based communication, persistent message storage, role-based user data, and a clean separation between backend REST APIs and frontend UI.

The application allows users to create an account, sign in, view their profile, access available chat rooms, and exchange messages inside selected channels. It uses PostgreSQL as the relational database and Hibernate/JPA for object-relational mapping.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Main Features](#main-features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Backend Overview](#backend-overview)
- [Frontend Overview](#frontend-overview)
- [Database Design](#database-design)
- [Why PostgreSQL?](#why-postgresql)
- [Security](#security)
- [Data Initialization](#data-initialization)
- [API Overview](#api-overview)
- [Project Structure](#project-structure)
- [How to Run the Project](#how-to-run-the-project)
- [Environment Variables](#environment-variables)
- [Available Frontend Scripts](#available-frontend-scripts)
- [Development Notes](#development-notes)
- [Future Improvements](#future-improvements)
- [What This Project Demonstrates](#what-this-project-demonstrates)
- [Author](#author)

---

## Project Overview

LarkTalk is designed as a communication platform organized around topic-based chat rooms.  
Users can register, log in, and join available channels such as general chat, history discussions, programming, or gaming.

The project demonstrates practical full-stack development skills, including:

- Building REST APIs with Spring Boot
- Persisting domain models with Spring Data JPA and Hibernate
- Designing relational entities and relationships
- Handling user authentication and password hashing
- Creating a responsive React frontend
- Communicating between frontend and backend using HTTP requests
- Storing application data in PostgreSQL
- Loading initial data from CSV files into the database

The application is structured as a portfolio-ready full-stack project, with a clear backend/frontend separation and an extendable domain model.

---

## Main Features

### User Features

- User registration
- User login
- Password hashing with BCrypt
- Profile preview with login, nickname, email, creation date, and roles
- Local session persistence on the frontend
- Logout confirmation modal

### Chat Features

- Channel-based chat rooms
- Fetching available channels assigned to the logged-in user
- Loading message history for a selected channel
- Sending new messages
- Messages displayed with sender name and timestamp
- Separate visual styling for current user messages and other users' messages

### Channel Features

- Channels stored in the database
- Channel settings support
- Active/inactive channel configuration
- Maximum occupancy configuration
- User-channel access model

### Admin/Role Features

- Role model prepared for users such as:
  - `admin`
  - `user`
  - `vip_user`
- Role-based profile data
- Frontend prepared to display an admin management button for admin users

---

## Technology Stack

### Backend

- **Java**
- **Spring Boot**
- **Spring Web MVC**
- **Spring Security**
- **Spring Data JPA**
- **Hibernate**
- **Jakarta Persistence API**
- **Lombok**
- **Maven**
- **OpenCSV**
- **PostgreSQL JDBC Driver**

### Frontend

- **React**
- **Vite**
- **JavaScript / JSX**
- **CSS**
- **React Hooks**
- **Fetch API**
- **ESLint**

### Database

- **PostgreSQL**

### Build Tools

- **Maven Wrapper** for backend
- **npm** for frontend package management

---

## Architecture

The application follows a classic full-stack architecture: React Frontend | | HTTP requests / JSON v Spring Boot REST API | | Spring Data JPA / Hibernate v PostgreSQL Database

The backend exposes REST endpoints under the `/api` path, while the frontend consumes these endpoints using the browser `fetch` API.

During frontend development, Vite proxies API requests to the Spring Boot backend, allowing both parts of the application to run independently while still communicating as a single system.

---

## Backend Overview

The backend is implemented with **Spring Boot** and follows a layered structure based on controllers, repositories, entities, request DTOs, and configuration classes.

### Key Backend Responsibilities

- Handling user registration and login
- Encoding passwords securely using BCrypt
- Managing users, roles, channels, channel settings, channel access, and messages
- Serving REST API endpoints
- Persisting data using Spring Data JPA repositories
- Mapping Java entities to relational database tables with Hibernate
- Loading initial application data from CSV files
- Serving the frontend single-page application in production-like scenarios

### Main Backend Components

#### Controllers

- `AuthController`  
  Handles registration, login, and user profile retrieval.

- `ChannelController`  
  Handles retrieving channels available to the currently logged-in user.

- `MessageController`  
  Handles loading message history and saving new messages.

- `SpaController`  
  Supports SPA routing by forwarding frontend routes to `index.html`.

#### Models / Entities

The domain model is represented with JPA entities:

- `User`
- `Role`
- `Channel`
- `ChannelSetting`
- `Message`
- `UserChannelAccess`
- `MessageType`

These entities are mapped to relational database tables using Jakarta Persistence annotations.

#### Repositories

Spring Data JPA repositories provide data access abstraction:

- `UserRepository`
- `RoleRepository`
- `ChannelRepository`
- `ChannelSettingRepository`
- `MessageRepository`
- `UserChannelAccessRepository`

This reduces boilerplate SQL code and provides readable repository methods such as:

- `findByLogin`
- `existsByEmail`
- `findByChannelIdOrderByTimestampAsc`
- `findByUserId`
- `countByChannelId`

---

## Frontend Overview

The frontend is built with **React** and **Vite**, providing a fast development environment and a modern single-page application experience.

### Key Frontend Responsibilities

- Display login and registration forms
- Validate registration input on the client side
- Store session-related data in `localStorage`
- Fetch authenticated user data from the backend
- Display available chat rooms
- Load and render messages
- Send new messages to the backend
- Display profile and logout modals
- Provide a responsive and modern UI

### Main Frontend Components

- `App.jsx`  
  Controls whether the user sees the login form or registration form.

- `LoginForm.jsx`  
  Handles user login and session initialization.

- `SignUpForm.jsx`  
  Handles user registration and client-side validation.

- `Dashboard.jsx`  
  Displays user dashboard, profile modal, available channels, chat window, and message handling.

### UI Design

The frontend uses custom CSS with a dark, modern, glass-like visual style.  
The interface includes:

- Gradient backgrounds
- Modal dialogs
- Responsive chat room cards
- Styled message bubbles
- Smooth hover effects
- Dedicated chat window layout

---

## Database Design

The application uses a relational database model suitable for chat-based applications.

### Main Tables

#### `users`

Stores user account data:

- login
- nickname
- password hash
- email
- creation date
- last login date

#### `roles`

Stores available user roles such as admin, user, and VIP user.

#### `user_roles`

Join table representing a many-to-many relationship between users and roles.

#### `channels`

Stores chat room information:

- channel name
- password hash
- description
- creation date

#### `channel_settings`

Stores configurable settings for channels, for example:

- active status
- maximum occupancy

#### `user_channel_access`

Represents which users have access to which channels.

#### `messages`

Stores chat messages:

- content
- timestamp
- message type
- sender
- channel

---

## Why PostgreSQL?

PostgreSQL was selected as the database because it is a strong, production-grade relational database system that fits the needs of this application well.

### Reasons for Choosing PostgreSQL

#### 1. Strong Relational Model

LarkTalk contains multiple related entities: users, roles, channels, messages, and access permissions.  
PostgreSQL handles these relationships naturally with foreign keys, joins, and relational constraints.

#### 2. Reliability and Data Integrity

For a chat application, message history and user data should be stored reliably.  
PostgreSQL provides ACID compliance, transactional safety, and strong consistency guarantees.

#### 3. Great Compatibility with Hibernate

PostgreSQL works very well with Hibernate and Spring Data JPA.  
Entity relationships such as `ManyToOne` and `ManyToMany` can be mapped cleanly to PostgreSQL tables.

#### 4. Scalability

PostgreSQL is suitable both for small projects and larger production systems.  
It can handle growing amounts of users, messages, and channels without requiring a database migration to another technology too early.

#### 5. Advanced Features for Future Development

PostgreSQL also gives room for future improvements, such as:

- full-text search in messages
- indexing message history
- JSONB columns for flexible metadata
- advanced analytics
- database-level constraints
- optimized queries for large chat histories

Because of this, PostgreSQL is a strong long-term choice for LarkTalk.

---

## Security

The application includes a basic security foundation based on Spring Security.

### Implemented Security Elements

- Passwords are not stored as plain text.
- Passwords are hashed using `BCryptPasswordEncoder`.
- Spring Security configuration disables default form login and HTTP Basic authentication.
- Selected API endpoints are exposed for application-level authentication flow.
- Frontend sends an authorization header for protected chat-related operations.

### Current Authentication Model

The current project uses a simplified token-like mechanism for development purposes.  
After login, the backend returns a token string that is stored in the browser local storage and sent with subsequent requests.

This is sufficient for demonstrating the application flow, but in a production-ready version it should be replaced with a real JWT-based authentication system or session-based security.

Recommended future security improvements include:

- Real JWT generation and validation
- Refresh tokens
- Role-based authorization on backend endpoints
- Request validation
- Rate limiting
- CORS policy hardening
- Secure cookie support
- CSRF strategy depending on authentication model

---

## Data Initialization

The backend includes a CSV-based data loader that initializes the database with sample data when the application starts and the user table is empty.

Initial data includes:

- roles
- users
- channels
- channel settings
- user-channel access records
- sample messages

This makes the application easier to test immediately after database setup.

CSV files are located in: src/main/resources/data


The loader uses OpenCSV and maps CSV IDs to generated PostgreSQL IDs, which helps avoid problems caused by auto-generated database identifiers.

---

## API Overview

The backend exposes REST endpoints under `/api`.

### Authentication

#### Register User

POST /api/signup

Creates a new user account.

Example request body:
```
json { "login": "john", "nickname": "John", "email": "john@example.com", "password": "StrongPassword123!" }
``` 


#### Login

POST /api/login

Authenticates a user and returns session data.

Example request body:
```
json { "login": "john", "password": "StrongPassword123!" }
```

Example response:
```
json { "token": "fake-jwt-token-for-john", "username": "john", "nickname": "John" }
```
#### Get Profile

GET /api/profile?login=john


Returns user profile data.

---

### Channels

#### Get User Channels

GET /api/channels/my

Requires an authorization header:

**http Authorization: Bearer fake-jwt-token-for-john**


Returns channels available to the logged-in user.

---

### Messages

#### Get Channel Messages

Returns channels available to the logged-in user.

---

### Messages

#### Get Channel Messages

GET /api/messages?chatId=1


Requires an authorization header.

Returns messages ordered by timestamp.

#### Send Message

POST /api/messages


Requires an authorization header.

Example request body:
```
json { "chatId": 1, "content": "Hello everyone!" }
```
Example response:
```
json { "success": true, "messageId": 15, "timestamp": "2026-05-16T12:30:00" }
```
---

## Project Structure

LarkTalk 
├── frontend 
│ ├── src 
│ │ ├── assets 
│ │ ├── css 
│ │ ├── jsx 
│ │ └── main.jsx 
│ ├── package.json 
│ └── vite.config.js 
├── src 
│ ├── main 
│ │ ├── java 
│ │ │ └── com.candle.larktalk 
│ │ │ ├── controller 
│ │ │ ├── csv 
│ │ │ ├── model 
│ │ │ ├── repository 
│ │ │ ├── request 
│ │ │ ├── security 
│ │ │ └── LarkTalkApplication.java 
│ │ │ ├── resources 
│ │ │ │   ├── data 
│ │ │ │   ├── static 
│ │ │ │   └── application.properties 
│ ├── test 
│ └── pom.xml 
├── mvnw 
└── mvnw.cmd


---

## How to Run the Project

### Prerequisites

Make sure you have installed:

- Java JDK
- Maven or Maven Wrapper
- Node.js
- npm
- PostgreSQL

---

### Backend Setup

#### 1. Create PostgreSQL Database

Create a PostgreSQL database named: 
**LarkTalkDB**

Example SQL:
```CREATE DATABASE "LarkTalkDB";
```

#### 2. Configure Database Password

The application expects the database password to be provided as an environment variable:

**DB_PASSWORD**

Example on Windows PowerShell:
```
$env:DB_PASSWORD="your_postgres_password"
```
Example on Linux/macOS:
```
export DB_PASSWORD="your_postgres_password"
```


#### 3. Run Backend

From the project root:
```
./mvnw spring-boot:run
```

On Windows:
```
.\mvnw.cmd spring-boot:run
```

The backend will start on:

http://localhost:6789


---

### Frontend Setup

Go to the frontend directory:
```
cd frontend
```
Install dependencies:
```
npm install
```

Run development server:

```
npm run dev
```

The frontend will start on:

http://localhost:5173

Vite is configured to proxy API requests to the backend:

 /api -> http://localhost:6789


---

## Environment Variables

### Backend

| Variable | Description |
|---|---|
| `DB_PASSWORD` | Password for the PostgreSQL user configured in `application.properties` |

Database connection example:

spring.datasource.url=jdbc:postgresql://localhost:5432/LarkTalkDB?sslmode=disable\

spring.datasource.username=postgres

spring.datasource.password=${DB_PASSWORD}

server.port=6789

spring.jpa.hibernate.ddl-auto=update


---

## Available Frontend Scripts

Inside the `frontend` directory:

### Start development server

```
npm run dev
```


### Build production bundle
```
npm run build
```

### Run ESLint
```
npm run lint
```

### Preview production build
```
npm run preview
```

---

## Development Notes

The project currently uses a simplified authentication token for demonstration purposes.  
This keeps the project easy to understand while showing the complete flow between login, frontend state, authorization headers, and backend validation.

The domain model is intentionally designed to be extendable. For example:

- `MessageType` already supports multiple message types such as text, image, video, and audio.
- `ChannelSetting` allows adding new configuration options without redesigning the whole channel table.
- `UserChannelAccess` can be extended with permissions, invitation status, moderation data, or notification settings.
- `Role` and `user_roles` prepare the project for more advanced authorization logic.

---

## Future Improvements

Planned or recommended improvements:

- Replace simplified token authentication with real JWT
- Add backend role-based authorization
- Add WebSocket support for real-time messaging
- Add message editing and deletion
- Add channel management panel for admins
- Add user invitations to private channels
- Add pagination for message history
- Add database migrations with Flyway or Liquibase
- Add DTO validation with Jakarta Bean Validation
- Add integration tests for REST endpoints
- Add Docker Compose for PostgreSQL and application startup
- Improve frontend routing with React Router
- Add global frontend state management if the application grows
- Add production deployment configuration

---

## What This Project Demonstrates

This project demonstrates practical knowledge of:

- Java backend development
- Spring Boot application structure
- REST API design
- Spring Data JPA repositories
- Hibernate entity mapping
- Relational database modeling
- PostgreSQL integration
- Password hashing and basic security concepts
- React component-based frontend development
- React hooks and local state management
- Frontend-backend integration
- CSV-based data initialization
- Clean separation of application layers

---

## Author

Candle

---

*Created as a full-stack portfolio project focused on Java, Spring Boot, Hibernate, PostgreSQL, and React.*

