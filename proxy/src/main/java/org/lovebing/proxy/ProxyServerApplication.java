package org.lovebing.proxy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import net.lightbody.bmp.mitm.RootCertificateGenerator;
import net.lightbody.bmp.mitm.manager.ImpersonatingMitmManager;
import org.littleshoot.proxy.*;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.lovebing.proxy.common.component.HttpClient;
import org.lovebing.proxy.config.HttpServiceConfig;
import org.lovebing.proxy.config.ProxyCacheConfig;
import org.lovebing.proxy.config.ProxyServerConfig;
import org.lovebing.proxy.filter.AnswerRequestFilter;
import org.lovebing.proxy.service.CacheManager;
import org.lovebing.proxy.service.CacheTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * @author lovebing Created on Apr 1, 2017
 */

@EnableConfigurationProperties
@SpringBootApplication
public class ProxyServerApplication {

    private static final Logger logger = LoggerFactory.getLogger(ProxyServerApplication.class);

    @Autowired
    private CacheTask cacheTask;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private ProxyServerConfig proxyServerConfig;

    @Autowired
    private ImpersonatingMitmManager mitmManager;

    @PostConstruct
    private void init() {

        HttpProxyServerBootstrap httpProxyServerBootstrap =DefaultHttpProxyServer
                .bootstrap()
                .withPort(proxyServerConfig.getPort())
                .withAllowLocalOnly(false)
                .plusActivityTracker(new DefaultActivityTracker(cacheTask))
                .withFiltersSource(new HttpFiltersSourceAdapter() {
                    @Override
                    public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                        return new AnswerRequestFilter(originalRequest, ctx, cacheManager);
                    }
                    @Override
                    public int getMaximumResponseBufferSizeInBytes() {
                        return proxyServerConfig.getMaximumResponseBufferSize();
                    }
                });
        if (proxyServerConfig.isSSLEnabled()) {
            httpProxyServerBootstrap.withManInTheMiddle(mitmManager);
        }
        httpProxyServerBootstrap.start();

        new Thread() {
            @Override
            public void run() {
                cacheTask.start();
            }
        }.start();
    }

    @Bean
    public HttpClient getHttpClient() {
        return new HttpClient();
    }

    public static void main(String[] args) {
        SpringApplication.run(ProxyServerApplication.class);
    }
}
