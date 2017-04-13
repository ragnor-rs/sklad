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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Reist on 25.06.16.
 */
public class CachedStorage implements Storage {

    private static final String TAG = CachedStorage.class.getSimpleName();

    private final Storage remoteStorage;
    private final Storage localStorage;

    private final CacheStatusHolder cacheStatusHolder;

    public CachedStorage(
            @NonNull Storage remoteStorage,
            @NonNull Storage localStorage,
            @NonNull CacheStatusHolder cacheStatusHolder
    ) {

        this.remoteStorage = remoteStorage;
        this.localStorage = localStorage;

        this.cacheStatusHolder = cacheStatusHolder;

    }

    @Override
    public boolean contains(@NonNull String id) throws IOException {
        return containsInLocalStorage(id) || containsInRemoteStorage(id);
    }

    public boolean containsInRemoteStorage(@NonNull String id) throws IOException {
        return remoteStorage.contains(id);
    }

    public boolean containsInLocalStorage(@NonNull String id) throws IOException {
        return localStorage.contains(id);
    }

    @NonNull
    @Override
    public OutputStream openOutputStream(@NonNull final String id) throws IOException {

        final OutputStream localStream = localStorage.openOutputStream(id);
        final OutputStream remoteStream = remoteStorage.openOutputStream(id);

        return new OutputStream() {

            @Override
            public void write(int i) throws IOException {
                localStream.write(i);
                remoteStream.write(i);
            }

            @Override
            public void write(byte[] b) throws IOException {
                localStream.write(b);
                remoteStream.write(b);
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                localStream.write(b, off, len);
                remoteStream.write(b, off, len);
            }

            @Override
            public void flush() throws IOException {

                localStream.flush();

                try {
                    remoteStream.flush();
                } finally {
                    cacheStatusHolder.put(id, true);
                }

            }

            @Override
            public void close() throws IOException {
                localStream.close();
                remoteStream.close();
            }

        };

    }

    @Override
    public InputStream openInputStream(@NonNull final String id) throws IOException {
        if (containsInLocalStorage(id)) {

            Log.d(TAG, "Reading " + id + " from local cache");

            return localStorage.openInputStream(id);

        } else if (containsInRemoteStorage(id)) {

            Log.d(TAG, "Reading " + id + " from remote storage");

            final InputStream srcStream = remoteStorage.openInputStream(id);
            if (srcStream == null) {
                throw new IllegalStateException("Remote stream is null");
            }

            final OutputStream dstStream = localStorage.openOutputStream(id);

            return new InputStream() {

                private void closeDstStream(boolean finished) throws IOException {

                    if (cacheStatusHolder.isCached(id)) {
                        return;
                    }

                    try {
                        dstStream.flush();
                    } finally {
                        dstStream.close();
                    }

                    if (finished) {
                        cacheStatusHolder.put(id, true);
                    } else {
                        localStorage.delete(id);
                    }

                }

                @Override
                public int read(@NonNull byte[] b) throws IOException {

                    int byteCount;

                    try {
                        byteCount = srcStream.read(b);
                    } catch (EOFException e) {
                        closeDstStream(true);
                        throw e;
                    }

                    if (byteCount == -1) {
                        closeDstStream(true);
                    } else {
                        dstStream.write(b, 0, byteCount);
                    }

                    return byteCount;

                }

                @Override
                public int read(@NonNull byte[] b, int off, int len) throws IOException {

                    int byteCount;

                    try {
                        byteCount = srcStream.read(b, off, len);
                    } catch (EOFException e) {
                        closeDstStream(true);
                        throw e;
                    }

                    if (byteCount == -1) {
                        closeDstStream(true);
                    } else {
                        dstStream.write(b, off, byteCount);
                    }

                    return byteCount;

                }

                @Override
                public int read() throws IOException {

                    int b;

                    try {
                        b = srcStream.read();
                    } catch (EOFException e) {
                        closeDstStream(true);
                        throw e;
                    }

                    if (b == -1) {
                        closeDstStream(true);
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
                        closeDstStream(false);
                    }
                }

                @Override
                public synchronized void mark(int readlimit) {
                    srcStream.mark(readlimit);
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
                public long skip(long n) throws IOException {
                    throw new UnsupportedOperationException();
                }

                @Override
                public boolean markSupported() {
                    return false;
                }

            };

        } else {
            return null;
        }
    }

    @Override
    public boolean delete(@NonNull String id) throws IOException {
        return remoteStorage.delete(id) && purge(id);
    }

    @Override
    public void deleteAll() throws IOException {
        localStorage.deleteAll();
        try {
            remoteStorage.deleteAll();
        } catch (UnsupportedOperationException ignored) {}
    }

    @NonNull
    Storage getLocalStorage() {
        return localStorage;
    }

    @NonNull
    Storage getRemoteStorage() {
        return remoteStorage;
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    public void download(String name) throws IOException {

        byte[] buffer = new byte[1024];

        OutputStream outputStream = localStorage.openOutputStream(name);

        try {

            InputStream inputStream = remoteStorage.openInputStream(name);

            if (inputStream == null) {
                throw new IllegalStateException("Input stream is null");
            }

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
                inputStream.close();
            }

        } finally {
            outputStream.close();
        }

    }

    public boolean purge(@NonNull String id) throws IOException {
        boolean result = localStorage.delete(id);
        if (result) {
            cacheStatusHolder.put(id, false);
        }
        return result;
    }

}
