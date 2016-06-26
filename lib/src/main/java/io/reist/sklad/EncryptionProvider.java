package io.reist.sklad;

import android.support.annotation.NonNull;

/**
 * A service to encrypt and decrypt byte arrays
 */
public interface EncryptionProvider {

    /**
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    void encrypt(@NonNull byte[] buffer, int offset, int length);

    /**
     * @see java.io.InputStream#read(byte[], int, int)
     */
    int decrypt(@NonNull byte[] b, int offset, int length);

}
