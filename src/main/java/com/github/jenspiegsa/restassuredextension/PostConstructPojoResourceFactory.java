package com.github.jenspiegsa.restassuredextension;

import java.util.function.Consumer;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * This resource factory enhances RESTEasy's {@link POJOResourceFactory} by a post construction step.
 *
 * @author Jens Piegsa
 *
 * @param <E>
 *            the resource class
 */
public class PostConstructPojoResourceFactory<E> extends POJOResourceFactory {

	private final Consumer<E> postConstruct;

	public PostConstructPojoResourceFactory(final Class<E> scannableClass, final Consumer<E> postConstruct) {
		super(scannableClass);
		this.postConstruct = postConstruct;
	}

	@Override
	public Object createResource(final HttpRequest request, final HttpResponse response, final ResteasyProviderFactory factory) {
		@SuppressWarnings("unchecked")
		final E resource = (E) super.createResource(request, response, factory);
		postConstruct.accept(resource);
		return resource;
	}

	/**
	 * Creates a {@link ResourceFactory} for a given JAX-RS POJO {@link Class} that is able to perform additional instance-level post construction steps utilizing the provided {@link Consumer}.
	 */
	public static <T> ResourceFactory wired(final Class<T> scannableClass, final Consumer<T> postConstruct) {
		return new PostConstructPojoResourceFactory<>(scannableClass, postConstruct);
	}
}
