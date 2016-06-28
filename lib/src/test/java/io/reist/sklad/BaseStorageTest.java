package io.reist.sklad;

import android.support.annotation.NonNull;

import org.junit.Test;

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
        storage.openOutputStream(TestUtils.TEST_NAME).flush();
        assertTrue(storage.contains(TestUtils.TEST_NAME));
    }

    @Test
    public void testStreams() throws Exception {
        S storage = createStorage();
        saveTestObject(storage);
        assertTestObject(storage);
    }

    @NonNull
    protected abstract S createStorage();

}
