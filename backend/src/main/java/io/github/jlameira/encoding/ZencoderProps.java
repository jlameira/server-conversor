package io.github.jlameira.encoding;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@ConfigurationProperties(prefix="encoding")
public class ZencoderProps {
    private String fullKey = "unknown";
    private String readKey = "unknown";
    private String url = "unknown";

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String name) {
        this.url = name;
    }

    public String getFullKey() {
        return this.fullKey;
    }

    public void setFullKey(String name) {
        this.fullKey = name;
    }

    public String getReadKey() {
        return this.readKey;
    }

    public void setReadKey(String name) {
        this.readKey = name;
    }
}
