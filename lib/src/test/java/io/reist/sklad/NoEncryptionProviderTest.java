package io.reist.sklad;

import android.support.annotation.NonNull;

/**
 * Created by Reist on 24.06.16.
 */
public class NoEncryptionProviderTest extends BaseEncryptionProviderTest<NoEncryptionProvider> {

    @NonNull
    @Override
    protected EncryptionProvider createEncryptionProvider() {
        return new NoEncryptionProvider();
    }

}