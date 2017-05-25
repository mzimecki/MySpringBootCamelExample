package com.mzimecki.camel.spring.route;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.support.AbstractApplicationContext;

import com.mzimecki.camel.spring.constants.ServiceConstants;

public class QueueReaderRouteTest extends CamelSpringTestSupport {

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
	
	private void configureMockEndpoint() throws Exception {
		final AdviceWithRouteBuilder mockSolr = new AdviceWithRouteBuilder() {

			@Override
			public void configure() throws Exception {
				 replaceFromWith("direct:deadLetterQueueMock");
				 interceptSendToEndpoint("jms:*").skipSendToOriginalEndpoint().process(exchange -> 
				 	exchange.getIn().setBody("sent to jms endpoint"));
			};
		};
		
		context().getRouteDefinition(ServiceConstants.QUEUE_READER_ROUTE_ID).adviceWith(context(), mockSolr);
	}
	
	@Test
	public void should_queue_reader_route_be_running() {
		assertTrue(context().getRouteStatus(ServiceConstants.QUEUE_READER_ROUTE_ID).isStarted());
	}

}
