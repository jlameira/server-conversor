package io.github.jlameira.encoding;

import io.github.jlameira.Application;
import io.github.jlameira.amazonconfig.PropertiesS3;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@EnableConfigurationProperties(ZencoderProps.class)
public class ZencoderService implements IZencoderService {

    private final ZencoderProps _properties;
    private final PropertiesS3 propertiesS3 ;


    @Autowired
    public ZencoderService(ZencoderProps properties, PropertiesS3 propertiesS3) {
        this._properties = properties;
        this.propertiesS3 = propertiesS3;
    }


    @Override
    public void initial() {

    }

    private String createJSONRequestBody(
            String input,
            String output
    ) {
        try {
            JSONObject request_body = new JSONObject();
            // Set API key with full access to Zencoder service.
            request_body.put("api_key",_properties.getFullKey());
            // Set input url from Amazon AWS.
            request_body.put(
                    "input",
                    "s3://" +
                            "conversor-jonathan" + input
            );
            request_body.put("region","us-virginia" );
            // Set output configuration data
            JSONObject json_output = new JSONObject();
            json_output.put(
                    "url",
                    "s3://"
                            +"conversor-jonathan" + output
            );
            json_output.put("region","us-virginia" );
            json_output.put("public", "true");
            request_body.put("output", json_output);
            // return request JSON
            return request_body.toString();
        } catch (Exception e) {
            return String.valueOf(e.getStackTrace());
//            throw new EncodingException("Error creating request body for encoding job.");
        }
    }

    /*
     Send POST request to run encoding job.
     params: Request body in JSON format.
     return: Request's response.
     */
    private String sendEncodeRequest(String request_body) {
        try {
            // Create connection.
            URL url = new URL("https://app.zencoder.com/api/v2/jobs");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            // Setting Header
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            // Setting output and Sending
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(request_body.toString());
            wr.flush();
            wr.close();
            // Get response code.
            int response_code = con.getResponseCode();
            //Log.
//            Application.logger.info("\nSending 'POST' request to URL : " + url);
//            Application.logger.info("Response Code : " + response_code);
            // Read response
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            // Return response in string format.
//            Application.logger.info("RESPONSE:");
//            Application.logger.info(response.toString());
            return response.toString();
        } catch (Exception e) {
//            throw new EncodingException("Error when sending Zencoder job.");
            return String.valueOf(e.getStackTrace());
        }
    }


    @Override
    public String encode(String input_filename, String input_path, String output_filename, String output_path) {
        String request_body = createJSONRequestBody("/" + input_path + "/" + input_filename,
                "/" + output_path + "/" + output_filename);
        Application.logger.info("acess request");

        String response = sendEncodeRequest(request_body);

        JSONObject response_JSON;
        try {
            response_JSON = new JSONObject(response);
            Object input_id = response_JSON.get("id");
            Object output_id = ((JSONObject) ((JSONArray) response_JSON.get("outputs")).get(0)).get("id");
            Object output_url = ((JSONObject) ((JSONArray) response_JSON.get("outputs")).get(0)).get("url");

            JSONObject return_JSON = new JSONObject();
            return_JSON.put("input_id", input_id);
            return_JSON.put("output_id", output_id);
            return_JSON.put("output_url", output_url);
            return_JSON.put("zencoder_key", _properties.getReadKey());
            return return_JSON.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            throw new ZencoderException("Error encode.");
        }
    }


}
