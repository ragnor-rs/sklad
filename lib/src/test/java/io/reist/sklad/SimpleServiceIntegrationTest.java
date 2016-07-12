package io.reist.sklad;

import android.support.annotation.NonNull;

/**
 * Created by Reist on 24.06.16.
 */
public class SimpleServiceIntegrationTest extends BaseSkladServiceTest<SimpleSkladService> {

    @Override
    @NonNull
    protected SimpleSkladService createSkladService() {
        return new SimpleSkladService(new MemoryStorage());
    }

}