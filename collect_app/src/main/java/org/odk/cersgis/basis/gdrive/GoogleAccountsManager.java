/*
 * Copyright (C) 2017 Shobhit
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.odk.cersgis.basis.gdrive;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import org.odk.cersgis.basis.preferences.GeneralKeys;
import org.odk.cersgis.basis.preferences.GeneralSharedPreferences;
import org.odk.cersgis.basis.utilities.ThemeUtils;

import java.io.IOException;

import javax.inject.Inject;

public class GoogleAccountsManager {

    private final GoogleAccountPicker accountPicker;

    private Intent intentChooseAccount;
    private Context context;
    private GeneralSharedPreferences preferences;
    private ThemeUtils themeUtils;

    @Inject
    public GoogleAccountsManager(@NonNull Context context, GoogleAccountPicker googleAccountPicker) {
        this.accountPicker = googleAccountPicker;
        initCredential(context);
    }

    /**
     * This constructor should be used only for testing purposes
     */
    public GoogleAccountsManager(@NonNull GoogleAccountCredential credential,
                                 @NonNull GeneralSharedPreferences preferences,
                                 @NonNull Intent intentChooseAccount,
                                 @NonNull ThemeUtils themeUtils
    ) {
        this.accountPicker = new GoogleAccountCredentialGoogleAccountPicker(credential);
        this.preferences = preferences;
        this.intentChooseAccount = intentChooseAccount;
        this.themeUtils = themeUtils;
    }

    public boolean isAccountSelected() {
        return accountPicker.getSelectedAccountName() != null;
    }

    @NonNull
    public String getLastSelectedAccountIfValid() {
        Account[] googleAccounts = accountPicker.getAllAccounts();
        String account = (String) preferences.get(GeneralKeys.KEY_SELECTED_GOOGLE_ACCOUNT);

        if (googleAccounts != null && googleAccounts.length > 0) {
            for (Account googleAccount : googleAccounts) {
                if (googleAccount.name.equals(account)) {
                    return account;
                }
            }

            preferences.reset(GeneralKeys.KEY_SELECTED_GOOGLE_ACCOUNT);
        }

        return "";
    }

    public void selectAccount(String accountName) {
        if (accountName != null) {
            preferences.save(GeneralKeys.KEY_SELECTED_GOOGLE_ACCOUNT, accountName);
            accountPicker.setSelectedAccountName(accountName);
        }
    }

    public String getToken() throws IOException, GoogleAuthException {
        String token = accountPicker.getToken();

        // Immediately invalidate so we get a different one if we have to try again
        GoogleAuthUtil.invalidateToken(context, token);
        return token;
    }

    public Intent getAccountChooserIntent() {
        Account selectedAccount = getAccountPickerCurrentAccount();
        intentChooseAccount.putExtra("selectedAccount", selectedAccount);
        intentChooseAccount.putExtra("overrideTheme", themeUtils.getAccountPickerTheme());
        intentChooseAccount.putExtra("overrideCustomTheme", 0);
        return intentChooseAccount;
    }

    private Account getAccountPickerCurrentAccount() {
        String selectedAccountName = getLastSelectedAccountIfValid();
        if (selectedAccountName.isEmpty()) {
            Account[] googleAccounts = accountPicker.getAllAccounts();
            if (googleAccounts != null && googleAccounts.length > 0) {
                selectedAccountName = googleAccounts[0].name;
            } else {
                return null;
            }
        }
        return new Account(selectedAccountName, "com.google");
    }

    private void initCredential(@NonNull Context context) {
        this.context = context;

        preferences = GeneralSharedPreferences.getInstance();

        intentChooseAccount = accountPicker.newChooseAccountIntent();
        themeUtils = new ThemeUtils(context);
    }
}
