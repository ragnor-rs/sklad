package io.reist.sklad;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by 4xes on 05/12/2016.
 */
public class ZipStorage implements Storage {

    private final ZipFile zipFile;

    public ZipStorage(ZipFile zipFile) {
        this.zipFile = zipFile;
    }

    @Override
    public boolean contains(@NonNull String id) throws IOException {
        return zipFile.getEntry(id) != null;
    }

    @Nullable
    @Override
    public InputStream openInputStream(@NonNull String id) throws IOException {
        return zipFile.getInputStream(zipFile.getEntry(id));
    }


    @NonNull
    @Override
    public OutputStream openOutputStream(@NonNull String id) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(id);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        return new ZipOutputStream(bufferedOutputStream);
    }

    @Override
    public boolean delete(@NonNull String id) throws IOException {
        throw  new UnsupportedOperationException();
    }

    @Override
    public void deleteAll() throws IOException {
        throw  new UnsupportedOperationException();
    }

    @SuppressWarnings("unused")
    public void zip(@NonNull ZipOutputStream outputStream, String[] srcFiles) throws IOException {
        byte[] buffer = new byte[4098];

        for (String srcFile : srcFiles) {

            ZipEntry zipEntry = new ZipEntry(srcFile);
            outputStream.putNextEntry(zipEntry);
            InputStream inputStream = new FileInputStream(srcFile);

            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.closeEntry();
            inputStream.close();
        }
        outputStream.close();
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
        OutputStream outputStream = new FileOutputStream(new File(filePath));
        byte[] bytesIn = new byte[4096];
        int read;
        while ((read = zipInputStream.read(bytesIn)) != -1) {
            outputStream.write(bytesIn, 0, read);
        }
        outputStream.close();
    }
}
