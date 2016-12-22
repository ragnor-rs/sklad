package io.reist.sklad;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipFile;
/**
 * Created by 4xes on 05/12/2016.
 */
public class ZipStorage implements Storage {


    private File file;

    public ZipStorage(@NonNull File file) throws IOException {
        this.file = file;
    }

    @Override
    public boolean contains(@NonNull String id) throws IOException {;
        return ZipUtil.containsEntry(file, id);
    }

    @Nullable
    @Override
    public InputStream openInputStream(@NonNull String id) throws IOException {
        ZipFile zf = new ZipFile(file);
        if (contains(id)) {
            return zf.getInputStream(zf.getEntry(id));
        }
        return null;
    }

    @NonNull
    @Override
    public OutputStream openOutputStream(@NonNull String id) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete(@NonNull String id) throws IOException {
        ZipUtil.removeEntry(file, id);
        return true;
    }

    @Override
    public void deleteAll() throws IOException {
        //noinspection ResultOfMethodCallIgnored
        file.delete();
        ZipUtil.packEntries(new File[]{}, file);
    }

    public File getFile() {
        return file;
    }
}
