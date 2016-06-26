package io.reist.sklad;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Reist on 25.06.16.
 */
public class CachedStorage implements Storage {
    
    private final Storage remoteStorage;
    private final Storage localStorage;

    public CachedStorage(@NonNull Storage remoteStorage, @NonNull Storage localStorage) {
        this.remoteStorage = remoteStorage;
        this.localStorage = localStorage;
    }

    @Override
    public boolean contains(@NonNull String name) {
        return containsInLocalStorage(name) || containsInRemoteStorage(name);
    }

    public boolean containsInRemoteStorage(@NonNull String name) {
        return remoteStorage.contains(name);
    }

    public boolean containsInLocalStorage(@NonNull String name) {
        return localStorage.contains(name);
    }

    @NonNull
    @Override
    public OutputStream openOutputStream(@NonNull String name) throws IOException {

        final OutputStream localStream = localStorage.openOutputStream(name);
        final OutputStream remoteStream = remoteStorage.openOutputStream(name);

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

        };

    }

    @Override
    public InputStream openInputStream(@NonNull String name) throws IOException {
        if (containsInLocalStorage(name)) {
            return localStorage.openInputStream(name);
        } else if (containsInRemoteStorage(name)) {

            final InputStream remoteStream = remoteStorage.openInputStream(name);
            final OutputStream localStream = remoteStorage.openOutputStream(name);

            if (remoteStream == null) {
                throw new IllegalStateException("Remote stream is null");
            }

            return new InputStream() {

                @Override
                public int read() throws IOException {
                    int r = remoteStream.read();
                    localStream.write(r);
                    return r;
                }

                @Override
                public int read(byte[] b) throws IOException {
                    int r = remoteStream.read(b);
                    localStream.write(b);
                    return r;
                }

                @Override
                public int read(byte[] b, int off, int len) throws IOException {
                    int r = remoteStream.read(b, off, len);
                    localStream.write(b, off, len);
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
                }

            };

        } else {
            return null;
        }
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
    public void cache(String name) throws IOException {

        byte[] buffer = new byte[1024];

        OutputStream outputStream = openOutputStream(name);

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

}
