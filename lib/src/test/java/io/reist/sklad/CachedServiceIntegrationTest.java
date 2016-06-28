package io.reist.sklad;

import android.support.annotation.NonNull;

import org.junit.Test;

import static io.reist.sklad.TestUtils.assertTestObject;
import static io.reist.sklad.TestUtils.saveTestObject;

/**
 * Created by Reist on 26.06.16.
 */
public class CachedServiceIntegrationTest extends BaseSkladServiceTest<CachedSkladService> {

    @Override
    @NonNull
    protected CachedSkladService createSkladService() {
        return new CachedSkladService(new MemoryStorage(), new MemoryStorage());
    }

    @Test
    public void testRemoteSaveAndLoad() throws Exception {
        CachedSkladService skladService = createSkladService();
        saveTestObject(skladService.getRemoteStorage());
        assertTestObject(skladService);
    }

    @Test
    public void testLocalSaveAndLoad() throws Exception {
        CachedSkladService skladService = createSkladService();
        saveTestObject(skladService.getLocalStorage());
        assertTestObject(skladService);
    }

}