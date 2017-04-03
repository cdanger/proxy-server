package org.lovebing.proxy.service;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import org.lovebing.proxy.common.domain.mongo.FileCacheTask;
import org.lovebing.proxy.common.domain.mongo.UrlIndex;
import org.lovebing.proxy.config.ProxyCacheConfig;
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
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.time.Instant;


/**
 * @author lovebing Created on Apr 2, 2017
 */

@Service
public class CacheManager {

    private final Logger logger = LoggerFactory.getLogger(CacheManager.class);

    @Autowired
    private MongoOperations mongoOperations;
    @Autowired
    private ProxyCacheConfig proxyCacheConfig;

    public void createCacheTaskIfNecessary(HttpRequest originalRequest, ChannelHandlerContext ctx) {
        String originUrl = UrlUtil.getAbsoluteUrl(originalRequest, ctx);
        if (proxyCacheConfig.getFileTypes() == null) {
            return;
        }
        try {
            logger.info("originUrl={}", originUrl);
            URL url = new URL(originUrl);

            String urlId = UrlUtil.removeQueryString(originUrl);
            String extName = UrlUtil.getExtName(url.getPath());

            if (extName.length() == 0) {
                return;
            }
            boolean valid = false;
            for (String type : proxyCacheConfig.getFileTypes()) {
                if (type.equals(extName)) {
                    valid = true;
                    break;
                }
            }
            if (!valid) {
                return;
            }
            Query query = Query.query(Criteria.where("_id").is(urlId));
            FileCacheTask fileCacheTask = mongoOperations.findOne(query, FileCacheTask.class);
            if (fileCacheTask != null) {
                return;
            }
            fileCacheTask = new FileCacheTask();
            fileCacheTask.setId(urlId);
            fileCacheTask.setRequestUrl(originUrl);
            fileCacheTask.setUserAgent(originalRequest.headers().get(HttpHeaders.Names.USER_AGENT));
            fileCacheTask.setCookie(originalRequest.headers().get(HttpHeaders.Names.COOKIE));
            fileCacheTask.setCreateTime(Instant.now());
            fileCacheTask.setDone(false);
            mongoOperations.save(fileCacheTask);
        }
        catch (Exception e) {
            logger.error("addTask|msg={}", e.getMessage());
        }

    }

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
        try {
            InputStream inputStream = new FileInputStream(file);
            ByteBuf content = Unpooled.buffer();
            content.writeBytes(inputStream, (int) file.length());
            HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
            HttpHeaders.setHeader(response, HttpHeaders.Names.CONTENT_LENGTH, file.length());
            return response;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
