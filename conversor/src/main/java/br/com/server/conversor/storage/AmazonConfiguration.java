package br.com.server.conversor.storage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.util.IOUtils;

import br.com.server.conversor.Configuration;



@Service
@EnableConfigurationProperties(PropertiesS3.class)
public class AmazonConfiguration implements PathServiceStorage {
	
	

	@Autowired
    private AmazonS3 amazonS3;
    private final StorageProperties storageProperties ;
	 private final PropertiesS3 propertiesS3 ;

    /*
    Class constructor.

    It sets up the amazon s3 client for further uinterface calls.
    */
    @Autowired
    public AmazonConfiguration(
        StorageProperties properties, PropertiesS3 s3_properties
    ) {
        this.storageProperties = properties;
        this.propertiesS3 = s3_properties;

//        AWSCredentials credentials = new BasicAWSCredentials(
//                    this.propertiesS3.getKey(),
//                    this.propertiesS3.getPrivateKey());
//
//        // create a client connection based on credentials
//        this.amazonS3 = new AmazonS3Client(credentials);
    }
    
    public AmazonConfiguration(AmazonS3 amazon, StorageProperties properties, PropertiesS3 s3_properties ) {
    	this.amazonS3 = amazon;
    	 this.storageProperties = properties;
         this.propertiesS3 = s3_properties;


    }

    /*
    This storage service does not require initial computation.
    */
    @Override
    public void initial() {}

    /*
    Store the MultipartFile file into Amazon's S3 storage.

    It uses path to resolve the file's folder inside S3
    */
    @Override
    public void store(MultipartFile file, String path) {
//    	if(file.getOriginalFilename() == null || file.getOriginalFilename() == '') {
//    		
//    	}
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        if (file.isEmpty()) {
		    throw new S3StorageException("Failed to store empty file " + filename);
		}
		if (filename.contains("..")) {
		    throw new S3StorageException(
		            "Cannot store file with relative path outside current directory "
		                    + filename);
		}
		
		// upload file to s3
		String fileName = path + "/" + filename;
		
		
       // Create a list of UploadPartResponse objects. You get one of these
		// for each part upload.
		List<PartETag> partETags = new ArrayList<PartETag>();

		// Step 1: Initialize.
		InitiateMultipartUploadRequest initRequest = new 
		     InitiateMultipartUploadRequest(this.propertiesS3.getBucketName(), fileName);
		InitiateMultipartUploadResult initResponse = 
				this.amazonS3.initiateMultipartUpload(initRequest);
		File file2 = null;
		try {
			 file2 = convert(file);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			throw new S3StorageException("Failed to store empty file " + filename,e1);
		} 
		long contentLength = file2.length();
		long partSize = 20 * 1024 * 1024; // Set part size to 5 MB.

		try {
		    // Step 2: Upload parts.
		    long filePosition = 0;
		    for (int i = 1; filePosition < contentLength; i++) {
		        // Last part can be less than 5 MB. Adjust part size.
		    	partSize = Math.min(partSize, (contentLength - filePosition));
		    	
		        // Create request to upload a part.
		        UploadPartRequest uploadRequest = new UploadPartRequest()
		            .withBucketName(this.propertiesS3.getBucketName()).withKey(fileName)
		            .withUploadId(initResponse.getUploadId()).withPartNumber(i)
		            .withFileOffset(filePosition)
		            .withFile(file2)
		            .withPartSize(partSize);

		        // Upload part and add response to our list.
		        partETags.add(
		        		this.amazonS3.uploadPart(uploadRequest).getPartETag());

		        filePosition += partSize;
		        Configuration.logger.info("Log Partition " + i);
		        Configuration.logger.info("Faltando " + (contentLength - filePosition));
		    }

		    // Step 3: Complete.
		    CompleteMultipartUploadRequest compRequest = new 
		                 CompleteMultipartUploadRequest(
		                		 this.propertiesS3.getBucketName(), 
		                		 fileName, 
		                            initResponse.getUploadId(), 
		                            partETags);

		    this.amazonS3.completeMultipartUpload(compRequest);
		} catch (Exception e) {
//			S3StorageException("Failed to store file " + filename, e);
			this.amazonS3.abortMultipartUpload(new AbortMultipartUploadRequest(
					this.propertiesS3.getBucketName(),fileName, initResponse.getUploadId()));
		}

   
//    		ObjectMetadata metadata = new ObjectMetadata();
//    		byte[] bytes = IOUtils.toByteArray(file.getInputStream());
//    		metadata.setContentLength(bytes.length);
//    		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
//    		PutObjectRequest putObjectRequest = new PutObjectRequest(this.propertiesS3.getBucketName(), fileName, byteArrayInputStream, metadata);
//    		this.amazonS3.putObject(putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead));
//    		this.amazonS3.putObject(new PutObjectRequest(this.propertiesS3.getBucketName(), fileName,
//    				file.getInputStream(),null)
		Configuration.logger.info("Stored "+fileName+" into S3");
    }

    /*
    Load and returns as resource the file within the path folder in S3.
    */
    @Override
    public Resource load(String filename, String path) {
        S3Object s3_file = this.amazonS3.getObject(this.propertiesS3.getBucketName(), path + "/" + filename);
        S3ObjectInputStream file_stream = s3_file.getObjectContent();

        Resource resource = new InputStreamResource(file_stream);
        if (resource.exists() || resource.isReadable()) {
        	Configuration.logger.info("Loaded "+path + "/" + filename+" into S3");
            return resource;
        }
        else {
            throw new S3FileNotFoundException(
                    "Could not read file: " + filename);
        }
    }

    @Override
    /*
    Return S3 path for the file name and path received.
    */
    public String path(String filename, String local_path) {
        String root_location = this.storageProperties.getLocation();
        return root_location + "/" + this.propertiesS3.getBucketName() + "/" + local_path + "/" + filename;
    }
    
    
    public static File convert(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

}
