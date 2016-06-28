package io.reist.sklad;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Reist on 28.06.16.
 */
public class FileStorage implements Storage {

    private final Context context;

    public FileStorage(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public boolean contains(@NonNull String name) throws IOException {
        return getFile(name).exists();
    }

    @NonNull
    @Override
    public OutputStream openOutputStream(@NonNull String name) throws IOException {
        return new FileOutputStream(getFile(name));
    }

    @Nullable
    @Override
    public InputStream openInputStream(@NonNull String name) throws IOException {
        return new FileInputStream(getFile(name));
    }

    private File getFile(String name) throws IOException {
        return new File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), name);
    }

}
