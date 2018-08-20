/*
 * Copyright (C) 2017 Renat Sarymsakov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.reist.sklad;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import io.reist.sklad.utils.FileUtils;

import static io.reist.sklad.utils.FileUtils.getFolderSize;

/**
 * Created by Reist on 28.06.16.
 */
public class FileStorage implements JournalingStorage {

    private final Set<String> existenceSet = new HashSet<>();

    private File parent;
    private final FileUtils.Filter filter;

    private long usedSpace;

    public FileStorage(@NonNull File parent) {
        this(parent, FileUtils.DEFAULT_FILTER);
    }

    public FileStorage(@NonNull File parent, @NonNull FileUtils.Filter filter) {
        this.parent = parent;
        this.filter = filter;
        recalculateUsedSpace();
        checkFileExistence();
    }

    private void checkFileExistence() {

        File[] files = parent.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {
            if (filter.accept(file)) {
                String id = getIdByFile(file);
                this.existenceSet.add(id);
            }
        }

    }

    private void recalculateUsedSpace() {
        usedSpace = getFolderSize(parent);
    }

    @Override
    public synchronized boolean contains(@NonNull String id) {
        return existenceSet.contains(id);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @NonNull
    @Override
    public synchronized OutputStream openOutputStream(@NonNull final String id) throws IOException {
        File file = getFileById(id);
        file.getParentFile().mkdirs();
        return new FileOutputStream(file) {

            @Override
            public void close() throws IOException {
                try {
                    super.close();
                } finally {
                    recalculateUsedSpace();
                    existenceSet.add(id);
                }
            }

        };
    }

    @Nullable
    @Override
    public InputStream openInputStream(@NonNull String id) {
        try {
            File file = getFileById(id);
            if (filter.accept(file)) {
                return new InterruptibleInputStream(new FileInputStream(file));
            }
        } catch (FileNotFoundException ignored) {}
        return null;
    }

    @Override
    public synchronized boolean delete(@NonNull String id) {
        try {
            File file = getFileById(id);
            if (filter.accept(file)) {
                return file.delete();
            }
        } finally {
            recalculateUsedSpace();
            existenceSet.remove(id);
        }
        return false;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public synchronized void deleteAll() {
        try {
            FileUtils.deleteFile(parent, filter);
        } finally {
            recalculateUsedSpace();
            existenceSet.clear();
        }
    }

    @Override
    public long getUsedSpace() {
        return usedSpace;
    }

    @Override
    public String getOldestId() {

        File[] files = parent.listFiles();

        if (files == null) {
            return null;
        }

        File oldest = null;

        for (File file : files) {

            if (file.isDirectory() || !filter.accept(file)) {
                continue;
            }

            if (oldest == null || file.lastModified() < oldest.lastModified()) {
                oldest = file;
            }

        }

        return oldest == null ? null : getIdByFile(oldest);

    }

    public File getFileById(@NonNull String id) {
        return new File(parent, id);
    }

    public String getIdByFile(File file) {
        return file.getName();
    }

    public synchronized void setParent(File parent) throws IOException {
        FileUtils.moveAllFiles(this.parent, parent, filter);
        this.parent = parent;
    }

    public File getParent() {
        return parent;
    }

}
