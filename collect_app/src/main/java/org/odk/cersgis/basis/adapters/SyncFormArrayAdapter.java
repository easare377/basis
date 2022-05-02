package org.odk.cersgis.basis.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.odk.cersgis.basis.activities.viewmodels.SyncFormViewModel;
import org.odk.cersgis.basis.databinding.LayoutSyncFormBinding;

public class SyncFormArrayAdapter extends ArrayAdapter<SyncFormViewModel> {
    private final SyncFormViewModel[] syncFormViewModels;
    private LayoutSyncFormBinding[] syncFormLayoutBindings;

    public SyncFormArrayAdapter(@NonNull Context context, int resource, @NonNull SyncFormViewModel[] syncFormViewModels) {
        super(context, resource, syncFormViewModels);
        this.syncFormViewModels = syncFormViewModels;
        syncFormLayoutBindings = new LayoutSyncFormBinding[syncFormViewModels.length];
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Nullable
    @Override
    public SyncFormViewModel getItem(int position) {
        return super.getItem(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutSyncFormBinding binding = syncFormLayoutBindings[position];
        if (binding == null) {
            LayoutInflater li =
                    (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            binding = LayoutSyncFormBinding.inflate(li, parent, false);
            syncFormLayoutBindings[position] = binding;
            binding.setSyncFormVm(syncFormViewModels[position]);
        }
        View v = binding.getRoot();
        return v;
    }
}
