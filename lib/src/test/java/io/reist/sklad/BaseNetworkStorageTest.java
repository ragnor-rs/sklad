package io.reist.sklad;

import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;

import static okhttp3.mockwebserver.SocketPolicy.NO_RESPONSE;

/**
 * Created by reist on 10.07.17.
 */

public abstract class BaseNetworkStorageTest<S extends Storage> extends BaseStorageTest<S> {

    protected HttpUrl baseUrl;

    @Override
    @Test
    public final void testStreams() throws Exception {

        Buffer buffer = new Buffer();
        buffer.readFrom(new ByteArrayInputStream(TestUtils.TEST_DATA_1));

        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(buffer));
        server.start();

        baseUrl = server.url("/");

        super.testStreams();

        server.shutdown();

    }

    @Before
    public final void setUp() throws Exception {
        baseUrl = null;
    }

    @Override
    public final void testDeleteAll() throws Exception {

        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(404)); // force no files for cache
        server.start();

        baseUrl = server.url("/");

        super.testDeleteAll();

        server.shutdown();

    }

    @Override
    public final void testDelete() throws Exception {

        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(404)); // force no files for cache
        server.start();

        baseUrl = server.url("/");

        super.testDelete();

        server.shutdown();

    }

    @Test
    @Override
    public final void testSkip() throws Exception {

        Buffer buffer = new Buffer();
        buffer.readFrom(new ByteArrayInputStream(TestUtils.TEST_DATA_1));

        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(buffer));
        server.start();

        baseUrl = server.url("/");

        super.testSkip();

        server.shutdown();

    }

    @Test
    @Override
    public final void testInterruption() throws Exception {

        MockWebServer server = startWebServerForInterruption();
        baseUrl = server.url("/");

        super.testInterruption();

        server.shutdown();

    }

    @NonNull
    private static MockWebServer startWebServerForInterruption() throws IOException {

        Buffer buffer = new Buffer();
        buffer.readFrom(new ByteArrayInputStream(TestUtils.TEST_DATA_1));

        MockWebServer server = new MockWebServer();
        server.enqueue(
                new MockResponse()
                        .setSocketPolicy(NO_RESPONSE)
                        .setBody(buffer)
        );
        server.start();
        return server;

    }

}
