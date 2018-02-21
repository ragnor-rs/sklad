package io.reist.sklad;

import java.io.IOException;

/**
 * Created by Reist on 21/02/2018.
 */

public class SimpleCachedStorageStates implements CachedStorageStates {

    @Override
    public void setFullyCached(Storage local, String id, boolean fullyCached) {
        if (!fullyCached) {
            try {
                local.delete(id);
            } catch (IOException e) {
                throw new RuntimeException("Error removing bad cache object " + id, e);
            }
        }
    }

    @Override
    public boolean isFullyCached(Storage local, String id) {
        try {
            return local.contains(id);
        } catch (IOException e) {
            throw new RuntimeException("Error checking cache object " + id, e);
        }
    }

}
