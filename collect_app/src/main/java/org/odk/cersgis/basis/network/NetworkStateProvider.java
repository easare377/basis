package org.odk.cersgis.basis.network;

import android.net.NetworkInfo;

public interface NetworkStateProvider {

    boolean isDeviceOnline();

    NetworkInfo getNetworkInfo();
}
