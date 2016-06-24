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
import java.io.OutputStream;

/**
 * Created by Reist on 24.06.16.
 */
public class SimpleSkladServiceTest {

    private static final String TEST_NAME = "zxc";
    private static final byte[] TEST_DATA = new byte[] {1, 2, 3};

    @NonNull
    private static SimpleSkladService createSkladService() throws IOException {
        Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.openOutputStream(Mockito.eq(TEST_NAME))).then(new Answer<OutputStream>() {

            @Override
            public OutputStream answer(InvocationOnMock invocation) throws Throwable {
                return new ByteArrayOutputStream();
            }

        });
        return new SimpleSkladService(
                storage,
                Mockito.mock(EncryptionProvider.class)
        );
    }

    @Test
    public void testSave() throws Exception {

        SimpleSkladService skladService = createSkladService();

        StorageObject storageObject = new StorageObject(TEST_NAME);
        storageObject.setInputStream(new ByteArrayInputStream(TEST_DATA));
        skladService.save(storageObject);

        Storage storage = skladService.getStorage();
        Mockito.verify(storage).contains(TEST_NAME);
        Mockito.verify(storage).openOutputStream(TEST_NAME);

        EncryptionProvider encryptionProvider = skladService.getEncryptionProvider();
        Mockito.verify(encryptionProvider).encrypt(Mockito.argThat(new BaseMatcher<byte[]>() {

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

        }), Mockito.eq(0), Mockito.eq(TEST_DATA.length));

    }

    @Test
    public void testLoad() throws Exception {

        SimpleSkladService skladService = createSkladService();

        StorageObject saved = new StorageObject(TEST_NAME);
        saved.setInputStream(new ByteArrayInputStream(TEST_DATA));
        skladService.save(saved);

        StorageObject load = skladService.load(TEST_NAME);

    }

}