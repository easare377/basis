package org.odk.cersgis.basis.support;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;

import org.odk.cersgis.basis.utilities.ScreenContext;
import org.odk.cersgis.testshared.FakeLifecycleOwner;

public class TestScreenContext implements ScreenContext {

    private final FragmentActivity activity;
    private final FakeLifecycleOwner lifecycleOwner = new FakeLifecycleOwner();

    public TestScreenContext(FragmentActivity activity) {
        this.activity = activity;
    }

    @Override
    public FragmentActivity getActivity() {
        return activity;
    }

    @Override
    public LifecycleOwner getViewLifecycle() {
        return lifecycleOwner;
    }

    public void destroyLifecycle() {
        lifecycleOwner.destroy();
    }
}
