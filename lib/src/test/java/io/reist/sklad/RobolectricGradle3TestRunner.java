package io.reist.sklad;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.res.FileFsFile;
import org.robolectric.util.Logger;

import java.lang.reflect.Method;

/**
 * This runner is to be used with Gradle 3-compatible Android plugin.
 * {@link RobolectricGradleTestRunner} fails due to a change in paths to AndroidManifest.xml caused
 * by the plugin.
 */
public class RobolectricGradle3TestRunner extends RobolectricGradleTestRunner {

    public RobolectricGradle3TestRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected AndroidManifest getAppManifest(final Config config) {

        if (config.constants() == Void.class) {
            Logger.error("Field 'constants' not specified in @Config annotation");
            Logger.error("This is required when using RobolectricGradleTestRunner!");
            throw new RuntimeException("No 'constants' field in @Config annotation!");
        }

        final String buildOutputDir = callSuperPrivateStatic("getBuildOutputDir", config);
        final String type = callSuperPrivateStatic("getType", config);
        final String flavor = callSuperPrivateStatic("getFlavor", config);
        final String abiSplit = callSuperPrivateStatic("getAbiSplit", config);
        final String packageName = callSuperPrivateStatic("getPackageName", config);

        final FileFsFile res;
        final FileFsFile assets;
        final FileFsFile manifest;

        if (FileFsFile.from(buildOutputDir, "data-binding-layout-out").exists()) {
            // Android gradle plugin 1.5.0+ puts the merged layouts in data-binding-layout-out.
            // https://github.com/robolectric/robolectric/issues/2143
            res = FileFsFile.from(buildOutputDir, "data-binding-layout-out", flavor, type);
        } else if (FileFsFile.from(buildOutputDir, "res", "merged").exists()) {
            // res/merged added in Android Gradle plugin 1.3-beta1
            res = FileFsFile.from(buildOutputDir, "res", "merged", flavor, type);
        } else if (FileFsFile.from(buildOutputDir, "res").exists()) {
            res = FileFsFile.from(buildOutputDir, "res", flavor, type);
        } else {
            res = FileFsFile.from(buildOutputDir, "bundles", flavor, type, "res");
        }

        if (FileFsFile.from(buildOutputDir, "assets").exists()) {
            assets = FileFsFile.from(buildOutputDir, "assets", flavor, type);
        } else {
            assets = FileFsFile.from(buildOutputDir, "bundles", flavor, type, "assets");
        }

        if (FileFsFile.from(buildOutputDir, "manifests").exists()) {
            if (FileFsFile.from(buildOutputDir, "manifests", "full").exists()) {
                // Android Gradle Plugins till 2.1.x
                manifest = FileFsFile.from(buildOutputDir, "manifests", "full", flavor, abiSplit, type, "AndroidManifest.xml");
            } else {
                // Android Gradle Plugin 2.2.x
                manifest = FileFsFile.from(buildOutputDir, "manifests", "aapt", flavor, abiSplit, type, "AndroidManifest.xml");
            }
        } else {
            manifest = FileFsFile.from(buildOutputDir, "bundles", flavor, abiSplit, type, "AndroidManifest.xml");
        }

        Logger.debug("Robolectric assets directory: " + assets.getPath());
        Logger.debug("   Robolectric res directory: " + res.getPath());
        Logger.debug("   Robolectric manifest path: " + manifest.getPath());
        Logger.debug("    Robolectric package name: " + packageName);

        return new AndroidManifest(manifest, res, assets, packageName) {

            @Override
            public String getRClassName() throws Exception {
                return config.constants().getPackage().getName().concat(".R");
            }

        };

    }

    private String callSuperPrivateStatic(String methodName, Config config) {
        try {
            Class<?> superclass = getClass().getSuperclass();
            Method method = superclass.getDeclaredMethod(methodName, Config.class);
            method.setAccessible(true);
            return (String) method.invoke(null, config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}