package org.lovebing.proxy.util;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.ssl.SslHandler;

import java.net.URI;

/**
 * @author lovebing Created on Apr 1, 2017
 */
public class UrlUtil {

    public static String removeQueryString(String url) {
        try {
            URI uri = new URI(url);
            StringBuilder stringBuilder = new StringBuilder();
            if (uri.getScheme() != null) {
                stringBuilder.append(uri.getScheme());
                stringBuilder.append("://");
            }
            if (uri.getHost() != null) {
                stringBuilder.append(uri.getHost());
            }
            if (uri.getPort() > 0) {
                stringBuilder.append(":");
                stringBuilder.append(uri.getPort());
            }
            stringBuilder.append(uri.getPath());
            return stringBuilder.toString();
        }
        catch (Exception e) {
            return url;
        }
    }

    public static String getAbsoluteUrl(HttpRequest originalRequest, ChannelHandlerContext ctx) {
        String url = originalRequest.getUri();
        if (!url.startsWith("http")) {
            String host = originalRequest.headers().get(HttpHeaders.Names.HOST);
            if (ctx != null && ctx.pipeline().get(SslHandler.class) != null) {
                url = "https://" + host + url;
            }
            else {
                url = "http://" + host + url;
            }
        }
        return url;
    }
}
