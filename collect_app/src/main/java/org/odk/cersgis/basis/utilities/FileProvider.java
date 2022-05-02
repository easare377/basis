package org.odk.cersgis.basis.utilities;

import android.net.Uri;

public interface FileProvider {
    Uri getURIForFile(String filePath);
}
