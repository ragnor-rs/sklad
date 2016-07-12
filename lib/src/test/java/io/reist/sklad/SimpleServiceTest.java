package io.reist.sklad;

import android.support.annotation.NonNull;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static io.reist.sklad.TestUtils.TEST_DATA;
import static io.reist.sklad.TestUtils.TEST_NAME;
import static io.reist.sklad.TestUtils.assertTestObject;
import static io.reist.sklad.TestUtils.saveTestObject;

/**
 * Created by Reist on 24.06.16.
 */
public class SimpleServiceTest {

    @NonNull
    private static SimpleSkladService createSkladService() throws IOException {
        Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.openOutputStream(Mockito.eq(TEST_NAME))).then(new Answer<OutputStream>() {

            @Override
            public OutputStream answer(InvocationOnMock invocation) throws Throwable {
                return new ByteArrayOutputStream();
            }

        });
        Mockito.when(storage.openInputStream(Mockito.eq(TEST_NAME))).then(new Answer<InputStream>() {

            @Override
            public InputStream answer(InvocationOnMock invocation) throws Throwable {
                return new ByteArrayInputStream(TEST_DATA);
            }

        });
        return new SimpleSkladService(storage);
    }

    @Test
    public void testSave() throws Exception {

        SimpleSkladService skladService = createSkladService();

        saveTestObject(skladService);

        Storage storage = skladService.getStorage();
        Mockito.verify(storage).contains(TEST_NAME);
        Mockito.verify(storage).openOutputStream(TEST_NAME);

    }

    @Test
    public void testLoad() throws Exception {
        SimpleSkladService skladService = createSkladService();
        saveTestObject(skladService);
        assertTestObject(skladService);
    }

}