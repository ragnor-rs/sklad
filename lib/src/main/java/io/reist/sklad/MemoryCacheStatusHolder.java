package io.reist.sklad;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by reist on 12.04.17.
 */

class MemoryCacheStatusHolder implements CacheStatusHolder {

    private final Map<String, Boolean> statusMap = new HashMap<>();

    @Override
    public boolean isCached(String id) {
        return Boolean.TRUE.equals(statusMap.get(id));
    }

    @Override
    public void put(String id, boolean cached) {
        statusMap.put(id, cached);
    }

}
