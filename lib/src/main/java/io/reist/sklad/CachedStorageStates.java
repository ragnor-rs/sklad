package io.reist.sklad;

import android.support.annotation.NonNull;

import java.io.IOException;

/**
 * Created by Reist on 21/02/2018.
 */

public interface CachedStorageStates {
    void setFullyCached(@NonNull Storage local, @NonNull String id, boolean fullyCached) throws IOException;
    boolean isFullyCached(@NonNull Storage local, @NonNull String id) throws IOException;
}
