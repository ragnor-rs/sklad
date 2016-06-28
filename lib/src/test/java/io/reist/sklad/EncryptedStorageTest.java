package io.reist.sklad;

import android.support.annotation.NonNull;

import org.junit.Before;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Reist on 28.06.16.
 */
public class EncryptedStorageTest extends BaseStorageTest<EncryptedStorage> {

    static final String TEST_KEY = "1d21ef261a";
    static final byte[] ENCRYPTED_TEST_DATA = new byte[]{-127, -100, 97, 108, -120, -37, -48, 2};

    private boolean containsTestObject;

    @Before
    public void setUp() throws Exception {
        containsTestObject = false;
    }

    @NonNull
    @Override
    protected EncryptedStorage createStorage() throws IOException {
        Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.openOutputStream(TestUtils.TEST_NAME)).then(new Answer<OutputStream>() {

            @Override
            public OutputStream answer(InvocationOnMock invocation) throws Throwable {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                EncryptedStorageTest.this.containsTestObject = true;
                return outputStream;
            }

        });
        Mockito.when(storage.contains(TestUtils.TEST_NAME)).then(new Answer<Boolean>() {

            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                return containsTestObject;
            }

        });
        Mockito.when(storage.openInputStream(TestUtils.TEST_NAME)).then(new Answer<InputStream>() {

            @Override
            public InputStream answer(InvocationOnMock invocation) throws Throwable {
                return new ByteArrayInputStream(ENCRYPTED_TEST_DATA);
            }

        });
        return new EncryptedStorage(storage, TEST_KEY);
    }

}