package io.reist.sklad.utils;


import android.os.Build;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Created by 4xes on 26/12/2016.
 */

public class ZipUtils {

    public static void writeEmptyZip(File destFile) throws IOException{
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destFile));
        zos.close();
    }

    /**
     * Copies an existing ZIP file and removes entries.
     *
     * @param srcFile an existing ZIP file (only read)
     * @param paths   the paths of entries to remove
     */
    public static boolean removeEntries(File srcFile, String[] paths) throws IOException {

        File tmpFile = null;
        ZipOutputStream out = null;

        boolean removed = false;
        try {
            tmpFile = FileUtils.tempFile(new File(srcFile.getParent()));
            out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(tmpFile)));
            removed = copyEntries(srcFile, out, new HashSet<>(Arrays.asList(paths)));
        } finally {
            try {
                out.close();
                FileUtils.deleteFile(srcFile);
                FileUtils.moveFile(tmpFile, srcFile);
            } catch (IOException e) {
                FileUtils.deleteFile(tmpFile);
            }
        }
        return removed;
    }

    /**
     * Copies all entries from one ZIP file to another, ignoring entries with path in skipEntries
     *
     * @param srcZip      source ZIP file.
     * @param out         target ZIP stream.
     * @param skipEntries paths of entries not to copy
     */
    public static boolean copyEntries(File srcZip, final ZipOutputStream out, Set<String> skipEntries) throws IOException {
        ZipFile zipFile = new ZipFile(srcZip);
        boolean skipped = false;

        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();

            if (skipEntries.contains(entry.getName())) {
                skipped = true;
                continue;
            }
            ZipUtils.copyEntry(zipFile, entry, out);
        }

        zipFile.close();
        return skipped;
    }

    @SuppressWarnings("ThrowFromFinallyBlock")
    public static void copyEntry(ZipFile zipFile, ZipEntry entryName, ZipOutputStream out) throws IOException {
        byte[] buffer;
        InputStream inputStream = null;
        try {
            buffer = new byte[4098];
            inputStream = zipFile.getInputStream(entryName);
            out.putNextEntry(entryName);
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            out.closeEntry();

        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
}
