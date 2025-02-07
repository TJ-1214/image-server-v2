package ws2.image.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.RestClientDefinitionException;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.Response;
import ws2.image.resource.ImageResourceInterface;

public class ServiceTest {

	private ImageResourceInterface service;

	private static final String HOST_URL = "http://localhost:8080";

	public ServiceTest() throws IllegalStateException, RestClientDefinitionException, MalformedURLException {
		service = RestClientBuilder.newBuilder().baseUrl(new URL(HOST_URL + "/v1")).build(ImageResourceInterface.class);
	}

	@Test
	public void removeImage() {
		Response response = service.remove("test");

		assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
	}

}
