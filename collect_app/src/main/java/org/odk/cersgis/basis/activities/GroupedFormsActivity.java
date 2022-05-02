package org.odk.cersgis.basis.activities;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;

import org.odk.cersgis.basis.R;
import org.odk.cersgis.basis.models.FormChooserData;
import org.odk.cersgis.basis.views.FormChooserListView;

import java.util.ArrayList;


public class GroupedFormsActivity extends CollectAbstractActivity {
    public static ArrayList<FormChooserData> formChooserDataArrayList;
    LinearLayout formGroupLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_grouped_form);
        //setTitle (getString (R.string.enter_data));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        formGroupLinearLayout = findViewById (R.id.form_group_linearlayout);

        for (FormChooserData formChooserData : formChooserDataArrayList) {
            addFormChooseList (formChooserData);
        }
    }

    void addFormChooseList(FormChooserData formChooserData) {
        FormChooserListView formChooserListView = new FormChooserListView (this);
        formChooserListView.setDisplayName (formChooserData.getDisplayName ());
        formChooserListView.setDate (formChooserData.getDisplaySubtext ());
        formChooserListView.setFormChooserData (formChooserData);
        formGroupLinearLayout.addView (formChooserListView);
    }

}
