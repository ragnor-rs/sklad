package io.reist.sklad;

import android.os.Build;
import android.support.annotation.NonNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.OngoingStubbing;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import static io.reist.sklad.TestUtils.TEST_NAME_1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @NonNull
    private static CachedStorageStates createHashMapStates() {
        return new CachedStorageStates() {

            private final Map<String, Boolean> stateMap = new HashMap<>();

            @Override
            public void setFullyCached(Storage local, String id, boolean fullyCached) throws IOException {
                stateMap.put(id, fullyCached);
            }

            @Override
            public boolean isFullyCached(Storage local, String id) {
                return Boolean.TRUE.equals(stateMap.get(id));
            }

        };
    }

    @SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
    private static void assertObjectJustCached(
            CachedStorageStates cachedStorageStates,
            Storage remote,
            InputStream remoteInputStream,
            Storage local,
            OutputStream localOutputStream,
            InputStream localInputStream,
            CachedStorage cachedStorage,
            boolean useCacheMethod,
            boolean readFully
    ) throws IOException {

        if (useCacheMethod) {

            try {
                cachedStorage.cache(TEST_NAME_1);
            } catch (IOException e) {
                if (readFully) {
                    throw e;
                }
            }

            verify(remote, times(1)).openInputStream(TEST_NAME_1);
            verify(local, times(1)).openOutputStream(TEST_NAME_1);
            verify(remoteInputStream, times(2)).read(any(byte[].class));
            verify(localOutputStream, times(1)).write(any(byte[].class), anyInt(), anyInt());
            verify(localOutputStream, times(1)).flush();
            verify(localOutputStream, times(1)).close();
            verify(remoteInputStream, times(1)).close();

        } else {

            InputStream inputStream1 = cachedStorage.openInputStream(TEST_NAME_1);
            inputStream1.read();
            inputStream1.close();

            verify(remote, times(1)).openInputStream(TEST_NAME_1);
            verify(local, times(1)).openOutputStream(TEST_NAME_1);
            verify(remoteInputStream, times(1)).read();
            verify(remoteInputStream, times(1)).available();
            verify(localOutputStream, times(1)).flush();
            verify(localOutputStream, times(1)).close();
            verify(remoteInputStream, times(1)).close();

        }

        assertEquals(readFully, cachedStorageStates.isFullyCached(null, TEST_NAME_1));

    }

    @SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
    private static void performCacheStateTest(boolean readFully, boolean useCacheMethod) throws IOException {

        CachedStorageStates cachedStorageStates = createHashMapStates();

        Storage local = mock(Storage.class);
        OutputStream localOutputStream = mock(OutputStream.class);
        when(local.openOutputStream(TEST_NAME_1)).thenReturn(localOutputStream);
        InputStream localInputStream = mock(InputStream.class);
        when(local.openInputStream(TEST_NAME_1)).thenReturn(localInputStream);

        Storage remote = mock(Storage.class);
        InputStream remoteInputStream = mock(InputStream.class);
        when(remote.openInputStream(TEST_NAME_1)).thenReturn(remoteInputStream);

        if (useCacheMethod) {

            // for caching
            OngoingStubbing<Integer> stubbing = when(remoteInputStream.read(any(byte[].class))).thenReturn(1);
            if (readFully) {
                stubbing.thenReturn(-1);
            } else {
                stubbing.thenThrow(IOException.class);
            }

            // for reading
            when(remoteInputStream.read()).thenReturn(123).thenReturn(-1);
            when(remoteInputStream.available()).thenReturn(1);

        } else {

            // for caching & reading
            when(remoteInputStream.read()).thenReturn(123).thenReturn(readFully ? -1 : 123);
            when(remoteInputStream.available()).thenReturn(readFully ? 0 : 1).thenReturn(0);

        }

        CachedStorage cachedStorage = new CachedStorage(
                remote,
                local,
                cachedStorageStates
        );

        assertObjectJustCached(
                cachedStorageStates,
                remote,
                remoteInputStream,
                local,
                localOutputStream,
                localInputStream,
                cachedStorage,
                useCacheMethod,
                readFully
        );

        clearInvocations(remote, local, remoteInputStream, localOutputStream);

        InputStream inputStream2 = cachedStorage.openInputStream(TEST_NAME_1);
        inputStream2.read();
        inputStream2.close();

        if (readFully) {
            assertEquals(localInputStream, inputStream2);
        } else {
            assertNotEquals(localInputStream, inputStream2);
        }

        if (readFully) {

            verify(remote, times(0)).openInputStream(TEST_NAME_1);
            verify(local, times(0)).openOutputStream(TEST_NAME_1);
            verify(remoteInputStream, times(0)).read();
            verify(remoteInputStream, times(0)).available();
            verify(localOutputStream, times(0)).flush();
            verify(localOutputStream, times(0)).close();
            verify(remoteInputStream, times(0)).close();

            verify(local, times(1)).openInputStream(TEST_NAME_1);
            verify(localInputStream, times(1)).read();
            verify(localInputStream, times(1)).close();

        } else {

            verify(remote, times(1)).openInputStream(TEST_NAME_1);
            verify(local, times(1)).openOutputStream(TEST_NAME_1);
            verify(remoteInputStream, times(1)).read();
            verify(remoteInputStream, times(1)).available();
            verify(localOutputStream, times(1)).flush();
            verify(localOutputStream, times(1)).close();
            verify(remoteInputStream, times(1)).close();

            verify(local, times(0)).openInputStream(TEST_NAME_1);
            verify(localInputStream, times(0)).read();
            verify(localInputStream, times(0)).close();

        }

    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
    @Test
    public final void testFullCachingWithRead() throws Exception {
        performCacheStateTest(true, false);
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
    @Test
    public final void testPartialCachingWithRead() throws Exception {
        performCacheStateTest(false, false);
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
    @Test
    public final void testFullCachingWithCache() throws Exception {
        performCacheStateTest(true, true);
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
    @Test
    public final void testPartialCachingWithCache() throws Exception {
        performCacheStateTest(false, true);
    }

}
