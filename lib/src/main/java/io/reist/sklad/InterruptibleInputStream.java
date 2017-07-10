package io.reist.sklad;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

/**
 * Created by reist on 10.07.17.
 */

class InterruptibleInputStream extends InputStream {

    private final InputStream inputStream;

    public InterruptibleInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public int read(@NonNull byte[] b) throws IOException {
        int read = inputStream.read(b);
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedIOException();
        }
        return read;
    }

    @Override
    public int read(@NonNull byte[] b, int off, int len) throws IOException {
        int read = inputStream.read(b, off, len);
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedIOException();
        }
        return read;
    }

    @Override
    public int read() throws IOException {
        int read = inputStream.read();
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedIOException();
        }
        return read;
    }

    @Override
    public long skip(long n) throws IOException {
        return inputStream.skip(n);
    }

    @Override
    public int available() throws IOException {
        return inputStream.available();
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        inputStream.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        inputStream.reset();
    }

    @Override
    public boolean markSupported() {
        return inputStream.markSupported();
    }

}
