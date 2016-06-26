package io.reist.sklad;

import android.support.annotation.NonNull;

import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Reist on 26.06.16.
 */
public class CachedStorageTest {

    private static final String TEST_NAME = "z12";
    private static final byte[] TEST_DATA = new byte[] {17, 25, 33};

    private static final String LOCAL_TEST_NAME = "123";
    private static final byte[] LOCAL_TEST_DATA = new byte[] {1, 2, 3};

    private static final String REMOTE_TEST_NAME = "qwe";
    private static final byte[] REMOTE_TEST_DATA = new byte[] {7, 5, 3};

    @NonNull
    private CachedStorage createCachedStorage() {
        return new CachedStorage(new MemoryStorage(), new MemoryStorage());
    }

    @Test
    public void testContains() throws Exception {

        CachedStorage cachedStorage = createCachedStorage();

        OutputStream localStream = cachedStorage.getLocalStorage().openOutputStream(LOCAL_TEST_NAME);
        localStream.write(LOCAL_TEST_DATA);
        localStream.flush();

        Assert.assertTrue(cachedStorage.contains(LOCAL_TEST_NAME));
        Assert.assertFalse(cachedStorage.contains(LOCAL_TEST_NAME + "q"));

        OutputStream remoteStream = cachedStorage.getRemoteStorage().openOutputStream(REMOTE_TEST_NAME);
        remoteStream.write(REMOTE_TEST_DATA);
        remoteStream.flush();

        Assert.assertTrue(cachedStorage.contains(REMOTE_TEST_NAME));
        Assert.assertFalse(cachedStorage.contains(REMOTE_TEST_NAME + "q"));

    }

    @Test
    public void testContainsInRemoteStorage() throws Exception {

        CachedStorage cachedStorage = createCachedStorage();

        OutputStream remoteStream = cachedStorage.getRemoteStorage().openOutputStream(REMOTE_TEST_NAME);
        remoteStream.write(REMOTE_TEST_DATA);
        remoteStream.flush();

        Assert.assertTrue(cachedStorage.containsInRemoteStorage(REMOTE_TEST_NAME));
        Assert.assertFalse(cachedStorage.containsInLocalStorage(REMOTE_TEST_NAME));

    }

    @Test
    public void testContainsInLocalStorage() throws Exception {

        CachedStorage cachedStorage = createCachedStorage();

        OutputStream localStream = cachedStorage.getLocalStorage().openOutputStream(LOCAL_TEST_NAME);
        localStream.write(LOCAL_TEST_DATA);
        localStream.flush();

        Assert.assertTrue(cachedStorage.containsInLocalStorage(LOCAL_TEST_NAME));
        Assert.assertFalse(cachedStorage.containsInRemoteStorage(LOCAL_TEST_NAME));

    }

    @Test
    public void testOpenOutputStream() throws Exception {

        CachedStorage cachedStorage = createCachedStorage();

        OutputStream outputStream = cachedStorage.openOutputStream(TEST_NAME);
        outputStream.write(TEST_DATA);
        outputStream.flush();

        byte[] buffer = new byte[TEST_DATA.length];

        InputStream localStream = cachedStorage.getLocalStorage().openInputStream(TEST_NAME);
        Assert.assertNotNull(localStream);
        Assert.assertEquals(TEST_DATA.length, localStream.read(buffer, 0, TEST_DATA.length));
        Assert.assertArrayEquals(TEST_DATA, buffer);

        InputStream remoteStream = cachedStorage.getRemoteStorage().openInputStream(TEST_NAME);
        Assert.assertNotNull(remoteStream);
        Assert.assertEquals(TEST_DATA.length, remoteStream.read(buffer, 0, TEST_DATA.length));
        Assert.assertArrayEquals(TEST_DATA, buffer);

    }

    @Test
    public void testOpenInputStream() throws Exception {

        CachedStorage cachedStorage = createCachedStorage();

        OutputStream outputStream = cachedStorage.openOutputStream(TEST_NAME);
        outputStream.write(TEST_DATA);
        outputStream.flush();

        byte[] buffer = new byte[TEST_DATA.length];

        InputStream inputStream = cachedStorage.openInputStream(TEST_NAME);
        Assert.assertNotNull(inputStream);
        Assert.assertEquals(TEST_DATA.length, inputStream.read(buffer, 0, TEST_DATA.length));
        Assert.assertArrayEquals(TEST_DATA, buffer);

        Assert.assertNull(cachedStorage.openInputStream(TEST_NAME + "q"));

    }

    @Test
    public void testCache() throws Exception {

        CachedStorage cachedStorage = createCachedStorage();

        OutputStream remoteStream = cachedStorage.getRemoteStorage().openOutputStream(TEST_NAME);
        remoteStream.write(TEST_DATA);
        remoteStream.flush();

        cachedStorage.cache(TEST_NAME);

        byte[] buffer = new byte[TEST_DATA.length];

        InputStream inputStream = cachedStorage.getRemoteStorage().openInputStream(TEST_NAME);
        Assert.assertNotNull(inputStream);
        Assert.assertEquals(TEST_DATA.length, inputStream.read(buffer, 0, TEST_DATA.length));
        Assert.assertArrayEquals(TEST_DATA, buffer);

    }

}