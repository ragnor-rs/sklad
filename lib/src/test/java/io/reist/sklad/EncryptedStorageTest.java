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

import org.junit.Before;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Reist on 28.06.16.
 */
public class EncryptedStorageTest extends BaseStorageTest<EncryptedStorage> {

    private boolean containsTestObject;

    @Before
    public void setUp() throws Exception {
        containsTestObject = false;
    }

    @NonNull
    @Override
    protected EncryptedStorage createStorage() throws IOException {
        Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.openOutputStream(TestUtils.TEST_NAME)).then(new Answer<OutputStream>() {

            @Override
            public OutputStream answer(InvocationOnMock invocation) throws Throwable {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                EncryptedStorageTest.this.containsTestObject = true;
                return outputStream;
            }

        });
        Mockito.when(storage.contains(TestUtils.TEST_NAME)).then(new Answer<Boolean>() {

            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                return containsTestObject;
            }

        });
        Mockito.when(storage.openInputStream(TestUtils.TEST_NAME)).then(new Answer<InputStream>() {

            @Override
            public InputStream answer(InvocationOnMock invocation) throws Throwable {
                return new ByteArrayInputStream(TestUtils.CIPHER_TEST_DATA);
            }

        });
        Mockito.doThrow(UnsupportedOperationException.class).when(storage).delete(Mockito.eq(TestUtils.TEST_NAME));
        Mockito.doThrow(UnsupportedOperationException.class).when(storage).deleteAll();
        return createEncryptedStorage(storage);
    }

    @NonNull
    static EncryptedStorage createEncryptedStorage(Storage storage) {
        return new EncryptedStorage(storage, TestUtils.CIPHER_TEST_KEY);
    }

}