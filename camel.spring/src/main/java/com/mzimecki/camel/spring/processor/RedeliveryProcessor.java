package com.mzimecki.camel.spring.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class RedeliveryProcessor implements Processor {
	
	private static final String REDELIVERY_COUNT_HEADER_NAME = "redeliveryCount";
	private static final int MAX_REDELIVERIES = 5;
	
	@Override
	public void process(Exchange exchange) throws Exception {
		final int redeliveryCount = exchange.getIn().getHeader(REDELIVERY_COUNT_HEADER_NAME, Integer.class).intValue();
		
		if (redeliveryCount <= MAX_REDELIVERIES) {
			exchange.getIn().setHeader(REDELIVERY_COUNT_HEADER_NAME, redeliveryCount + 1);
		} else {
			exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
		}
	}

}
