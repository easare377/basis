package org.odk.cersgis.basis.backgroundwork;

import java.util.function.Function;

public interface ChangeLock {

    <T> T withLock(Function<Boolean, T> function);
}
