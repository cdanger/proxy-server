package org.lovebing.proxy.service;

import com.google.common.util.concurrent.RateLimiter;
import org.lovebing.proxy.common.component.HttpClient;
import org.lovebing.proxy.common.domain.mongo.FileCacheTask;
import org.lovebing.proxy.common.domain.mongo.UrlIndex;
import org.lovebing.proxy.config.ProxyCacheConfig;
import org.lovebing.proxy.util.UrlUtil;
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
            return;
        }
        running = true;

        while (true) {
            if (!processTask()) {
                break;
            }
        }
        running = false;
    }

    public void addTask(String originUrl) {
        if (proxyCacheConfig.getFileTypes() == null) {
            return;
        }
        try {
            logger.info("originUrl={}", originUrl);
            URL url = new URL(originUrl);

            String urlId = UrlUtil.removeQueryString(originUrl);
            String file = url.getPath();
            int lastDotIndex = file.lastIndexOf(".");
            if (lastDotIndex == -1) {
                return;
            }
            String extName = file.substring(lastDotIndex);

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
            fileCacheTask.setCreateTime(Instant.now());
            fileCacheTask.setDone(false);
            mongoOperations.save(fileCacheTask);
            if (!running) {
                start();
            }
        }
        catch (Exception e) {
            logger.error("addTask|msg={}", e.getMessage());
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
        File tempFile = new File(path);
        if (!tempFile.exists()) {
            tempFile.mkdirs();
        }

        String savePath = path + "/cache";

        InputStream inputStream = httpClient.executeWithStream(fileCacheTask.getId());
        FileOutputStream outputStream = new FileOutputStream(savePath);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }
        outputStream.close();
        inputStream.close();

        UrlIndex urlIndex = new UrlIndex();
        urlIndex.setId(fileCacheTask.getId());
        urlIndex.setSavePath(savePath);
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
