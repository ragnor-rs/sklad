package io.reist.sklad;

import android.support.annotation.NonNull;

import java.io.IOException;

/**
 * Created by Reist on 26.06.16.
 */
public class CachedSkladService implements SkladService {

    private final Storage remoteStorage;
    private final Storage localStorage;

    private final SkladService remoteSkladService;
    private final SkladService localSkladService;

    public CachedSkladService(
            @NonNull Storage remoteStorage,
            @NonNull Storage localStorage
    ) {

        this.remoteStorage = remoteStorage;
        this.localStorage = localStorage;

        CachedStorage cachedStorage = new CachedStorage(remoteStorage, localStorage);

        remoteSkladService = new SimpleSkladService(cachedStorage);
        localSkladService = new SimpleSkladService(localStorage);

    }

    @Override
    public boolean save(@NonNull StorageObject storageObject) throws IOException {
        return localSkladService.save(storageObject);
    }

    @Override
    public StorageObject load(@NonNull String id) throws IOException {
        return remoteSkladService.load(id);
    }

    @NonNull
    Storage getLocalStorage() {
        return localStorage;
    }

    @NonNull
    Storage getRemoteStorage() {
        return remoteStorage;
    }

}
