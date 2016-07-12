package io.reist.sklad;

import android.security.NetworkSecurityPolicy;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@Implements(NetworkSecurityPolicy.class)
public class ShadowNetworkSecurityPolicy {

    @SuppressWarnings("unused")
    @Implementation
    public static NetworkSecurityPolicy getInstance() {
        try {
            Class<?> shadow = Class.forName("android.security.NetworkSecurityPolicy");
            return (NetworkSecurityPolicy) shadow.newInstance();
        } catch (Exception e) {
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unused")
    @Implementation
    public boolean isCleartextTrafficPermitted() {
        return true;
    }

}