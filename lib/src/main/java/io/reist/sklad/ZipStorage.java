package io.reist.sklad;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

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

        final File tmpFile = FileUtils.tempFile(new File(file.getParent()));
        final ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(tmpFile)));

        HashSet<String> skipSet = new HashSet<>();
        skipSet.add(id);
        ZipUtils.copyEntries(file, out, skipSet);

        out.putNextEntry(new ZipEntry(id));

        return new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                out.write(b);
            }

            @Override
            public void close() throws IOException {
                out.closeEntry();
                out.close();

                FileUtils.deleteFile(file);
                FileUtils.moveFile(tmpFile, file);
                FileUtils.deleteFile(tmpFile);
            }

            @Override
            public void write(byte[] b) throws IOException {
                out.write(b);
            }

            @Override
            public void flush() throws IOException {
                out.flush();
            }

            @Override
            public void write(@NonNull byte[] b, int off, int len) throws IOException {
                out.write(b, off, len);
            }
        };
    }

    @Override
    public boolean delete(@NonNull String id) throws IOException {
        ZipUtils.removeEntries(file, new String[] {id});
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
