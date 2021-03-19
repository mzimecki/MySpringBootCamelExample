package com.mzimecki.camel.spring.webservice;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mzimecki.camel.spring.constants.ServiceConstants;

@Controller
@RequestMapping("service")
public class HelloService {
	
	private final ProducerTemplate producer;
	
	private final CamelContext camelContext;

	public HelloService(ProducerTemplate producer, CamelContext camelContext) {
		this.producer = producer;
		this.camelContext = camelContext;
	}
	
	@PostMapping(value = "/hello", consumes={MediaType.TEXT_PLAIN_VALUE}, produces = {MediaType.TEXT_PLAIN_VALUE})
	@ResponseBody
	public ResponseEntity<String> hello(@RequestBody String requestBody) {
		final Exchange requestExchange = ExchangeBuilder.anExchange(camelContext).withBody(requestBody).build();
		final Exchange responseExchange = producer.send(ServiceConstants.HELLO_SERVICE_ENDPOINT, requestExchange);
		final String responseBody = responseExchange.getMessage().getBody(String.class);
		final int responseCode = responseExchange.getMessage().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
		return ResponseEntity.status(responseCode).body(responseBody);
	}
}
