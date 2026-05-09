Docker Compose setup — running all services

Prerequisites
- Docker Engine and Docker Compose installed and running on your machine.
- Copy `.env.example` to `.env` and edit values as needed.

Quick start
1. From the project root, build and start all services:

```bash
docker-compose up --build
```

2. To run in detached mode:

```bash
docker-compose up --build -d
```

3. To stop and remove containers:

```bash
docker-compose down
```

Notes
- The `backend` service builds from `./backend/Dockerfile` and expects the Spring Boot app to run on port 8080 inside the container.
- The `ai-service` builds from `./ai-service/Dockerfile` and exposes port 5000.
- The `frontend` builds from `./frontend/Dockerfile` and exposes port 5173.
- Healthchecks are configured; if a service depends on another, Docker Compose will attempt to start dependencies first, but you should ensure your services retry connecting until dependencies are healthy.

If you want, I can also create minimal Dockerfiles for `backend`, `ai-service`, and `frontend` to make the stack runnable immediately.
