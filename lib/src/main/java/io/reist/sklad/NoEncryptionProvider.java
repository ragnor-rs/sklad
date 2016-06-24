package io.reist.sklad;

/**
 * Created by Reist on 24.06.16.
 */
public class NoEncryptionProvider implements EncryptionProvider {

    @Override
    public void encrypt(byte[] buffer, int offset, int length) {}

    @Override
    public int decrypt(byte[] b, int offset, int length) {
        return length;
    }

}
