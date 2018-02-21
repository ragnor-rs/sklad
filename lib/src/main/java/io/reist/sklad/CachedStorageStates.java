package io.reist.sklad;

import java.io.IOException;

/**
 * Created by Reist on 21/02/2018.
 */

public interface CachedStorageStates {
    void setFullyCached(Storage local, String id, boolean fullyCached) throws IOException;
    boolean isFullyCached(Storage local, String id);
}
