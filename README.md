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
    <version>${image version}</version>
</dependency>
```

## Dependencies
To integrate the Image Server into your project, add the following dependency:
```xml
<dependency>
    <groupId>io.tj</groupId>
    <artifactId>io.image</artifactId>
    <version>${image version}</version>
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

# Release Notes: Version 1.1.3 to 1.2.0

## What's New?

- Added integration tests for easier and more efficient testing.
- Introduced new endpoints.
- Supports multiple images under one owner key.
- Optimized performance.
   
   ---

#  Upload Image
## Endpoint

```
POST v2/image/upload
```

## Description

Uploads an image with associated metadata.

## Parameters
- Media type : `Multipart form`
- `ownerClass` (string): Class of the owner entity.
- `ownerKey` (string): Key of the owner entity.
- `data` (file): Image data in `multipart/form-data` format.

## Response

- **202 Accepted** - Image upload successful.
  - Body: `"Image upload successful"`

### Error Responses

- **417 Expectation Failed** - Invalid image format.
- **500 Internal Server Error** - Persistence error.

---

### Notes:
- Ensure `data` parameter contains valid image data.
- Handles various errors during upload and processing.


# Bulk Image Upload API

## Endpoint

```
POST v2/images/upload
```

## Description

Uploads multiple images with associated metadata in bulk.

## Parameters
- Media type : `Multipart form`
- `attachments` (multipart/form-data): List of attachments containing image data and optional text fields.

## Response

- **202 Accepted** - Bulk image upload successful.
  - Body: `"Image upload successful"`

- **400 Bad Request** - Image upload unsuccessful or invalid request.

### Error Responses

- **417 Expectation Failed** - Invalid image format detected.

---



## Update Image

Updates an existing image.

### Endpoint

```
PUT v2/image/update/{uniqueId}
```

### Parameters
- Media type : `Multipart form`
- `uniqueId` (path parameter): Unique identifier of the image.
- `data` (request body): Updated image data as byte array.

### Response

- **202 Accepted** - Image update successful.
  - Body: `"Image update successful"`

- **400 Bad Request** - Image update unsuccessful or invalid request.

### Error Responses

- **417 Expectation Failed** - Invalid image format detected.

---

## Remove Image

Removes an existing image.

### Endpoint

```
DELETE v2/image/remove/{uniqueId}
```

### Parameters

- `uniqueId` (path parameter): Unique identifier of the image to be removed.

### Response

- **202 Accepted** - Image removal successful.
  - Body: `"Image removal successful"`

- **400 Bad Request** - Image removal unsuccessful or invalid request.

---

## Get Image Keys by Owner

Retrieves keys of all images belonging to a specific owner entity.

### Endpoint

```
GET v2/images/keys/{ownerClass}/{ownerKey}
```

### Parameters

- `ownerClass` (path parameter): Class of the owner entity.
- `ownerKey` (path parameter): Key of the owner entity.

### Response

- **200 OK** - Keys retrieved successfully.
  - Body: Comma-separated list of image keys.

---

## Get Image by ID

Retrieves an image by its unique ID.

### Endpoint

```
GET v2/image/{id}
```

### Parameters

- `id` (path parameter): Unique identifier of the image.

### Response

- **200 OK** - Image retrieved successfully.
  - Body: Image data as binary stream.

- **404 Not Found** - Image with the given ID not found.

---

## Get Images by Owner

Retrieves all images belonging to a specific owner entity.

### Endpoint

```
GET v2/images/{ownerClass}/{ownerKey}
```

### Parameters

- `ownerClass` (path parameter): Class of the owner entity.
- `ownerKey` (path parameter): Key of the owner entity.

### Response

- **200 OK** - Images retrieved successfully.
  - Body: Concatenated binary data of all images separated by a specified separator.

--- 

### Notes:
- Adjust paths and parameters as per your application's endpoint configuration.
- Ensure proper error handling and response codes are implemented for each endpoint.
- Use appropriate content types (`multipart/form-data`, `application/octet-stream`, `text/plain`) for request and response payloads.

---







# Upload Image (Deprecated)

## Endpoint: `/v1/image/upload`
- **Method:** `POST`
- **Content-Type:** `multipart/form-data`
- **Description:** Uploads a new image along with metadata.
- **Request Parameters:**
  - `ownerClass` (form parameter): The class of the owner of the image.
  - `ownerKey` (form parameter): The key of the owner of the image.
  - `data` (form parameter): The image file.
- **Response:**
  - **Status 201 (Created):** Image uploaded successfully.
  - **Status 417 (Expectation Failed):** Invalid image format.
  - **Status 500 (Internal Server Error):** Failed to upload image.
