package ws2.image.resource;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;


@Path("/v1")
public interface ImageResourceInterface {

	
	@POST
	@Path("/image/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response create(MultipartFormDataInput input);

	

	
	@GET
	@Path("/images/{ownerClass}/{ownerKey}")
	@Produces(MediaType.MULTIPART_FORM_DATA)
    public Response findImages(@PathParam("ownerClass") String ownerClass, @PathParam("ownerKey") String ownerKey);

	
	@Path("/image/update/{uniqueId}")
	@PUT
	public  Response update(@PathParam("uniqueId") String uniqueId, byte[] data);

	@Path("/image/remove/{uniqueId}")
	@DELETE
	public Response remove(@PathParam("uniqueId") String uniqueId);

	
	@GET
	@Path("/image/{id}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public   Response findImage(@PathParam("id") String id);

	
	@GET
	@Path("/images/keys/{ownerClass}/{ownerKey}")
	@Produces(MediaType.TEXT_PLAIN)
	public   Response findKeys(@PathParam("ownerClass") String ownerClass, @PathParam("ownerKey") String ownerKey);
}
