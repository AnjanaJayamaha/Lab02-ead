# Library API — Docker Lab 03

Prerequisites: Docker Desktop running (Linux containers) and JDK 21 or newer.
The Maven compiler targets Java 21, matching the Docker runtime.

## Build and start

The local `.env` is ignored by Git. On a fresh clone, copy `.env.example` to `.env`
and set `DB_ROOT_PASSWORD` before starting.

```powershell
$env:JAVA_HOME = "$env:USERPROFILE\.jdks\corretto-24.0.2" # Existing JDK on this machine; use your own JDK path elsewhere.
.\mvnw.cmd clean package
docker compose up --build -d
docker compose ps
docker compose logs api
```

Wait for `db` to be healthy and the API log to show `Started LibraryApiApplication`.
Tests use an isolated H2 database; the application uses MySQL.

API: http://localhost:8080/api/books

Swagger: http://localhost:8080/swagger-ui.html

Create a book with POST `/api/books` and JSON such as:

```json
{
  "title": "YOUR_INDEX_NUMBER - Clean Code",
  "author": "Robert C. Martin",
  "isbn": "9780132350884",
  "publishedYear": 2008
}
```

Replace `YOUR_INDEX_NUMBER` with your actual index number. Create at least three books.
GET `/api/books` lists books; GET, PUT and DELETE `/api/books/{id}` act on one book.
POST returns 201, DELETE returns 204, missing books return 404, and invalid input returns 400.
Title and author are required. Published year, if provided, must be positive.
Lab 02's sheet was not supplied; compare the fields with its requirements if they differ.

## Database and persistence checks

In MySQL Workbench connect to `127.0.0.1:3307`, user `root`, using the password in `.env`.
Run `SELECT * FROM library_db.book;`.

Record the books and IDs, then run:

```powershell
docker compose down
docker volume ls
docker compose up -d
```

After MySQL becomes healthy, GET `/api/books` should return the same books and IDs.
`docker compose down -v` deletes the database volume and its data.
Rebuild the JAR and use `docker compose up --build -d` after changing Java code.

## Other lab checkpoints

The standalone MySQL volume experiment in Part B and the expected localhost
connection failure in Part C.4 are separate manual exercises described in the PDF.
Stop the Compose stack before running them if their ports conflict.

Commit the source, Dockerfile, `.dockerignore`, `compose.yaml`, and `.env.example`.
Do not commit `.env`. Push to your existing Lab 02 repository and submit its link on LMS.
