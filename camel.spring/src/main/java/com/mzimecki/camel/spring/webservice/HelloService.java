package com.mzimecki.camel.spring.webservice;

import javax.servlet.http.HttpServletRequest;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mzimecki.camel.spring.constants.ServiceConstants;

@Controller
@RequestMapping("service")
public class HelloService {
	
	@Autowired
	private ProducerTemplate producer;
	
	@Autowired
	private CamelContext camelContext;
	
	@RequestMapping(value = "/hello", method = RequestMethod.GET, produces = {"text/plain"})
	@ResponseBody
	public ResponseEntity<?> hello(final HttpServletRequest request) {
		final Exchange requestExchange = ExchangeBuilder.anExchange(camelContext).build();
		final Exchange responseExchange = producer.send(ServiceConstants.HELLO_SERVICE_ENDPOINT, requestExchange);
		final String body = responseExchange.getOut().getBody(String.class);
		final int responseCode = responseExchange.getOut().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class).intValue();
		return ResponseEntity.status(responseCode).body(body);
	}
}
