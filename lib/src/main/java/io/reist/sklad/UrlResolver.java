package io.reist.sklad;

import java.io.IOException;

/**
 * Transforms {@link StorageObject} ids into URLs.
 *
 * @see NetworkStorage
 */
public interface UrlResolver {

    String toUrl(String id) throws IOException;

}
