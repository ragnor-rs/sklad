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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.Assert.assertTrue;

/**
 * Created by Reist on 28.06.16.
 */
public class TestUtils {

    static final String TEST_NAME_1 = "z12";
    static final byte[] TEST_DATA_1 = new byte[] {17, 25, 33};
    static final String TEST_DATA_1_CIPHER_KEY = "1d21ef261a";
    static final byte[] TEST_DATA_1_CIPHER = new byte[] {-127, -100, 97, 108, -120, -37, -48, 2};

    static final String TEST_NAME_2 = "123";
    static final String TEST_NAME_2_INVALID = TEST_NAME_2 + "q";

    static final byte[] TEST_DATA_2 = new byte[] {1, 2, 3};
    static final String TEST_NAME_3 = "qwe";
    static final String TEST_NAME_3_INVALID = TEST_NAME_3 + "q";
    static final byte[] TEST_DATA_3 = new byte[] {7, 5, 3};

    private TestUtils() {}

    static void saveTestObject(Storage storage) throws IOException {
        saveTestObject(storage, TEST_NAME_1, TEST_DATA_1);
    }

    static void saveTestObject(Storage storage, String id, byte[] data) throws IOException {
        OutputStream outputStream = storage.openOutputStream(id);
        outputStream.write(data);
        outputStream.flush();
        outputStream.close();
    }

    static void assertTestObject(Storage storage) throws IOException {
        assertTestObject(storage, 0);
    }

    static void assertTestObject(Storage storage, long bytesToSkip) throws IOException {
        assertInputStream(storage.openInputStream(TEST_NAME_1), TEST_DATA_1, bytesToSkip);
    }

    private static void assertInputStream(InputStream inputStream, byte[] data, long bytesToSkip) throws IOException {

        Assert.assertNotNull(inputStream);

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

    static void assertHierarchy(File root, File file, File fileDeeper) {

        File parent = file.getParentFile();
        File parentDeeper = fileDeeper.getParentFile();

        assertTrue(file.exists());
        assertTrue(fileDeeper.exists());

        String absolutePath = root.getAbsolutePath();
        String parentAbsolutePath = parent.getAbsolutePath();
        String parentDeeperAbsolutePath = parentDeeper.getAbsolutePath();
        assertTrue(parentAbsolutePath.startsWith(absolutePath) && parentDeeperAbsolutePath.startsWith(absolutePath));

    }

}
