package io.github.jlameira.amazonconfig.test;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import io.github.jlameira.amazonconfig.AmazonConfiguration;
import io.github.jlameira.amazonconfig.AmazonService;
import io.github.jlameira.amazonconfig.PropertiesS3;
import io.github.jlameira.amazonconfig.StorageProperties;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AmazonServiceTest {

    @Autowired
    private AmazonS3 amazonS3;
    private AmazonConfiguration amazonConfiguration;
    private AmazonService amazonService;
    private PropertiesS3 propertiesS3 = new PropertiesS3();
    private StorageProperties storageProperties = new StorageProperties();

    @Before
    public void init(){
        storageProperties.setLocation("https://s3.amazonaws.com");
        propertiesS3.setBucketName("conversor-jonathan");
        propertiesS3.setKey("");
        propertiesS3.setPrivateKey("");
        propertiesS3.setRegion("us-east-1");
        amazonConfiguration = new AmazonConfiguration(propertiesS3);
        amazonService = new AmazonService(amazonConfiguration.amazonS3(),storageProperties, propertiesS3 );


    }

    @Test
    public void store() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "test.txt",                //filename
                "Hallo World".getBytes()); //content
        amazonService.store(mockMultipartFile, "test_output");
    }

    @Test
    public void load() throws IOException {
        Resource file = amazonService.load("test.txt", "test_output");
        InputStream output_stream = file.getInputStream();
        String output = IOUtils.toString(output_stream, StandardCharsets.UTF_8);
        assertTrue("OutPut equal to test file", output.equals("Hallo World"));
    }

    @Test(expected = AmazonS3Exception.class)
    public void testLoadWrongFile() throws IOException{
        Resource file = amazonService.load("test2.txt", "test_input");
    }

    @Test
    public void path() {
        System.out.println("KEY:");
        System.out.println(propertiesS3.getKey());

        String path = amazonService.path("sample.dv", "test_input");
        assertEquals(path, "https://s3.amazonaws.com/conversor-jonathan/test_input/sample.dv");
    }
}