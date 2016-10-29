package io.reist.sklad;

import android.os.Build;
import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.annotation.Config;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;

/**
 * Created by Reist on 28.06.16.
 */
@RunWith(RobolectricGradle3TestRunner.class)
@Config(
        constants = BuildConfig.class,
        sdk = Build.VERSION_CODES.LOLLIPOP,
        shadows = ShadowNetworkSecurityPolicy.class
)
public class NetworkStorageTest extends BaseStorageTest<NetworkStorage>  {

    private HttpUrl baseUrl;

    @NonNull
    static NetworkStorage createNetworkStorage(final HttpUrl baseUrl) throws IOException {
        UrlResolver urlResolver = Mockito.mock(UrlResolver.class);
        Mockito.when(urlResolver.toUrl(Mockito.anyString())).then(new Answer<String>() {

            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                if (baseUrl == null) {
                    throw new IllegalStateException();
                }
                return baseUrl.toString() + invocation.getArguments()[0].toString();
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

    @Before
    public void setUp() throws Exception {
        baseUrl = null;
    }

    @Override
    public void testDeleteAll() throws Exception {

        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(200)); // contains a file - 200
        server.start();

        baseUrl = server.url("/");

        super.testDeleteAll();

        server.shutdown();

    }

    @Override
    public void testDelete() throws Exception {

        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(200)); // contains a file - 200
        server.start();

        baseUrl = server.url("/");

        super.testDelete();

        server.shutdown();

    }

}
