# image-fetch

A Spring Boot microservice that fetches random pictures of cats, dogs or bears
from given image urls, stores them in Postgres, and serves the most recently stored one.

## Included tests

- image fetch/save logic with the external provider stubbed
- web layer service test for last_picture and fetch_and_save
- uses postgres:latest Testcontainer setup

## UI

Shows up a basic ui once application runs

## Run

Isolated the containerised run at `port 8080` and spring boot run at `port 8081` so that both can be tested

With Docker (includes postgres 17):

```sh
docker compose up --build        # then open http://localhost:8080/
docker compose down -v           # stops and removes the containers and resources
```

Without Docker (needs JDK 21+ and a Postgres on `localhost:5436`, db `images`, user/password `postgres`/`postgres`):

```sh
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081   # then open http://localhost:8081/
```

In the UI, pick an animal, choose how many to fetch, click **Fetch & Save**, then **Show Last**.

## Using the REST APIs

```sh
POST /api/pictures/{type}?count=N  ->  Fetch `N` (default 1) pictures of `{type}` from the provider and save them. Returns JSON metadata.
```

```sh
GET  /api/pictures/{type}/last     ->  Return the raw bytes of the most recently stored `{type}` picture.
```

`{type}` is one of `cat`, `dog`, `bear`.

- Simple UI: <http://localhost:8080/>
- OpenAPI / Swagger UI: <http://localhost:8080/swagger-ui.html>

### Example

```sh
# fetch and save 3 cat pictures
curl -X POST "http://localhost:8080/api/pictures/cat?count=3"

# download the most recent cat picture
curl "http://localhost:8080/api/pictures/cat/last" --output last-cat.jpg
```

## Build and test

```sh
./mvnw package     # self-contained jar in the target folder
TESTCONTAINERS_RYUK_DISABLED=true ./mvnw test        # unit tests + a Testcontainers context-load test (needs Docker running)
```

## Configuration

Image providers are configured per animal type in `src/main/resources/application.properties`
(`animal.sources.*`). `{w}`/`{h}` placeholders are replaced with random dimensions so repeated
fetches return different pictures:

```properties
animal.sources.cat=https://cataas.com/cat
animal.sources.dog=https://place.dog/{w}/{h}
animal.sources.bear=https://placebear.com/{w}/{h}
```
