package io.reist.sklad;

import android.app.Application;
import android.os.Build;
import android.support.annotation.NonNull;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.IOException;

/**
 * Created by 4xes on 21/12/2016.
 */
@RunWith(RobolectricTestRunner.class)
@Config(
        constants = BuildConfig.class,
        sdk = Build.VERSION_CODES.LOLLIPOP
)
public class ZipStorageTest extends BaseStorageTest<ZipStorage> {

    @NonNull
    @Override
    protected ZipStorage createStorage() throws IOException {
        Application application = RuntimeEnvironment.application;
        File zipFile = new File(application.getCacheDir(), "test.zip");
        zipFile.delete();
        return new ZipStorage(zipFile);
    }

}
