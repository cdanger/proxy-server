package org.lovebing.proxy.service;

import com.google.common.util.concurrent.RateLimiter;
import org.lovebing.proxy.common.component.HttpClient;
import org.lovebing.proxy.common.domain.mongo.FileCacheTask;
import org.lovebing.proxy.common.domain.mongo.UrlIndex;
import org.lovebing.proxy.common.exception.HttpException;
import org.lovebing.proxy.config.ProxyCacheConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.util.List;

/**
 * @author lovebing Created on Apr 2, 2017
 */
@Service
public class CacheTask {

    private static volatile boolean running = false;
    private static final int MONGO_DATA_LIMIT = 100;

    private final RateLimiter rateLimiter = RateLimiter.create(1);
    private Logger logger = LoggerFactory.getLogger(CacheTask.class);
    @Autowired
    private MongoOperations mongoOperations;
    @Autowired
    private HttpClient httpClient;
    @Autowired
    private ProxyCacheConfig proxyCacheConfig;

    public void start() {
        if (running) {
            logger.warn("task is already running");
            return;
        }
        running = true;
        try {
            while (true) {
                if (!running) {
                    logger.warn("task is stop, break");
                    break;
                }
                logger.warn("processTask");
                if (processTask()) {
                    logger.warn("next");
                    continue;
                }
                try {
                    logger.warn("no records, sleep 10s");
                    Thread.sleep(10000);
                }
                catch (Exception e){
                    logger.warn("task break");
                    break;
                }
            }
        }
        catch (Exception e) {
            logger.warn("Exception|msg={}", e.getMessage());
            e.printStackTrace();
        }
        finally {
            logger.warn("task stop");
            running = false;
        }
    }

    public void stop() {
        running = false;
    }

    public boolean processTask() {
        logger.info("processTask");
        List<FileCacheTask> list = getFileCacheTaskList();
        list.forEach(fileCacheTask -> {
            try {
                executeTask(fileCacheTask);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
        return list.size() > 0;
    }

    private void executeTask(FileCacheTask fileCacheTask) throws IOException {
        rateLimiter.acquire();
        URL url = new URL(fileCacheTask.getId());

        String path = proxyCacheConfig.getCachePath() + "/" + url.getHost() + url.getPath();
        String[] pathInfo = path.split("/");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < pathInfo.length - 1; i++) {
            stringBuilder.append("/").append(pathInfo[i]);
        }
        File tempFile = new File(stringBuilder.toString());
        if (!tempFile.exists()) {
            if (!tempFile.mkdirs()) {
                logger.error("创建目录失败|path={}", stringBuilder.toString());
            }
        }
        InputStream inputStream;
        try {
            inputStream = httpClient.executeWithStream(fileCacheTask.getId());
        }
        catch (HttpException e) {
            logger.error("msg={}|code={}", e.getMessage(), e.getCode());
            fileCacheTask.setDone(true);
            fileCacheTask.setDoneTime(Instant.now());
            fileCacheTask.setHttpStatusCode(e.getCode());
            mongoOperations.save(fileCacheTask);
            return;
        }
        FileOutputStream outputStream = new FileOutputStream(path);
        byte[] buffer = new byte[1024];
        int length;
        try {
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
        }
        finally {
            outputStream.close();
            inputStream.close();
        }

        UrlIndex urlIndex = new UrlIndex();
        urlIndex.setId(fileCacheTask.getId());
        urlIndex.setSavePath(path);
        urlIndex.setCreateTime(Instant.now());
        mongoOperations.save(urlIndex);

        fileCacheTask.setDone(true);
        fileCacheTask.setDoneTime(Instant.now());
        mongoOperations.save(fileCacheTask);
    }

    private List<FileCacheTask> getFileCacheTaskList() {
        return mongoOperations.find(Query.query(Criteria.where("done").is(false)).limit(MONGO_DATA_LIMIT), FileCacheTask.class);
    }

}
