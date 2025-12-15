# Colados Leaderboard API

The API for the Colados Leaderboard project. This application handles score submissions, photo uploads, and leaderboard generation.

## How It Works
1. Accepts score submissions via REST API endpoints.
2. Accepts scoreboard photo uploads via REST API endpoints.
3. Stores user profiles, scores, and leaderboards in a PostgreSQL database.
4. Store uploaded photos in a MinIO object storage service.
5. Publishes messages to a RabbitMQ message queue to notify the Image Processor service of new photos for processing.
6. Consumes processed image results from the Image Processor service via RabbitMQ messages.
7. Updates scores and leaderboards based on processed image results. Maps game characters from the results to user profiles in order to assign scores correctly.
8. Provides endpoints to retrieve:
    - Game results
    - Monthly leaderboards
    - Others

### Additional features.
- Root user bootstrapping on first run for admin tasks like creating championships and registering external users.
- User authentication via Google OAuth2 (registered external users only).

## How to Run/Setup for Development

> **Note:** 
The following instructions are for running this project only. To run the complete Colados Leaderboard project, please refer to the section [Running the Complete Project](../README.md#running-the-complete-project).

Requirements:

- Java 21+
- Maven 3.6+
- Docker and Docker Compose (for running dependent services)

Steps:

1. Clone the repository:

Please refer to the main project README for cloning instructions.

2. Navigate to the project directory:
```bash
cd colados-leaderboard-api
```

3. Install dependencies using Maven:
```bash
mvn clean install
```

4. Create a `.env` file in the project root based on the provided `.env.example` file and update the variables as needed.

> **Note:** As the `.env` file is located in the main project root, ensure you are referencing it correctly when running the application from this subdirectory.

5. Start dependent services using Docker Compose:
```bash
docker-compose up -d colados-image-processor rabbitmq minio mongodb mongo-express postgres
```

> **Note:** Please refer to the [colados-image-processor/README.md](../colados-image-processor/README.md) for setting up and running the Image Processor service.

## How to test

Run the API locally using the following command:

```bash
mvn spring-boot:run
```

The API will be accessible at `http://localhost:8080` and the Swagger UI at `http://localhost:8080/swagger-ui/index.html`.

Please refer to the section [API Guide](#api-guide) below for testing the main endpoints.

## API Guide

This guide provides an overview of the Colados Leaderboard API main endopints for testing purposes.

### Registering users

This endpoint allows registering external users using the `root app user` credentials specified in the `.env` file.

Section: Admin App Users
Endpoint: `POST /api/v1/admin/app-users`

### Authentication (Google OAuth2)

This endpoint allows users to authenticate using their Google accounts. Only registered external users can authenticate.

Section: Authentication
Endpoint: `POST /api/v1/auth/google`

### Create championship

This endpoint allows admin users to create championships.

Section: Championships
Endpoint: `POST /api/v1/championships`

### Game submission

This endpoint allows users to submit game scores and/or game score image. It is possible to include the game score image but request not to process it (i.e., manual score submission only).

Section: Games
Endpoint: `POST /api/v1/games`

### Accepting image processing results

This endpoint allows accepting results that where either entered manually or processed from uploaded images.

Section: Games
Endpoint: `PATCH /api/v1/games/{gameId}/game-results-status`

### Retrieving game results

This endpoint allows retrieving game results by game ID.

Section: Games
Endpoint: `GET /api/v1/games/{gameId}/results`

### Close game

This endpoint allows closing a game by game ID. Once closed, no further score submissions are allowed for that game.

Section: Games
Endpoint: `PATCH /api/v1/games/{gameId}/status-for-edition`

### Retrieving monthly leaderboards

This endpoint allows retrieving monthly leaderboards by championship ID and month/year. Only games that are closed and have their results accepted will be considered for leaderboard generation.

Section: Leaderboards
Endpoint: `GET /api/v1/leaderboards/monthly?championshipId={championshipId}&month={month}&year={year}`