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

import java.io.InputStream;

/**
 * A named object which can be stored in {@link SkladService}.
 */
public class StorageObject {

    private String id;

    private InputStream inputStream;

    private boolean inputStreamDepleted;

    public StorageObject(@NonNull String id, @NonNull InputStream inputStream) {
        this.id = id;
        this.inputStream = inputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    void setInputStreamDepleted(boolean inputStreamDepleted) {
        this.inputStreamDepleted = inputStreamDepleted;
    }

    @NonNull
    public String getId() {
        return id;
    }

    boolean isInputStreamDepleted() {
        return inputStreamDepleted;
    }

}
