package org.lovebing.proxy.service;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import org.lovebing.proxy.common.domain.mongo.UrlIndex;
import org.lovebing.proxy.config.HttpServiceConfig;
import org.lovebing.proxy.util.HostUtil;
import org.lovebing.proxy.util.UrlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URI;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @author lovebing Created on Apr 2, 2017
 */

@Service
public class CacheManager {

    private final Logger logger = LoggerFactory.getLogger(CacheManager.class);

    private final Sync sync = new Sync();
    @Autowired
    private MongoOperations mongoOperations;
    @Autowired
    private HttpServiceConfig httpServiceConfig;

    public HttpResponse createResponse(HttpRequest originalRequest, ChannelHandlerContext ctx) {

        String originUrl = UrlUtil.getAbsoluteUrl(originalRequest, ctx);
        try {
            URI uri = new URI(originUrl);
            if (uri.getHost() == HostUtil.getHostAddress()) {
                return null;
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        String url = UrlUtil.removeQueryString(originUrl);
        UrlIndex urlIndex = mongoOperations.findOne(Query.query(Criteria.where("_id").is(url)), UrlIndex.class);

        if (urlIndex == null) {
            return null;
        }

        File file = new File(urlIndex.getSavePath());
        if (!file.isFile()) {
            return null;
        }

        HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
        HttpHeaders.setHeader(response, HttpHeaders.Names.LOCATION, getFileDownloadUrl(urlIndex.getSavePath()));
        return response;
    }

    private String getFileDownloadUrl(String path) {
        String host;
        try {
            host = HostUtil.getHostAddress();
        }
        catch (Exception e) {
            host = "localhost";
        }
        return "http://" + host + ":" + httpServiceConfig.getPort() + "/file/download/?path=" + path;
    }

    private static class Sync extends AbstractQueuedSynchronizer {
        boolean isOpened() {
            return getState() == 1;
        }

        @Override
        protected int tryAcquireShared(int ignore) {
            return isOpened() ? 1 : -1;
        }

        @Override
        protected boolean tryReleaseShared(int status) {
            setState(status);
            return status == 1;
        }
    }

}
