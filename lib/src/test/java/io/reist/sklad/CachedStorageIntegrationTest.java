package io.reist.sklad;

import android.support.annotation.NonNull;

import org.junit.Test;

import java.io.OutputStream;

import static io.reist.sklad.TestUtils.TEST_NAME;
import static io.reist.sklad.TestUtils.assertTestObject;
import static io.reist.sklad.TestUtils.saveTestObject;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Reist on 26.06.16.
 */
public class CachedStorageIntegrationTest extends BaseStorageTest<CachedStorage> {

    private static final String LOCAL_TEST_NAME = "123";
    private static final byte[] LOCAL_TEST_DATA = new byte[] {1, 2, 3};

    private static final String REMOTE_TEST_NAME = "qwe";
    private static final byte[] REMOTE_TEST_DATA = new byte[] {7, 5, 3};

    @Override
    @NonNull
    protected CachedStorage createStorage() {
        return new CachedStorage(new MemoryStorage(), new MemoryStorage());
    }

    @Test
    @Override
    public void testContains() throws Exception {

        super.testContains();

        CachedStorage storage = createStorage();

        OutputStream localStream = storage.getLocalStorage().openOutputStream(LOCAL_TEST_NAME);
        localStream.write(LOCAL_TEST_DATA);
        localStream.flush();

        assertTrue(storage.contains(LOCAL_TEST_NAME));
        assertFalse(storage.contains(LOCAL_TEST_NAME + "q"));

        OutputStream remoteStream = storage.getRemoteStorage().openOutputStream(REMOTE_TEST_NAME);
        remoteStream.write(REMOTE_TEST_DATA);
        remoteStream.flush();

        assertTrue(storage.contains(REMOTE_TEST_NAME));
        assertFalse(storage.contains(REMOTE_TEST_NAME + "q"));

    }

    @Test
    @Override
    public void testStreams() throws Exception {
        super.testStreams();
        CachedStorage storage = createStorage();
        saveTestObject(storage);
        assertTestObject(storage.getLocalStorage());
        assertTestObject(storage.getRemoteStorage());
    }

    @Test
    public void testContainsInRemoteStorage() throws Exception {

        CachedStorage storage = createStorage();

        OutputStream remoteStream = storage.getRemoteStorage().openOutputStream(REMOTE_TEST_NAME);
        remoteStream.write(REMOTE_TEST_DATA);
        remoteStream.flush();

        assertTrue(storage.containsInRemoteStorage(REMOTE_TEST_NAME));
        assertFalse(storage.containsInLocalStorage(REMOTE_TEST_NAME));

    }

    @Test
    public void testContainsInLocalStorage() throws Exception {

        CachedStorage storage = createStorage();

        OutputStream localStream = storage.getLocalStorage().openOutputStream(LOCAL_TEST_NAME);
        localStream.write(LOCAL_TEST_DATA);
        localStream.flush();

        assertTrue(storage.containsInLocalStorage(LOCAL_TEST_NAME));
        assertFalse(storage.containsInRemoteStorage(LOCAL_TEST_NAME));

    }

    @Test
    public void testCache() throws Exception {
        CachedStorage storage = createStorage();
        saveTestObject(storage.getRemoteStorage());
        storage.cache(TEST_NAME);
        assertTestObject(storage.getLocalStorage());
    }

}