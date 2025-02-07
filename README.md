# Image Server Microservice

The Image Server is a simple microservice built with Jakarta EE and deployed on an OpenLiberty container. Its primary functionality is to retrieve and save images, with additional support for image format validation and compression. This microservice utilizes the `io.image` library version 1.0.1 to perform these tasks efficiently.

## Features
- **Retrieve Images:** Provides endpoints to fetch images based on owner class and key.
- **Save Images:** Allows uploading and saving images along with metadata.
- **Image Format Validation:** Validates the format of uploaded images to ensure compatibility.
- **Image Compression:** Supports image compression to optimize storage and transmission.
