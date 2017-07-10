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
import android.support.annotation.Nullable;

import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Reist on 29.06.16.
 */
public class TestNetworkStorage extends NetworkStorage {

    private final NetworkStorage mock = Mockito.mock(NetworkStorage.class);

    TestNetworkStorage(UrlResolver urlResolver) {
        super(
                urlResolver,
                BaseStorageTest.WORKER_DURATION * 2,
                BaseStorageTest.WORKER_DURATION * 2,
                BaseStorageTest.WORKER_DURATION * 2
        );
    }

    @NonNull
    @Override
    public OutputStream openOutputStream(@NonNull final String id) throws IOException {
        mock.openOutputStream(id);
        return new OutputStream() {

            private final OutputStream mock = Mockito.mock(OutputStream.class);

            @Override
            public void write(int i) throws IOException {
                mock.write(i);
            }

            @Override
            public void write(byte[] b) throws IOException {
                mock.write(b);
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                mock.write(b, off, len);
            }

            @Override
            public void flush() throws IOException {
                mock.flush();
            }

            @Override
            public void close() throws IOException {
                mock.close();
                Mockito.verify(TestNetworkStorage.this.mock).openOutputStream(id);
            }

        };
    }

    @Override
    public boolean contains(@NonNull String id) throws IOException {
        mock.contains(id);
        return super.contains(id);
    }

    @Nullable
    @Override
    public InputStream openInputStream(@NonNull String id) throws IOException {
        mock.openInputStream(id);
        return super.openInputStream(id);
    }

}
