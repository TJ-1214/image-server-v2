package com.casi.ws.image.resource;

import com.ibm.websphere.security.jwt.InvalidBuilderException;
import com.ibm.websphere.security.jwt.JwtBuilder;
import com.ibm.websphere.security.jwt.JwtException;
import com.ibm.websphere.security.jwt.JwtToken;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/")
public class KeyResource {
	
	@GET
	@Path("key")
	@Produces(MediaType.TEXT_PLAIN)
	public String generatedKey() throws JwtException, InvalidBuilderException {
		JwtToken key = JwtBuilder.create("keyGenerated").
				buildJwt();
		return key.compact();
	}


	

}
