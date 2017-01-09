package io.reist.sklad.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class FileUtils {


    private FileUtils() {
        throw new AssertionError();
    }


    /**
     * write file, the bytes will be written to the begin of the file
     *
     * @param file
     * @param stream
     * @return
     * @see {@link #writeFile(File, InputStream, boolean)}
     */
    public static void writeFile(File file, InputStream stream) throws IOException {
        writeFile(file, stream, false);
    }

    /**
     * write file
     *
     * @param file   the file to be opened for writing.
     * @param stream the input stream
     * @param append if <code>true</code>, then bytes will be written to the end of the file rather than the beginning
     * @return return true
     * @throws RuntimeException if an error occurs while operator FileOutputStream
     */
    public static void writeFile(File file, InputStream stream, boolean append) throws IOException {
        OutputStream out = null;
        try {
            ensureDirExists(file);
            out = new FileOutputStream(file, append);
            byte data[] = new byte[1024];
            int length;
            while ((length = stream.read(data)) != -1) {
                out.write(data, 0, length);
            }
            out.flush();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }


    public static boolean ensureDirExists(File folder) throws IOException {
        if(folder.exists() && folder.isDirectory() || folder.mkdirs()) {
            return true;
        } else{
            throw new IOException("Can't ensure directory");
        }
    }

    public static void moveFile(File srcFile, File destFile) throws IOException {
        boolean rename = srcFile.renameTo(destFile);
        if (!rename) {
            copyFile(srcFile, destFile);
            deleteFile(srcFile);
        }
    }

    public static void copyFile(File srcFile, File destFile) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(srcFile);
            writeFile(destFile, inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    /**
     * delete file or directory
     * <ul>
     * <li>if path is null or empty, return true</li>
     * <li>if path not exist, return true</li>
     * <li>if path exist, delete recursion. return true</li>
     * <ul>
     */
    public static boolean deleteFile(File file) {
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                //noinspection ResultOfMethodCallIgnored
                f.delete();
            } else if (f.isDirectory()) {
                deleteFile(f);
            }
        }
        return file.delete();
    }

    public static File tempFile(File directory) throws IOException {
        return File.createTempFile(UUID.randomUUID().toString(), null, directory);
    }
}