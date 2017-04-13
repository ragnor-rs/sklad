package io.reist.sklad;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import io.reist.sklad.utils.FileUtils;
import io.reist.sklad.utils.ZipUtils;

/**
 * Created by 4xes on 05/12/2016.
 */
public class ZipStorage implements Storage {


    private File file;

    public ZipStorage(@NonNull File file) throws IOException {

        this.file = file;

        ZipFile zipFile;

        try {
            zipFile = new ZipFile(file);
        } catch (FileNotFoundException notFile) {
            ZipUtils.writeEmptyZip(file);
            zipFile = new ZipFile(file);
        }

        zipFile.close();

    }

    @Override
    public boolean contains(@NonNull String id) throws IOException {
        ZipFile zipFile = new ZipFile(file);
        boolean b = zipFile.getEntry(id) != null;
        zipFile.close();
        return b;
    }

    @Nullable
    @Override
    public InputStream openInputStream(@NonNull String id) throws IOException {
        if (contains(id)) {
            final ZipFile zipFile = new ZipFile(file);
            final InputStream inputStream = zipFile.getInputStream(zipFile.getEntry(id));
            return new InputStream() {

                @Override
                public int read(@NonNull byte[] b) throws IOException {
                    return inputStream.read(b);
                }

                @Override
                public int read(@NonNull byte[] b, int off, int len) throws IOException {
                    return inputStream.read(b, off, len);
                }

                @Override
                public long skip(long n) throws IOException {
                    return inputStream.skip(n);
                }

                @Override
                public int available() throws IOException {
                    return inputStream.available();
                }

                @Override
                public void close() throws IOException {
                    try {
                        inputStream.close();
                    } finally {
                        zipFile.close();
                    }
                }

                @Override
                public synchronized void mark(int readlimit) {
                    inputStream.mark(readlimit);
                }

                @Override
                public synchronized void reset() throws IOException {
                    inputStream.reset();
                }

                @Override
                public boolean markSupported() {
                    return inputStream.markSupported();
                }

                @Override
                public int read() throws IOException {
                    return inputStream.read();
                }

            };
        } else {
            return null;
        }
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
                try {
                    out.closeEntry();
                    out.close();
                    FileUtils.deleteFile(file);
                    FileUtils.moveFile(tmpFile, file);
                } finally {
                    FileUtils.deleteFile(tmpFile);
                }
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
        return ZipUtils.removeEntries(file, new String[] {id});
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
