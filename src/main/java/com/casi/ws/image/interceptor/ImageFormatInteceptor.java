/*
 * package com.casi.ws.image.interceptor;
 * 
 * import java.io.IOException; import java.io.InputStream;
 * 
 * import com.casi.image.interceptor.binding.ImageFormatBinding; import
 * com.casi.ws.image.util.ImageFormat; import
 * com.casi.ws.image.util.ImageFormatDetector;
 * 
 * import jakarta.ws.rs.container.ContainerRequestContext; import
 * jakarta.ws.rs.container.ContainerRequestFilter; import
 * jakarta.ws.rs.core.Response; import jakarta.ws.rs.ext.Provider;
 * 
 * @Provider
 * 
 * @ImageFormatBinding public class ImageFormatInteceptor implements
 * ContainerRequestFilter,Contextr {
 * 
 * @Override public void filter(ContainerRequestContext requestContext) throws
 * IOException {
 * 
 * if (requestContext.getProperty("name")!=null) { requestContext.abortWith(
 * Response.status(Response.Status.EXPECTATION_FAILED).entity("Format invalid").
 * build()); // InputStream stream = requestContext.getEntityStream(); // byte[]
 * imageData = stream.readAllBytes(); // // // ImageFormat format =
 * ImageFormatDetector.detect(imageData); // boolean isInvalidFormat =
 * format.equals(ImageFormat.UNKNOWN); // // if (isInvalidFormat) { //
 * requestContext.abortWith( //
 * Response.status(Response.Status.EXPECTATION_FAILED).entity("Format invalid").
 * build()); // } } // requestContext.abortWith( //
 * Response.status(Response.Status.BAD_REQUEST).entity("bad request").build());
 * }
 * 
 * }
 */