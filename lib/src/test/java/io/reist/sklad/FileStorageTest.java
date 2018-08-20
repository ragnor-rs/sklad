/*
 * Copyright (C) 2017 Renat Sarymsakov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.reist.sklad;

import android.app.Application;
import android.os.Build;
import android.support.annotation.NonNull;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import io.reist.sklad.utils.FileUtils;

import static io.reist.sklad.TestUtils.TEST_DATA_1;
import static io.reist.sklad.TestUtils.TEST_NAME_1;
import static io.reist.sklad.TestUtils.saveTestObject;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Reist on 28.06.16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(
        constants = BuildConfig.class,
        sdk = Build.VERSION_CODES.LOLLIPOP
)
public class FileStorageTest extends BaseStorageTest<FileStorage> {

    private static final String FILTER_TEST_EXT = ".test";

    private static final String FILTER_TEST_INVISIBLE1_ID = "invisible1";
    private static final String FILTER_TEST_INVISIBLE2_ID = "invisible2";

    private static final String FILTER_TEST_VISIBLE1_ID = "visible1";
    private static final String FILTER_TEST_VISIBLE2_ID = "visible2";

    @NonNull
    @Override
    protected FileStorage createStorage() {
        Application application = RuntimeEnvironment.application;
        return new FileStorage(application.getCacheDir());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void testParentChange() throws IOException {

        File cacheDir = new File(BuildConfig.BUILD_DIR + "/" + FileUtils.tempName());

        FileUtils.deleteFile(cacheDir);

        cacheDir.mkdirs();

        try {

            String deeper = "deep" + "/" + TEST_NAME_1;

            File root1 = new File(cacheDir, "root1");
            root1.mkdirs();
            FileStorage storage = new FileStorage(root1);

            saveTestObject(storage, TEST_NAME_1, TEST_DATA_1);
            saveTestObject(storage, deeper, TEST_DATA_1);

            File file1 = storage.getFileById(TEST_NAME_1);
            File fileDeeper1 = storage.getFileById(deeper);
            TestUtils.assertHierarchy(root1, file1, fileDeeper1);

            File root2 = new File(cacheDir, "root2");
            storage.setParent(root2);

            // files shouldn't be contained in the previous place
            assertFalse(file1.exists());
            assertFalse(fileDeeper1.exists());

            File file2 = storage.getFileById(TEST_NAME_1);
            File fileDeeper2 = storage.getFileById(deeper);
            TestUtils.assertHierarchy(root2, file2, fileDeeper2);

            // check if the data has been preserved
            TestUtils.assertTestObject(storage);

        } finally {
            FileUtils.deleteFile(cacheDir);
        }

    }

    @Test
    public void filterTest() throws IOException, InterruptedException {

        Application application = RuntimeEnvironment.application;

        File root1 = new File(application.getCacheDir(), "filter_test1");
        if (!root1.mkdirs()) {
            fail("Cannot create filter_test");
        }

        File visibleFile1 = new File(root1, FILTER_TEST_VISIBLE1_ID + FILTER_TEST_EXT);
        if (!visibleFile1.createNewFile()) {
            fail("Cannot create visible file 1");
        }

        Thread.sleep(1000); // wait for oldest id to work

        File nonVisibleFile1 = new File(root1, FILTER_TEST_INVISIBLE1_ID);
        if (!nonVisibleFile1.createNewFile()) {
            fail("Cannot create invisible file 1");
        }

        FileStorage storage = new FileStorage(root1, new FileUtils.Filter() {

            @Override
            public boolean accept(@NonNull File f) {
                return f.getName().endsWith(FILTER_TEST_EXT);
            }

        }) {

            @Override
            public File getFileById(@NonNull String id) {
                return new File(getParent(), id + FILTER_TEST_EXT);
            }

            @Override
            public String getIdByFile(File file) {
                String fileName = file.getName();
                return fileName.substring(0, fileName.lastIndexOf(FILTER_TEST_EXT));
            }

        };

        assertFalse("Invisible file 1 is visible", storage.contains(FILTER_TEST_INVISIBLE1_ID));
        assertNull("Invisible file 1 is readable", storage.openInputStream(FILTER_TEST_INVISIBLE1_ID));

        assertTrue("Visible file 1 is invisible", storage.contains(FILTER_TEST_VISIBLE1_ID));
        assertNotNull("Visible file 1 is not readable", storage.openInputStream(FILTER_TEST_VISIBLE1_ID));

        assertEquals("The oldest file is not visible 1", FILTER_TEST_VISIBLE1_ID, storage.getOldestId());

        Thread.sleep(1000); // wait for oldest id to work

        OutputStream outputStream = null;
        try {
            outputStream = storage.openOutputStream(FILTER_TEST_VISIBLE2_ID);
            outputStream.write(1);
            outputStream.flush();
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }

        Thread.sleep(1000); // wait for oldest id to work

        File nonVisibleFile2 = new File(root1, FILTER_TEST_INVISIBLE2_ID);
        if (!nonVisibleFile2.createNewFile()) {
            fail("Cannot create invisible file 2");
        }

        assertFalse("Invisible file 2 is visible", storage.contains(FILTER_TEST_INVISIBLE2_ID));
        assertNull("Invisible file 2 is readable", storage.openInputStream(FILTER_TEST_INVISIBLE2_ID));

        assertTrue("Visible file is not visible", storage.contains(FILTER_TEST_VISIBLE2_ID));
        assertNotNull("Visible file is not readable", storage.openInputStream(FILTER_TEST_VISIBLE2_ID));

        assertEquals("The oldest file is not visible 1", FILTER_TEST_VISIBLE1_ID, storage.getOldestId());

        File root2 = new File(application.getCacheDir(), "filter_test2");

        storage.setParent(root2);

        String[] expected1 = {
                storage.getFileById(FILTER_TEST_VISIBLE1_ID).getName(),
                storage.getFileById(FILTER_TEST_VISIBLE2_ID).getName()
        };
        Arrays.sort(expected1);
        String[] actual1 = root2.list();
        Arrays.sort(actual1);

        Assert.assertArrayEquals("Visible files have not been moved", expected1, actual1);

        storage.setParent(root1);
        storage.deleteAll();

        String[] expected2 = {
                FILTER_TEST_INVISIBLE1_ID,
                FILTER_TEST_INVISIBLE2_ID
        };
        Arrays.sort(expected2);
        String[] actual2 = root1.list();
        Arrays.sort(actual2);

        Assert.assertArrayEquals("Invisible files have not been ignored", expected2, actual2);

    }

}