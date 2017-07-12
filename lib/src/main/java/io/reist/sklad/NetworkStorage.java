/*
 * Copyright (C) 2017 Renat Sarymsakov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.reist.sklad;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;

/**
 * Created by Reist on 28.06.16.
 */
public class NetworkStorage implements Storage {

    private final List<Request> activeRequests = new CopyOnWriteArrayList<>();

    private final OkHttpClient client;

    private final UrlResolver urlResolver;

    public NetworkStorage(UrlResolver urlResolver) {
        this(urlResolver, 10, 10, 10);
    }

    public NetworkStorage(
            UrlResolver urlResolver,
            final long connectTimeout,
            final long readTimeout,
            final long writeTimeout
    ) {

        this.urlResolver = urlResolver;

        this.client = new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .addNetworkInterceptor(new StuckOnReadHeadersInterceptor(readTimeout, this))
                .build();

    }

    private Response request(@NonNull String name) throws IOException {
        String url = urlResolver.toUrl(name);
        Request request = new Request.Builder().url(url).build();
        activeRequests.add(request);
        try {
            return client.newCall(request).execute();
        } catch (SocketException e) {
            if (!activeRequests.contains(request)) {
                throw new InterruptedIOException();
            } else {
                throw e;
            }
        }
    }

    @Override
    public boolean contains(@NonNull String id) throws IOException {
        return request(id).isSuccessful();
    }

    @NonNull
    @Override
    public OutputStream openOutputStream(@NonNull String id) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public InputStream openInputStream(@NonNull String id) throws IOException {
        final ResponseBody body = request(id).body();
        final long contentLength = body.contentLength();
        final BufferedSource source = body.source();
        return new InputStream() {

            private int position = 0;

            @Override
            public int read(byte[] b) throws IOException {
                int numRead = source.read(b);
                position += numRead;
                return numRead;
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                int numRead = source.read(b, off, len);
                position += numRead;
                return numRead;
            }

            @Override
            public long skip(long n) throws IOException {
                source.skip(n);
                position += n;
                return n;
            }

            @Override
            public int available() throws IOException {
                return (int) contentLength - position;
            }

            @Override
            public void close() throws IOException {
                body.close();
            }

            @Override
            public synchronized void mark(int readlimit) {
                throw new UnsupportedOperationException();
            }

            @Override
            public synchronized void reset() throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean markSupported() {
                return false;
            }

            @Override
            public int read() throws IOException {
                position++;
                return source.readByte() & 0xFF;
            }

        };
    }

    @Override
    public boolean delete(@NonNull String id) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll() throws IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * Forces a thread which performs socket reading to throw {@link ConnectException}
     * if {@link java.net.SocketTimeoutException} is not thrown within timeout interval.
     * See https://bugs.openjdk.java.net/browse/JDK-8049846
     */
    private static class StuckOnReadHeadersInterceptor implements Interceptor {

        private final List<Chain> activeChains = new CopyOnWriteArrayList<>();

        private final long readTimeout;
        private final NetworkStorage parent;

        StuckOnReadHeadersInterceptor(long readTimeout, NetworkStorage parent) {
            this.readTimeout = readTimeout;
            this.parent = parent;
        }

        @Override
        public Response intercept(final Chain chain) throws IOException {
            activeChains.add(chain);
            final Connection connection = chain.connection();
            (new Thread() {

                @SuppressWarnings("SuspiciousMethodCalls")
                @Override
                public void run() {

                    // wait more than just timeout to distinguish from real timeout
                    // in NetworkStorage.request
                    try {
                        sleep(readTimeout + 1000);
                    } catch (InterruptedException ignored) {}

                    try {
                         if (activeChains.contains(chain)) {

                            // tag refers to the original request
                            parent.activeRequests.remove(chain.request().tag());

                            try {
                                connection.socket().close();
                            } catch (IOException ignored) {}

                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                }

            }).start();
            Response proceed = chain.proceed(chain.request());
            activeChains.remove(chain);
            return proceed;
        }

    }

}
