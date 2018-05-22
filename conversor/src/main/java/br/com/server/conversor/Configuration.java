package br.com.server.conversor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Configuration {
	public static final Logger logger = LogManager.getLogger(Configuration.class);
	public static void main(String[] args) {
		SpringApplication.run(Configuration.class, args);
	}
	

}
