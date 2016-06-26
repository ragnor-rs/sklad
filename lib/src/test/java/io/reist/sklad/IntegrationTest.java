package io.reist.sklad;

import android.support.annotation.NonNull;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Reist on 24.06.16.
 */
public class IntegrationTest {

    private static final String TEST_NAME = "z12";
    private static final byte[] TEST_DATA = new byte[] {17, 25, 33};

    @NonNull
    private static SimpleSkladService createSkladService() {
        return new SimpleSkladService(new MemoryStorage(), new NoEncryptionProvider());
    }

    @Test
    public void testWrite() throws IOException {

        SkladService sklad = createSkladService();

        assertFalse(sklad.save(new StorageObject(TEST_NAME, new ByteArrayInputStream(TEST_DATA))));

        assertTrue(sklad.save(new StorageObject(TEST_NAME, new ByteArrayInputStream(TEST_DATA))));

    }

    @Test(expected = IllegalStateException.class)
    public void testDepletedInputStream() throws IOException {

        SkladService sklad = createSkladService();

        StorageObject savedObject = new StorageObject(TEST_NAME, new ByteArrayInputStream(TEST_DATA));

        sklad.save(savedObject);

        sklad.save(savedObject); // second call should throw an exception

    }

    @Test
    public void testRead() throws IOException {

        SkladService sklad = createSkladService();

        assertNull(sklad.load(TEST_NAME));

        StorageObject savedObject = new StorageObject(TEST_NAME, new ByteArrayInputStream(TEST_DATA));
        sklad.save(savedObject);

        StorageObject loadedObject = sklad.load(TEST_NAME);
        assertEquals(TEST_NAME, loadedObject.getName());

    }

    @Test
    public void testReadAndWrite() throws IOException {

        SkladService sklad = createSkladService();

        StorageObject savedObject = new StorageObject(TEST_NAME, new ByteArrayInputStream(TEST_DATA));
        sklad.save(savedObject);

        StorageObject loadedObject = sklad.load(TEST_NAME);
        InputStream inputStream = loadedObject.getInputStream();

        byte[] buffer = new byte[TEST_DATA.length];
        assertEquals(TEST_DATA.length, inputStream.read(buffer));
        assertArrayEquals(TEST_DATA, buffer);

    }

}