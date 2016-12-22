package io.reist.sklad;

import android.app.Application;
import android.os.Build;
import android.support.annotation.NonNull;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.zeroturnaround.zip.ZipUtil;

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
        return createFileStorage();
    }

    @NonNull
    private static ZipStorage createFileStorage() throws IOException {
        Application application = RuntimeEnvironment.application;

        File zipFile = new File(application.getCacheDir(), "temp.zip");
        ZipUtil.packEntries(new File[]{}, zipFile);

        return new ZipStorage(zipFile);
    }
}