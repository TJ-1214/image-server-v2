package com.casi.ws.image.interceptor;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.ibm.websphere.security.jwt.Claims;
import com.ibm.websphere.security.jwt.InvalidConsumerException;
import com.ibm.websphere.security.jwt.InvalidTokenException;
import com.ibm.websphere.security.jwt.JwtConsumer;
import com.ibm.websphere.security.jwt.JwtToken;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
@AuthorizationFilter
public class AuthorizationInterceptor implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		String header = requestContext.getHeaderString("Authorization");
		
		if (header == null) {
			requestContext.abortWith(
					Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized: header is missing").build());
		}
	

		if (header != null && !header.startsWith("Bearer")) {

			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
					.entity("Unauthorized: Auth-Scheme is invalid or missing").build());
		}

		if (header != null && header.startsWith("Bearer")) {
			header = header.replaceAll("(Bearer|\\s)", "");
		
			try {
				
				JwtToken token = JwtConsumer.create().createJwt(header);
				Claims claims = token.getClaims();

			
				LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(claims.getExpiration()),
						ZoneId.systemDefault());
				
				
				if (dateTime.isBefore(LocalDateTime.now(ZoneId.systemDefault()) )) {
					requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
							.entity("Unauthorized: Token Expired").build());
				}
				

			} catch (InvalidTokenException e  ) {
				
				requestContext.abortWith(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity("Server Error: Invalid Token").build());
			}catch(InvalidConsumerException e)
			{
				requestContext.abortWith(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity("Server Error: Invalid Consumer").build());
			}

		}

	}

}
