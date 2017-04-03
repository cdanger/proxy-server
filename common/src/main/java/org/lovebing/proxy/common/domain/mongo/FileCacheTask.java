package org.lovebing.proxy.common.domain.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * @author lovebing Created on Apr 2, 2017
 */

@Document
public class FileCacheTask {

    @Id
    private String id;
    private String requestUrl;
    private String cookie;
    private String userAgent;
    private Boolean done;
    private Instant createTime;
    private Instant doneTime;
    private Integer httpStatusCode;

    public void setId(String id) {
        this.id = id;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public void setDoneTime(Instant doneTime) {
        this.doneTime = doneTime;
    }

    public void setHttpStatusCode(Integer httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public Boolean getDone() {
        return done;
    }

    public String getId() {
        return id;
    }

    public Instant getDoneTime() {
        return doneTime;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getCookie() {
        return cookie;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getUserAgent() {
        return userAgent;
    }
}
