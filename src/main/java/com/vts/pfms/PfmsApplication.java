package com.vts.pfms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;


//@EnableEurekaClient
@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@EnableAsync
@EnableCaching
public class PfmsApplication {


	public static void main(String[] args) {
		SpringApplication.run(PfmsApplication.class, args);
	}

	  @Bean
	   public RestTemplate getRestTemplate() {
	      return new RestTemplate();
	   }
	
}
