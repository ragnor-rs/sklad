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

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import org.junit.Test;

import java.io.IOException;
import java.io.InterruptedIOException;

import static io.reist.sklad.TestUtils.TEST_DATA_1;
import static io.reist.sklad.TestUtils.assertTestObject;
import static io.reist.sklad.TestUtils.saveTestObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Reist on 28.06.16.
 */
public abstract class BaseStorageTest<S extends Storage> {

    protected static final int WORKER_DURATION = 4000;

    private final Object testLock = new Object();

    @Test
    @CallSuper
    public void testContains() throws Exception {
        S storage = createStorage();
        assertFalse(storage.contains(TestUtils.TEST_NAME_1));
        saveTestObject(storage);
        assertTrue(storage.contains(TestUtils.TEST_NAME_1));
    }

    @Test
    @CallSuper
    public void testStreams() throws Exception {
        S storage = createStorage();
        saveTestObject(storage);
        assertTestObject(storage);
    }

    @NonNull
    protected abstract S createStorage() throws IOException;

    @Test
    @CallSuper
    public void testDelete() throws Exception {
        S storage = createStorage();
        saveTestObject(storage);
        try {
            storage.delete(TestUtils.TEST_NAME_1);
            assertFalse(storage.contains(TestUtils.TEST_NAME_1));
        } catch (UnsupportedOperationException ignored) {}
    }

    @Test
    @CallSuper
    public void testDeleteAll() throws Exception {
        S storage = createStorage();
        saveTestObject(storage);
        try {
            storage.deleteAll();
            assertFalse(storage.contains(TestUtils.TEST_NAME_1));
        } catch (UnsupportedOperationException ignored) {}
    }

    @Test
    @CallSuper
    public void testSkip() throws Exception {
        S storage = createStorage();
        saveTestObject(storage);
        assertTestObject(storage, TEST_DATA_1.length / 2);
    }

    @Test
    @CallSuper
    public void testInterruption() throws Exception {

        final S storage = createStorage();
        saveTestObject(storage);

        Worker worker = new Worker(storage, testLock);
        worker.start();

        System.out.println("Main thread sleeps");
        Thread.sleep(WORKER_DURATION / 2);
        System.out.println("Main thread interrupts");
        worker.interrupt();
        synchronized (testLock) {
            if (worker.exception != null) {
                throw worker.exception;
            }
            System.out.println("Main thread waits");
            testLock.wait();
        }

        assertNotNull(worker.exception);
        assertEquals(InterruptedIOException.class, worker.exception.getClass());

    }

    private static class Worker extends Thread {

        private final Storage storage;
        private final Object lock;

        private Exception exception;

        public Worker(Storage storage, Object lock) {
            this.storage = storage;
            this.lock = lock;
        }

        @Override
        public void run() {

            System.out.println("Worker starts");

            try {
                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < WORKER_DURATION) {
                    assertTestObject(storage);
                    yield();
                }
                System.out.println("Worker finishes");
            } catch (Exception e) {
                System.out.println("Worker raises an exception");
                this.exception = e;
            }


            synchronized (lock) {
                lock.notify();
            }

        }

    }

}
