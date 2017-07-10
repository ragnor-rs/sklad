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

import android.os.Build;
import android.support.annotation.NonNull;

import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

/**
 * Created by Reist on 28.06.16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(
        constants = BuildConfig.class,
        sdk = Build.VERSION_CODES.LOLLIPOP,
        shadows = ShadowNetworkSecurityPolicy.class
)
public class NetworkStorageTest extends BaseNetworkStorageTest<NetworkStorage> {

    @NonNull
    static NetworkStorage createNetworkStorage(final HttpUrl baseUrl) throws IOException {
        UrlResolver urlResolver = Mockito.mock(UrlResolver.class);
        Mockito.when(urlResolver.toUrl(Mockito.anyString())).then(new Answer<String>() {

            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                if (baseUrl == null) {
                    throw new IllegalStateException();
                }
                return baseUrl.toString() + "/" + invocation.getArguments()[0].toString();
            }

        });
        return new TestNetworkStorage(urlResolver);
    }

    @NonNull
    @Override
    protected NetworkStorage createStorage() throws IOException {
        return createNetworkStorage(baseUrl);
    }

    @Override
    public void testContains() throws Exception {

        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(404)); // nothing yet saved - return 404
        server.enqueue(new MockResponse()); // an object is saved - return 200
        server.start();

        baseUrl = server.url("/");

        super.testContains();

        server.shutdown();

    }

}
