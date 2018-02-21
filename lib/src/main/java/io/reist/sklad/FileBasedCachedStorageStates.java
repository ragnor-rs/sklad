package io.reist.sklad;

import java.io.File;
import java.io.IOException;

/**
 * Created by Reist on 21/02/2018.
 */

public class FileBasedCachedStorageStates implements CachedStorageStates {

    private final File root;

    public FileBasedCachedStorageStates(String root) {
        this.root = new File(root);
    }

    public FileBasedCachedStorageStates(File root) {
        this.root = root;
    }

    @Override
    public void setFullyCached(Storage local, String id, boolean fullyCached) throws IOException {
        File marker = new File(root, id);
        if (fullyCached) {
            try {
                if (!marker.createNewFile()) {
                    throw new IOException("Cannot create marker file for " + id);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (!marker.delete()) {
                throw new IOException("Error deleting marker file for " + id);
            }
        }
    }

    @Override
    public boolean isFullyCached(Storage local, String id) {
        return (new File(root, id)).exists();
    }

}
