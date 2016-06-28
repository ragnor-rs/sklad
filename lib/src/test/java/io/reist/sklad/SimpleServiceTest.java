package io.reist.sklad;

import android.support.annotation.NonNull;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
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

    public static final BaseMatcher<byte[]> TEST_DATA_MATCHER = new BaseMatcher<byte[]>() {

        @Override
        public boolean matches(Object item) {
            byte[] data = (byte[]) item;
            for (int i = 0; i < TEST_DATA.length; i++) {
                if (data[i] != TEST_DATA[i]) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("<data-to-encrypt>");
        }

    };

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

        EncryptionProvider encryptionProvider = Mockito.mock(EncryptionProvider.class);
        Mockito.when(encryptionProvider.decrypt(
                Mockito.argThat(TEST_DATA_MATCHER), Mockito.eq(0), Mockito.eq(TEST_DATA.length)
        )).then(new Answer<Integer>() {

            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                return TEST_DATA.length;
            }

        });

        return new SimpleSkladService(storage, encryptionProvider);

    }

    @Test
    public void testSave() throws Exception {

        SimpleSkladService skladService = createSkladService();

        saveTestObject(skladService);

        Storage storage = skladService.getStorage();
        Mockito.verify(storage).contains(TEST_NAME);
        Mockito.verify(storage).openOutputStream(TEST_NAME);

        verifyEncryption(skladService);

    }

    private static void verifyEncryption(SimpleSkladService skladService) {
        EncryptionProvider encryptionProvider = skladService.getEncryptionProvider();
        Mockito.verify(encryptionProvider).encrypt(
                Mockito.argThat(TEST_DATA_MATCHER), Mockito.eq(0), Mockito.eq(TEST_DATA.length)
        );
    }

    @Test
    public void testLoad() throws Exception {
        SimpleSkladService skladService = createSkladService();
        saveTestObject(skladService);
        assertTestObject(skladService);
        verifyEncryption(skladService);
    }

}