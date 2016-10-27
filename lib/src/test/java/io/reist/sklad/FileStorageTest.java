package io.reist.sklad;

import android.app.Application;
import android.os.Build;
import android.support.annotation.NonNull;

import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;

/**
 * Created by Reist on 28.06.16.
 */
@RunWith(RobolectricGradle3TestRunner.class)
@Config(
        constants = BuildConfig.class,
        sdk = Build.VERSION_CODES.LOLLIPOP
)
public class FileStorageTest extends BaseStorageTest<FileStorage> {

    @NonNull
    @Override
    protected FileStorage createStorage() throws IOException {
        return createFileStorage();
    }

    @NonNull
    static FileStorage createFileStorage() {
        Application application = RuntimeEnvironment.application;
        return new FileStorage(application, application.getCacheDir());
    }

}