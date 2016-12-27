package io.reist.sklad;

import android.support.annotation.NonNull;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import io.reist.sklad.utils.FileUtils;
import io.reist.sklad.utils.ZipUtils;

/**
 * Created by 4xes on 23/12/2016.
 */
public class ZipEntryOutputStream extends OutputStream {

    private File srcFile;
    private File tmpFile;
    private String entryName;
    private ZipOutputStream out;

    public ZipEntryOutputStream(File srcFile, String entryName) throws IOException {
        super();
        this.srcFile = srcFile;
        this.entryName = entryName;
        tmpFile = FileUtils.tempFile(new File(srcFile.getParent()));
        copyAll();
    }

    public void copyAll() throws IOException {
        out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(tmpFile)));

        HashSet<String> skipSet = new HashSet<>();
        skipSet.add(entryName);
        ZipUtils.copyEntries(srcFile, out, skipSet);

        out.putNextEntry(new ZipEntry(entryName));
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }

    @Override
    public void close() throws IOException {
        out.closeEntry();
        out.close();

        FileUtils.deleteFile(srcFile);
        FileUtils.moveFile(tmpFile, srcFile);
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
}
