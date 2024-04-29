# Image Server V2 for Jakarta EE 


You can run the application by executing the following command from the directory where this file resides. Please ensure you have installed a [Java SE 8+ implementation](https://adoptium.net/?variant=openjdk8) appropriate for your Jakarta EE version and runtime choice (we have tested with Java SE 8, Java SE 11 and Java SE 17). Note, the [Maven Wrapper](https://maven.apache.org/wrapper/) is already included in the project, so a Maven install is not actually needed. You may first need to execute `chmod +x mvnw`.

```
./mvnw liberty:dev
```

Once the runtime starts, you can access the project at http://localhost:9080.

You can also run the project via Docker. To build the Docker image, execute the following commands from the directory where this file resides. Please ensure you have installed a [Java SE 8+ implementation](https://adoptium.net/?variant=openjdk8) appropriate for your Jakarta EE version/runtime choice and [Docker](https://docs.docker.com/get-docker/) (we have tested with Java SE 8, Java SE 11 and Java SE 17). Note, the [Maven Wrapper](https://maven.apache.org/wrapper/) is already included in the project, so a Maven install is not actually needed. You may first need to execute `chmod +x mvnw`.

```
./mvnw clean package
docker build -t ${project.artifactId}:v1
```

You can then run the Docker image by executing:

```
docker run -it --rm -p 9080:9080 ${project.artifactId}:v1
```

Once the runtime starts, you can access the project at http://localhost:9080/.
