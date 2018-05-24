package br.com.server.conversor.amazonconf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class NewAmazonConfiguration {
	private static final String ACCESS_KEY="AKIAJMPPS2SNWFX5H2OQ";
	private static final String SECRET_KEY="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";
	private static final String REGION="us-east-2";
	
	
	@Bean
	public BasicAWSCredentials basicAWSCredentials() {
	    return new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
	}
	
	@Bean
	public AmazonS3 amazonS3() {
	    return AmazonS3ClientBuilder.standard().withRegion(REGION)
	            .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials())).build();
	}

}
