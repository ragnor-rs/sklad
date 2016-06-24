package io.reist.sklad;

import android.support.annotation.NonNull;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Reist on 24.06.16.
 */
public class SimpleSkladServiceTest {

    private static final String TEST_DATA = "sejfibfiaewoi";

    public static final String STORAGE_OBJECT_ONE = "object1";

    @NonNull
    private SimpleSkladService createSkladService() {
        return new SimpleSkladService(new MemoryStorage(), new NoEncryptionProvider());
    }

    @Test
    public void testWrite() throws IOException {

        SkladService sklad = createSkladService();

        assertFalse(sklad.save(new StorageObject(STORAGE_OBJECT_ONE)));

        assertTrue(sklad.save(new StorageObject(STORAGE_OBJECT_ONE)));

    }

    @Test(expected = IllegalStateException.class)
    public void testDepletedInputStream() throws IOException {

        SkladService sklad = createSkladService();

        StorageObject savedObject = new StorageObject(STORAGE_OBJECT_ONE);
        savedObject.setInputStream(new ByteArrayInputStream(TEST_DATA.getBytes("UTF-8")));

        sklad.save(savedObject);

        sklad.save(savedObject); // second call should throw an exception

    }

    @Test
    public void testRead() throws IOException {

        SkladService sklad = createSkladService();

        assertNull(sklad.load(STORAGE_OBJECT_ONE));

        StorageObject savedObject = new StorageObject(STORAGE_OBJECT_ONE);
        sklad.save(savedObject);

        StorageObject loadedObject = sklad.load(STORAGE_OBJECT_ONE);
        assertEquals(STORAGE_OBJECT_ONE, loadedObject.getName());

    }

    @Test
    public void testReadAndWrite() throws IOException {

        SkladService sklad = createSkladService();

        StorageObject savedObject = new StorageObject(STORAGE_OBJECT_ONE);
        savedObject.setInputStream(new ByteArrayInputStream(TEST_DATA.getBytes("UTF-8")));
        sklad.save(savedObject);

        StorageObject loadedObject = sklad.load(STORAGE_OBJECT_ONE);
        InputStream inputStream = loadedObject.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        assertEquals(TEST_DATA, reader.readLine());

    }

}