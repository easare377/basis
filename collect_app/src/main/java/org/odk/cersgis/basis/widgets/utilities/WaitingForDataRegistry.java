package org.odk.cersgis.basis.widgets.utilities;

import org.javarosa.core.model.FormIndex;

public interface WaitingForDataRegistry {

    void waitForData(FormIndex index);

    boolean isWaitingForData(FormIndex index);

    void cancelWaitingForData();
}
