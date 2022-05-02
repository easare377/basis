package org.odk.cersgis.basis.support;

import org.odk.cersgis.basis.backgroundwork.ChangeLock;

import java.util.function.Function;

public class BooleanChangeLock implements ChangeLock {

    private boolean locked;

    @Override
    public <T> T withLock(Function<Boolean, T> function) {
        return function.apply(!locked);
    }

    public void lock() {
        locked = true;
    }
}
