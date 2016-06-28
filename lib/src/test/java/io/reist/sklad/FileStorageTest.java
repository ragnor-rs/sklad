package io.reist.sklad;

import android.os.Build;
import android.support.annotation.NonNull;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;

/**
 * Created by Reist on 28.06.16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(
        constants = BuildConfig.class,
        sdk = Build.VERSION_CODES.LOLLIPOP
)
public class FileStorageTest extends BaseStorageTest<FileStorage> {

    @NonNull
    @Override
    protected FileStorage createStorage() throws IOException {
        return new FileStorage(RuntimeEnvironment.application);
    }

}