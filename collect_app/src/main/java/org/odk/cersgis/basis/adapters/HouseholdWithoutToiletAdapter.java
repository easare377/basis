package org.odk.cersgis.basis.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.odk.cersgis.basis.databinding.LayoutHouseholdWithoutToiletBinding;
import org.odk.cersgis.basis.fragments.viewmodels.HouseholdReportViewModel;

public class HouseholdWithoutToiletAdapter extends ArrayAdapter<HouseholdReportViewModel> {

    private HouseholdReportViewModel[] objects;
    private LayoutHouseholdWithoutToiletBinding[] bindings;
    public HouseholdWithoutToiletAdapter(@NonNull Context context, int resource,
                                         @NonNull HouseholdReportViewModel[] objects) {
        super(context, resource, objects);
        this.objects = objects ;
        bindings = new LayoutHouseholdWithoutToiletBinding[objects.length];
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutHouseholdWithoutToiletBinding binding = bindings[position];
        if (binding == null) {
            LayoutInflater li =
                    (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            binding = LayoutHouseholdWithoutToiletBinding.inflate(li, parent, false);
            bindings[position] = binding;
            binding.setHousehold(objects[position]);
        }
        return binding.getRoot();
    }
}
