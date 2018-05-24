package br.com.server.conversor.storage.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;

import br.com.server.conversor.amazonconf.NewAmazonConfiguration;
import br.com.server.conversor.storage.AmazonConfiguration;
import br.com.server.conversor.storage.PropertiesS3;
import br.com.server.conversor.storage.StorageProperties;

public class S3StorageServiceTests {

    private StorageProperties properties = new StorageProperties();
    private PropertiesS3 s3_properties = new PropertiesS3();
    
    @Autowired
    private AmazonS3 amazonS3;
    private AmazonConfiguration service;
    private NewAmazonConfiguration newAmazon;

    @Before
    public void init() {
    	newAmazon = new NewAmazonConfiguration();
        properties.setLocation("https://s3.us-east-2.amazonaws.com");
        s3_properties.setBucketName("encoder-jonathan");
        s3_properties.setKey("AKIAJMPPS2SNWFX5H2OQ");
        s3_properties.setPrivateKey("eZ8jsTYxf/STeZIYQRKgFOk3dDT6cCiiB1Ox3LL8");
        s3_properties.setRegion("us-east-2");
        service = new AmazonConfiguration(newAmazon.amazonS3(),properties, s3_properties);
        service.initial();    
        }

    @Test
    public void testLoad() throws IOException {
        Resource file = service.load("test.txt", "test_output");
        InputStream output_stream = file.getInputStream();
        String output = IOUtils.toString(output_stream, StandardCharsets.UTF_8);
        assertTrue("OutPut equal to test file", output.equals("Hallo World"));
    }

    @Test(expected = AmazonS3Exception.class)
    public void testLoadWrongFile() throws IOException{
        Resource file = service.load("test2.txt", "test_input");
    }

    @Test
    public void testStore() {
      MockMultipartFile mockMultipartFile = new MockMultipartFile(
       "test.txt",                //filename
       "Hallo World".getBytes()); //content
       service.store(mockMultipartFile, "test_output");
    }

    @Test
    public void testPath() {
        System.out.println("KEY:");
        System.out.println(s3_properties.getKey());

        String path = service.path("sample.dv", "test_input");
        assertEquals(path, "https://s3.us-east-2.amazonaws.com/encoder-jonathan/test_input/sample.dv");
    }

}

