package io.github.jlameira.encoding.test;

import io.github.jlameira.amazonconfig.PropertiesS3;
import io.github.jlameira.encoding.ZencoderProps;
import io.github.jlameira.encoding.ZencoderService;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ZencoderServiceTest {

    private ZencoderProps zencoderProps = new ZencoderProps();
    private PropertiesS3 propertiesS3 = new PropertiesS3();
    private ZencoderService zencoderService;


    @Before
    public void setUp() throws Exception {
        propertiesS3.setRegion("us-east-1");
        zencoderProps.setFullKey("");
        zencoderProps.setReadKey("");
        zencoderProps.setUrl("https://app.zencoder.com/api/v2/jobs");
        this.zencoderService = new ZencoderService(zencoderProps, propertiesS3);
        this.zencoderService.initial();
    }

    @Test
    public void encode() {
        try {
            String response = zencoderService.encode("sample.dv", "test_input",
                    "sample.mp4", "test_output");
            JSONObject r_json = new JSONObject(response);

            assertTrue(r_json.has("input_id"));
            assertTrue(r_json.has("output_id"));
            assertTrue(r_json.has("output_url"));
            assertEquals(r_json.get("zencoder_key"), zencoderProps.getReadKey());
        } catch (Exception e){
            fail(e.getMessage());
        }
    }
}