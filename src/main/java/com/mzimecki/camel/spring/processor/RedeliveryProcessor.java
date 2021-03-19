package com.mzimecki.camel.spring.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.mzimecki.camel.spring.constants.ServiceConstants;

public class RedeliveryProcessor implements Processor {
	
	private static final int MAX_REDELIVERIES = 5;
	
	@Override
	public void process(Exchange exchange) {
		final int redeliveryCount = exchange.getIn().getHeader(ServiceConstants.REDELIVERY_COUNT_HEADER_NAME, Integer.class);
		
		if (redeliveryCount <= MAX_REDELIVERIES) {
			exchange.getIn().setHeader(ServiceConstants.REDELIVERY_COUNT_HEADER_NAME, redeliveryCount + 1);
		} else {
			exchange.setRouteStop(true);
		}
	}

}
