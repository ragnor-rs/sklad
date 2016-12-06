package io.reist.sklad;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by 4xes on 05/12/2016.
 */
public class ZipStorage implements Storage {

    private final Storage wrappedStorage;

    public ZipStorage(Storage wrappedStorage) {
        this.wrappedStorage = wrappedStorage;
    }

    @Override
    public boolean contains(@NonNull String id) throws IOException {
        return wrappedStorage.contains(id);
    }

    @NonNull
    @Override
    public OutputStream openOutputStream(@NonNull String id) throws IOException {
        OutputStream outputStream = wrappedStorage.openOutputStream(id);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
        return new ZipOutputStream(bufferedOutputStream);
    }

    @Nullable
    @Override
    public InputStream openInputStream(@NonNull String id) throws IOException {
        ZipFile zf = new ZipFile(id);
        Enumeration entries = zf.entries();
        List<InputStream> inputStreams = new ArrayList<>();
        ZipEntry entry;
        while ((entry = (ZipEntry) entries.nextElement()) != null) {
            inputStreams.add(zf.getInputStream(entry));
        }
        return new SequenceInputStream(Collections.enumeration(inputStreams));
    }

    @Override
    public boolean delete(@NonNull String id) throws IOException {
        return wrappedStorage.delete(id);
    }

    @Override
    public void deleteAll() throws IOException {
        wrappedStorage.deleteAll();
    }

    @SuppressWarnings("unused")
    public void zip(@NonNull String srcZipFile, String[] srcFiles) throws IOException {
        byte[] buffer = new byte[4098];

        ZipOutputStream zos = (ZipOutputStream) openOutputStream(srcZipFile);

        for (String srcFile : srcFiles) {

            File file = new File(srcFile);
            InputStream inputStream = wrappedStorage.openInputStream(srcFile);
            if (inputStream == null) {
                continue;
            }

            zos.putNextEntry(new ZipEntry(file.getName()));
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }

            zos.closeEntry();
            inputStream.close();
        }
        zos.close();
    }

    /**
     * Extracts a zip file from inputStream to a directory specified by
     * destDirectory (will be created if does not exists)
     *
     * @param inputStream   input stream of zipFile
     * @param destDirectory path
     * @throws IOException
     */
    @SuppressWarnings("unused")
    public void unzip(@NonNull InputStream inputStream, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            destDir.mkdir();
        }

        ZipInputStream zis = new ZipInputStream(inputStream);
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                extractFile(zis, filePath);
            } else {
                File dir = new File(filePath);
                //noinspection ResultOfMethodCallIgnored
                dir.mkdir();
            }
            zis.closeEntry();
        }
        zis.close();
    }

    /**
     * Extracts a zip entry (file entry)
     *
     * @param zipInputStream from parent zip file
     * @param filePath       path for entry file
     * @throws IOException
     */
    private void extractFile(ZipInputStream zipInputStream, String filePath) throws IOException {
        OutputStream outputStream = wrappedStorage.openOutputStream(filePath);
        byte[] bytesIn = new byte[4096];
        int read;
        while ((read = zipInputStream.read(bytesIn)) != -1) {
            outputStream.write(bytesIn, 0, read);
        }
        outputStream.close();
    }
}
