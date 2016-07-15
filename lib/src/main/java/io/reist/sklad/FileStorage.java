package io.reist.sklad;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    public boolean contains(@NonNull String id) throws IOException {
        return getFile(id).exists();
    }

    @NonNull
    @Override
    public OutputStream openOutputStream(@NonNull String id) throws IOException {
        return new FileOutputStream(getFile(id));
    }

    @Nullable
    @Override
    public InputStream openInputStream(@NonNull String id) throws IOException {
        try {
            return new FileInputStream(getFile(id));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public boolean delete(@NonNull String id) throws IOException {
        return getFile(id).delete();
    }

    protected File getFile(String name) throws IOException {
        return new File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), name);
    }

}
