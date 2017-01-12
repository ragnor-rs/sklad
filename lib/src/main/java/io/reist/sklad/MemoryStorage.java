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
    public boolean contains(@NonNull String id) {
        return dataMap.containsKey(id);
    }

    @NonNull
    @Override
    public OutputStream openOutputStream(@NonNull final String id) {
        return new ByteArrayOutputStream() {

            @Override
            public void flush() throws IOException {
                super.flush();
                dataMap.put(id, new DataHolder(toByteArray(), size()));
            }

        };
    }

    @Override
    public InputStream openInputStream(@NonNull String id) {
        DataHolder dataHolder = dataMap.get(id);
        return dataHolder == null ?
                null :
                new ByteArrayInputStream(dataHolder.data, 0, dataHolder.length);
    }

    @Override
    public boolean delete(@NonNull String id) throws IOException {
        return dataMap.remove(id) != null;
    }

    @Override
    public void deleteAll() throws IOException {
        dataMap.clear();
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