package org.odk.cersgis.basis.application.initialization.migration;

import android.content.SharedPreferences;

public interface Migration {
    void apply(SharedPreferences prefs);
}
