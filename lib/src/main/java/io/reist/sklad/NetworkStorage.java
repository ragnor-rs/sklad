package io.reist.sklad;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
        return request(id).body().byteStream();
    }

}
