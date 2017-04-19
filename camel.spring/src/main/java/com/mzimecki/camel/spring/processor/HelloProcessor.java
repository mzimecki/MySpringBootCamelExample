package com.mzimecki.camel.spring.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.httpclient.HttpStatus;

public class HelloProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		exchange.getOut().setBody("Hello from camel processed message!");
		exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.SC_ACCEPTED);
	}

}
