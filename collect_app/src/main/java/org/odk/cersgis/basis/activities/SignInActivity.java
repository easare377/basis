package org.odk.cersgis.basis.activities;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import com.emma.general_backend_library.HttpRequest.ApiRequestAsync;
import com.emma.general_backend_library.HttpRequest.ApiRequestListener;
import com.emma.general_backend_library.HttpRequest.RequestAsyncTaskResponse;
import com.google.gson.JsonObject;

import org.odk.cersgis.basis.R;
import org.odk.cersgis.basis.activities.viewmodels.SignInViewModel;
import org.odk.cersgis.basis.databinding.ActivitySignInBinding;
import org.odk.cersgis.basis.models.Uris;

import io.paperdb.Paper;

public class SignInActivity extends CollectAbstractActivity {

    private SignInViewModel signInViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(autoSignIn()){
            finish ();
            return;
        }
        ActivitySignInBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_sign_in);
        signInViewModel = new SignInViewModel() {
            @Override
            public void signInCommand(String phoneNumber) {
                if(phoneNumber == null || phoneNumber.equals("")){
                    setErrorMessage("Phone number is required!");
                    return;
                }
                if(phoneNumber.length() != 10 || !phoneNumber.matches("[0-9]+")){
                    setErrorMessage("Invalid phone number!");
                    Toast.makeText (SignInActivity.this, "Invalid phone number!",
                            Toast.LENGTH_LONG).show ();
                    return;
                }
                SignInActivity.this.signInBasis(phoneNumber);
            }
        };
        binding.setSignInVm(signInViewModel);
        requestPermission();
    }


    private boolean autoSignIn() {
        super.onStart ();
        String phoneNumber = Paper.book ().read ("phoneNumber");
        String username = Paper.book ().read ("username");
        if (phoneNumber != null && username != null) {
            startActivity (new Intent (this, MainMenuActivity.class));
            return true;
        }
        return false;
    }

    private void signInBasis(String phoneNumber){
        signInViewModel.setBusy(true);
        ApiRequestAsync request = new ApiRequestAsync(new ApiRequestListener() {
            @Override
            public void processFinish(RequestAsyncTaskResponse requestAsyncTaskResponse, Exception e) {
                processSelectedFormDataResponse(requestAsyncTaskResponse, e, phoneNumber);
            }

            @Override
            public void processFinish(RequestAsyncTaskResponse requestAsyncTaskResponse, Exception e, Object o) {

            }
        }, 15000);
        request.downloadObject(Uris.getSignInUrl() + phoneNumber);
    }

    private void processSelectedFormDataResponse(RequestAsyncTaskResponse response, Exception e, String phoneNumber) {
        signInViewModel.setBusy(false);
        if (e != null) {
            showNoInternetErrorToast();
            return;
        }
        try {
            if (response.getStatusCode() == 200) {
                JsonObject signInResponse = response.getResponseObject(JsonObject.class);
                if(signInResponse.has("name")){
                    //get the username returned from the server.
                    String username = signInResponse.get("name").getAsString();
                    //the username and phone number is saved for future auto logins.
                   saveSignInData(phoneNumber, username);
                   //goto the main activity after successful login.
                   gotoMainMenuActivity();
                }else{
                    Toast.makeText (this, "Phone number does not exist!",
                            Toast.LENGTH_SHORT).show ();
                }
            } else {
                showNoInternetErrorToast();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            showNoInternetErrorToast();
        }
    }

    private void saveSignInData(String phoneNumber, String username){
        Paper.book().write("phoneNumber", phoneNumber);
        Paper.book().write("username",username);
    }

    private void gotoMainMenuActivity(){
        Intent intent = new Intent (this, MainMenuActivity.class);
        startActivity (intent);
        finish ();
    }

    private void showNoInternetErrorToast(){
        Toast.makeText (SignInActivity.this, "Could not contact the server! Please make sure you are connected to the internet.",
                Toast.LENGTH_SHORT).show ();
    }

    private void requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, 2235);
        }

//        @Override
//        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//            super.onActivityResult(requestCode, resultCode, data);
//            if (requestCode == 2296) {
//                if (SDK_INT >= Build.VERSION_CODES.R) {
//                    if (Environment.isExternalStorageManager()) {
//                        // perform action when allow permission success
//                    } else {
//                        Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        }
    }
}