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

import io.reist.sklad.utils.FileUtils;

import static io.reist.sklad.utils.FileUtils.getFolderSize;

/**
 * Created by Reist on 28.06.16.
 */
public class FileStorage implements JournalingStorage {

    private File parent;

    public FileStorage(@NonNull File parent) {
        this.parent = parent;
    }

    @Override
    public boolean contains(@NonNull String id) throws IOException {
        return getFileById(id).exists();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @NonNull
    @Override
    public synchronized OutputStream openOutputStream(@NonNull String id) throws IOException {
        File file = getFileById(id);
        file.getParentFile().mkdirs();
        return new FileOutputStream(file);
    }

    @Nullable
    @Override
    public InputStream openInputStream(@NonNull String id) throws IOException {
        try {
            return new InterruptibleInputStream(new FileInputStream(getFileById(id)));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public synchronized boolean delete(@NonNull String id) throws IOException {
        return getFileById(id).delete();
    }

    @Override
    public synchronized void deleteAll() throws IOException {
        File[] files = parent.listFiles();
        for (File f : files) {
            f.delete();
        }
    }

    @Override
    public long getUsedSpace() {
        return getFolderSize(parent);
    }

    @Override
    public String getOldestId() {
        File oldest = null;
        for (File file : parent.listFiles()) {

            if (file.isDirectory()) {
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
        FileUtils.moveAllFiles(this.parent, parent);
        this.parent = parent;
    }

}
