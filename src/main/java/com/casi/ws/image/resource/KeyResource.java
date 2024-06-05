package com.casi.ws.image.resource;

import java.util.concurrent.CompletableFuture;

import com.ibm.websphere.security.jwt.InvalidBuilderException;
import com.ibm.websphere.security.jwt.JwtBuilder;
import com.ibm.websphere.security.jwt.JwtException;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
public class KeyResource {

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("key")
	public void generatedKey(@Suspended AsyncResponse asyncResponse) {
		CompletableFuture.supplyAsync(() -> {
			try {
				return JwtBuilder.create().buildJwt().compact();
			} catch (JwtException | InvalidBuilderException e) {
				throw new RuntimeException(e);
			}
		}).thenApplyAsync(key -> {
			// Handle the successful creation of the JWT key
			return Response.ok(key).build();
		}).exceptionally(throwable -> {
			// Handle any exceptions that occurred during the asynchronous processing
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error generating key: " + throwable.getMessage()).build();
		}).thenAccept(asyncResponse::resume);
	}


}
