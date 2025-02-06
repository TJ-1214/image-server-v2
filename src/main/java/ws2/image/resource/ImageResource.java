package ws2.image.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import ws2.image.dao.ImageDao;
import ws2.image.model.Image;
import ws2.image.util.Format;
import ws2.image.util.ImageUtil;

@Path("/v1")
public class ImageResource {

	@Inject
	private ImageDao imageDao;

	@POST
	@Path("/image/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response create(MultipartFormDataInput input) {
		// Retrieve the parts from the multipart form data.
		Map<String, List<InputPart>> formParts = input.getFormDataMap();

		// Retrieve the ownerClass field.
		String ownerClass;
		try {
			ownerClass = formParts.get("ownerClass").get(0).getBodyAsString();
		} catch (Exception e) {
			throw new WebApplicationException("Error reading ownerClass: " + e.getMessage(),
					Response.Status.BAD_REQUEST);
		}

		// Retrieve the ownerKey field.
		String ownerKey;
		try {
			ownerKey = formParts.get("ownerKey").get(0).getBodyAsString();
		} catch (Exception e) {
			throw new WebApplicationException("Error reading ownerKey: " + e.getMessage(), Response.Status.BAD_REQUEST);
		}

		// Retrieve the file/data field.
		byte[] raw;
		try {
			InputPart filePart = formParts.get("data").get(0);
			// Option 1: Direct conversion to a byte array
			raw = filePart.getBody(byte[].class, null);
			// Option 2: Alternatively, if you need to work with streams, you can use:
			// InputStream inputStream = filePart.getBody(InputStream.class, null);
			// raw = inputStream.readAllBytes();
		} catch (IOException e) {
			throw new WebApplicationException("IO Exception: " + e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new WebApplicationException("Error reading file data: " + e.getMessage(),
					Response.Status.BAD_REQUEST);
		}

		// Validate the image format.
		Format format = ImageUtil.format(raw);
		if (format.equals(Format.UNKNOWN)) {
			throw new WebApplicationException("Invalid Format", Response.Status.EXPECTATION_FAILED);
		}

		// Create and populate the Image object.
		Image image = new Image();
		image.setFileName("Document_type_" + LocalDateTime.now());
		image.setData(raw);
		image.setOwnerKey(ownerKey);
		image.setOwnerClass(ownerClass);
		image.setFileType(format.name());

		// Persist the image using the DAO.
		imageDao.newImage(image);

		return Response.status(Response.Status.ACCEPTED).entity("Image upload successful").build();
	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("/images/{ownerClass}/{ownerKey}")
	@Produces(MediaType.MULTIPART_FORM_DATA)
	public Response findImages(@PathParam("ownerClass") String ownerClass, @PathParam("ownerKey") String ownerKey) {

		MultipartFormDataOutput output = new MultipartFormDataOutput();

		// Assuming imageDao returns Stream<Image>
		@SuppressWarnings("rawtypes")
		Stream images = imageDao.findImages(ownerKey, ownerClass);

		images.forEach(imageStream -> {
			Image image = (Image) imageStream;

			InputStream readImage = new ByteArrayInputStream(image.getData());

			output.addFormData("data", readImage, MediaType.APPLICATION_OCTET_STREAM_TYPE);

		});
		if (output.getParts().isEmpty()) {
			return Response.status(Response.Status.NO_CONTENT).entity("No image/s found").build();
		}

		return Response.ok().entity(output).build();

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
	@Path("/images/keys/{ownerClass}/{ownerKey}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response findKeys(@PathParam("ownerClass") String ownerClass, @PathParam("ownerKey") String ownerKey) {
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

}
