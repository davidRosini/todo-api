# TODO API
- API Backend reactive restful to create todo items.

- Project uses Java 15+, **Kotlin**, springboot, webflux, r2dbc, actuator, springdoc, mapstruct, H2.

- Based on **Gradle** dependency model.

## Index

- [API's configuration](#api-configuration)
  - [Local run](#local-run)
  - [Docker](#docker)
- [API location](#api-location)


## API configuration

- Install **OpenJDK 15** -> http://jdk.java.net/15/
    - Make sure that `JAVA_HOME` is set with correct jdk install path


- Install **Gradle** -> https://gradle.org/install/

### Local run

- Run **gradle** command in the terminal on project root folder:
  ```SHELL
  ./gradlew bootRunLocal
  ```

### Docker

- Install **Docker** -> https://docs.docker.com/engine/install/


- Run command in the terminal on project root folder to build image from project **Dockerfile**:
  ```SHELL
  docker build -t todo-api .
  ```
  

- Then start a **Docker** container with:
  ```SHELL
  docker run --name todo-api -p 8081:8081 todo-api:latest
  ```


## API location

- http://localhost:8081/todo
- http://localhost:8081/health
- http://localhost:8081/swagger-ui.html
