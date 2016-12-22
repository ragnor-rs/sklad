package io.reist.sklad;

import org.apache.tools.ant.taskdefs.Zip;
import org.junit.Assert;
import org.zeroturnaround.zip.ZipUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
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

    static final String CIPHER_TEST_KEY = "1d21ef261a";
    static final byte[] CIPHER_TEST_DATA = new byte[]{-127, -100, 97, 108, -120, -37, -48, 2};

    private TestUtils() {}

    static void saveTestObject(Storage storage) throws IOException {
//        if (storage instanceof ZipStorage) {
//            saveTestObject((ZipStorage) storage);
//            return;
//        }
        OutputStream outputStream = storage.openOutputStream(TEST_NAME);
        outputStream.write(TEST_DATA);
        outputStream.flush();
        outputStream.close();
    }

    static void saveTestObject(ZipStorage storage) throws IOException {
        File zf = storage.getFile();
        ZipUtil.addEntry(zf, TEST_NAME, TEST_DATA);
    }

    static boolean saveTestObject(SkladService skladService) throws IOException {
        return skladService.save(new StorageObject(TEST_NAME, new ByteArrayInputStream(TEST_DATA)));
    }

    static void assertTestObject(SkladService skladService) throws IOException {
        StorageObject object = skladService.load(TEST_NAME);
        assertEquals(TEST_NAME, object.getId());
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
