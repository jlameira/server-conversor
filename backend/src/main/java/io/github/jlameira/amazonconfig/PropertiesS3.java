package io.github.jlameira.amazonconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties("storage.s3")
public class PropertiesS3 {
    private String bucketName = "unknown";
    private String key = "unknown";
    private String privateKey = "unknown";
    private String region = "unknown";

    public String getRegion() {
        return this.region;
    }
    public void setRegion(String name) {
        this.region = name;
    }

    public String getKey() {
        return this.key;
    }
    public void setKey(String name) {
        this.key = name;
    }

    public String getPrivateKey() {
        return this.privateKey;
    }

    public void setPrivateKey(String name) {
        this.privateKey = name;
    }

    public String getBucketName() {
        return this.bucketName;
    }

    public void setBucketName(String name) {
        this.bucketName = name;
    }

}
