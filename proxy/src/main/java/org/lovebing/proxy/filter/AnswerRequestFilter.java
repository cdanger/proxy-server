package org.lovebing.proxy.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.lovebing.proxy.service.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lovebing Created on Apr 1, 2017
 */
public class AnswerRequestFilter extends HttpFiltersAdapter {
    private final Logger logger = LoggerFactory.getLogger(AnswerRequestFilter.class);

    private CacheManager cacheManager;

    public AnswerRequestFilter(HttpRequest originalRequest, ChannelHandlerContext ctx, CacheManager cacheManager) {
        super(originalRequest, ctx);
        this.cacheManager = cacheManager;
    }

    @Override
    public HttpResponse clientToProxyRequest(HttpObject httpObject) {
        return getCacheResponse();
    }

    private HttpResponse getCacheResponse() {
        if (!originalRequest.getMethod().equals(HttpMethod.GET)) {
            return null;
        }
        return cacheManager.createResponse(originalRequest, ctx);
    }
}
