package com.casi.ws.image.interceptor;

import java.io.IOException;

import com.casi.ws.image.constant.Constant;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
@PreMatching
public class AuthorizationInterceptor implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		String header = requestContext.getHeaderString("Authorization");

		if (header == null) {
			requestContext.abortWith(
					Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized: header is missing").build());
		}

		if (header != null && !header.startsWith("X-Act")) {

			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
					.entity("Unauthorized: Auth-Scheme is invalid or missing").build());
		}

		if (header != null && header.startsWith("X-Act")) {

			header = header.replaceAll("(X-Act|\\s)", "");
			if (!header.equals(Constant.API_KEY)) {
				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
						.entity("Unauthorized: API key is missing or invalid").build());
			}
		}

	}

}
