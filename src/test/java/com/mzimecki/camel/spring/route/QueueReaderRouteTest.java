package com.mzimecki.camel.spring.route;

import static org.junit.jupiter.api.Assertions.*;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.test.spring.junit5.CamelSpringTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.support.AbstractApplicationContext;

import com.mzimecki.camel.spring.constants.ServiceConstants;

public class QueueReaderRouteTest extends CamelSpringTestSupport {

	private static final String TEST_BODY_PAYLOAD = "sent to jms endpoint";

	@Override
	protected AbstractApplicationContext createApplicationContext() {
		return new AnnotationConfigApplicationContext(ContextConfiguration.class);
	}
	
	@Configuration
	@EnableAutoConfiguration
	@ComponentScan(basePackageClasses = {ActivemqConfiguration.class, QueueReaderRoute.class}, 
		useDefaultFilters = false,		
		includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, 
			classes={ActivemqConfiguration.class, QueueReaderRoute.class}))
	static class ContextConfiguration {
		
	}
	
	@Configuration
	static class ActivemqConfiguration {
		@Bean
		public ConnectionFactory jmsConnectionFactory() {
			final PooledConnectionFactory pool = new PooledConnectionFactory();
			pool.setConnectionFactory(new ActiveMQConnectionFactory("userName", "password", "tcp://localhost:61616"));
			return pool;
		}
	}
	
	private Exchange createExchange(final int redeliveryCount) {
		final Exchange exchange = ExchangeBuilder.anExchange(context()).build();
		exchange.getIn().setHeader(ServiceConstants.REDELIVERY_COUNT_HEADER_NAME, redeliveryCount);
		return exchange;
	}
	
	private void configureMockEndpoint() throws Exception {
		AdviceWith.adviceWith(context(), ServiceConstants.QUEUE_READER_ROUTE_ID, a -> {
			a.replaceFromWith("direct:deadLetterQueueMock");
			a.interceptSendToEndpoint("jms:*")
					.skipSendToOriginalEndpoint()
					.process(exchange -> exchange.getIn().setBody(TEST_BODY_PAYLOAD));
		});
	}
	
	@Test
	public void should_queue_reader_route_be_running() {
		assertTrue(context().getRouteController().getRouteStatus(ServiceConstants.QUEUE_READER_ROUTE_ID).isStarted());
	}
	
	@Test
	public void should_increment_redelivery_count_and_send_to_jms_endpoint() throws Exception {
		configureMockEndpoint();
		final Exchange requestExchange = createExchange(0);
		final Exchange resultExchange = template.send("direct:deadLetterQueueMock", requestExchange);
		final int redeliveryCountResult = resultExchange.getIn().getHeader(ServiceConstants.REDELIVERY_COUNT_HEADER_NAME, Integer.class).intValue();
		assertEquals(1, redeliveryCountResult);
		assertEquals(TEST_BODY_PAYLOAD, resultExchange.getIn().getBody(String.class));
	}
	
	@Test
	public void should_not_increment_redelivery_count_and_not_reach_jms_enpoint() throws Exception {
		configureMockEndpoint();
		final Exchange requestExchange = createExchange(6);
		final Exchange resultExchange = template.send("direct:deadLetterQueueMock", requestExchange);
		final int redeliveryCountResult = resultExchange.getIn().getHeader(ServiceConstants.REDELIVERY_COUNT_HEADER_NAME, Integer.class).intValue();
		assertEquals(6, redeliveryCountResult);
		assertNull(resultExchange.getIn().getBody(String.class));
	}

}
