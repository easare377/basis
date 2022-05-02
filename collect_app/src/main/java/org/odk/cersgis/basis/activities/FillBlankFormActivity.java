/*
 * Copyright (C) 2009 University of Washington
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

package org.odk.cersgis.basis.activities;

import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import org.odk.cersgis.basis.R;
import org.odk.cersgis.basis.adapters.FormListAdapter;
import org.odk.cersgis.basis.dao.FormsDao;
import org.odk.cersgis.basis.formmanagement.BlankFormListMenuDelegate;
import org.odk.cersgis.basis.formmanagement.BlankFormsListViewModel;
import org.odk.cersgis.basis.injection.DaggerUtils;
import org.odk.cersgis.basis.listeners.DiskSyncListener;
import org.odk.cersgis.basis.listeners.PermissionListener;
import org.odk.cersgis.basis.models.FormChooserData;
import org.odk.cersgis.basis.views.FormGroupListView;
import org.odk.cersgis.basis.network.NetworkStateProvider;
import org.odk.cersgis.basis.preferences.GeneralKeys;
import org.odk.cersgis.basis.preferences.GeneralSharedPreferences;
import org.odk.cersgis.basis.preferences.ServerAuthDialogFragment;
import org.odk.cersgis.basis.provider.FormsProviderAPI.FormsColumns;
import org.odk.cersgis.basis.storage.StorageInitializer;
import org.odk.cersgis.basis.tasks.DiskSyncTask;
import org.odk.cersgis.basis.utilities.ApplicationConstants;
import org.odk.cersgis.basis.utilities.DialogUtils;
import org.odk.cersgis.basis.utilities.MultiClickGuard;
import org.odk.cersgis.basis.utilities.PermissionUtils;
import org.odk.cersgis.basis.views.ObviousProgressBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.inject.Inject;

import timber.log.Timber;

import static org.odk.cersgis.basis.utilities.PermissionUtils.finishAllActivities;

/**
 * Responsible for displaying all the valid forms in the forms directory. Stores the path to
 * selected form for use by {@link MainMenuActivity}.
 *
 * @author Yaw Anokwa (yanokwa@gmail.com)
 * @author Carl Hartung (carlhartung@gmail.com)
 */
