package com.github.jenspiegsa.restassuredextension;

import com.sun.net.httpserver.HttpServer;
import org.jboss.resteasy.plugins.server.sun.http.HttpContextBuilder;
import org.jboss.resteasy.spi.ResourceFactory;

/**
 * @author Jens Piegsa
 */
public class RestAssuredSetup {

	private final int port;
	private final String contextPath;
	private final ResourceFactory[] resourceFactories;

	private HttpContextBuilder contextBuilder;
	private HttpServer server;

	public RestAssuredSetup(final int port, final String contextPath, final ResourceFactory[] resourceFactories) {

		this.port = port;
		this.contextPath = contextPath;
		this.resourceFactories = resourceFactories;
	}

	public int getPort() {
		return port;
	}

	public String getContextPath() {
		return contextPath;
	}

	public ResourceFactory[] getResourceFactories() {
		return resourceFactories;
	}

	public HttpContextBuilder getContextBuilder() {
		return contextBuilder;
	}

	public void setContextBuilder(final HttpContextBuilder contextBuilder) {
		this.contextBuilder = contextBuilder;
	}

	public HttpServer getServer() {
		return server;
	}

	public void setServer(final HttpServer server) {
		this.server = server;
	}
}
