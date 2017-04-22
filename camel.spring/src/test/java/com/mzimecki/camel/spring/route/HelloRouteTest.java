package com.mzimecki.camel.spring.route;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.mzimecki.camel.spring.constants.ServiceConstants;

public class HelloRouteTest extends CamelSpringTestSupport {
	
	private static final String TEST_MESSAGE_PAYLOAD = "my message";

	@Override
	protected AbstractApplicationContext createApplicationContext() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.scan("com.mzimecki.camel.spring");
		ctx.refresh();
		return ctx;
	}
	
	@Test
	public void should_hello_route_be_running() throws Exception {
		assertTrue(context().getRouteStatus(ServiceConstants.HELLO_ROUTE_ID).isStarted());
	}
	
	@Test
	public void should_hello_route_process_the_message() throws Exception {
		final Endpoint endpoint = getMandatoryEndpoint(ServiceConstants.HELLO_SERVICE_ENDPOINT);
		final Exchange requestExchange = ExchangeBuilder.anExchange(context()).withBody(TEST_MESSAGE_PAYLOAD).build();
		final Exchange resultExchange = context().createProducerTemplate().send(endpoint, requestExchange);
		final String resultBody = resultExchange.getOut().getBody(String.class);
		assertTrue(resultBody.contains(TEST_MESSAGE_PAYLOAD));
	}

}
