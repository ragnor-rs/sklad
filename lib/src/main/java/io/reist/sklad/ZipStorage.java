package io.reist.sklad;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipFile;

import io.reist.sklad.utils.FileUtils;
import io.reist.sklad.utils.IOUtils;
import io.reist.sklad.utils.ZipUtils;

/**
 * Created by 4xes on 05/12/2016.
 */
public class ZipStorage implements Storage {


    private File file;

    public ZipStorage(@NonNull File file) throws IOException {
        this.file = file;
    }

    @Override
    public boolean contains(@NonNull String id) throws IOException {
        ZipFile zipFile = null;

        try {
            zipFile = new ZipFile(file);
            return zipFile.getEntry(id) != null;
        } finally {
            IOUtils.closeQuietly(zipFile);
        }
    }

    @Nullable
    @Override
    public InputStream openInputStream(@NonNull String id) throws IOException {
        ZipFile zipFile = new ZipFile(file);
        if (contains(id)) {
            return zipFile.getInputStream(zipFile.getEntry(id));
        }
        return null;
    }

    @NonNull
    @Override
    public OutputStream openOutputStream(@NonNull String id) throws IOException {
        return new ZipEntryOutputStream(file, id);
    }

    @Override
    public boolean delete(@NonNull String id) throws IOException {
        ZipUtils.removeEntries(file, new String[]{id});
        return true;
    }

    @Override
    public void deleteAll() throws IOException {
        FileUtils.deleteFile(file);
        ZipUtils.writeEmptyZip(file);
    }

    public File getFile() {
        return file;
    }
}
