package com.quantori.restfullservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class MyRestfullServiceApplication {

		public static void main(String[] args) {

		final Logger logger = LoggerFactory.getLogger(MyRestfullServiceApplication.class);

		logger.info("!!!MyRestfullServiceApplication starting!!!");
		SpringApplication.run(MyRestfullServiceApplication.class, args);
	}

}

