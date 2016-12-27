package io.reist.sklad.utils;

import android.os.Build;

import java.io.Closeable;
import java.io.IOException;
import java.util.zip.ZipFile;

public class IOUtils {

    private IOUtils() {
        throw new AssertionError();
    }

    /**
     * Close closable object and wrap {@link IOException} with {@link RuntimeException}
     *
     * @param closeable closeable object
     */
    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                throw new RuntimeException("IOException occurred. ", e);
            }
        }
    }

    /**
     * Close closable and hide possible {@link IOException}
     *
     * @param closeable closeable object
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // Ignored
            }
        }
    }

    /**
     * Close closable and hide possible {@link IOException}
     *
     * @param zipFile if it closeable
     */
    public static void closeQuietly(ZipFile zipFile) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                if (zipFile != null) {
                    zipFile.close();
                }
            } catch (IOException ioe) {
                // ignore
            }
        }
    }

}