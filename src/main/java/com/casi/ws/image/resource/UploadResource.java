package com.casi.ws.image.resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import com.casi.ws.image.dao.ImageDao;
import com.casi.ws.image.interceptor.AuthorizationFilter;
import com.casi.ws.image.model.Image;

import io.image.util.Format;
import io.image.util.ImageUtil;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
@AuthorizationFilter
public class UploadResource {

	@Inject
	private ImageDao imageDao;

	@POST
	@Path("upload")
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

}
