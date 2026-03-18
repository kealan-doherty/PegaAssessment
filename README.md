# Reading List Service

The service is built with Java 21, Spring Boot, Spring Data JPA, SQLite, and Docker (version: 29.1.1 ). 

## Persistence Choice
SQLite was chosen as the Database for this assessment because it is a simple and lightweight file-based database. it runs without needing the installation of a database engine like Postgres simplyfing the development and use of my service.Another benefit of using SQLite was it was a simple setup to Presist the Database in Docker. Allowing user data to survive container Restarts. Which I believe was the right choice for my local service. 
## AI Assistance
I used AI assisstence during this assessment in certains area to help improve the overall functionality of my project and save time in certain areas. Like my maven workflo I had my AI agent make changes to the maven.yml file to have them auto run on pushes to main. I did have to add my dev branch to it as well which was something my AI agent missed. Another area I used AI during this assessment to set up my ReadingListItem class to save sometime on basic setup. I did have to make a change which was to remove the @JsonProperty hook which caused my service to break.I also used AI to assist me in editing my Dockerfile to add the SQLite Database to it. which the AI agent handled well and did not need any manual changes from me to function properly. AI was also used to help me write my README on how to start and use my service. But My AI assistence and Data Persistence sections were fully written out by me with no AI assisstence. 

## How To Start

```bash
docker compose up --build
```

The API will be available at `http://localhost:8080`.

To stop the service:

```bash
docker compose down 
```

To remove the persisted SQLite data as well:

```bash
docker compose down -v
```

## Running Tests

```bash
./mvnw test
```

## Cross-Platform Notes

- Docker commands shown in this README work on Linux, macOS, and Windows when Docker is installed.
- The Maven wrapper command above is for bash-style shells (Linux, macOS, Git Bash, WSL).
- In Windows PowerShell or CMD, use:

```powershell
.\mvnw.cmd test
```

- In Windows PowerShell, prefer `curl.exe` for HTTP examples to avoid PowerShell alias behavior:

```powershell
curl.exe "http://localhost:8080/items"
```

## API Usage

All endpoints use query parameters.

### List all items

```bash
curl "http://localhost:8080/items"
```

### Filter items by title and/or author

```bash
curl "http://localhost:8080/items?title=Dune&author=Herbert"
```

### Get one item by ID

```bash
curl "http://localhost:8080/getById?id=1"
```

### Create an item

```bash
curl -X POST "http://localhost:8080/add?title=Dune&author=Frank%20Herbert&notes=Sci-fi%20classic&readStatus=false"
```

### Update title

```bash
curl -X PUT "http://localhost:8080/updateTitle?id=1&newTitle=Dune%20Messiah"
```

### Update author

```bash
curl -X PUT "http://localhost:8080/updateAuthor?id=1&newAuthor=Frank%20Herbert"
```

### Update notes

```bash
curl -X PUT "http://localhost:8080/updateNotes?id=1&newNotes=Great%20sequel"
```

### Update read status

```bash
curl -X PUT "http://localhost:8080/updateReadStatus?id=1&newReadStatus=true"
```

### Delete an item

```bash
curl -X DELETE "http://localhost:8080/delete?id=1"
```

## Error Handling

Validation and lookup failures return JSON responses with a field name and message, for example:

```json
{
	"field": "id",
	"message": "ID is required and must be greater than 0"
}
```
