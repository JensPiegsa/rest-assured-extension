package com.github.jenspiegsa.restassuredextension;

import static com.github.jenspiegsa.restassuredextension.PostConstructPojoResourceFactory.wired;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import io.restassured.http.ContentType;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.spi.ResourceFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author Jens Piegsa
 */
@DisplayName("RestAssuredExtension usage")
@ExtendWith(RestAssuredExtension.class)
class ExampleTest {

	@ConfigureRestAssured(contextPath = "/api", port = 8989)
	ResourceFactory[] resourceFactories = {wired(ExampleResource.class, this::prepareResource)};

	private void prepareResource(final ExampleResource resource) {
		resource.log = mock(Logger.class);
		resource.service = mock(ExampleService.class);
		given(resource.service.readData()).willReturn(42);
	}

	@Test @DisplayName("example")
	void example() {

		given()
			.accept(ContentType.JSON)
		.when()
			.get("/api/example")
		.then()
			.statusCode(Response.Status.OK.getStatusCode())
			.body("example", equalTo(42));
	}
}
