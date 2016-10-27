package io.reist.sklad;

import android.support.annotation.NonNull;

import org.junit.Test;

import java.io.IOException;

import static io.reist.sklad.TestUtils.assertTestObject;
import static io.reist.sklad.TestUtils.saveTestObject;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Reist on 28.06.16.
 */
public abstract class BaseStorageTest<S extends Storage> {

    @Test
    public void testContains() throws Exception {
        S storage = createStorage();
        assertFalse(storage.contains(TestUtils.TEST_NAME));
        saveTestObject(storage);
        assertTrue(storage.contains(TestUtils.TEST_NAME));
    }

    @Test
    public void testStreams() throws Exception {
        S storage = createStorage();
        saveTestObject(storage);
        assertTestObject(storage);
    }

    @NonNull
    protected abstract S createStorage() throws IOException;

    @Test
    public void testDelete() throws Exception {
        S storage = createStorage();
        saveTestObject(storage);
        try {
            storage.delete(TestUtils.TEST_NAME);
            assertFalse(storage.contains(TestUtils.TEST_NAME));
        } catch (UnsupportedOperationException ignored) {}
    }

    @Test
    public void testDeleteAll() throws Exception {
        S storage = createStorage();
        saveTestObject(storage);
        try {
            storage.deleteAll();
            assertFalse(storage.contains(TestUtils.TEST_NAME));
        } catch (UnsupportedOperationException ignored) {}
    }

}
