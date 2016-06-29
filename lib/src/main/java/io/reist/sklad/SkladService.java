package io.reist.sklad;

import android.support.annotation.NonNull;

import java.io.IOException;

/**
 * A persistent storage service for {@link StorageObject}s.
 */
public interface SkladService {

    /**
     * @throws IllegalStateException    if the given object's {@link java.io.InputStream} has
     *                                  reached its end
     *
     * @throws IOException              if an I/O error occurs during persisting the object
     *
     * @return true     if the object has been overwritten
     */
    boolean save(@NonNull StorageObject storageObject) throws IOException;

    /**
     * @return null     if an object with the given id doesn't exist
     */
    StorageObject load(@NonNull String id) throws IOException;

}
