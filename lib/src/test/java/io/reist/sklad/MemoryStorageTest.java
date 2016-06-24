package io.reist.sklad;

import android.support.annotation.NonNull;

import org.junit.Test;

import java.io.InputStream;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Reist on 24.06.16.
 */
public class MemoryStorageTest {

    private static final String TEST_OBJECT_NAME = "qwe";

    @NonNull
    private static MemoryStorage createStorage() {
        return new MemoryStorage();
    }

    @Test
    public void testOpenOutputStream() throws Exception {
        MemoryStorage storage = createStorage();
        storage.openOutputStream(TEST_OBJECT_NAME).flush();
        Map<String, MemoryStorage.DataHolder> dataMap = storage.getDataMap();
        MemoryStorage.DataHolder dataHolder = dataMap.get(TEST_OBJECT_NAME);
        assertNotNull(dataHolder);
        assertNotNull(dataHolder.data);
        assertEquals(0, dataHolder.length);
    }

    @Test
    public void testContains() throws Exception {
        Storage storage = createStorage();
        assertFalse(storage.contains(TEST_OBJECT_NAME));
        storage.openOutputStream(TEST_OBJECT_NAME).flush();
        assertTrue(storage.contains(TEST_OBJECT_NAME));
    }

    @Test
    public void testOpenInputStream() throws Exception {
        MemoryStorage storage = createStorage();
        storage.openOutputStream(TEST_OBJECT_NAME).flush();
        InputStream inputStream = storage.openInputStream(TEST_OBJECT_NAME);
        assertNotNull(inputStream);
        assertEquals(0, inputStream.available());
    }

}