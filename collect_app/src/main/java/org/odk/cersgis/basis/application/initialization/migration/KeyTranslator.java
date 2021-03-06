package org.odk.cersgis.basis.application.initialization.migration;

import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

import static org.odk.cersgis.basis.application.initialization.migration.MigrationUtils.replace;

public class KeyTranslator implements Migration {

    String oldKey;
    String newKey;
    Object tempOldValue;
    Map<Object, Object> translatedValues = new HashMap<>();

    KeyTranslator(String oldKey) {
        this.oldKey = oldKey;
    }

    public KeyTranslator toKey(String newKey) {
        this.newKey = newKey;
        return this;
    }

    public KeyTranslator fromValue(Object oldValue) {
        this.tempOldValue = oldValue;
        return this;
    }

    public KeyTranslator toValue(Object newValue) {
        translatedValues.put(tempOldValue, newValue);
        return this;
    }

    public void apply(SharedPreferences prefs) {
        if (prefs.contains(oldKey) && !prefs.contains(newKey)) {
            Object oldValue = prefs.getAll().get(oldKey);
            Object newValue = translatedValues.get(oldValue);
            if (newValue != null) {
                replace(prefs, oldKey, newKey, newValue);
            }
        }
    }
}
