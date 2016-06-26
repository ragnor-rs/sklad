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
            @NonNull Storage localStorage,
            @NonNull EncryptionProvider localEncryptionProvider
    ) {

        this.remoteStorage = remoteStorage;
        this.localStorage = localStorage;

        remoteSkladService = new SimpleSkladService(
                new CachedStorage(remoteStorage, localStorage),
                new NoEncryptionProvider()
        );

        localSkladService = new SimpleSkladService(localStorage, localEncryptionProvider);

    }

    @Override
    public boolean save(@NonNull StorageObject storageObject) throws IOException {
        return localSkladService.save(storageObject);
    }

    @Override
    public StorageObject load(@NonNull String name) throws IOException {
        return remoteSkladService.load(name);
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
