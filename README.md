# TaskManagerApp

## Prerequisites

- Java 21 or later.
- Use the provided Maven wrapper (`./mvnw`). A separate Maven installation is not required.

## Environment variables

The application reads the following variables from `src/main/resources/application.properties`:

- `DB_URL` – JDBC connection string for the database.
- `DB_USERNAME` – database username.
- `DB_PASSWORD` – database password.
- `JWT_SECRET` – secret key used for JWT tokens.
- `MAIL_USERNAME` – username for the SMTP server.
- `MAIL_PASSWORD` – password for the SMTP server.

Set these variables before building or running the application.

## Building

Run the following commands from the `TaskManagerApp` directory:

```bash
cd TaskManagerApp
./mvnw package
```

The compiled JAR will be created under `target/TaskManagerApp-0.0.1-SNAPSHOT.jar`.

## Running the application

Start the application with Maven:

```bash
./mvnw spring-boot:run
```

Alternatively run the packaged JAR:

```bash
java -jar target/TaskManagerApp-0.0.1-SNAPSHOT.jar
```

## Running tests

Execute the unit tests with:

```bash
./mvnw test
```
