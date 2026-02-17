# be-low-level

Rust implementation of the backend API using Axum.

## Features

- **Login Service**: Email/password authentication with token-based sessions
- **Game Service**: Thread-safe luck-based game with day-based win counter
- **REST API**: Three endpoints (`/api/login`, `/api/logout`, `/api/try_luck`)

## API Endpoints

### POST /api/login
Login with email and password.

**Request:**
```json
{
  "email": "user@example.com",
  "password": "r2isthebest"
}
```

**Response (200):**
```json
{
  "token": "uuid-token-here"
}
```

**Response (401):**
```json
{
  "error": "Invalid email or password"
}
```

### POST /api/logout
Logout and invalidate the current token.

**Headers:**
```
Authorization: Bearer <token>
```

**Response (200):**
```
OK
```

**Response (401):**
```json
{
  "error": "Unauthorized"
}
```

### POST /api/try_luck
Try your luck in the game.

**Headers:**
```
Authorization: Bearer <token>
```

**Response (200):**
```json
{
  "win": true
}
```

**Response (401):**
```json
{
  "error": "Unauthorized"
}
```

## Building

```bash
cargo build --release
```

## Running

```bash
cargo run
```

The server will start on `http://0.0.0.0:4000`

## Testing

```bash
cargo test
```

## Docker

Build the Docker image:
```bash
docker build -t be-low-level .
```

Run the container:
```bash
docker run -p 4000:4000 be-low-level
```
