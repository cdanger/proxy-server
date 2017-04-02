package org.lovebing.proxy.common.component;

import okhttp3.*;
import okio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author lovebing Created on Apr 1, 2017
 */
public class HttpClient {

    private Logger logger = LoggerFactory.getLogger(HttpClient.class);
    private OkHttpClient okHttpClient;

    private static final String USER_AGENT_IPHONE = "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1";
    private static final String USER_AGENT_ANDROID = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.98 Mobile Safari/537.36";
    private static final String USER_AGENT_PC = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.98 Safari/537.36";

    private final ProgressListener progressListener = (long bytesRead, long contentLength, boolean done) -> {
        double percent = bytesRead / contentLength * 100;
        logger.info("percent={}", percent);
    };

    public HttpClient() {
        createOkHttpClient();
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public InputStream executeWithStream (String url) throws IOException {
        Request request = new Request.Builder().url(url).get().build();
        return getOkHttpClient().newCall(request).execute().body().byteStream();
    }

    private void createOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(chain -> {
            Request request = chain.request().newBuilder()
                    .removeHeader("User-Agent")
                    .addHeader("User-Agent", USER_AGENT_PC)
                    .addHeader("Connection", "keep-alive")
                    .build();
            return chain.proceed(request);
        });
        builder.followRedirects(true);
        builder.addNetworkInterceptor(chain -> {
            Response originalResponse = chain.proceed(chain.request());
            return originalResponse
                    .newBuilder()
                    .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                    .build();
        });
        okHttpClient = builder.build();
    }

    private static class ProgressResponseBody extends ResponseBody {
        private final ResponseBody responseBody;
        private final ProgressListener progressListener;
        private BufferedSource bufferedSource;

        public ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
            this.responseBody = responseBody;
            this.progressListener = progressListener;
        }

        @Override
        public MediaType contentType() {
            return responseBody.contentType();
        }


        @Override
        public long contentLength() {
            return responseBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()));
            }
            return bufferedSource;
        }

        private Source source(Source source) {
            return new ForwardingSource(source) {
                long totalBytesRead = 0L;

                @Override public long read(Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                    progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                    return bytesRead;
                }
            };
        }
    }

    interface ProgressListener {
        void update(long bytesRead, long contentLength, boolean done);
    }
}
