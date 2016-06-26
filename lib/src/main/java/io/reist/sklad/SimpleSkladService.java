package io.reist.sklad;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Reist on 24.06.16.
 */
public class SimpleSkladService implements SkladService {

    public static final int BUFFER_SIZE = 1024;

    private final Storage storage;
    private final EncryptionProvider encryptionProvider;

    public SimpleSkladService(
            @NonNull Storage storage,
            @NonNull EncryptionProvider encryptionProvider
    ) {
        this.storage = storage;
        this.encryptionProvider = encryptionProvider;
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    @Override
    public boolean save(@NonNull StorageObject storageObject) throws IOException {

        if (storageObject.isInputStreamDepleted()) {
            throw new IllegalStateException("Input stream of " + storageObject.getName() + " is depleted");
        }

        boolean overwritten = storage.contains(storageObject.getName());

        OutputStream outputStream = storage.openOutputStream(storageObject.getName());

        try {

            InputStream inputStream = storageObject.getInputStream();

            if (inputStream != null) {

                try {

                    byte[] buffer = new byte[BUFFER_SIZE];

                    while (true) {
                        int numRead = inputStream.read(buffer);
                        if (numRead == -1) {
                            break;
                        }
                        encryptionProvider.encrypt(buffer, 0, numRead);
                        outputStream.write(buffer, 0, numRead);
                    }

                } finally {
                    inputStream.close();
                    storageObject.setInputStreamDepleted(true);
                }

            }

            outputStream.flush();

        } finally {
            outputStream.close();
        }

        return overwritten;

    }

    @Override
    public StorageObject load(@NonNull String name) throws IOException {

        final InputStream inputStream = storage.openInputStream(name);

        if (inputStream == null) {
            return null;
        }

        InputStream wrappedStream = new InputStream() {

            private final byte[] singleByte = new byte[1];

            @Override
            public int read() throws IOException {
                int b = inputStream.read();
                if (b == -1) {
                    return -1;
                }
                singleByte[0] = (byte) b;
                return encryptionProvider.decrypt(singleByte, 0, 1);
            }

            @Override
            public int read(byte[] b) throws IOException {
                int numRead = inputStream.read(b);
                return encryptionProvider.decrypt(b, 0, numRead);
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                int numRead = inputStream.read(b, off, len);
                return encryptionProvider.decrypt(b, off, numRead);
            }

        };

        StorageObject result = new StorageObject(name);
        result.setInputStream(wrappedStream);
        return result;

    }

    @NonNull
    Storage getStorage() {
        return storage;
    }

    @NonNull
    EncryptionProvider getEncryptionProvider() {
        return encryptionProvider;
    }

}
