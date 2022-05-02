package org.odk.cersgis.basis.utilities;

import org.odk.cersgis.basis.BuildConfig;
import org.odk.cersgis.utilities.UserAgentProvider;

public final class AndroidUserAgent implements UserAgentProvider {

    @Override
    public String getUserAgent() {
        return String.format("%s/%s %s",
                BuildConfig.APPLICATION_ID,
                BuildConfig.VERSION_NAME,
                System.getProperty("http.agent"));
    }

}
