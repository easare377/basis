package org.odk.cersgis.basis.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.emma.general_backend_library.Functions;
import com.emma.general_backend_library.HttpRequest.ApiRequestAsync;
import com.emma.general_backend_library.HttpRequest.ApiRequestListener;
import com.emma.general_backend_library.HttpRequest.RequestAsyncTaskResponse;


import org.odk.cersgis.basis.R;
import org.odk.cersgis.basis.RequestCsvTask;
import org.odk.cersgis.basis.activities.viewmodels.SyncDataViewModel;
import org.odk.cersgis.basis.activities.viewmodels.SyncFormViewModel;
import org.odk.cersgis.basis.databinding.ActivitySyncDataBinding;
import org.odk.cersgis.basis.databinding.LayoutSyncFormBinding;
import org.odk.cersgis.basis.listeners.CsvDownloadListener;
import org.odk.cersgis.basis.models.CsvData2;
import org.odk.cersgis.basis.models.Uris;

import io.paperdb.Paper;

public class SyncDataActivity extends CollectAbstractActivity {

    private SyncDataViewModel syncDataViewModel;
    private String phoneNumber;
    //private List<AsyncTask> requestCsvTaskList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_sync_data);
        //Create a view model for this activity and bind to the view.
        phoneNumber = Paper.book().read("phoneNumber");
        ActivitySyncDataBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_sync_data);
        syncDataViewModel = new SyncDataViewModel() {
            @Override
            public void onDownloadSelectedFormsCommand() {
                syncDataViewModel.setDownloadingFormsCount(0);
                syncDataViewModel.setFormsDownloaded(0);
                syncDataViewModel.setFormsDownloadFailed(0);
                syncData();
            }

            @Override
            public void onExitCommand() {
                cancelCsvDownloadQueue();
                SyncDataActivity.this.finish();
            }

            @Override
            public void onRefreshFormsCommand() {
                syncDataViewModel.setBusy(true);
                requestOdkFormListAsync();
            }

            @Override
            public void onCancelFormsDownloadCommand() {
//                cancelCsvDownloadQueue();
//                setSelectAll(false, false);
            }

            @Override
            protected void newForms(SyncFormViewModel[] forms) {
                binding.formViewsLinearLayout.removeAllViews();
                for (SyncFormViewModel formViewModel : forms) {
                    addFormView(binding.formViewsLinearLayout, formViewModel);
                }
            }

        };
        binding.setSyncDataVm(syncDataViewModel);
        //Is busy is set to true.
        //This will cause the progress indicator to be displayed to the user.
        syncDataViewModel.setBusy(true);
        //The forms are requested.
        requestOdkFormListAsync();
    }

    private void requestOdkFormListAsync() {
        ApiRequestAsync request = new ApiRequestAsync(new ApiRequestListener() {
            @Override
            public void processFinish(RequestAsyncTaskResponse requestAsyncTaskResponse, Exception e) {
                processOdkFormListResponse(requestAsyncTaskResponse, e);
            }

            @Override
            public void processFinish(RequestAsyncTaskResponse requestAsyncTaskResponse, Exception e, Object o) {

            }
        }, 15000);
        request.downloadObject(Uris.getOdkFormListUrl());
    }

    private void processOdkFormListResponse(RequestAsyncTaskResponse response, Exception e) {
        syncDataViewModel.setBusy(false);
        if (e != null) {
            showNoInternetConnectionToast();
            return;
        }
        try {
            if (response.getStatusCode() == 200) {
                String[] formNames = response.getResponseObject(String[].class);
                updateViewModels(formNames);
            } else {
                showNoInternetConnectionToast();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            showNoInternetConnectionToast();
        }
    }

    private void updateViewModels(String[] formNames) {
        SyncFormViewModel[] syncFormViewModels = new SyncFormViewModel[formNames.length];
        for (int i = formNames.length - 1; i >= 0; i--) {
            syncFormViewModels[i] = new SyncFormViewModel(formNames[i]){

                @Override
                public void onCheckChanged(boolean checked) {
                    if(!checked)
                        syncDataViewModel.setSelectAll(false,false);
                }

                @Override
                public void onStateChanged(State currentState) {
                    switch (currentState) {
                        case COMPLETE:
                            syncDataViewModel.setFormsDownloaded(syncDataViewModel.getFormsDownloaded() + 1);
                            break;
                        case FAILED:
                            syncDataViewModel.setFormsDownloadFailed(syncDataViewModel.getFormsDownloadFailed() + 1);
                            break;
                        default:
                            break;
                    }
                }
            };
        }
        syncDataViewModel.setForms(syncFormViewModels);
    }

    private void syncData() {
        boolean formSelected = false;
        //requestCsvTaskList = new ArrayList<>();
        for (SyncFormViewModel formViewModel : syncDataViewModel.getForms()) {
            if (formViewModel.isSelected()) {
                formSelected = true;
                formViewModel.setCurrentState(SyncFormViewModel.State.DOWNLOADING);
                syncDataViewModel.setDownloadingFormsCount(syncDataViewModel.getDownloadingFormsCount() + 1);
                requestSelectedFormData(formViewModel);
            }
        }
        if (!formSelected){
            //requestCsvTaskList = null;
            Toast.makeText(this, "Please select a form to sync", Toast.LENGTH_LONG).show();
        }
    }

    private void requestSelectedFormData(SyncFormViewModel syncFormViewModel) {
        ApiRequestAsync request = new ApiRequestAsync(new ApiRequestListener() {
            @Override
            public void processFinish(RequestAsyncTaskResponse requestAsyncTaskResponse, Exception e) {
                processSelectedFormDataResponse(requestAsyncTaskResponse, e, syncFormViewModel);
            }

            @Override
            public void processFinish(RequestAsyncTaskResponse requestAsyncTaskResponse, Exception e, Object o) {

            }
        }, 2 * 60000);
        //requestCsvTaskList.add(request);
        request.downloadObject(Uris.getOdkFormGetCsvUrl() + "/?formname=" + syncFormViewModel.getFormName() + "&contact=" + phoneNumber, false);
    }

    private void processSelectedFormDataResponse(RequestAsyncTaskResponse response, Exception e, SyncFormViewModel formViewModel) {
        if (e != null) {
            formViewModel.setCurrentState(SyncFormViewModel.State.FAILED);
            return;
        }
        try {
            if (response.getStatusCode() == 200) {
                CsvData2 csvData = response.getResponseObject(CsvData2.class);
                System.out.println(csvData);
                String url = csvData.getGenericCsvs()[0].getUrl();
                downloadCsvs(formViewModel, csvData);
            } else {
                formViewModel.setCurrentState(SyncFormViewModel.State.FAILED);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            formViewModel.setCurrentState(SyncFormViewModel.State.FAILED);
        }
    }

    private void downloadCsvs(SyncFormViewModel formViewModel, CsvData2 csvData) {
        RequestCsvTask requestCsvTask = new RequestCsvTask(new CsvDownloadListener() {
            @Override
            public void fileDownloadComplete(int totalFilesDownloaded, int totalFiles) {
                formViewModel.setFilesDownloaded(totalFilesDownloaded);
                formViewModel.setTotalFiles(totalFiles);
            }

            @Override
            public void batchDownloadComplete(boolean isSuccess) {
                if (isSuccess) {
                    formViewModel.setCurrentState(SyncFormViewModel.State.COMPLETE);
                    formViewModel.setSelected(false);
                    formViewModel.setFilesDownloaded(0);
                    syncDataViewModel.setSelectAll(false, false);
                } else {
                    formViewModel.setCurrentState(SyncFormViewModel.State.FAILED);
                }
            }
        }, formViewModel.getFormName(), csvData, phoneNumber);
        requestCsvTask.execute();
        //requestCsvTaskList.add(requestCsvTask);
    }



    private void addFormView(LinearLayout formViewsContainer, SyncFormViewModel formViewModel) {
        View formView = getLayoutInflater().inflate(R.layout.layout_sync_form, null);
        LayoutSyncFormBinding binding = LayoutSyncFormBinding.bind(formView);
        binding.setSyncFormVm(formViewModel);
        formView = binding.getRoot();
        //Set layout properties of this view
        LinearLayout.LayoutParams sv_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        int rl_margin = 0; //getResources().getDimensionPixelSize(0);
        sv_params.setMargins(rl_margin, Functions.dpTopx(0, formView),
                rl_margin, Functions.dpTopx(0, formView));
        formView.setLayoutParams(sv_params);
        formViewsContainer.addView(formView);
    }

    private void cancelCsvDownloadQueue(){
//        if (requestCsvTaskList != null){
//            for (AsyncTask task : requestCsvTaskList) {
//                if(task.getStatus() != AsyncTask.Status.FINISHED){
//                    task.cancel(true);
//                }
//            }
//        }
//        syncDataViewModel.setSelectAll(false, false);
    }

    private void showNoInternetConnectionToast() {
        Toast.makeText(this, "Error downloading forms! Please make sure you are connected to the internet.", Toast.LENGTH_LONG).show();
    }
}
