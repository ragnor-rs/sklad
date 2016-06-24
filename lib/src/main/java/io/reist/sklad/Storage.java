package io.reist.sklad;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * I/O stream manager
 */
public interface Storage {

    boolean contains(@NonNull String name);

    OutputStream openOutputStream(String name) throws IOException;

    InputStream openInputStream(String name) throws IOException;

}
