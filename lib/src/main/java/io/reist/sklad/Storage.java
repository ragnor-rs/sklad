package io.reist.sklad;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * I/O stream manager
 */
public interface Storage {

    boolean contains(@NonNull String name) throws IOException;

    @NonNull
    OutputStream openOutputStream(@NonNull String name) throws IOException;

    @Nullable
    InputStream openInputStream(@NonNull String name) throws IOException;

}
