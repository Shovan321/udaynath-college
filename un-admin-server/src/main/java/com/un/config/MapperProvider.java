package com.un.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class MapperProvider {
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper()
				  .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);		
	}
}
