package io.reist.sklad;

import org.junit.Assert;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by Reist on 28.06.16.
 */
public class TestUtils {

    static final String TEST_NAME = "z12";
    static final byte[] TEST_DATA = new byte[] {17, 25, 33};

    private TestUtils() {}

    static void saveTestObject(Storage storage) throws IOException {
        OutputStream outputStream = storage.openOutputStream(TEST_NAME);
        outputStream.write(TEST_DATA);
        outputStream.flush();
    }

    static boolean saveTestObject(SkladService skladService) throws IOException {
        return skladService.save(new StorageObject(TEST_NAME, new ByteArrayInputStream(TEST_DATA)));
    }

    static void assertTestObject(SkladService skladService) throws IOException {
        StorageObject object = skladService.load(TEST_NAME);
        assertEquals(TEST_NAME, object.getName());
        assertFalse(object.isInputStreamDepleted());
        assertInputStream(object.getInputStream(), TEST_DATA);
    }

    static void assertTestObject(Storage storage) throws IOException {
        assertInputStream(storage.openInputStream(TEST_NAME), TEST_DATA);
    }

    static void assertInputStream(InputStream stream, byte[] data) throws IOException {
        Assert.assertNotNull(stream);
        BufferedInputStream inputStream = new BufferedInputStream(stream);
        byte[] buffer = new byte[data.length];
        assertEquals(data.length, inputStream.read(buffer));
        inputStream.close();
        Assert.assertArrayEquals(data, buffer);
    }

}
