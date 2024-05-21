# Image Server Microservice

The Image Server is a simple microservice built with Jakarta EE and deployed on an OpenLiberty container. Its primary functionality is to retrieve and save images, with additional support for image format validation and compression. This microservice utilizes the `io.image` library version 1.0.1 to perform these tasks efficiently.

## Features
- **Retrieve Images:** Provides endpoints to fetch images based on owner class and key.
- **Save Images:** Allows uploading and saving images along with metadata.
- **Image Format Validation:** Validates the format of uploaded images to ensure compatibility.
- **Image Compression:** Supports image compression to optimize storage and transmission.

## Dependencies
To integrate the Image Server into your project, add the following dependency:
```xml
<dependency>
    <groupId>io.tj</groupId>
    <artifactId>io.image</artifactId>
    <version>1.0.1</version>
</dependency>
```

## Dependencies
To integrate the Image Server into your project, add the following dependency:
```xml
<dependency>
    <groupId>io.tj</groupId>
    <artifactId>io.image</artifactId>
    <version>1.0.1</version>
</dependency>
```

## Before Running the Application

Before running the Image Server application, make sure to complete the following steps:

1. **Install PostgreSQL:**
   PostgreSQL must be installed on your machine. You can download PostgreSQL from [here](https://www.postgresql.org/download/). Choose the appropriate download based on your operating system.

2. **Configure Server XML:**
   Open the `server.xml` file located under the folder `src/main/liberty/config/`. Replace the following credentials with your PostgreSQL database connection details:
   - `serverName`
   - `portNumber`
   - `user`
   - `password`

   These configurations are necessary to establish a connection with the PostgreSQL database where image data will be stored.

 On main project folder, execute `chmod +x mvnw`.

```
./mvnw liberty:dev
```

---

   

## Endpoints
### Retrieve Image
- **Endpoint:** `GET /{ownerClass}/image/{ownerKey}`
  - Retrieves an image based on the owner class and key.
### Save Image
- **Endpoint:** `POST /upload`
  - Saves an uploaded image along with metadata.
### Image Format Validation
- **Endpoint:** `POST /validate`
  - Validates the format of an uploaded image.
### Image Compression
- **Endpoint:** `POST /compress`
  - Compresses an image to optimize storage and transmission.

## Usage
1. Send a GET request to retrieve an image.
2. Upload an image using a POST request to save it.
3. Use the validation endpoint to check the format of an image.
4. Compress images to reduce their size.

## Deployment
The Image Server is designed to be deployed on an OpenLiberty container. Simply package the application into a WAR file and deploy it to your OpenLiberty server.


## Note
Ensure that the `io.image` library version 1.0.1 is included in your project's dependencies to utilize the Image Server's functionality effectively.



# Persist Image

## Endpoint: `/upload`
- **Method:** `POST`
- **Content-Type:** `multipart/form-data`
- **Parameters:**
  - `ownerClass`: The owner class of the image.
  - `ownerKey`: The owner key of the image.
  - `data`: The image file.
- **Response:**
  - **Status 201 (Created):** Image uploaded successfully.
  - **Status 417 (Expectation Failed):** Invalid image format.
  - **Status 400 (Bad Request):** Failed to upload image.
- **Description:** This endpoint allows you to upload an image along with its metadata. Send a POST request with the image file and its associated metadata (owner class and owner key) as form data. The server will validate the image format and attempt to upload it. If successful, it will respond with a 201 status code. If the image format is invalid, it will return a 417 status code. If there's an issue with the request, it will return a 400 status code.

## Fetch Image

### Endpoint: `GET /verify/{ownerClass}/image/data/{ownerKey}`
- **Description:** Retrieves the binary data of an image record based on the owner class and owner key.
- **Request Parameters:**
  - `{ownerClass}` (path parameter, string): The class of the owner of the image.
  - `{ownerKey}` (path parameter, string): The key of the owner of the image.
- **Response:**
  - **Content Type:** `application/octet-stream`
  - **Body:** Binary data representing the image.
- **Success Response:**
  - **Status Code:** 200 OK
  - **Body:** Binary data representing the image.
- **Error Responses:**
  - **Status Code:** 404 Not Found
  - **Body:** Error message indicating that the requested image record was not found.

### Endpoint: `GET /verify/{ownerClass}/image/{ownerKey}`
- **Description:** Retrieves an image record based on the owner class and owner key.
- **Request Parameters:**
  - `{ownerClass}` (path parameter, string): The class of the owner of the image.
  - `{ownerKey}` (path parameter, string): The key of the owner of the image.
- **Response:**
  - **Content Type:** `application/json`
  - **Body:** Image object representing the retrieved image record.
- **Success Response:**
  - **Status Code:** 200 OK
  - **Body:** Image object representing the retrieved image record.
- **Error Responses:**
  - **Status Code:** 404 Not Found
  - **Body:** Error message indicating that the requested image record was not found.

### Endpoint: `GET /verify/{ownerClass}/images`
- **Description:** Retrieves a list of image keys associated with a specific owner class.
- **Request Parameters:**
  - `{ownerClass}` (path parameter, string): The class of the owner of the image.
- **Response:**
  - **Content-Type:** `application/json`
  - **Body:** A JSON array containing image keys.
- **Status Codes:**
  - **200 OK:** Returned on successful retrieval of image keys.
  - **404 Not Found:** If no images are found for the specified owner class.
  - **500 Internal Server Error:** If an unexpected error occurs during the process.

## Implementation Details

- This endpoint retrieves image keys associated with the specified owner class from the database.
- It returns a JSON array containing the image keys.
- If no images are found for the specified owner class, an empty array is returned.
- Any errors that occur during the process are logged internally.

# Generate Token Access

## API Documentation

### Overview

This API provides functionality for generating JWT (JSON Web Token) keys.

### Base URL

The base URL for accessing this API is `[base_url]`. Replace `[base_url]` with the actual base URL where the API is deployed.

### Authentication

This API endpoint does not require authentication.

### Endpoint

#### Generate JWT Key

- **URL:** `/key`
- **Method:** `GET`
- **Description:** Generates a JWT key with the specified subject.
- **Request Parameters:** None
- **Request Headers:** None
- **Response:**
  - **Content Type:** `text/plain`
  - **Body:** Compact representation of the generated JWT key.
- **Example Request:**
  ```http
  GET [base_url]/key
  ```
- **Example Response:**
  ```plaintext
  eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJrZXlHZW5lcmF0ZWQiLCJleHAiOjE2MjA5MjQyOTF9.HDgBCs-EfKl1hCtJlK7EPlXN3zl4-2ghSICfB4m2N4k
  ```

### Errors

- **500 Internal Server Error:** If an internal server error occurs while generating the JWT key.

### Libraries Used

- **JAX-RS (Java API for RESTful Web Services):** Used for defining RESTful web services in Java.
- **JWT (JSON Web Token) Library:** Used for generating JSON Web Tokens.

---

Remember to replace `[base_url]` with the actual base URL where your API is deployed.
