package org.odk.cersgis.basis.views;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.emma.general_backend_library.Functions;

import org.odk.cersgis.basis.R;
import org.odk.cersgis.basis.activities.GroupedFormsActivity;
import org.odk.cersgis.basis.models.FormChooserData;

import java.util.ArrayList;

public class FormGroupListView extends RelativeLayout {

    LayoutInflater mInflater;

    public FormGroupListView(Context context) {
        super (context);
        mInflater = LayoutInflater.from (context);
        init ();
    }

    TextView formGroupTextView;
    TextView formCountTextView;
    private ArrayList<FormChooserData> formChooserDataArrayList;

    private void init() {
        View v = mInflater.inflate (R.layout.form_group_list_view, this, true);
        formGroupTextView = findViewById (R.id.form_group_textview);
        formCountTextView = findViewById (R.id.form_count_textview);
        LinearLayout.LayoutParams sv_params = new LinearLayout.LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        int rl_margin = 0; //getResources().getDimensionPixelSize(0);

        sv_params.setMargins (rl_margin, Functions.dpTopx (0, this), rl_margin, Functions.dpTopx (0, this));
        sv_params.weight = 1.0f;
        //this.setBackgroundResource(R.drawable.rounded_corner);
        this.setLayoutParams (sv_params);
        //this.isClickable() = true;
        this.setClickable (true);
        this.setOnClickListener (v1 -> {
            GroupedFormsActivity.formChooserDataArrayList = formChooserDataArrayList;

            Intent intent = new Intent (getContext (), GroupedFormsActivity.class);

            //intent.putExtra ("id",feedBackData.community_id);
            getContext ().startActivity (intent);

        });


    }

    public void setGroupName(String groupName) {
        formGroupTextView.setText (groupName + " Forms");
    }

    public void setGroupCount(int groupCount) {
        if(groupCount == 1)
            formCountTextView.setText (String.valueOf (groupCount) + " Form");
            else
            formCountTextView.setText (String.valueOf (groupCount) + " Forms");

    }

    public void setFormChooserDataArrayList(ArrayList<FormChooserData> formChooserDataArrayList) {
        this.formChooserDataArrayList = formChooserDataArrayList;
        //Collections.sort (formChooserDataArrayList);
    }
}
