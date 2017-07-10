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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.IOException;

import static io.reist.sklad.TestUtils.TEST_DATA_1;
import static io.reist.sklad.TestUtils.TEST_NAME_1;
import static io.reist.sklad.TestUtils.saveTestObject;
import static junit.framework.Assert.assertFalse;

/**
 * Created by Reist on 28.06.16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(
        constants = BuildConfig.class,
        sdk = Build.VERSION_CODES.LOLLIPOP
)
public class FileStorageTest extends BaseStorageTest<FileStorage> {

    @NonNull
    @Override
    protected FileStorage createStorage() throws IOException {
        Application application = RuntimeEnvironment.application;
        return new FileStorage(application.getCacheDir());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void testParentChange() throws IOException {

        String deeper = "deep" + "/" + TEST_NAME_1;
        File cacheDir = new File(BuildConfig.BUILD_DIR);

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

    }

}