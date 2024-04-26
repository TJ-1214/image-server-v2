package com.casi.ws.image.resource;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.logging.Logger;

import com.casi.ws.image.dao.ImageDao;
import com.casi.ws.image.model.Image;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
public class VerificationResource {
	private final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	@Inject
	private ImageDao imageDao;

	@GET
	@Path("image/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Image findImage(@PathParam("id") String id) {
		logger.info("fetching record");
		Image image = imageDao.find(id);

		if (image == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return image;
	}

	@GET
	@Path("image/data/{id}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public byte[] findImageData(@PathParam("id") String id) {
		logger.info("fetching record");
		Image image = imageDao.find(id);

		if (image != null && image.getData() != null) {
			return image.getData();
		}
		return new byte[] { 0x00 };

	}

	@GET
	@Path("images/{ownerClass}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Image> findImages(@PathParam("ownerClass") String ownerClass) {
		return imageDao.findAll(ownerClass);

	}

}
