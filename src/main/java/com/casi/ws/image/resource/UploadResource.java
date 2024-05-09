package com.casi.ws.image.resource;

import java.io.IOException;
import java.time.LocalDateTime;

import com.casi.ws.image.dao.ImageDao;
import com.casi.ws.image.model.Image;
import com.casi.ws.image.util.ImageFormat;
import com.casi.ws.image.util.ImageFormatDetector;
import com.casi.ws.image.util.ImageUtil;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
public class UploadResource {

	@Inject
	private ImageDao imageDao;

	@POST
	@Path("upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response create(@FormParam("ownerClass") EntityPart ownerClass, @FormParam("ownerKey") EntityPart ownerKey,
			@FormParam("data") EntityPart data) throws IOException {
		byte[] file = data.getContent().readAllBytes();
		ImageFormat format = ImageFormatDetector.detect(file);
		boolean isInvalidFormat = format.equals(ImageFormat.UNKNOWN);

		if (isInvalidFormat) {
			return Response.status(Response.Status.EXPECTATION_FAILED).entity("Invalid Format").build();
		}
		System.out.println("original Size: " + file.length);

		Image n = new Image();
		n.setFileName("Document_type_" + LocalDateTime.now().toString());
		n.setData(ImageUtil.compress(file)  );
		n.setOwnerClass(ownerClass.getContent(String.class));
		n.setOwnerKey(ownerKey.getContent(String.class));
		n.setFileType(format.name());

	
		System.out.println(n.getFileType()+ " compressed Size: " + n.getData().length);

		boolean isPersist = imageDao.newImage(n);

		if (isPersist) {
			return Response.status(Response.Status.CREATED).entity("Persist successfully").build();
		}
		return Response.status(Response.Status.BAD_REQUEST).entity("Persist unsuccessfully").build();
	}
	
	
	

}
