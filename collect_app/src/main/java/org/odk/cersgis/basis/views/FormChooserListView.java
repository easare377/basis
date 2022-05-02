package org.odk.cersgis.basis.views;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.emma.general_backend_library.Functions;

import org.odk.cersgis.basis.R;
import org.odk.cersgis.basis.models.FormChooserData;
import org.odk.cersgis.basis.provider.FormsProviderAPI;
import org.odk.cersgis.basis.utilities.ApplicationConstants;

public class FormChooserListView extends RelativeLayout {

    LayoutInflater mInflater;

    public FormChooserListView(Context context) {
        super (context);
        mInflater = LayoutInflater.from (context);
        init ();
    }

    TextView versionTextView;
    TextView formDescriptionTextView;
    TextView dateTextview;
    FormChooserData formChooserData;

    private void init() {
        View v = mInflater.inflate (R.layout.form_chooser_list_view, this, true);
        formDescriptionTextView = findViewById (R.id.form_description_textview);
        dateTextview = findViewById (R.id.date_textview);
        versionTextView = findViewById (R.id.version_textview);
        LinearLayout.LayoutParams sv_params = new LinearLayout.LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT,
                Functions.dpTopx (70, this));
        int rl_margin = 0; //getResources().getDimensionPixelSize(0);

        sv_params.setMargins (rl_margin, Functions.dpTopx (1, this), rl_margin, Functions.dpTopx (0, this));
        sv_params.weight = 1.0f;
        //this.setBackgroundResource(R.drawable.rounded_corner);
        this.setLayoutParams (sv_params);
        //this.isClickable() = true;
        this.setClickable (true);
        this.setOnClickListener (v1 -> {
            Uri formUri = ContentUris.withAppendedId (FormsProviderAPI.FormsColumns.CONTENT_URI, Long.parseLong (formChooserData.get_id ()));
            Intent intent = new Intent (Intent.ACTION_EDIT, formUri);
            intent.putExtra (ApplicationConstants.BundleKeys.FORM_MODE, ApplicationConstants.FormModes.EDIT_SAVED);
            getContext ().startActivity (intent);


        });


    }

    public void setDisplayName(String displayName) {
        formDescriptionTextView.setText (displayName);
    }

    public void setDate(String value) {
        dateTextview.setText (value);
    }

    public void setFormChooserData(FormChooserData formChooserData) {
        this.formChooserData = formChooserData;
        if (formChooserData.getJrVersion () != null)
            versionTextView.setText ("Version: " + formChooserData.getJrVersion ());

    }
}
