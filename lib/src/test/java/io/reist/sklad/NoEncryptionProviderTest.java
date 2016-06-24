package io.reist.sklad;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by Reist on 24.06.16.
 */
public class NoEncryptionProviderTest {

    public static final byte[] ORIGINAL_DATA = new byte[] {1, 2, 3};
    public static final byte[] ENCRYPTED_DATA = new byte[] {1, 2, 3};

    private EncryptionProvider createEncryptionProvider() {
        return new NoEncryptionProvider();
    }

    @Test
    public void testEncrypt() throws Exception {
        EncryptionProvider encryptionProvider = createEncryptionProvider();
        byte[] buffer = new byte[ORIGINAL_DATA.length];
        System.arraycopy(ORIGINAL_DATA, 0, buffer, 0, ORIGINAL_DATA.length);
        encryptionProvider.encrypt(buffer, 0, buffer.length);
        assertArrayEquals(ENCRYPTED_DATA, buffer);
    }

    @Test
    public void testDecrypt() throws Exception {
        EncryptionProvider encryptionProvider = createEncryptionProvider();
        byte[] buffer = new byte[ENCRYPTED_DATA.length];
        System.arraycopy(ENCRYPTED_DATA, 0, buffer, 0, ENCRYPTED_DATA.length);
        int numRead = encryptionProvider.decrypt(buffer, 0, buffer.length);
        assertArrayEquals(ORIGINAL_DATA, buffer);
        assertEquals(ORIGINAL_DATA.length, numRead);
    }

}