package io.github.jlameira.encoding;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("encoding")
public class ZencoderProps {
    private String full_key = "unknown";
    private String read_key = "unknown";
    private String url = "unknown";

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String name) {
        this.url = name;
    }

    public String getFullKey() {
        return this.full_key;
    }

    public void setFullKey(String name) {
        this.full_key = name;
    }

    public String getReadKey() {
        return this.read_key;
    }

    public void setReadKey(String name) {
        this.read_key = name;
    }
}
