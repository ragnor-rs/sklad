package io.reist.sklad.utils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


public class FileUtils {

    private FileUtils() {}

    public static void moveFile(File srcFile, File dstFile) throws IOException {
        boolean rename = srcFile.renameTo(dstFile);
        if (!rename) {
            throw new IOException(
                    "Can't rename file " + srcFile.getAbsolutePath() + " (" + srcFile.exists() + ") to " + dstFile.getAbsolutePath() + " (" + dstFile.exists() + ")"
            );
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
        return File.createTempFile(tempName(), null, directory);
    }

    public static String tempName() {
        return UUID.randomUUID().toString();
    }

    public static long getFolderSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += getFolderSize(file);
        }
        return length;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void moveAllFiles(File from, File to) throws IOException {

        if (!from.exists() || !from.isDirectory()) {
            throw new IOException(from.getAbsolutePath() + " must be an existing directory");
        }

        if (to.exists() && !to.isDirectory()) {
            throw new IOException(to.getAbsolutePath() + " is not a directory");
        } else if (!to.exists() && !to.mkdirs()) {
            throw new IOException("Can't create " + to.getAbsolutePath());
        }

        for (File file : from.listFiles()) {
            String absolutePath = file.getAbsolutePath();
            String relative = absolutePath.substring(from.getAbsolutePath().length() + 1);
            File newFile = new File(to, relative);
            if (file.isDirectory()) {
                System.out.println("create(" + newFile.getAbsoluteFile() + ") = " + newFile.mkdirs());
                moveAllFiles(file, newFile);
            } else {
                moveFile(file, newFile);
            }
        }

    }
    
}