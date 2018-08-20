package io.reist.sklad.utils;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;


public class FileUtils {

    public static final Filter DEFAULT_FILTER = new Filter() {

        @Override
        public boolean accept(@NonNull File f) {
            return true;
        }

    };

    private FileUtils() {}

    @SuppressWarnings({"ResultOfMethodCallIgnored", "TryFinallyCanBeTryWithResources"})
    public static void moveFile(File srcFile, File dstFile) throws IOException {
        if (dstFile.exists()) {
            throw new IOException("Destination file already exists: " + dstFile.getAbsolutePath());
        }
        File dstContainer = dstFile.getParentFile();
        dstContainer.mkdirs();
        if (!dstContainer.exists()) {
            throw new IOException("Destination container doesn't exist: " + dstContainer.getAbsolutePath());
        }
        FileOutputStream fileOutputStream = new FileOutputStream(dstFile);
        try {
            FileInputStream fileInputStream = new FileInputStream(srcFile);
            try {
                byte[] data = new byte[16 * 1024];
                int n;
                while ((n = fileInputStream.read(data)) > 0) {
                    fileOutputStream.write(data, 0, n);
                }
                fileOutputStream.flush();
            } finally {
                fileInputStream.close();
            }
        } finally {
            fileOutputStream.close();
        }
        if (!srcFile.delete()) {
            throw new IOException("Can't delete the source file: " + srcFile.getAbsolutePath());
        }
    }

    public static boolean deleteFile(@NonNull File file) {
        return deleteFile(file, DEFAULT_FILTER);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static boolean deleteFile(@NonNull File file, @NonNull Filter filter) {

        if (!file.exists()) {
            return true;
        }

        if (file.isFile()) {
            return file.delete();
        }

        if (!file.isDirectory()) {
            return false;
        }

        File[] files = file.listFiles();

        if (files == null) {
            return false;
        }

        boolean b = true;

        for (File f : files) {
            if (!filter.accept(f)) {
                continue;
            }
            if (f.isFile()) {
                b &= f.delete();
            } else if (f.isDirectory()) {
                b &= deleteFile(f);
            }
        }

        b &= file.delete();

        return b;

    }

    public static File tempFile(File directory) throws IOException {
        return File.createTempFile(tempName(), null, directory);
    }

    @SuppressWarnings("WeakerAccess")
    public static String tempName() {
        return UUID.randomUUID().toString();
    }

    public static long getFolderSize(File directory) {
        long length = 0;
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile())
                    length += file.length();
                else
                    length += getFolderSize(file);
            }
        }
        return length;
    }

    @SuppressWarnings("unused")
    public static void moveAllFiles(@NonNull File from, @NonNull File to) throws IOException {
        moveAllFiles(from, to, DEFAULT_FILTER);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void moveAllFiles(@NonNull File from, @NonNull File to, @NonNull Filter filter) throws IOException {

        if (!from.exists() || !from.isDirectory()) {
            throw new IOException(from.getAbsolutePath() + " must be an existing directory");
        }

        if (to.exists() && !to.isDirectory()) {
            throw new IOException(to.getAbsolutePath() + " is not a directory");
        } else if (!to.exists() & !to.mkdirs()) {
            throw new IOException("Can't create " + to.getAbsolutePath());
        }

        File[] files = from.listFiles();

        if (files == null) {
            throw new IOException("from.listFiles returned null");
        }

        for (File file : files) {
            if (!filter.accept(file)) {
                continue;
            }
            String absolutePath = file.getAbsolutePath();
            String relative = absolutePath.substring(from.getAbsolutePath().length() + 1);
            File newFile = new File(to, relative);
            if (file.isDirectory()) {
                System.out.println("create(" + newFile.getAbsoluteFile() + ") = " + newFile.mkdirs());
                moveAllFiles(file, newFile, filter);
            } else {
                moveFile(file, newFile);
            }
        }

    }

    public interface Filter {
        boolean accept(@NonNull File f);
    }

}