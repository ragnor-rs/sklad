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

import android.os.Build;
import android.support.annotation.NonNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;

import static io.reist.sklad.TestUtils.TEST_DATA_1;
import static io.reist.sklad.TestUtils.TEST_DATA_2;
import static io.reist.sklad.TestUtils.TEST_DATA_3;
import static io.reist.sklad.TestUtils.TEST_NAME_1;
import static io.reist.sklad.TestUtils.TEST_NAME_2;
import static io.reist.sklad.TestUtils.TEST_NAME_3;
import static io.reist.sklad.TestUtils.saveTestObject;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by Reist on 28.06.16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(
        constants = BuildConfig.class,
        sdk = Build.VERSION_CODES.LOLLIPOP
)
public class LimitedStorageTest extends BaseStorageTest<LimitedStorage> {

    @NonNull
    @Override
    protected LimitedStorage createStorage() throws IOException {
        return createLimitedStorage(new MemoryStorage());
    }

    @NonNull
    static LimitedStorage createLimitedStorage(JournalingStorage journalingStorage) {
        return new LimitedStorage(
                journalingStorage,
                TEST_DATA_1.length + TEST_DATA_2.length
        );
    }

    @Test
    public void testLimit() throws IOException {
        LimitedStorage storage = createStorage();
        saveTestObject(storage, TEST_NAME_1, TEST_DATA_1);
        saveTestObject(storage, TEST_NAME_2, TEST_DATA_2);
        saveTestObject(storage, TEST_NAME_3, TEST_DATA_3);
        assertFalse(storage.contains(TEST_NAME_1));
        assertTrue(storage.contains(TEST_NAME_2));
        assertTrue(storage.contains(TEST_NAME_3));
    }

    @Test
    public void testLimitChange() throws IOException {
        LimitedStorage storage = createStorage();
        saveTestObject(storage, TEST_NAME_1, TEST_DATA_1);
        storage.setCapacity(TEST_DATA_1.length - 1);
        assertFalse(storage.contains(TEST_NAME_1));
    }


}