package io.reist.sklad;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;

/**
 * Created by Reist on 21/02/2018.
 */

public class FileBasedCachedStorageStates implements CachedStorageStates {

    @NonNull
    private final File root;

    public FileBasedCachedStorageStates(@NonNull String root) {
        this.root = new File(root);
    }

    public FileBasedCachedStorageStates(@NonNull File root) {
        this.root = root;
    }

    @Override
    public void setFullyCached(Storage local, String id, boolean fullyCached) throws IOException {

        if (!root.mkdirs() && !root.exists()) {
            throw new IOException("Cache state directory doesn't exist");
        }

        File marker = getMarkerFile(id);

        if (fullyCached) {
            if (!marker.createNewFile()) {
                throw new IOException("Cannot create marker file for " + id);
            }
        } else {
            if (!marker.delete()) {
                throw new IOException("Error deleting marker file for " + id);
            }
        }

    }

    @NonNull
    private File getMarkerFile(String id) {
        return new File(root, id);
    }

    @Override
    public boolean isFullyCached(Storage local, String id) {
        return getMarkerFile(id).exists();
    }

}
