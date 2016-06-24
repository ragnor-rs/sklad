package io.reist.sklad;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * I/O stream manager
 */
public interface Storage {

    OutputStream openOutputStream(String name) throws IOException;

    boolean contains(@NonNull String name);

    InputStream openInputStream(String name) throws IOException;

}
