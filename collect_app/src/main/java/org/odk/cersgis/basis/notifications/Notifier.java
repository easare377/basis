package org.odk.cersgis.basis.notifications;

import org.odk.cersgis.basis.formmanagement.ServerFormDetails;
import org.odk.cersgis.basis.forms.FormSourceException;

import java.util.HashMap;
import java.util.List;

public interface Notifier {

    void onUpdatesAvailable(List<ServerFormDetails> updates);

    void onUpdatesDownloaded(HashMap<ServerFormDetails, String> result);

    void onSync(FormSourceException exception);

    void onSubmission(boolean failure, String message);
}
