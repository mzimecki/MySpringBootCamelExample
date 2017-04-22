package com.mzimecki.camel.spring.route;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.apache.camel.test.spring.CamelTestContextBootstrapper;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.test.context.BootstrapWith;

import com.mzimecki.camel.spring.constants.ServiceConstants;

@BootstrapWith(CamelTestContextBootstrapper.class)
public class HelloRouteTest extends CamelSpringTestSupport {
	
	private static final String TEST_MESSAGE_PAYLOAD = "my message";

	@Override
	protected AbstractApplicationContext createApplicationContext() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(HelloRoute.class);
		ctx.refresh();
		return ctx;
	}
	
	@Test
	public void should_hello_route_be_running() throws Exception {
		context().addRoutes(applicationContext.getBean(HelloRoute.class));
		assertTrue(context().getRouteStatus(ServiceConstants.HELLO_ROUTE_ID).isStarted());
	}
	
	@Test
	public void should_hello_route_process_the_message() throws Exception {
		context().addRoutes(applicationContext.getBean(HelloRoute.class));
		Endpoint endpoint = getMandatoryEndpoint(ServiceConstants.HELLO_SERVICE_ENDPOINT);
		Exchange requestExchange = ExchangeBuilder.anExchange(context()).withBody(TEST_MESSAGE_PAYLOAD).build();
		Exchange resultExchange = context().createProducerTemplate().send(endpoint, requestExchange);
		String resultBody = resultExchange.getOut().getBody(String.class);
		assertTrue(resultBody.contains(TEST_MESSAGE_PAYLOAD));
	}


}