- **Error Response:**
  - **Status 500 (Internal Server Error):** If an unexpected error occurs during the upload process.
- **Description:** This endpoint allows you to upload a new image along with its metadata. Send a POST request with the image file and its associated metadata (owner class and owner key) as form data. The server will process the uploaded file, validate its format, create an Image object with the provided metadata, and attempt to persist it. If successful, it will respond with a 201 status code. If the image format is invalid, it will return a 417 status code. If there's an issue with the request or the upload process, it will return a 500 status code.


# Verify Image (Deprecated)

## Endpoint: `/v1/image/verify/{ownerClass}/{ownerKey}`
- **Method:** `GET`
- **Produces:** `application/json`
- **Description:** Retrieves an image record based on the owner class and owner key.
- **Path Parameters:**
  - `{ownerClass}` (path parameter, string): The class of the owner of the image.
  - `{ownerKey}` (path parameter, string): The key of the owner of the image.
- **Response:**
  - **Status Code:** 200 OK
  - **Content Type:** `application/json`
  - **Body:** Image object representing the retrieved image record.
- **Error Responses:**
  - **Status Code:** 404 Not Found
  - **Description:** If the requested image record was not found, it returns a 404 status code.

## Endpoint: `/v1/image/verify/data/{ownerClass}/{ownerKey}`
- **Method:** `GET`
- **Produces:** `application/octet-stream`
- **Description:** Retrieves the binary data of an image record based on the owner class and owner key.
- **Path Parameters:**
  - `{ownerClass}` (path parameter, string): The class of the owner of the image.
  - `{ownerKey}` (path parameter, string): The key of the owner of the image.
- **Response:**
  - **Status Code:** 200 OK
  - **Content Type:** `application/octet-stream`
  - **Body:** Binary data representing the image.
- **Error Responses:**
  - **Status Code:** 404 Not Found
  - **Description:** If the requested image record was not found, it returns a 404 status code.

## Endpoint: `/v1/images/verify/{ownerClass}`
- **Method:** `GET`
- **Produces:** `application/json`
- **Description:** Retrieves a list of image keys associated with a specific owner class.
- **Path Parameters:**
  - `{ownerClass}` (path parameter, string): The class of the owner of the image.
- **Response:**
  - **Status Code:** 200 OK
  - **Content Type:** `application/json`
  - **Body:** A JSON array containing image keys.
- **Error Responses:**
  - **Status Code:** 500 Internal Server Error
  - **Description:** If an unexpected error occurs during the process, it returns a 500 status code.



  
# Update Image (Deprecated)

## Endpoint: `/v1/image/update/{ownerClass}/{ownerKey}`
- **Method:** `PUT`
- **Description:** Updates an existing image record based on the owner class and owner key.
- **Path Parameters:**
  - `{ownerKey}` (path parameter, string): The key of the owner of the image.
  - `{ownerClass}` (path parameter, string): The class of the owner of the image.
- **Request Body:**
  - `data` (byte array): The updated image data.
- **Response:**
  - **Status 200 (OK):** Returned if the image is successfully updated.
  - **Status 500 (Internal Server Error):** If an unexpected error occurs during the update process.
- **Error Response:**
  - **Status 500 (Internal Server Error):** If the image update fails unexpectedly.
- **Usage:**
  - Send a PUT request to the specified endpoint with the owner class and owner key as path parameters, and the updated image data in the request body. The server will attempt to find the existing image record based on the provided owner class and owner key, update its data with the provided data, and then update the image record in the database. If successful, it will respond with a 200 status code. If the update fails unexpectedly, it will return a 500 status code.

  
# Delete Image (Deprecated)

## Endpoint: `/v1/image/delete/{ownerClass}/{ownerKey}`
- **Method:** `DELETE`
- **Description:** Deletes an image record based on the owner class and owner key.
- **Path Parameters:**
  - `{ownerKey}` (path parameter, string): The key of the owner of the image.
  - `{ownerClass}` (path parameter, string): The class of the owner of the image.
- **Response:**
  - **Status 200 (OK):** Returned if the image is successfully deleted.
  - **Status 500 (Internal Server Error):** If an unexpected error occurs during the deletion process.
- **Error Response:**
  - **Status 500 (Internal Server Error):** If the image deletion fails unexpectedly.
- **Usage:**
  - Send a DELETE request to the specified endpoint with the owner class and owner key as path parameters. The server will attempt to delete the image record associated with the provided owner class and owner key. If successful, it will respond with a 200 status code. If the deletion fails unexpectedly, it will return a 500 status code.


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


### Notes:
- Handles both image and text attachments in `multipart/form-data`.
- Validates and processes each attachment for proper handling and storage.






