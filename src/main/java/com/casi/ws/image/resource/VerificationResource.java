package com.casi.ws.image.resource;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.casi.ws.image.dao.ImageDao;
import com.casi.ws.image.interceptor.AuthorizationFilter;
import com.casi.ws.image.model.Image;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
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
	public Image findImage(@PathParam("ownerClass") String oClass, @PathParam("ownerKey") String oKey) {
		logger.info("fetching record");
		Image image = imageDao.find(oKey, oClass);
		
		if (image == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return image;
	}

	@GET
	@Path("verify/{ownerClass}/image/data/{ownerKey}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public StreamingOutput findImageData(@PathParam("ownerClass") String oClass, @PathParam("ownerKey") String oKey) {
		logger.info("fetching record");
		Image image = imageDao.find(oKey, oClass);

		if (image != null && image.getData() != null) {
			return outputStream -> {
				try {
					// automatically flush
					outputStream.write(image.getData());

				} catch (IOException e) {
					throw new WebApplicationException("Error streaming image data", e,
							Response.Status.INTERNAL_SERVER_ERROR);
				}
				
			};
			
		}
		
		throw new WebApplicationException(Response.Status.NOT_FOUND);
	}
	


	@GET
	@Path("verify/{ownerClass}/images")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> findAllImageKey(@PathParam("ownerClass") String oClass) {
		

		
		logger.info("fetching record");
		List<Image> images = imageDao.findAll(oClass);

		List<String> imagesKey = new ArrayList<>();

		for (Image image : images) {
			imagesKey.add(image.getOwnerKey());
		}

		return imagesKey;

	}


	
}
