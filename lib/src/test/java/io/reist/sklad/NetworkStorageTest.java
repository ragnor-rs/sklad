package io.reist.sklad;

import android.os.Build;
import android.support.annotation.NonNull;

import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;

/**
 * Created by Reist on 28.06.16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(
        constants = BuildConfig.class,
        sdk = Build.VERSION_CODES.LOLLIPOP,
        shadows = ShadowNetworkSecurityPolicy.class
)
public class NetworkStorageTest extends BaseStorageTest<NetworkStorage>  {

    private static final String TEST_URL = "test";

    private HttpUrl baseUrl;

    @NonNull
    @Override
    protected NetworkStorage createStorage() throws IOException {
        UrlResolver urlResolver = Mockito.mock(UrlResolver.class);
        Mockito.when(urlResolver.getUrlByName(TestUtils.TEST_NAME)).then(new Answer<String>() {

            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                if (baseUrl == null) {
                    throw new IllegalStateException();
                }
                return baseUrl + TEST_URL;
            }

        });
        return new NetworkStorage(urlResolver) {

            @NonNull
            @Override
            public OutputStream openOutputStream(@NonNull String name) throws IOException {
                return Mockito.mock(OutputStream.class);
            }

        };
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

    @Override
    public void testStreams() throws Exception {

        Buffer buffer = new Buffer();
        buffer.readFrom(new ByteArrayInputStream(TestUtils.TEST_DATA));

        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(buffer));
        server.start();

        baseUrl = server.url("/");

        super.testStreams();

        server.shutdown();

    }

}
