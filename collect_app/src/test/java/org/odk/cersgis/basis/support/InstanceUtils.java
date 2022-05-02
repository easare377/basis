package org.odk.cersgis.basis.support;

import org.odk.cersgis.basis.instances.Instance;

public class InstanceUtils {

    private InstanceUtils() {

    }

    public static Instance.Builder buildInstance(long id, String formId, String version) {
        return buildInstance(id, formId, version, "display name", Instance.STATUS_INCOMPLETE,
                null);
    }

    public static Instance.Builder buildInstance(long id, String formId, String version, String displayName,
                                                 String status, Long deletedDate) {
        return new Instance.Builder()
                .id(id)
                .jrFormId(formId)
                .jrVersion(version)
                .displayName(displayName)
                .instanceFilePath(formId + version + Math.random())
                .status(status)
                .lastStatusChangeDate(System.currentTimeMillis())
                .status(status)
                .deletedDate(deletedDate);
    }
}
