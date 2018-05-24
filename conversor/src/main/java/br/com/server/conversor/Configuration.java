package br.com.server.conversor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import br.com.server.conversor.storage.PathServiceStorage;
import br.com.server.conversor.storage.StorageProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class Configuration {
	public static final Logger logger = LogManager.getLogger(Configuration.class);
	public static void main(String[] args) {
		SpringApplication.run(Configuration.class, args);
	}
	
	@Bean
	CommandLineRunner initial(PathServiceStorage pathServiceStorage) {
		return (docs) -> {
			pathServiceStorage.initial();
		};
	}
	
//	@Bean
//	public MultipartResolver multipartResolver() {
//	    CommonsMultipartResolver multipartResolver
//	      = new CommonsMultipartResolver();
//	    multipartResolver.setMaxUploadSize(109800197);
//	    return multipartResolver;
//	}
	

}
