package io.reist.sklad;

import android.support.annotation.NonNull;

import java.io.InputStream;

/**
 * A named object which can be stored in {@link SkladService}.
 */
public class StorageObject {

    private String name;

    private InputStream inputStream;

    private boolean inputStreamDepleted;

    public StorageObject(@NonNull String name) {
        this.name = name;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setInputStreamDepleted(boolean inputStreamDepleted) {
        this.inputStreamDepleted = inputStreamDepleted;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public boolean isInputStreamDepleted() {
        return inputStreamDepleted;
    }

}