public class FillBlankFormActivity extends FormListActivity implements
        DiskSyncListener, AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String FORM_CHOOSER_LIST_SORTING_ORDER = "formChooserListSortingOrder";
    private static final boolean EXIT = true;

    private DiskSyncTask diskSyncTask;
    private ScrollView scrollView;
    private LinearLayout formGroupLinearlayout;
    @Inject
    NetworkStateProvider networkStateProvider;

    @Inject
    BlankFormsListViewModel.Factory blankFormsListViewModelFactory;

    BlankFormListMenuDelegate menuDelegate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_chooser_list);
        DaggerUtils.getComponent(this).inject(this);

        setTitle(getString(R.string.enter_data));
        scrollView = findViewById (R.id.scrollView_id);
        formGroupListViews = new ArrayList<> ();
        listView.setVisibility (View.GONE);
        scrollView.setVisibility (View.VISIBLE);

        formGroupLinearlayout = findViewById (R.id.form_group_linearlayout);
        BlankFormsListViewModel blankFormsListViewModel = new ViewModelProvider(this, blankFormsListViewModelFactory).get(BlankFormsListViewModel.class);
        blankFormsListViewModel.isSyncing().observe(this, syncing -> {
            ObviousProgressBar progressBar = findViewById(R.id.progressBar);

            if (syncing) {
                progressBar.show();
            } else {
                progressBar.hide(View.GONE);
            }
        });

        blankFormsListViewModel.isAuthenticationRequired().observe(this, authenticationRequired -> {
            if (authenticationRequired) {
                DialogUtils.showIfNotShowing(ServerAuthDialogFragment.class, getSupportFragmentManager());
            } else {
                DialogUtils.dismissDialog(ServerAuthDialogFragment.class, getSupportFragmentManager());
            }
        });

        menuDelegate = new BlankFormListMenuDelegate(this, blankFormsListViewModel, networkStateProvider);

        new PermissionUtils(R.style.Theme_Collect_Dialog_PermissionAlert).requestStoragePermissions(this, new PermissionListener() {
            @Override
            public void granted() {
                // must be at the beginning of any activity that can be called from an external intent
                try {
                    new StorageInitializer().createOdkDirsOnStorage();
                    init();
                } catch (RuntimeException e) {
                    createErrorDialog(e.getMessage(), EXIT);
                    return;
                }
            }

            @Override
            public void denied() {
                // The activity has to finish because ODK Collect cannot function without these permissions.
                finishAllActivities(FillBlankFormActivity.this);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menuDelegate.onCreateOptionsMenu(getMenuInflater(), menu);
        return result;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean result = super.onPrepareOptionsMenu(menu);
        menuDelegate.onPrepareOptionsMenu(menu);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!MultiClickGuard.allowClick(getClass().getName())) {
            return true;
        }

        if (super.onOptionsItemSelected(item)) {
            return true;
        } else {
            return menuDelegate.onOptionsItemSelected(item);
        }
    }

    private void init() {
        setupAdapter();

        // DiskSyncTask checks the disk for any forms not already in the content provider
        // that is, put here by dragging and dropping onto the SDCard
        diskSyncTask = (DiskSyncTask) getLastCustomNonConfigurationInstance();
        if (diskSyncTask == null) {
            Timber.i("Starting new disk sync task");
            diskSyncTask = new DiskSyncTask();
            diskSyncTask.setDiskSyncListener(this);
            diskSyncTask.execute((Void[]) null);
        }
        sortingOptions = new int[]{
                R.string.sort_by_name_asc, R.string.sort_by_name_desc,
                R.string.sort_by_date_asc, R.string.sort_by_date_desc,
        };

        setupAdapter();
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        // pass the thread on restart
        return diskSyncTask;
    }

    /**
     * Stores the path of selected form and finishes.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (MultiClickGuard.allowClick(getClass().getName())) {
            // get uri to form
            long idFormsTable = listView.getAdapter().getItemId(position);
            Uri formUri = ContentUris.withAppendedId(FormsColumns.CONTENT_URI, idFormsTable);

            String action = getIntent().getAction();
            if (Intent.ACTION_PICK.equals(action)) {
                // caller is waiting on a picked form
                setResult(RESULT_OK, new Intent().setData(formUri));
            } else {
                // caller wants to view/edit a form, so launch formentryactivity
                Intent intent = new Intent(Intent.ACTION_EDIT, formUri);
                intent.putExtra(ApplicationConstants.BundleKeys.FORM_MODE, ApplicationConstants.FormModes.EDIT_SAVED);
                startActivity(intent);
            }

            finish();
        }
    }

    public void onMapButtonClick(AdapterView<?> parent, View view, int position, long id) {
        final Uri formUri = ContentUris.withAppendedId(FormsColumns.CONTENT_URI, id);
        final Intent intent = new Intent(Intent.ACTION_EDIT, formUri, this, FormMapActivity.class);
        new PermissionUtils(R.style.Theme_Collect_Dialog_PermissionAlert).requestLocationPermissions(this, new PermissionListener() {
            @Override
            public void granted() {
                startActivity(intent);
            }

            @Override
            public void denied() {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (diskSyncTask != null) {
            diskSyncTask.setDiskSyncListener(this);
            if (diskSyncTask.getStatus() == AsyncTask.Status.FINISHED) {
                syncComplete(diskSyncTask.getStatusMessage());
            }
        }
    }

    @Override
    protected void onPause() {
        if (diskSyncTask != null) {
            diskSyncTask.setDiskSyncListener(null);
        }
        super.onPause();
    }

    /**
     * Called by DiskSyncTask when the task is finished
     */
    @Override
    public void syncComplete(@NonNull String result) {
        Timber.i("Disk scan complete");
        hideProgressBarAndAllow();
        showSnackbar(result);
    }

    private void setupAdapter() {
        String[] columnNames = {
                FormsColumns.DISPLAY_NAME,
                FormsColumns.JR_VERSION,
                hideOldFormVersions() ? FormsColumns.MAX_DATE : FormsColumns.DATE,
                FormsColumns.GEOMETRY_XPATH
        };
        int[] viewIds = {
                R.id.form_title,
                R.id.form_subtitle,
                R.id.form_subtitle2,
                R.id.map_view
        };

        listAdapter = new FormListAdapter(
                listView, FormsColumns.JR_VERSION, this, R.layout.form_chooser_list_item,
                this::onMapButtonClick, columnNames, viewIds);
        listView.setAdapter(listAdapter);
    }

    @Override
    protected String getSortingOrderKey() {
        return FORM_CHOOSER_LIST_SORTING_ORDER;
    }

    @Override
    protected void updateAdapter() {
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    /**
     * Creates a dialog with the given message. Will exit the activity when the user preses "ok" if
     * shouldExit is set to true.
     */
    private void createErrorDialog(String errorMsg, final boolean shouldExit) {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setIcon(android.R.drawable.ic_dialog_info);
        alertDialog.setMessage(errorMsg);
        DialogInterface.OnClickListener errorListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                switch (i) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if (shouldExit) {
                            finish();
                        }
                        break;
                }
            }
        };
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok), errorListener);
        alertDialog.show();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        showProgressBar();

        return new FormsDao().getFormsCursorLoader(getFilterText(), getSortingOrder(), hideOldFormVersions());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        hideProgressBarIfAllowed();
        //get formchooser data from cursor
        ArrayList<FormChooserData> rows = new ArrayList<> ();
        if (cursor.moveToFirst ()) {
            do {
                FormChooserData formChooserData = new FormChooserData ();
                formChooserData.setJrFormId (cursor.getString (cursor.getColumnIndex ("jrFormId")));
                // formChooserData.setLastDetectedFormVersionHash (cursor.getString (cursor.getColumnIndex ("lastDetectedFormVersionHash")));
                formChooserData.setDate (cursor.getString (cursor.getColumnIndex ("MAX(date)")));
                formChooserData.setDisplayName (cursor.getString (cursor.getColumnIndex ("displayName")));
                formChooserData.setBase64RsaPublicKey (cursor.getString (cursor.getColumnIndex ("base64RsaPublicKey")));
                formChooserData.setAutoSubmit (cursor.getString (cursor.getColumnIndex ("autoSubmit")));
                formChooserData.setJrcacheFilePath (cursor.getString (cursor.getColumnIndex ("jrcacheFilePath")));
                formChooserData.setDescription (cursor.getString (cursor.getColumnIndex ("description")));
                formChooserData.setJrVersion (cursor.getString (cursor.getColumnIndex ("jrVersion")));
                formChooserData.setMd5Hash (cursor.getString (cursor.getColumnIndex ("md5Hash")));
                formChooserData.setFormMediaPath (cursor.getString (cursor.getColumnIndex ("formMediaPath")));
                formChooserData.setSubmissionUri (cursor.getString (cursor.getColumnIndex ("submissionUri")));
                formChooserData.setLanguage (cursor.getString (cursor.getColumnIndex ("language")));
                formChooserData.set_id (cursor.getString (cursor.getColumnIndex ("_id")));
                formChooserData.setFormFilePath (cursor.getString (cursor.getColumnIndex ("formFilePath")));
                //formChooserData.setDisplaySubtext (cursor.getString (cursor.getColumnIndex ("displaySubtext")));
                formChooserData.setAutoDelete (cursor.getString (cursor.getColumnIndex ("autoDelete")));
                rows.add (formChooserData);
            } while (cursor.moveToNext ());
        }
        displayGroupedForms(rows);
        //listAdapter.swapCursor(cursor);
    }

    Hashtable<String, ArrayList<FormChooserData>> groupedHashtable;


    //group forms
    void displayGroupedForms(ArrayList<FormChooserData> rows) {
        //group forms using key after '='
        groupedHashtable = new Hashtable<> ();
        ArrayList<FormChooserData> nullList = new ArrayList ();
        //}
        for (FormChooserData formChooserData : rows) {
            String displayName = formChooserData.getJrFormId ().trim ();//.toUpperCase ();
            String groupName ;
            if (displayName.contains ("=")){
                groupName =  displayName.split ("=")[1].trim ();
            }else{
                groupName =  "Null"; //.split ("=")[1];
            }

            if (!groupedHashtable.containsKey (groupName)) {
                ArrayList<FormChooserData> groupData = new ArrayList<> ();
                groupData.add (formChooserData);
                groupedHashtable.put (groupName, groupData);
            } else if (groupName.equalsIgnoreCase ("Null")) {
                nullList.add (formChooserData);
            } else {
                ArrayList<FormChooserData> groupData = groupedHashtable.get (groupName);
                groupData.add (formChooserData);
            }
        }
        Enumeration e = groupedHashtable.keys ();
        ArrayList<String> keys = new ArrayList<> ();

        //iterate through Hashtable keys Enumeration
        while (e.hasMoreElements ()) {
            String key = (String) e.nextElement ();
            keys.add (key);
        }

        for (int i = 0; i < keys.size (); i++) {
            ArrayList<FormChooserData> groupList = groupedHashtable.get (keys.get (i));
            if (groupList.size () == 1) {
                if (groupedHashtable.containsKey ("Other")) {
                    groupedHashtable.get ("Other").add (groupList.get (0));
                } else {
                    groupedHashtable.put ("Other", groupList);
                    //groupedHashtable.get ("Others").add (groupList);
                }
                groupedHashtable.remove (keys.get (i));
            }
        }

        if(nullList.size () > 0){
            if (groupedHashtable.containsKey ("Other"))
                groupedHashtable.get ("Other").addAll (nullList);
            else
                groupedHashtable.put ("Other", nullList);
        }


        Collections.sort (keys);
        keys.add ("Other");

        for (String key : keys) {
            createGroupingViews (key);
        }
    }


    ArrayList<FormGroupListView> formGroupListViews;
    ArrayList<String> groupNamesList;


    //Create grouped forms view into linear layout
    void createGroupingViews(String groupName) {
        if (groupNamesList == null) {
            groupNamesList = new ArrayList<> ();
        }
        if (!groupNamesList.contains (groupName)) {
            FormGroupListView formGroupListView = new FormGroupListView (this);
            if (groupedHashtable.get (groupName) != null) {
                ArrayList<FormChooserData> groupedForms = groupedHashtable.get (groupName);
                int c = groupedForms.size ();
                formGroupListView.setGroupName (groupName);
                formGroupListView.setGroupCount (c);
                //Sort forms according to their display name
                Collections.sort (groupedForms, (o1, o2) -> o1.getDisplayName ().compareTo (o2.getDisplayName ()));
                formGroupListView.setFormChooserDataArrayList (groupedForms);
                formGroupLinearlayout.addView (formGroupListView);
                groupNamesList.add (groupName);

            }
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {
        listAdapter.swapCursor(null);
    }

    private boolean hideOldFormVersions() {
        return GeneralSharedPreferences.getInstance().getBoolean(GeneralKeys.KEY_HIDE_OLD_FORM_VERSIONS, false);
    }
}
