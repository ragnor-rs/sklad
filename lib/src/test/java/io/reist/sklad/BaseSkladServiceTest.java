package io.reist.sklad;

import android.support.annotation.NonNull;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static io.reist.sklad.TestUtils.assertTestObject;
import static io.reist.sklad.TestUtils.saveTestObject;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Reist on 28.06.16.
 */
public abstract class BaseSkladServiceTest<S extends SkladService> {

    @NonNull
    protected abstract S createSkladService();

    @Test
    public void testWrite() throws IOException {
        SkladService sklad = createSkladService();
        assertFalse(saveTestObject(sklad));
        assertTrue(saveTestObject(sklad));
    }

    @Test(expected = IllegalStateException.class)
    public void testDepletedInputStream() throws IOException {
        SkladService sklad = createSkladService();
        StorageObject savedObject = new StorageObject(
                TestUtils.TEST_NAME,
                new ByteArrayInputStream(TestUtils.TEST_DATA)
        );
        sklad.save(savedObject);
        sklad.save(savedObject); // second call should throw an exception
    }

    @Test
    public void testRead() throws IOException {
        SkladService sklad = createSkladService();
        assertNull(sklad.load(TestUtils.TEST_NAME));
    }

    @Test
    public void testSaveAndLoad() throws Exception {
        SkladService skladService = createSkladService();
        saveTestObject(skladService);
        assertTestObject(skladService);
    }

}
