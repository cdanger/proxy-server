package org.lovebing.proxy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author lovebing Created on Apr 2, 2017
 */
@Component
@ConfigurationProperties(prefix = "proxyCache")
public class ProxyCacheConfig {

    private String[] fileTypes;
    private String cachePath;

    public void setFileTypes(String[] fileTypes) {
        this.fileTypes = fileTypes;
    }

    public void setCachePath(String cachePath) {
        this.cachePath = cachePath;
    }

    public String[] getFileTypes() {
        return fileTypes;
    }

    public String getCachePath() {
        return cachePath;
    }
}
