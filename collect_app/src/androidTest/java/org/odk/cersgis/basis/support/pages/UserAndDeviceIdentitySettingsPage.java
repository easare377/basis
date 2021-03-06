package org.odk.cersgis.basis.support.pages;

import androidx.test.rule.ActivityTestRule;

import org.odk.cersgis.basis.R;

public class UserAndDeviceIdentitySettingsPage extends Page<UserAndDeviceIdentitySettingsPage> {

    public UserAndDeviceIdentitySettingsPage(ActivityTestRule rule) {
        super(rule);
    }

    @Override
    public UserAndDeviceIdentitySettingsPage assertOnPage() {
        assertText(R.string.user_and_device_identity_title);
        return this;
    }

    public FormMetadataPage clickFormMetadata() {
        clickOnString(R.string.form_metadata);
        return new FormMetadataPage(rule);
    }
}
