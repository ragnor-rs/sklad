package io.reist.sklad;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;

/**
 * Created by Reist on 28.06.16.
 */
public class NetworkStorage implements Storage {

    private final OkHttpClient client = new OkHttpClient();

    private final UrlResolver urlResolver;

    public NetworkStorage(UrlResolver urlResolver) {
        this.urlResolver = urlResolver;
    }

    private Response request(@NonNull String name) throws IOException {
        String url = urlResolver.toUrl(name);
        Request request = new Request.Builder().url(url).build();
        return client.newCall(request).execute();
    }

    @Override
    public boolean contains(@NonNull String id) throws IOException {
        return request(id).isSuccessful();
    }

    @NonNull
    @Override
    public OutputStream openOutputStream(@NonNull String id) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public InputStream openInputStream(@NonNull String id) throws IOException {
        final ResponseBody body = request(id).body();
        final long contentLength = body.contentLength();
        final BufferedSource source = body.source();
        return new InputStream() {

            private int position = 0;

            @Override
            public int read(byte[] b) throws IOException {
                int numRead = source.read(b);
                position += numRead;
                return numRead;
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                int numRead = source.read(b, off, len);
                position += numRead;
                return numRead;
            }

            @Override
            public long skip(long n) throws IOException {
                source.skip(n);
                position += n;
                return n;
            }

            @Override
            public int available() throws IOException {
                return (int) contentLength - position;
            }

            @Override
            public void close() throws IOException {
                body.close();
            }

            @Override
            public synchronized void mark(int readlimit) {
                throw new UnsupportedOperationException();
            }

            @Override
            public synchronized void reset() throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean markSupported() {
                return false;
            }

            @Override
            public int read() throws IOException {
                position++;
                return source.readByte() & 0xFF;
            }

        };
    }

    @Override
    public boolean delete(@NonNull String id) throws IOException {
        String url = urlResolver.toUrl(id);
        Request request = new Request.Builder().url(url).delete().build();
        return client.newCall(request).execute().isSuccessful();
    }

}
