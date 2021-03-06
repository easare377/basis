package org.odk.cersgis.basis.support.pages;

import androidx.test.rule.ActivityTestRule;

import org.odk.cersgis.basis.R;

public class ServerAuthDialog extends Page<ServerAuthDialog> {

    public ServerAuthDialog(ActivityTestRule rule) {
        super(rule);
    }

    @Override
    public ServerAuthDialog assertOnPage() {
        assertText(R.string.server_requires_auth);
        return this;
    }

    public ServerAuthDialog fillUsername(String username) {
        inputText(R.string.username, username);
        return this;
    }

    public ServerAuthDialog fillPassword(String password) {
        inputText(R.string.password, password);
        return this;
    }

    public <D extends Page<D>> D clickOK(D destination) {
        clickOnString(R.string.ok);
        return destination.assertOnPage();
    }
}
