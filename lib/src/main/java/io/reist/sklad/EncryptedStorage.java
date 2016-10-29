package io.reist.sklad;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Reist on 28.06.16.
 */
public class EncryptedStorage implements Storage {

    public static final String ALGORITHM = "Blowfish";
    public static final String TRANSFORMATION = ALGORITHM;

    private final Storage wrappedStorage;
    private final String key;

    public EncryptedStorage(Storage wrappedStorage, String key) {
        this.wrappedStorage = wrappedStorage;
        this.key = key;
    }

    @Override
    public boolean contains(@NonNull String id) throws IOException {
        return wrappedStorage.contains(id);
    }

    @NonNull
    static Cipher getCipher(int mode, String key) throws GeneralSecurityException {
        byte[] encoded = new BigInteger(key, 16).toByteArray();
        SecretKey secretKey = new SecretKeySpec(encoded, ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(mode, secretKey);
        return cipher;
    }

    @NonNull
    @Override
    public OutputStream openOutputStream(@NonNull String id) throws IOException {
        try {
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, key);
            OutputStream outputStream = wrappedStorage.openOutputStream(id);
            return new CipherOutputStream(outputStream, cipher);
        } catch (GeneralSecurityException e) {
            throw new IOException(e);
        }
    }

    @Nullable
    @Override
    public InputStream openInputStream(@NonNull final String id) throws IOException {
        try {
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE, key);
            final InputStream inputStream = wrappedStorage.openInputStream(id);
            return inputStream == null ? null : new CipherInputStream(inputStream, cipher) {

                @Override
                public int available() throws IOException {
                    return inputStream.available();
                }

                @Override
                public long skip(long n) throws IOException {
                    byte[] data = new byte[1024];
                    int read;
                    long skipped = 0;
                    while ((read = read(data, 0, (int) Math.min(data.length, n - skipped))) > 0) {
                        skipped += read;
                    }
                    return skipped;
                }

            };
        } catch (GeneralSecurityException e) {
            throw new IOException(e);
        }
    }

    @Override
    public boolean delete(@NonNull String id) throws IOException {
        return wrappedStorage.delete(id);
    }

    @Override
    public void deleteAll() throws IOException {
        wrappedStorage.deleteAll();
    }

}
