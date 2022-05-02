package org.odk.cersgis.basis.activities.viewmodels;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import org.odk.cersgis.basis.BR;

public abstract class SignInViewModel extends BaseObservable {
    private String phoneNumber;
    private String errorMessage;
    private boolean isBusy;

    @Bindable
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if(this.phoneNumber == phoneNumber)
            return;
        this.phoneNumber = phoneNumber;
        notifyPropertyChanged(BR.phoneNumber);
        setErrorMessage(null);
    }

    @Bindable
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        notifyPropertyChanged(BR.errorMessage);
    }

    @Bindable
    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
        notifyPropertyChanged(BR.busy);
    }

    public abstract void signInCommand(String phoneNumber);
}
