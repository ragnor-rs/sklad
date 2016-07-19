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

    private final Storage remoteStorage;
    private final Storage localStorage;

    public CachedStorage(@NonNull Storage remoteStorage, @NonNull Storage localStorage) {
        this.remoteStorage = remoteStorage;
        this.localStorage = localStorage;
    }

    @Override
    public boolean contains(@NonNull String id) throws IOException {
        return containsInLocalStorage(id) || containsInRemoteStorage(id);
    }

    public boolean containsInRemoteStorage(@NonNull String name) throws IOException {
        return remoteStorage.contains(name);
    }

    public boolean containsInLocalStorage(@NonNull String name) throws IOException {
        return localStorage.contains(name);
    }

    @NonNull
    @Override
    public OutputStream openOutputStream(@NonNull String id) throws IOException {

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
                remoteStream.flush();
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

            final InputStream remoteStream = remoteStorage.openInputStream(id);
            final OutputStream localStream = localStorage.openOutputStream(id);

            if (remoteStream == null) {
                throw new IllegalStateException("Remote stream is null");
            }

            return new InputStream() {

                public boolean skipped;

                @Override
                public int read() throws IOException {
                    int r = remoteStream.read();
                    if (!skipped) {
                        localStream.write(r); // cache iff no skips happened
                    }
                    return r;
                }

                @Override
                public int read(byte[] b) throws IOException {
                    int r = remoteStream.read(b);
                    if (!skipped) {
                        localStream.write(b); // cache iff no skips happened
                    }
                    return r;
                }

                @Override
                public int read(byte[] b, int off, int len) throws IOException {
                    int r = remoteStream.read(b, off, len);
                    if (!skipped) {
                        localStream.write(b, off, len);
                    }
                    return r;
                }

                @Override
                public void close() throws IOException {

                    try {
                        localStream.flush();
                    } finally {
                        localStream.close();
                        remoteStream.close();
                    }

                    // todo allow partial caching - https://dreams.atlassian.net/browse/ZAN-674
                    // don't leave partial files
                    if (available() > 0 && !skipped) {
                        removeCachedObject();
                    }

                }

                private void removeCachedObject() throws IOException {
                    if (localStorage.delete(id)) {
                        Log.d(TAG, "Removed partial cache for " + id);
                    } else if (localStorage.contains(id)) {
                        throw new IOException("Cannot wipe partially cached object");
                    } else {
                        Log.d(TAG, "No cached objects for " + id);
                    }
                }

                @Override
                public long skip(long n) throws IOException {
                    if (!skipped && n > 0) {
                        skipped = true;
                        Log.d(TAG, "Skipped " + n + " bytes in " + id);
                        removeCachedObject();
                    }
                    return remoteStream.skip(n);
                }

                @Override
                public int available() throws IOException {
                    return remoteStream.available();
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

            };

        } else {
            return null;
        }
    }

    @Override
    public boolean delete(@NonNull String id) throws IOException {
        return remoteStorage.delete(id) && purge(id);
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

    }

    public boolean purge(@NonNull String id) throws IOException {
        return localStorage.delete(id);
    }

}
