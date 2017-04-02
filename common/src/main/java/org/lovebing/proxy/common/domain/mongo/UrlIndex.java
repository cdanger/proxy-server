package org.lovebing.proxy.common.domain.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * @author lovebing Created on Apr 2, 2017
 */
@Document
public class UrlIndex {

    @Id
    private String id;
    private Integer size;
    private String mime;
    private String savePath;
    private Instant createTime;


    public void setId(String id) {
        this.id = id;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public Integer getSize() {
        return size;
    }

    public String getMime() {
        return mime;
    }

    public String getSavePath() {
        return savePath;
    }

    public Instant getCreateTime() {
        return createTime;
    }
}
