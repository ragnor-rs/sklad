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
    public void setFullyCached(@NonNull Storage local, @NonNull String id, boolean fullyCached) throws IOException {

        if (!root.mkdirs() && !root.exists()) {
            throw new IOException("Cache state directory doesn't exist");
        }

        File marker = getMarkerFile(id);

        if (fullyCached) {
            if (!marker.exists() && !marker.createNewFile()) {
                throw new IOException("Cannot create marker file for " + id);
            }
        } else {
            if (marker.exists() && !marker.delete()) {
                throw new IOException("Error deleting marker file for " + id);
            }
        }

    }

    @NonNull
    private File getMarkerFile(String id) {
        return new File(root, id);
    }

    @Override
    public boolean isFullyCached(@NonNull Storage local, @NonNull String id) throws IOException {
        File markerFile = getMarkerFile(id);
        if (markerFile.exists()) {
            if (local.contains(id)) {
                return true;
            } else if (!markerFile.delete()) {
                throw new IOException("Error deleting marker file for " + id);
            }
        }
        return false;
    }

}
