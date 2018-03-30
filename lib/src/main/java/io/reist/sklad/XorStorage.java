package io.reist.sklad;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Reist on 26.10.16.
 */

public class XorStorage implements JournalingStorage {

    private final int encryptionBufferSize;
    private final int encryptionStepDenominator;

    private final JournalingStorage journalingStorage;
    private final KeyProvider keyProvider;

    public XorStorage(
            int encryptionBufferSize,
            int encryptionStepDenominator,
            JournalingStorage journalingStorage,
            KeyProvider keyProvider
    ) {
        this.encryptionBufferSize = encryptionBufferSize;
        this.encryptionStepDenominator = encryptionStepDenominator;
        this.journalingStorage = journalingStorage;
        this.keyProvider = keyProvider;
    }

    @Override
    public boolean contains(@NonNull String id) throws IOException {
        return journalingStorage.contains(id);
    }

    @NonNull
    @Override
    public OutputStream openOutputStream(@NonNull String id) throws IOException {

        final OutputStream wrappedStream = journalingStorage.openOutputStream(id);

        final byte[] encryptionKey = keyProvider.get();

        return new OutputStream() {

            private long pos = 0;

            @Override
            public void write(byte[] b) throws IOException {
                write(b, 0, b.length);
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                byte[] out = new byte[len];
                for (int i = 0; i < len; i++) {
                    out[i] = xorIfNeeded(pos + i, b[off + i], encryptionKey);
                }
                wrappedStream.write(out, 0, len);
                pos += len;
            }

            @Override
            public void flush() throws IOException {
                wrappedStream.flush();
            }

            @Override
            public void close() throws IOException {
                wrappedStream.close();
            }

            @Override
            public void write(int b) throws IOException {
                wrappedStream.write(xorIfNeeded(pos, (byte) b, encryptionKey));
                pos++;
            }

        };

    }

    @Nullable
    @Override
    public InputStream openInputStream(@NonNull String id) throws IOException {

        final InputStream wrappedStream = journalingStorage.openInputStream(id);

        if (wrappedStream == null) {
            return null;
        }

        final byte[] encryptionKey = keyProvider.get();

        return new InputStream() {

            private long pos = 0;

            @Override
            public int read(byte[] b) throws IOException {
                return read(b, 0, b.length);
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                int read = wrappedStream.read(b, off, len);
                for (int i = 0; i < read; i++) {
                    b[off + i] = xorIfNeeded(pos + i, b[off + i], encryptionKey);
                }
                pos += read;
                return read;
            }

            @Override
            public long skip(long n) throws IOException {
                long skipped = wrappedStream.skip(n);
                pos += skipped;
                return skipped;
            }

            @Override
            public int available() throws IOException {
                return wrappedStream.available();
            }

            @Override
            public void close() throws IOException {
                wrappedStream.close();
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
                int b = wrappedStream.read();
                long currentPos = pos;
                try {
                    return xorIfNeeded(currentPos, (byte) b, encryptionKey) & 0xFF;
                } finally {
                    pos++;
                }
            }

        };

    }

    private byte xorIfNeeded(long pos, byte b, byte[] encryptionKey) {
        long leftBoundary = (pos / encryptionBufferSize) * encryptionBufferSize;
        int lengthToEncrypt = encryptionBufferSize / encryptionStepDenominator;
        long rightBoundary = leftBoundary + lengthToEncrypt - 1;
        if (leftBoundary <= pos && pos <= rightBoundary) {
            int keyPos = (int) ((pos - leftBoundary) % encryptionKey.length);
            return (byte) (b ^ encryptionKey[keyPos]);
        } else {
            return b;
        }
    }

    @Override
    public boolean delete(@NonNull String id) throws IOException {
        return journalingStorage.delete(id);
    }

    @Override
    public void deleteAll() throws IOException {
        journalingStorage.deleteAll();
    }

    @Override
    public long getUsedSpace() {
        return journalingStorage.getUsedSpace();
    }

    @Override
    public String getOldestId() {
        return journalingStorage.getOldestId();
    }

    public interface KeyProvider {
        byte[] get();
    }

}
