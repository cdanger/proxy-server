package org.lovebing.proxy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.lovebing.proxy.common.domain.mongo.UrlIndex;
import org.lovebing.proxy.config.ProxyCacheConfig;
import org.lovebing.proxy.service.CacheTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;

/**
 * @author lovebing Created on Apr 1, 2017
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoTest {

    private Logger logger = LoggerFactory.getLogger(MongoTest.class);

    @Autowired
    private MongoOperations mongoOperations;
    @Autowired
    private CacheTask cacheTask;

    @Test
    public void addUrlIndex() {

        UrlIndex urlIndex = new UrlIndex();
        urlIndex.setId("https://www.google.com");
        urlIndex.setMime("text/html");
        urlIndex.setSize(10240);
        urlIndex.setSavePath("/data/proxy/cache/google");
        urlIndex.setCreateTime(Instant.now());
        mongoOperations.save(urlIndex);

    }

    @Test
    public void task() {
        cacheTask.stop();
        cacheTask.start();
        try {
            Thread.sleep(60 * 1000);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
