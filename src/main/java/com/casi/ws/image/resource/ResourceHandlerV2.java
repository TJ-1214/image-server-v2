package com.casi.ws.image.resource;

import java.io.IOException;
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
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/v2")
@AuthorizationFilter
public class ResourceHandlerV2 {
	
	@Inject
	private ImageDao imageDao;

	
	
    @POST
    @Path("/image/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response create(@FormParam("ownerClass") EntityPart ownerClass,
                           @FormParam("ownerKey") EntityPart ownerKey,
                           @FormParam("data") EntityPart data) { 
    	final byte[] raw ;
		try {
			raw = data.getContent().readAllBytes();
		} catch (IOException e) {
			 throw new WebApplicationException("IO Excepetion: "+e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
		}
    	 Format format = ImageUtil.format(raw);
    	 
    	 if (format.equals(Format.UNKNOWN)) {
             throw new WebApplicationException("Invalid Format", Response.Status.EXPECTATION_FAILED);
         }
    	
    	 final byte[] file = raw;
        // Immediately respond that the item has been received for processing
        CompletableFuture.runAsync(() -> {
            try {
              
                // Create Image object
                Image image = new Image();
                image.setFileName("Document_type_" + LocalDateTime.now().toString());
                image.setData(file);
                image.setOwnerKey(ownerKey.getContent(String.class));
                image.setOwnerClass(ownerClass.getContent(String.class));
                image.setFileType(format.name());

                // Persist the Image object asynchronously
                imageDao.newImage(image);
            } catch (IOException  e) {
                throw new WebApplicationException("Persist unsuccessfully", Response.Status.INTERNAL_SERVER_ERROR);
            }
        });
        
        return Response.status(Response.Status.ACCEPTED).entity("Image upload  for processing").build();
    }

}
