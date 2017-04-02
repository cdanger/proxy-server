package org.lovebing.proxy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author lovebing Created on Apr 2, 2017
 */

@Component
@ConfigurationProperties(prefix = "server")
public class HttpServiceConfig {

    private Integer port;

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getPort() {
        return port;
    }
}
