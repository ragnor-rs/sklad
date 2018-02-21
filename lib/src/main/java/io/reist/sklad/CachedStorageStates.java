package io.reist.sklad;

/**
 * Created by Reist on 21/02/2018.
 */

public interface CachedStorageStates {
    void setFullyCached(Storage local, String id, boolean readFully);
    boolean isFullyCached(Storage local, String id);
}
