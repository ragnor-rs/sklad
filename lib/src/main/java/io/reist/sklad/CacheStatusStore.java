package io.reist.sklad;

/**
 * Created by reist on 12.04.17.
 */

public interface CacheStatusStore {

    boolean isCached(String id);

    void put(String id, boolean cached);

}
