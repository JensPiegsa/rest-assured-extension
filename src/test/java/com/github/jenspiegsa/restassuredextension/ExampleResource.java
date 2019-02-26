package com.github.jenspiegsa.restassuredextension;

import java.util.logging.Logger;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Jens Piegsa
 */
@Path("example")
@Consumes(MediaType.APPLICATION_JSON)
public class ExampleResource {

	@Inject Logger log;
	@Inject ExampleService service;

	@GET @Produces(MediaType.APPLICATION_JSON)
	public Response getData() {

		log.info("service invoked (getting json)");

		final JsonObject result = Json.createObjectBuilder().add("example", service.readData()).build();

		return Response.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.entity(result)
				.build();
	}
}
