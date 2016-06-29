package io.reist.sklad;

import java.io.IOException;

/**
 * Transforms {@link StorageObject} names into URLs.
 *
 * @see NetworkStorage
 */
public interface UrlResolver {

    String getUrlByName(String name) throws IOException;

}
