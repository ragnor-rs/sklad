package io.reist.sklad;

/**
 * Created by reist on 17.04.17.
 */

public interface JournalingStorage extends Storage {

    long getUsedSpace();

    String getOldestId();

}
