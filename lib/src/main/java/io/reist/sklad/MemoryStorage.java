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
    public OutputStream openOutputStream(final String name) {
        return new ByteArrayOutputStream() {

            @Override
            public void flush() throws IOException {
                super.flush();
                dataMap.put(name, new DataHolder(toByteArray(), size()));
            }

        };
    }

    @Override
    public boolean contains(@NonNull String name) {
        return dataMap.containsKey(name);
    }

    @Override
    public InputStream openInputStream(String name) {
        DataHolder dataHolder = dataMap.get(name);
        return dataHolder == null ?
                null :
                new ByteArrayInputStream(dataHolder.data, 0, dataHolder.length);
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