package io.reist.sklad;

import android.support.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Reist on 24.06.16.
 */
public class SimpleSkladService implements SkladService {

    private final Map<String, DataHolder> dataMap = new HashMap<>();

    @Override
    public boolean save(@NonNull StorageObject storageObject) throws IOException {

        if (storageObject.isInputStreamDepleted()) {
            throw new IllegalStateException("Input stream of " + storageObject.getName() + " is depleted");
        }

        byte[] data = new byte[1024];
        int length = 0;

        InputStream inputStream = storageObject.getInputStream();

        if (inputStream != null) {

            BufferedInputStream bufferedInputStream = null;

            try {

                bufferedInputStream = new BufferedInputStream(inputStream);

                int b;
                while ((b = bufferedInputStream.read()) != -1) {

                    if (length == data.length) {
                        byte[] newData = new byte[data.length * 2];
                        System.arraycopy(data, 0, newData, 0, length);
                        data = newData;
                    }

                    data[length] = (byte) b;
                    length++;

                }

            } finally {

                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }

            }

            storageObject.setInputStreamDepleted(true);

        }

        boolean overwritten = dataMap.containsKey(storageObject.getName());

        dataMap.put(storageObject.getName(), new DataHolder(data, length));

        return overwritten;

    }

    @Override
    public StorageObject load(@NonNull String name) {

        DataHolder dataHolder = dataMap.get(name);

        if (dataHolder == null) {
            return null;
        }

        StorageObject result = new StorageObject(name);
        result.setInputStream(new ByteArrayInputStream(dataHolder.data, 0, dataHolder.length));
        return result;

    }

    private static class DataHolder {

        private final byte[] data;
        private final int length;

        public DataHolder(byte[] data, int length) {
            this.data = data;
            this.length = length;
        }

    }

}
