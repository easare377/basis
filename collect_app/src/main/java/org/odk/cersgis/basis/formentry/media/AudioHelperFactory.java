package org.odk.cersgis.basis.formentry.media;

import android.content.Context;

import org.odk.cersgis.basis.audio.AudioHelper;

public interface AudioHelperFactory {

    AudioHelper create(Context context);
}
