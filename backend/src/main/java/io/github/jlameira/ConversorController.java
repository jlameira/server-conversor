package io.github.jlameira;


import io.github.jlameira.amazonconfig.IAmazonService;
import io.github.jlameira.encoding.ZencoderService;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ConversorController {

    private final Logger logger = LoggerFactory.getLogger(ConversorController.class);
    private final IAmazonService amazonService;

    public static final String TEST_API_KEY = "4a08ca50cf2218b50c6a51e6e0b85ab1";
    private final ZencoderService encodingService;



    @Autowired
    public ConversorController(IAmazonService amService, ZencoderService encodingService) {
        this.amazonService = amService;
        this.encodingService = encodingService;
    }


    @CrossOrigin(origins = "http://localhost:8081")
    @PostMapping("/api/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile uploadfile) {

        logger.debug("Single file upload!");

        if (uploadfile.isEmpty()) {
            return new ResponseEntity("please select a file!", HttpStatus.OK);
        }

        try {

            // AWS S3 file
        this.amazonService.store(uploadfile, "input");

            String input_filename = uploadfile.getOriginalFilename();
            String output_filename =
                    FilenameUtils.removeExtension(input_filename) + ".mp4";

            // Zencoder file
            String response = encodingService.encode(
                    input_filename, "input",
                    output_filename, "output"
            );


            return new ResponseEntity( response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }





}

