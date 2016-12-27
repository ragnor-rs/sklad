package io.reist.sklad.utils;


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

    public static void writeEmptyZip(File destFile) throws IOException {
        FileOutputStream fot = new FileOutputStream(destFile);
        ZipOutputStream zos = new ZipOutputStream(fot);
        zos.finish();
        zos.close();
        fot.close();
    }

    /**
     * Copies an existing ZIP file and removes entries.
     *
     * @param srcFile an existing ZIP file (only read)
     * @param paths   the paths of entries to remove
     */
    public static void removeEntries(File srcFile, String[] paths) throws IOException {
        File tmpFile = FileUtils.tempFile(new File(srcFile.getParent()));
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(tmpFile)));
        copyEntries(srcFile, out, new HashSet<>(Arrays.asList(paths)));

        IOUtils.closeQuietly(out);
        FileUtils.deleteFile(srcFile);
        FileUtils.moveFile(tmpFile, srcFile);
        FileUtils.deleteFile(tmpFile);
    }

    /**
     * Copies all entries from one ZIP file to another, ignoring entries with path in skipEntries
     *
     * @param srcZip      source ZIP file.
     * @param out         target ZIP stream.
     * @param skipEntries paths of entries not to copy
     */
    public static void copyEntries(File srcZip, final ZipOutputStream out, Set<String> skipEntries) throws IOException {
        ZipFile zipFile = new ZipFile(srcZip);

        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();

            if (skipEntries.contains(entry.getName())) {
                continue;
            }
            ZipUtils.copyEntry(zipFile, entry, out);
        }
        IOUtils.closeQuietly(zipFile);

    }

    public static void copyEntry(ZipFile zipFile, ZipEntry entryName, ZipOutputStream out) throws IOException {
        byte[] buffer = new byte[4098];

        InputStream inputStream = zipFile.getInputStream(entryName);
        out.putNextEntry(entryName);
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
        out.closeEntry();
        IOUtils.closeQuietly(inputStream);
    }
}
