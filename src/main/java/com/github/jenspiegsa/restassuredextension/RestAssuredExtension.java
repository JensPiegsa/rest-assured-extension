package com.github.jenspiegsa.restassuredextension;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.junit.platform.commons.util.AnnotationUtils.findAnnotatedFields;

import com.sun.net.httpserver.HttpServer;
import io.restassured.RestAssured;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.ws.rs.Path;
import org.jboss.resteasy.plugins.server.sun.http.HttpContextBuilder;
import org.jboss.resteasy.spi.ResourceFactory;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.platform.commons.util.ReflectionUtils;

/**
 * @author Jens Piegsa
 */
public class RestAssuredExtension implements TestInstancePostProcessor, BeforeEachCallback, AfterEachCallback {

	private static final Logger LOGGER = Logger.getLogger(RestAssuredExtension.class.getName());

	private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(RestAssuredExtension.class);

	@Override
	public void postProcessTestInstance(final Object testInstance, final ExtensionContext context) throws Exception {

		final List<RestAssuredSetup> restAssuredSetups = new ArrayList<>();

		for (Field field : retrieveAnnotatedFields(context, ConfigureRestAssured.class, ResourceFactory[].class)) {

			final ConfigureRestAssured annotation = field.getAnnotation(ConfigureRestAssured.class);

			final int port = annotation.port();
			final String contextPath = annotation.contextPath();

			final ResourceFactory[] resourceFactories = ReflectionUtils.tryToReadFieldValue(field, testInstance)
					.toOptional()
					.filter(ResourceFactory[].class::isInstance)
					.map(ResourceFactory[].class::cast)
					.orElseGet(() -> new ResourceFactory[0]);

			restAssuredSetups.add(new RestAssuredSetup(port, contextPath, resourceFactories));
		}

		context.getStore(NAMESPACE).put(testInstance.getClass(), restAssuredSetups);
	}

	@Override
	public void beforeEach(final ExtensionContext context) throws Exception {

		for (RestAssuredSetup setup : collectSetups(context)) {

			final int port = setup.getPort();
			final String contextPath = setup.getContextPath();
			final ResourceFactory[] resourceFactories = setup.getResourceFactories();
			RestAssured.port = port;

			final HttpServer server = HttpServer.create(new InetSocketAddress(port), 10);
			setup.setServer(server);

			final HttpContextBuilder contextBuilder = new HttpContextBuilder();
			contextBuilder.setPath(contextPath);
			contextBuilder.getDeployment().getResourceFactories().addAll(asList(resourceFactories));
			contextBuilder.bind(server);
			setup.setContextBuilder(contextBuilder);

			server.start();

			LOGGER.info(server.getAddress() + " started serving " + endpoints(contextPath, resourceFactories));
		}
	}

	@Override
	public void afterEach(final ExtensionContext context) {

		final List<RestAssuredSetup> setups = collectSetups(context);
		for (RestAssuredSetup setup : setups) {

			final HttpContextBuilder contextBuilder = setup.getContextBuilder();
			final HttpServer server = setup.getServer();

			contextBuilder.cleanup();
			server.stop(0);

			LOGGER.info(server.getAddress() + " stopped.");
		}
	}

	private static List<Field> retrieveAnnotatedFields(final ExtensionContext context,
	                                                   final Class<? extends Annotation> annotationType,
	                                                   final Class<?> fieldType) {

		return retrieveAnnotatedFields(context, annotationType, field -> fieldType.isAssignableFrom(field.getType()));
	}

	private static List<Field> retrieveAnnotatedFields(final ExtensionContext context,
	                                                   final Class<? extends Annotation> annotationType,
	                                                   final Predicate<Field> fieldPredicate) {

		return context.getElement()
				.filter(Class.class::isInstance)
				.map(Class.class::cast)
				.map(testInstanceClass -> findAnnotatedFields(testInstanceClass, annotationType, fieldPredicate))
				.orElseGet(Collections::emptyList);
	}

	private static List<RestAssuredSetup> collectSetups(final ExtensionContext context) {

		return collectTestClasses(context)
				.map(testClass -> context.getStore(NAMESPACE).get(testClass))
				.filter(Objects::nonNull)
				.map(List.class::cast)
				.flatMap(Collection::stream)
				.map(RestAssuredSetup.class::cast)
				.collect(toList());
	}

	private static String endpoints(final String contextPath, final ResourceFactory[] resourceFactories) {

		return Arrays.stream(resourceFactories)
				.map(ResourceFactory::getScannableClass)
				.map(resourceClass -> resourceClass.getAnnotation(Path.class))
				.filter(Objects::nonNull)
				.map(Path::value)
				.map(pathValue -> contextPath + "/" + pathValue)
				.collect(joining("\n\t", "\n\t", ""));
	}


	private static Stream<Class<?>> collectTestClasses(final ExtensionContext context) {

		return Stream.concat(
				Stream.of(context.getRequiredTestClass()),
				context.getParent()
						.filter(parentContext -> parentContext != context.getRoot())
						.map(RestAssuredExtension::collectTestClasses)
						.orElseGet(Stream::empty)
		).distinct();
	}
}
