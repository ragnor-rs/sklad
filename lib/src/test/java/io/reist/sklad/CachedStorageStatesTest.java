package io.reist.sklad;

import android.os.Build;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Reist on 21/02/2018.
 */

@RunWith(RobolectricTestRunner.class)
@Config(
        constants = BuildConfig.class,
        sdk = Build.VERSION_CODES.LOLLIPOP
)
public class CachedStorageStatesTest {

    private static final String ID = "1";

    @Test
    public void testFileBasedStates() throws IOException {

        File cacheDir = RuntimeEnvironment.application.getCacheDir();
        CachedStorageStates cachedStorageStates = new FileBasedCachedStorageStates(cacheDir);

        cachedStorageStates.setFullyCached(null, ID, true);
        assertTrue(cachedStorageStates.isFullyCached(null, ID));

        cachedStorageStates.setFullyCached(null, ID, false);
        assertFalse(cachedStorageStates.isFullyCached(null, ID));

    }

}
