package io.reist.sklad;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by reist on 17.04.17.
 */

public class LimitedStorage implements Storage {

    private final JournalingStorage journalingStorage;

    private long capacity;

    public LimitedStorage(JournalingStorage journalingStorage, long capacity) {
        this.journalingStorage = journalingStorage;
        this.capacity = capacity;
    }

    @Override
    public boolean contains(@NonNull String id) throws IOException {
        return journalingStorage.contains(id);
    }

    @NonNull
    @Override
    public OutputStream openOutputStream(@NonNull String id) throws IOException {
        final OutputStream outputStreamToWrap = journalingStorage.openOutputStream(id);
        if (capacity == 0) {
            return new OutputStream() {

                @Override
                public void write(int b) throws IOException {
                    throw new IOException("Cannot write when capacity is 0");
                }

                @Override
                public void flush() throws IOException {
                    throw new IOException("Cannot flush when capacity is 0");
                }

            };
        } else {
            return new OutputStream() {

                @Override
                public void write(@NonNull byte[] b) throws IOException {
                    allocate(b.length);
                    outputStreamToWrap.write(b);
                }

                @Override
                public void write(@NonNull byte[] b, int off, int len) throws IOException {
                    allocate(len);
                    outputStreamToWrap.write(b, off, len);
                }

                @Override
                public void write(int b) throws IOException {
                    allocate(1);
                    outputStreamToWrap.write(b);
                }

                @Override
                public void close() throws IOException {
                    outputStreamToWrap.close();
                }

                @Override
                public void flush() throws IOException {
                    outputStreamToWrap.flush();
                }

            };
        }
    }

    @Nullable
    @Override
    public InputStream openInputStream(@NonNull String id) throws IOException {
        return journalingStorage.openInputStream(id);
    }

    @Override
    public boolean delete(@NonNull String id) throws IOException {
        return journalingStorage.delete(id);
    }

    @Override
    public void deleteAll() throws IOException {
        journalingStorage.deleteAll();
    }

    private void allocate(int len) throws IOException {

        if (capacity == -1) {
            return;
        }

        while (capacity - journalingStorage.getUsedSpace() < len) {
            String oldestId = journalingStorage.getOldestId();
            if (oldestId == null) {
                break;
            } else {
                if (!journalingStorage.delete(oldestId)) {
                    throw new IOException("Unable to free space");
                }
            }
        }

    }

    public void setCapacity(long capacity) throws IOException {
        this.capacity = capacity;
        allocate(0);
    }

    public long getCapacity() {
        return capacity;
    }

}
