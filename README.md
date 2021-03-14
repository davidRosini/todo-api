# TODO CRUD API
- API Backend reactive restful to create todo items.

- Project uses Java 15+, Kotlin, Springboot, webflux, r2dbc, actuator, Springdoc, mapstruct, H2.

- Based on Gradle dependency model.

## API's configuration

- Install OpenJDK 15 -> http://jdk.java.net/15/
    - Make sure that ```JAVA_HOME``` is set with correct jdk install path


- Install Gradle -> https://gradle.org/install/

### Local run

- Run gradle command in the terminal on project root folder:
  ```gradlew bootRunLocal```

### Docker

- Install Docker -> https://docs.docker.com/engine/install/


- Run command in the terminal on project root folder to build image from project Dockerfile:
  ```docker build -t todo-api .```
  

- Then start a Docker container with:
  ```docker run --name todo-api -p 8081:8081 todo-api:latest```


## API location 

- http://localhost:8081/
- http://localhost:8081/health
- http://localhost:8081/swagger-ui.html
