package org.lovebing.proxy;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.littleshoot.proxy.ActivityTracker;
import org.littleshoot.proxy.FlowContext;
import org.littleshoot.proxy.FullFlowContext;
import org.lovebing.proxy.service.CacheTask;
import org.lovebing.proxy.util.UrlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.net.ssl.SSLSession;
import java.net.InetSocketAddress;

/**
 * @author lovebing Created on Apr 1, 2017
 */
public class DefaultActivityTracker implements ActivityTracker {

    private final Logger logger = LoggerFactory.getLogger(DefaultActivityTracker.class);

    private CacheTask cacheTask;

    public DefaultActivityTracker(CacheTask cacheTask) {
        this.cacheTask = cacheTask;
    }

    @Override
    public void clientConnected(InetSocketAddress clientAddress) {

    }

    @Override
    public void clientSSLHandshakeSucceeded(InetSocketAddress clientAddress, SSLSession sslSession) {

    }

    @Override
    public void clientDisconnected(InetSocketAddress clientAddress, SSLSession sslSession) {

    }

    @Override
    public void bytesReceivedFromClient(FlowContext flowContext, int numberOfBytes) {

    }

    @Override
    public void requestReceivedFromClient(FlowContext flowContext, HttpRequest httpRequest) {
    }

    @Override
    public void bytesSentToServer(FullFlowContext flowContext, int numberOfBytes) {

    }

    @Override
    public void requestSentToServer(FullFlowContext flowContext, HttpRequest httpRequest) {

    }

    @Override
    public void bytesReceivedFromServer(FullFlowContext flowContext, int numberOfBytes) {

    }

    @Override
    public void responseReceivedFromServer(FullFlowContext flowContext, HttpResponse httpResponse) {

    }

    @Override
    public void bytesSentToClient(FlowContext flowContext, int numberOfBytes) {

    }

    @Override
    public void responseSentToClient(FlowContext flowContext, HttpResponse httpResponse) {

    }
}
