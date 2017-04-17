package io.reist.sklad;

import android.app.Application;
import android.os.Build;
import android.support.annotation.NonNull;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.Assert.assertEquals;

/**
 * Created by Reist on 27.10.16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(
        constants = BuildConfig.class,
        sdk = Build.VERSION_CODES.LOLLIPOP
)
public class XorStorageTest extends BaseStorageTest<XorStorage> {

    private static final int ENCRYPTION_BUFFER_SIZE = 96 * 1024;
    private static final int ENCRYPTION_STEP_DENOMINATOR = 3;

    private static byte[] buffer = new byte[ENCRYPTION_BUFFER_SIZE];

    private static final byte[] ENCRYPTION_KEY = new byte[] {35, 61, -122, 61, -88};

    private static final String ORIGINAL_STRING = "Что-то про Дениса. Спросить Васю.";

    @Test
    public void testEncryptionAndDecryption() throws IOException {

        Application application = RuntimeEnvironment.application;

        File cacheDir = application.getCacheDir();
        File originalFile = new File(cacheDir, "original");
        File resultFile1 = new File(cacheDir, "result_1");
        File resultFile2 = new File(cacheDir, "result_2");

        String unencryptedData = generateTestString();

        FileUtils.write(originalFile, unencryptedData);

        // encrypt with old mechanism
        xorTrackFile(ENCRYPTION_KEY, originalFile, resultFile1);

        XorStorage xorStorage = createStorage();

        byte[] unencryptedBytes = unencryptedData.getBytes();

        // decrypt with new mechanism
        InputStream inputStream1 = xorStorage.openInputStream(resultFile1.getName());
        assertInputStream(inputStream1, unencryptedBytes);

        // encrypt with new mechanism
        OutputStream outputStream = xorStorage.openOutputStream(resultFile2.getName());
        outputStream.write(unencryptedBytes);
        outputStream.flush();
        outputStream.close();

        // decrypt with new mechanism
        InputStream inputStream2 = xorStorage.openInputStream(resultFile2.getName());
        assertInputStream(inputStream2, unencryptedBytes);

    }

    @NonNull
    private static String generateTestString() {
        String unencryptedData = "";
        int times = (ENCRYPTION_BUFFER_SIZE / ORIGINAL_STRING.getBytes().length + 1) * 2;
        for (int i = 0; i < times; i++) {
            unencryptedData += ORIGINAL_STRING;
        }
        unencryptedData += ORIGINAL_STRING.substring(0, ORIGINAL_STRING.length() / 3);
        return unencryptedData;
    }

    private static void xorTrackFile(byte[] key, File trackFile, File resultFile) throws IOException {

        InputStream inputStream = new BufferedInputStream(new FileInputStream(trackFile));
        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(resultFile));

        int readCount;
        while ((readCount = inputStream.read(buffer)) != -1) {
            int bufferPartLengthToEncrypt = ENCRYPTION_BUFFER_SIZE / ENCRYPTION_STEP_DENOMINATOR;
            int numToProcess = readCount < bufferPartLengthToEncrypt ? readCount : bufferPartLengthToEncrypt;
            for (int i = 0; i < numToProcess; i++) {
                buffer[i] ^= key[i % key.length];
            }
            outputStream.write(buffer, 0, readCount);
        }

        inputStream.close();

        outputStream.flush();
        outputStream.close();

    }

    private static void assertInputStream(InputStream stream, byte[] data) throws IOException {
        Assert.assertNotNull(stream);
        BufferedInputStream inputStream = new BufferedInputStream(stream);
        byte[] buffer = new byte[data.length];
        assertEquals(data.length, inputStream.read(buffer));
        inputStream.close();
        Assert.assertArrayEquals(data, buffer);
    }

    @NonNull
    @Override
    protected XorStorage createStorage() throws IOException {
        return createXorStorage(RuntimeEnvironment.application.getCacheDir());
    }

    @NonNull
    static XorStorage createXorStorage(File fileDir) {
        return new XorStorage(
                new FileStorage(fileDir),
                new XorStorage.KeyProvider() {

                    @Override
                    public byte[] get() {
                        return ENCRYPTION_KEY;
                    }

                }
        );
    }

}
