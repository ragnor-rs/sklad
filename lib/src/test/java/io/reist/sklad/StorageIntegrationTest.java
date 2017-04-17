package io.reist.sklad;

import android.app.Application;
import android.support.annotation.NonNull;

import org.robolectric.RuntimeEnvironment;

import java.io.File;
import java.io.IOException;

/**
 * Created by reist on 17.04.17.
 */

public class StorageIntegrationTest extends CacheStorageTest {

    @NonNull
    @Override
    protected CacheStorage createStorage() throws IOException {

        Application application = RuntimeEnvironment.application;

        File cacheDir = application.getCacheDir();
        File filesDir = application.getFilesDir();

        return new CacheStorage(
                new CacheStorage(
                        NetworkStorageTest.createNetworkStorage(baseUrl),
                        LimitedStorageTest.createLimitedStorage(
                               XorStorageTest.createXorStorage(cacheDir)
                        )
                ),
                XorStorageTest.createXorStorage(filesDir)
        );

    }

}
