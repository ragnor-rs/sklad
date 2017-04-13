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

import org.junit.Assert;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.Assert.assertTrue;

/**
 * Created by Reist on 28.06.16.
 */
public class TestUtils {

    static final String TEST_NAME = "z12";
    static final byte[] TEST_DATA = new byte[] {17, 25, 33};

    static final String CIPHER_TEST_KEY = "1d21ef261a";
    static final byte[] CIPHER_TEST_DATA = new byte[] {-127, -100, 97, 108, -120, -37, -48, 2};

    private TestUtils() {}

    static void saveTestObject(Storage storage) throws IOException {
        OutputStream outputStream = storage.openOutputStream(TEST_NAME);
        outputStream.write(TEST_DATA);
        outputStream.flush();
        outputStream.close();
    }

    static void assertTestObject(Storage storage) throws IOException {
        assertTestObject(storage, 0);
    }

    static void assertTestObject(Storage storage, long bytesToSkip) throws IOException {
        assertInputStream(storage.openInputStream(TEST_NAME), TEST_DATA, bytesToSkip);
    }

    private static void assertInputStream(InputStream stream, byte[] data, long bytesToSkip) throws IOException {

        Assert.assertNotNull(stream);

        BufferedInputStream inputStream = new BufferedInputStream(stream);

        long bytesSkipped = bytesToSkip == 0 ? 0 : inputStream.skip(bytesToSkip);
        if (bytesToSkip > 0) {
            assertTrue(bytesSkipped > 0);
        }

        byte[] buffer = new byte[(int) (data.length - bytesToSkip)];
        int bytesRead = inputStream.read(buffer);
        inputStream.close();

        byte[] expected = new byte[bytesRead];
        System.arraycopy(data, (int) bytesSkipped, expected, 0, bytesRead);

        byte[] actual = new byte[bytesRead];
        System.arraycopy(buffer, 0, actual, 0, bytesRead);

        Assert.assertArrayEquals(expected, actual);

    }

}
