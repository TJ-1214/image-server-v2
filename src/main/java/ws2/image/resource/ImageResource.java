package ws2.image.resource;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ws2.image.dao.ImageDao;
import ws2.image.model.Image;
import ws2.image.util.Format;
import ws2.image.util.ImageUtil;

@Path("/v2")
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

}
