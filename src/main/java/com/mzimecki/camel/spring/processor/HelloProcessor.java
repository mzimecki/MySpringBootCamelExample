package com.mzimecki.camel.spring.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.httpclient.HttpStatus;

public class HelloProcessor implements Processor {

	@Override
	public void process(Exchange exchange) {
		final String body = exchange.getIn().getBody(String.class);
		exchange.getMessage().setBody("Hello from camel processed message! Received payload: " + body);
		exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.SC_ACCEPTED);
	}

}
