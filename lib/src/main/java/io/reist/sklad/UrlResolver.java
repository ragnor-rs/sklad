package io.reist.sklad;

import java.io.IOException;

/**
 * Created by Reist on 28.06.16.
 */
public interface UrlResolver {

    String getUrlByName(String name) throws IOException;

}
