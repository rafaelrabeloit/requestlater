package com.neptune.api.requestdelayer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.internal.util.Base64;

public class BasicAuthFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext requestContext)
			throws IOException {

		String method = requestContext.getMethod();
		String path = requestContext.getUriInfo().getPath();

		if (method.equals("GET")
				&& (path.equals("application.wadl") || path
						.equals("application.wadl/xsd0.xsd"))) {
			return;
		}

		// OPTIONS is allowed because of CORS
		if (method.equals("OPTIONS")) {
			return;
		}

		// Header Auth
		String auth = requestContext.getHeaderString("Authorization");

		// No Auth, then abort
		if (auth == null) {
			throw new WebApplicationException(Status.UNAUTHORIZED);
		}

		// User and Passowrd
		List<String> lap = Arrays.asList(this.auth(auth));

		// Something wrong...
		if (lap == null || lap.size() != 2) {
			throw new WebApplicationException(Status.UNAUTHORIZED);
		}

		//TODO: database checks
		if (lap.get(0).equals("voyeur_admin") && lap.get(1).equals("123456")) {
			return;
		} else {
			throw new WebApplicationException(Status.UNAUTHORIZED);
		}

	}

	private String[] auth(String authorization) {
		if (authorization != null && authorization.startsWith("Basic")) {
			
			// Authorization: Basic base64credentials
			String base64Credentials = authorization
					.substring("Basic".length()).trim();
			
			String credentials = new String(Base64.decode(base64Credentials
					.getBytes()), Charset.forName("UTF-8"));
			
			// credentials = username:password
			return credentials.split(":", 2);
		}
		return null;
	}
}