# Guía de Despliegue — Buganvilla Tours

## Prerrequisitos

- Docker Desktop 4.x o Docker Engine 24+
- Docker Compose v2
- Git

## Despliegue con Docker Compose

### 1. Clonar y configurar

```bash
git clone <repo-url>
cd DesarrolloWebIntegrado
cp .env.example .env
# Editar .env con las credenciales reales
```

### 2. Compilar el backend

```bash
cd backend
./mvnw package -DskipTests
cd ..
```

### 3. Levantar todos los servicios

```bash
docker compose up --build -d
```

### 4. Verificar que los servicios están sanos

```bash
docker compose ps
# Los 3 servicios deben mostrar "healthy"

# Health check del backend
curl http://localhost:8080/actuator/health

# Frontend
curl http://localhost:80
```

### 5. Verificar login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@buganvilla.com","password":"tu_password"}'
```

## Puertos expuestos

| Servicio | Puerto interno | Puerto host |
|----------|---------------|-------------|
| SQL Server | 1433 | 1433 |
| Backend (Spring Boot) | 8080 | 8080 |
| Frontend (nginx) | 80 | 80 |

## Variables de entorno críticas

Ver `.env.example` para la lista completa. Las mínimas para arrancar:
- `DB_PASSWORD` — contraseña de SQL Server
- `JWT_SECRET` — mínimo 256 bits aleatorios
- `DB_URL` — se sobreescribe automáticamente en Docker para apuntar al servicio `sqlserver`

## Actualizar en producción

```bash
git pull
cd backend && ./mvnw package -DskipTests && cd ..
docker compose up --build -d backend frontend
```

## Ver logs

```bash
docker compose logs -f backend
docker compose logs -f frontend
docker compose logs -f sqlserver
```

## Detener

```bash
docker compose down
# Para eliminar también el volumen de datos (¡destruye la BD!):
docker compose down -v
```
