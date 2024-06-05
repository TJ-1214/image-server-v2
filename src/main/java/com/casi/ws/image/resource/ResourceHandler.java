package com.casi.ws.image.resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.casi.ws.image.dao.ImageDao;
import com.casi.ws.image.interceptor.AuthorizationFilter;
import com.casi.ws.image.model.Image;

import io.image.util.Format;
import io.image.util.ImageUtil;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.StreamingOutput;

@Path("/v1")
@AuthorizationFilter
public class ResourceHandler {
	

	@Inject
	private ImageDao imageDao;



	@POST
	@Path("/image/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void create(@FormParam("ownerClass") EntityPart ownerClass, @FormParam("ownerKey") EntityPart ownerKey,
			@FormParam("data") EntityPart data, @Suspended AsyncResponse asyncResponse) {

		CompletableFuture.supplyAsync(() -> {
			// Process the uploaded file
			byte[] file = new byte[0];
			try {
				file = data.getContent().readAllBytes();
			} catch (IOException e) {
				throw new WebApplicationException("Persist unsuccessfully", Response.Status.INTERNAL_SERVER_ERROR);
			}
			Format format = ImageUtil.format(file);
			if (format.equals(Format.UNKNOWN)) {
				throw new WebApplicationException("Invalid Format", Response.Status.EXPECTATION_FAILED);
			}
			// Create Image object
			Image image = new Image();
			image.setFileName("Document_type_" + LocalDateTime.now().toString());
			image.setData(file);
			try {
				image.setOwnerKey(new String(ownerKey.getContent().readAllBytes(), StandardCharsets.UTF_8));
				image.setOwnerClass(new String(ownerClass.getContent().readAllBytes(), StandardCharsets.UTF_8));
			} catch (IOException e) {
				throw new WebApplicationException("Persist unsuccessfully", Response.Status.INTERNAL_SERVER_ERROR);
			}

			image.setFileType(format.name());

			// Persist the Image object
			boolean isPersist = imageDao.newImage(image);

			if (isPersist) {
				return Response.status(Response.Status.CREATED).entity("Persist successfully").build();

			}
			throw new WebApplicationException("Persist unsuccessfully", Response.Status.INTERNAL_SERVER_ERROR);

		}).exceptionally(ex -> {
			asyncResponse.resume(ex);
			return null;
		}).thenAcceptAsync(asyncResponse::resume);
	}

	@GET
	@Path("/image/verify/{ownerClass}/{ownerKey}")
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
	@Path("/image/verify/data/{ownerClass}/{ownerKey}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public void findImageData(@PathParam("ownerClass") String oClass, @PathParam("ownerKey") String oKey,
			@Suspended AsyncResponse asyncResponse) {

	
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
	@Path("/images/verify/{ownerClass}")
	@Produces(MediaType.APPLICATION_JSON)
	public void findAllImageKey(@PathParam("ownerClass") String oClass, @Suspended AsyncResponse asyncResponse) {
		

		CompletableFuture.supplyAsync(() -> {
			List<String> imagesKey = new ArrayList<>();
			imageDao.findAll(oClass).
			forEach(image -> imagesKey.add(image.getOwnerKey()));
			

			return imagesKey;
		}).thenApply(Response::ok).thenApply(Response.ResponseBuilder::build).exceptionally(ex -> {
			
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error fetching image keys").build();
		}).thenAccept(asyncResponse::resume);
	}
	
	@PUT
	@Path("/image/update/{ownerClass}/{ownerKey}")
	public void imageUpdate(@PathParam("ownerKey") String oKey,
							@PathParam("ownerClass") String oClass,
							byte[] data,
							@Suspended AsyncResponse asyncResponse)
	{
		CompletableFuture.supplyAsync(() -> imageDao.find(oKey, oClass))
				.thenAccept( result -> {
						result.setData(data);
			
								try
									{
									
									imageDao.update(result);
									}
								catch(RuntimeException e) {
									throw new WebApplicationException("Error Update", 
												e,
												Response.Status.INTERNAL_SERVER_ERROR);
		} } ).thenAccept(asyncResponse::resume).exceptionally(ex -> {
				asyncResponse.resume(ex.getCause());
				return null;}
				);


	}
	
	@DELETE
	@Path("/image/delete/{ownerClass}/{ownerKey}")
	public Response imageDelete(@PathParam("ownerKey") String oKey, @PathParam("ownerClass") String oClass) 
	{
		if (imageDao.delete(oKey, oClass))
		{
			return Response.ok().build();
		}

		throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
	}
	
	
}
