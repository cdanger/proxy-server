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
    private Boolean done;
    private Instant createTime;
    private Instant doneTime;

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
}
