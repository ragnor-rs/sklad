package io.reist.sklad;

/**
 * A service to encrypt and decrypt byte arrays
 */
public interface EncryptionProvider {

    /**
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    void encrypt(byte[] buffer, int offset, int length);

    /**
     * @see java.io.InputStream#read(byte[], int, int)
     */
    int decrypt(byte[] b, int offset, int length);

}
