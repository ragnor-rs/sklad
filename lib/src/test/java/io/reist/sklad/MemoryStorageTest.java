package io.reist.sklad;

import android.support.annotation.NonNull;

/**
 * Created by Reist on 24.06.16.
 */
public class MemoryStorageTest extends BaseStorageTest<MemoryStorage> {

    @Override
    @NonNull
    protected MemoryStorage createStorage() {
        return new MemoryStorage();
    }

}