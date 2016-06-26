package io.reist.sklad;

import android.support.annotation.NonNull;

/**
 * Created by Reist on 24.06.16.
 */
public class NoEncryptionProvider implements EncryptionProvider {

    @Override
    public void encrypt(@NonNull byte[] buffer, int offset, int length) {}

    @Override
    public int decrypt(@NonNull byte[] b, int offset, int length) {
        return length;
    }

}
