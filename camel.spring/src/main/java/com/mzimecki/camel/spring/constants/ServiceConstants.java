package com.mzimecki.camel.spring.constants;

public final class ServiceConstants {
	
	public static final String HELLO_SERVICE_ENDPOINT = "direct:hello";
	public static final String HELLO_ROUTE_ID = "HelloRoute";
	public static final String QUEUE_READER_ROUTE_ID = "QueueReaderRoute";
	
	private ServiceConstants() {
		
	}
}
