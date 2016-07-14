package io.reist.sklad;

import android.support.annotation.NonNull;

import java.io.InputStream;

/**
 * A named object which can be stored in {@link SkladService}.
 */
public class StorageObject {

    private String id;

    private InputStream inputStream;

    private boolean inputStreamDepleted;

    public StorageObject(@NonNull String id, @NonNull InputStream inputStream) {
        this.id = id;
        this.inputStream = inputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    void setInputStreamDepleted(boolean inputStreamDepleted) {
        this.inputStreamDepleted = inputStreamDepleted;
    }

    @NonNull
    public String getId() {
        return id;
    }

    boolean isInputStreamDepleted() {
        return inputStreamDepleted;
    }

}
