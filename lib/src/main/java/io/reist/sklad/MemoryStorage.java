package io.reist.sklad;

import android.support.annotation.NonNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class MemoryStorage implements Storage {

    private final Map<String, DataHolder> dataMap = new HashMap<>();

    @Override
    public boolean contains(@NonNull String name) {
        return dataMap.containsKey(name);
    }

    @NonNull
    @Override
    public OutputStream openOutputStream(@NonNull final String name) {
        return new ByteArrayOutputStream() {

            @Override
            public void flush() throws IOException {
                super.flush();
                dataMap.put(name, new DataHolder(toByteArray(), size()));
            }

        };
    }

    @Override
    public InputStream openInputStream(@NonNull String name) {
        DataHolder dataHolder = dataMap.get(name);
        return dataHolder == null ?
                null :
                new ByteArrayInputStream(dataHolder.data, 0, dataHolder.length);
    }

    @NonNull
    Map<String, DataHolder> getDataMap() {
        return dataMap;
    }

    static class DataHolder {

        final byte[] data;
        final int length;

        public DataHolder(byte[] data, int length) {
            this.data = data;
            this.length = length;
        }

    }

}