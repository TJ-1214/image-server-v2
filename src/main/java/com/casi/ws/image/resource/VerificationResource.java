package com.casi.ws.image.resource;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import com.casi.ws.image.dao.ImageDao;
import com.casi.ws.image.interceptor.AuthorizationFilter;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;

@Path("/")
@AuthorizationFilter
public class VerificationResource {
	private final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	@Inject
	private ImageDao imageDao;

	@GET
	@Path("verify/{ownerClass}/image/{ownerKey}")
	@Produces(MediaType.APPLICATION_JSON)
	public void findImage(@PathParam("ownerClass") String oClass, @PathParam("ownerKey") String oKey,
			@Suspended AsyncResponse aResponse) {

		CompletableFuture.supplyAsync(() -> imageDao.find(oKey, oClass)).thenApplyAsync(image -> {
			if (image == null) {
				throw new WebApplicationException(Response.Status.NOT_FOUND);
			}
			return image;
		}).thenAcceptAsync(aResponse::resume).exceptionally(ex -> {

			Throwable cause = ex.getCause();
			if (cause instanceof WebApplicationException) {
				aResponse.resume(cause);
			} else {
				aResponse.resume(new WebApplicationException("Error retrieving image data", cause,
						Response.Status.INTERNAL_SERVER_ERROR));
			}
			return null;
		});

	}

	@GET
	@Path("verify/{ownerClass}/image/data/{ownerKey}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public void findImageData(@PathParam("ownerClass") String oClass, @PathParam("ownerKey") String oKey,
			@Suspended AsyncResponse asyncResponse) {

		logger.info("fetching record");

		CompletableFuture.supplyAsync(() -> imageDao.find(oKey, oClass))
				.thenApply(image -> (StreamingOutput) outputStream -> {
					try {
						outputStream.write(image.getData());
						outputStream.flush();
					} catch (IOException e) {
						throw new WebApplicationException("Error streaming image data", e,
								Response.Status.INTERNAL_SERVER_ERROR);
					}
				}).thenAccept(asyncResponse::resume).exceptionally(ex -> {
					Throwable cause = ex.getCause();
					if (cause instanceof WebApplicationException) {
						asyncResponse.resume(cause);
					} else {
						asyncResponse.resume(new WebApplicationException("Error retrieving image data", cause,
								Response.Status.INTERNAL_SERVER_ERROR));
					}
					return null;
				});
	}

	@GET
	@Path("verify/{ownerClass}/images")
	@Produces(MediaType.APPLICATION_JSON)
	public void findAllImageKey(@PathParam("ownerClass") String oClass, @Suspended AsyncResponse asyncResponse) {
		logger.info("fetching record");

		CompletableFuture.supplyAsync(() -> {
			List<String> imagesKey = new ArrayList<>();
			imageDao.findAll(oClass).
			forEach(image -> imagesKey.add(image.getOwnerKey()));
			

			return imagesKey;
		}).thenApply(Response::ok).thenApply(Response.ResponseBuilder::build).exceptionally(ex -> {
			logger.severe("Error fetching image keys: " + ex.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error fetching image keys").build();
		}).thenAccept(asyncResponse::resume);
	}

}
