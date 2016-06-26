package io.reist.sklad;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Reist on 26.06.16.
 */
public class CachedSkladServiceTest {

    private static final String TEST_NAME = "z12";
    private static final byte[] TEST_DATA = new byte[] {17, 25, 33};

    @Test
    public void testSave() throws Exception {

        CachedSkladService cachedSkladService = new CachedSkladService(
                new MemoryStorage(),
                new MemoryStorage(),
                new NoEncryptionProvider()
        );

        StorageObject storageObject = new StorageObject(TEST_NAME);
        storageObject.setInputStream(new ByteArrayInputStream(TEST_DATA));
        cachedSkladService.save(storageObject);

        byte[] buffer = new byte[TEST_DATA.length];

        Storage localStorage = cachedSkladService.getLocalStorage();
        InputStream inputStream = localStorage.openInputStream(TEST_NAME);
        Assert.assertNotNull(inputStream);
        Assert.assertEquals(TEST_DATA.length, inputStream.read(buffer, 0, TEST_DATA.length));
        Assert.assertArrayEquals(TEST_DATA, buffer);

    }

    @Test
    public void testLoad() throws Exception {

        CachedSkladService cachedSkladService = new CachedSkladService(
                new MemoryStorage(),
                new MemoryStorage(),
                new NoEncryptionProvider()
        );

        StorageObject saved = new StorageObject(TEST_NAME);
        saved.setInputStream(new ByteArrayInputStream(TEST_DATA));
        cachedSkladService.save(saved);

        OutputStream outputStream = cachedSkladService.getRemoteStorage().openOutputStream(TEST_NAME);
        outputStream.write(TEST_DATA);
        outputStream.flush();

        StorageObject loaded = cachedSkladService.load(TEST_NAME);
        BufferedInputStream inputStream = new BufferedInputStream(loaded.getInputStream());
        byte[] buffer = new byte[TEST_DATA.length];
        int len = inputStream.read(buffer);
        Assert.assertEquals(TEST_DATA.length, len);
        inputStream.close();

    }

}