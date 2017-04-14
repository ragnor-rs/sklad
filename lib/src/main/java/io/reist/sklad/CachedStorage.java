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
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Reist on 25.06.16.
 */
public class CachedStorage implements Storage {

    private static final String TAG = CachedStorage.class.getSimpleName();

    private final Storage source;
    private final Storage cache;

    private final CacheStatusStore cacheStatusStore;

    public CachedStorage(
            @NonNull Storage source,
            @NonNull Storage cache,
            @NonNull CacheStatusStore cacheStatusStore
    ) {

        this.source = source;
        this.cache = cache;

        this.cacheStatusStore = cacheStatusStore;

    }

    @Override
    public boolean contains(@NonNull String id) throws IOException {
        return cache.contains(id) || source.contains(id);
    }

    @NonNull
    @Override
    public OutputStream openOutputStream(@NonNull final String id) throws IOException {
        return source.openOutputStream(id);
    }

    @Override
    public InputStream openInputStream(@NonNull final String id) throws IOException {
        if (cacheStatusStore.isCached(id)) {

            Log.d(TAG, "Reading " + id + " from local cache");

            return cache.openInputStream(id);

        } else {

            final InputStream srcStream = source.openInputStream(id);

            if (srcStream == null) {
                return null;
            }

            final OutputStream dstStream = cache.openOutputStream(id);

            Log.d(TAG, "Reading " + id + " from remote storage");

            return new InputStream() {

                private boolean readFully = false;

                @Override
                public int read(@NonNull byte[] b) throws IOException {

                    int byteCount = srcStream.read(b);

                    if (byteCount == -1 || srcStream.available() == 0) {
                        readFully = true;
                    } else {
                        dstStream.write(b, 0, byteCount);
                    }

                    return byteCount;

                }

                @Override
                public int read(@NonNull byte[] b, int off, int len) throws IOException {

                    int byteCount = srcStream.read(b, off, len);

                    if (byteCount == -1 || srcStream.available() == 0) {
                        readFully = true;
                    } else {
                        dstStream.write(b, off, byteCount);
                    }

                    return byteCount;

                }

                @Override
                public int read() throws IOException {

                    int b = srcStream.read();

                    if (b == -1 || srcStream.available() == 0) {
                        readFully = true;
                    } else {
                        dstStream.write(b);
                    }

                    return b;

                }

                @Override
                public void close() throws IOException {
                    try {

                        srcStream.close();

                    } finally {

                        try {
                            dstStream.flush();
                        } finally {
                            dstStream.close();
                        }

                        if (readFully) {
                            cacheStatusStore.put(id, true);
                        } else {
                            cache.delete(id);
                        }

                    }
                }

                @Override
                public long skip(long n) throws IOException {
                    byte[] buffer = new byte[1024];
                    int byteCount;
                    long totalByteCount = 0;
                    while (n > totalByteCount && (byteCount = read(buffer, 0, (int) Math.min(buffer.length, n - totalByteCount))) > 0) {
                        totalByteCount += byteCount;
                    }
                    return totalByteCount;
                }

                @Override
                public int available() throws IOException {
                    return srcStream.available();
                }

                @Override
                public synchronized void reset() throws IOException {
                    throw new UnsupportedOperationException();
                }

                @Override
                public synchronized void mark(int readlimit) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public boolean markSupported() {
                    return false;
                }

            };

        }
    }

    @Override
    public boolean delete(@NonNull String id) throws IOException {
        return source.delete(id) && purge(id);
    }

    @Override
    public void deleteAll() throws IOException {
        cache.deleteAll();
        try {
            source.deleteAll();
        } catch (UnsupportedOperationException ignored) {}
    }

    @NonNull
    Storage getCache() {
        return cache;
    }

    @NonNull
    Storage getSource() {
        return source;
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    public void cache(String id) throws IOException {

        if (cacheStatusStore.isCached(id)) {
            return;
        }

        byte[] buffer = new byte[1024];

        InputStream inputStream = source.openInputStream(id);

        if (inputStream == null) {
            throw new IllegalStateException("Input stream is null");
        }

        try {

            OutputStream outputStream = cache.openOutputStream(id);

            try {

                while (true) {
                    int numRead = inputStream.read(buffer);
                    if (numRead == -1) {
                        break;
                    }
                    outputStream.write(buffer, 0, numRead);
                }

                outputStream.flush();

            } finally {
                outputStream.close();
            }

            cacheStatusStore.put(id, true);

        } finally {
            inputStream.close();
        }

    }

    public boolean purge(@NonNull String id) throws IOException {
        boolean result = cache.delete(id);
        if (result) {
            cacheStatusStore.put(id, false);
        }
        return result;
    }

    CacheStatusStore getCacheStatusStore() {
        return cacheStatusStore;
    }

}
