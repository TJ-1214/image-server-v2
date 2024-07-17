package com.casi.ws.image.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import com.casi.ws.image.dao.ImageDao;
import com.casi.ws.image.interceptor.AuthorizationFilter;
import com.casi.ws.image.model.Image;
import com.ibm.websphere.jaxrs20.multipart.IAttachment;

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
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;

@Path("/v2")
@AuthorizationFilter
public class ResourceHandlerV2 {
	
	@Inject
	private ImageDao imageDao;

	
	
	@POST
	@Path("/image/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response create(@FormParam("ownerClass") EntityPart ownerClass, @FormParam("ownerKey") EntityPart ownerKey,
			@FormParam("data") EntityPart data) {

		final byte[] raw;
		try {
			raw = data.getContent().readAllBytes();
		} catch (IOException e) {
			throw new WebApplicationException("IO Excepetion: " + e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		}
		Format format = ImageUtil.format(raw);

		if (format.equals(Format.UNKNOWN)) {
			throw new WebApplicationException("Invalid Format", Response.Status.EXPECTATION_FAILED);
		}

		try {

			// Create Image object
			Image image = new Image();
			image.setFileName("Document_type_" + LocalDateTime.now().toString());
			image.setData(raw);
			image.setOwnerKey(ownerKey.getContent(String.class));
			image.setOwnerClass(ownerClass.getContent(String.class));
			image.setFileType(format.name());

			imageDao.newImage(image);
			return Response.status(Response.Status.ACCEPTED).entity("Image upload succesful").build();
		} catch (IOException e) {
			throw new WebApplicationException("Persist unsuccessfully", Response.Status.INTERNAL_SERVER_ERROR);

		}
	}

	@POST
	@Path("/images/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response bulkCreate(List<IAttachment> attachments) throws IOException {

		List<Image> images = new ArrayList<>();
		Map<String, String> fields = new ConcurrentHashMap<>();
		Set<byte[]> rawImages = ConcurrentHashMap.newKeySet();

		attachments.forEach(attachment -> {
			try {
				if (attachment.getDataHandler().getContentType().equals(MediaType.TEXT_PLAIN)) {
					fields.put(attachment.getDataHandler().getName(),
							new String(attachment.getDataHandler().getInputStream().readAllBytes()));
				} else {
					rawImages.add(attachment.getDataHandler().getInputStream().readAllBytes());
				}
			} catch (IOException e) {
				throw new WebApplicationException("Error reading attachment data", e,
						Response.Status.INTERNAL_SERVER_ERROR);
			}
		});

		rawImages.stream().map(rawImage -> {
			Image image = new Image();
			Format format = ImageUtil.format(rawImage);
			if (format.equals(Format.UNKNOWN)) {
				throw new WebApplicationException("Invalid Format", Response.Status.EXPECTATION_FAILED);
			}
			image.setFileName("Document_type_" + LocalDateTime.now().toString());
			image.setData(rawImage);
			image.setFileType(format.name());
			return image;
		}).forEach(images::add);

		images.forEach(image -> {
			fields.forEach((key, value) -> {
				try {
					Field field = Image.class.getDeclaredField(key);
					field.setAccessible(true);
					field.set(image, value);
				} catch (IllegalAccessException | IllegalArgumentException e) {
					throw new WebApplicationException("Invalid Format", Response.Status.BAD_REQUEST);
				} catch (NoSuchFieldException e) {
//                        TODO: add loggings when no such field or not existing
				}
			});
		});

		if (imageDao.bulkUpload(images)) {
			return Response.status(Response.Status.ACCEPTED).entity("Image upload succesful").build();
		}

		return Response.status(Response.Status.BAD_REQUEST).entity("Image upload unsuccesful").build();

	}
            
           
            	
			@Path("/image/update")
			@PUT
			public Response update(@PathParam("uniqueId") String uniqueId, byte[] data) {
				Image image = imageDao.find(uniqueId);

				Format format = ImageUtil.format(data);
				if (format.equals(Format.UNKNOWN)) {
					throw new WebApplicationException("Invalid Format", Response.Status.EXPECTATION_FAILED);
				}
				image.setData(data);
				image.setFileType(format.name());

				if (imageDao.updateImage(image)) {
					return Response.status(Response.Status.ACCEPTED).entity("Image update succesful").build();
				}
				return Response.status(Response.Status.BAD_REQUEST).entity("Image update unsuccesful").build();
			}
			
			@Path("/image/remove")
			@DELETE
			public Response remove(@PathParam("uniqueId") String uniqueId) {
				Image image = imageDao.find(uniqueId);

				if (imageDao.delete(image)) {
					return Response.status(Response.Status.ACCEPTED).entity("Image remove succesful").build();
				}
				return Response.status(Response.Status.BAD_REQUEST).entity("Image update unsuccesful").build();
			}
			
			
			
			@GET
			@Path("/images/keys/{ownerClass}/{ownerKey}")
			@Produces(MediaType.TEXT_PLAIN)
			public Response findKeys(@PathParam("ownerClass") String ownerClass,
					@PathParam("ownerKey") String ownerKey) {
				List<Image> images = imageDao.findAll(ownerClass, ownerKey);
				List<String> keys = new ArrayList<>();

				if (images != null && !images.isEmpty()) {
					images.forEach(i -> keys.add(i.getId().toString()));
				}

				StreamingOutput output = new StreamingOutput() {

					@Override
					public void write(OutputStream output) throws IOException, WebApplicationException {

						
							try {
								output.write(keys.toString().getBytes());
								
								output.flush();
							} catch (IOException e) {
								throw new WebApplicationException(Response.Status.BAD_REQUEST);
							
						}

					}
				};
				return Response.ok().entity(output).build();

			}
			
			
			@GET
			@Path("/image/{id}")
			@Produces(MediaType.APPLICATION_OCTET_STREAM)
			public Response findImage(@PathParam("id") String id) {
				Image image = imageDao.find(id);

				if (image != null) {
					StreamingOutput streamData = new StreamingOutput() {

						@Override
						public void write(OutputStream output) throws IOException, WebApplicationException {
							output.write(image.getData());
							output.flush();

						}
					};

					return Response.ok(streamData).build();
				}
				return Response.status(Response.Status.NOT_FOUND).entity("Image not found").build();

			}
			
			@GET
			@Path("/images/{ownerClass}/{ownerKey}")
			@Produces(MediaType.APPLICATION_OCTET_STREAM)
			public Response findImages(@PathParam("ownerClass") String ownerClass,
			                           @PathParam("ownerKey") String ownerKey) {
			    String separator = "\n--image-separator--\n";
			   
			    // Assuming imageDao returns Stream<Image>
			    Stream  images = imageDao.findImages(ownerKey, ownerClass);

			   
			    StreamingOutput output = new StreamingOutput() {
			        @Override
			        public void write(OutputStream outputStream) throws IOException, WebApplicationException {
			            // Use try-with-resources to ensure the stream is properly closed
			            try (Stream<Image> stream = images) {
			                stream.forEach(image -> {
			                    try {
			                        outputStream.write(image.getData());
			                        outputStream.write(separator.getBytes());
			                    } catch (IOException e) {
			                        throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
			                    }
			                });
			                outputStream.flush();
			            }
			        }
			    };

			    return Response.ok().entity(output).build();
			}

    }


