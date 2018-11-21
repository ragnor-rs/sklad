package io.reist.sklad;

import android.app.Application;
import android.support.annotation.NonNull;

import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;

import static io.reist.sklad.TestUtils.TEST_NAME_1;
import static io.reist.sklad.TestUtils.assertTestObject;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.fail;

/**
 * Created by reist on 17.04.17.
 */

public class StorageIntegrationTest extends CachedStorageTest {

    @NonNull
    @Override
    protected CachedStorage createStorage() throws IOException {

        Application application = RuntimeEnvironment.application;

        File cacheDir = application.getCacheDir();
        File filesDir = application.getFilesDir();

        return new CachedStorage(
                createCachedNetworkStorage(cacheDir),
                XorStorageTest.createXorStorage(filesDir)
        );

    }

    @NonNull
    private CachedStorage createCachedNetworkStorage(File cacheDir) throws IOException {
        return new CachedStorage(
                NetworkStorageTest.createNetworkStorage(baseUrl),
                LimitedStorageTest.createLimitedStorage(
                       XorStorageTest.createXorStorage(cacheDir)
                )
        );
    }

    @Test
    public void testCachedWithDisabledLimitingStorage() throws IOException {

        Buffer buffer = new Buffer();
        buffer.readFrom(new ByteArrayInputStream(TestUtils.TEST_DATA_1));

        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(buffer));
        server.enqueue(new MockResponse().setBody(buffer));
        server.start();

        baseUrl = server.url("/");

        CachedStorage cachedStorage = createCachedNetworkStorage(RuntimeEnvironment.application.getCacheDir());
        LimitedStorage limitedStorage = (LimitedStorage) cachedStorage.getLocal();
        limitedStorage.setCapacity(0);

        try {
            cachedStorage.cache(TEST_NAME_1);
            fail();
        } catch (IOException ignored) {}

        assertTestObject(cachedStorage);

        assertFalse(cachedStorage.getLocal().contains(TEST_NAME_1));

        assertFalse(cachedStorage.isFullyCached(TEST_NAME_1));

        server.shutdown();

    }

}
