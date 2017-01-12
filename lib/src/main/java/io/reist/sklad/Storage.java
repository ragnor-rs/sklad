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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * I/O stream manager
 */
public interface Storage {

    boolean contains(@NonNull String id) throws IOException;

    @NonNull
    OutputStream openOutputStream(@NonNull String id) throws IOException;

    @Nullable
    InputStream openInputStream(@NonNull String id) throws IOException;

    boolean delete(@NonNull String id) throws IOException;

    void deleteAll() throws IOException;

}
