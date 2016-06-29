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

    public SimpleSkladService(@NonNull Storage storage) {
        this.storage = storage;
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    @Override
    public boolean save(@NonNull StorageObject storageObject) throws IOException {

        if (storageObject.isInputStreamDepleted()) {
            throw new IllegalStateException("Input stream of " + storageObject.getId() + " is depleted");
        }

        boolean overwritten = storage.contains(storageObject.getId());

        OutputStream outputStream = storage.openOutputStream(storageObject.getId());

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
    public StorageObject load(@NonNull String id) throws IOException {

        final InputStream inputStream = storage.openInputStream(id);

        if (inputStream == null) {
            return null;
        }

        return new StorageObject(id, inputStream);

    }

    @NonNull
    Storage getStorage() {
        return storage;
    }

}
