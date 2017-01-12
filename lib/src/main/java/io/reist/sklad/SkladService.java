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

import java.io.IOException;

/**
 * A persistent storage service for {@link StorageObject}s.
 */
public interface SkladService {

    /**
     * @throws IllegalStateException    if the given object's {@link java.io.InputStream} has
     *                                  reached its end
     *
     * @throws IOException              if an I/O error occurs during persisting the object
     *
     * @return true     if the object has been overwritten
     */
    boolean save(@NonNull StorageObject storageObject) throws IOException;

    /**
     * @return null     if an object with the given id doesn't exist
     */
    StorageObject load(@NonNull String id) throws IOException;

}
