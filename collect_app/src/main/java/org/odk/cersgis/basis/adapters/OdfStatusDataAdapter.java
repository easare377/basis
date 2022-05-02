package org.odk.cersgis.basis.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.odk.cersgis.basis.databinding.LayoutOdfStatusItemBinding;
import org.slf4j.event.KeyValuePair;

public class OdfStatusDataAdapter extends ArrayAdapter<KeyValuePair> {

    private KeyValuePair[] odfStatusData;
    private LayoutOdfStatusItemBinding[] bindings;
    public OdfStatusDataAdapter(@NonNull Context context, int resource, @NonNull KeyValuePair[] odfStatusData) {
        super(context, resource, odfStatusData);
        this.odfStatusData = odfStatusData;
        bindings = new LayoutOdfStatusItemBinding[odfStatusData.length];
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Nullable
    @Override
    public KeyValuePair getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutOdfStatusItemBinding binding = bindings[position];
        if (binding == null) {
            LayoutInflater li =
                    (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            binding = LayoutOdfStatusItemBinding.inflate(li, parent, false);
            bindings[position] = binding;
            binding.setItemKvp(odfStatusData[position]);
            binding.setItemIndex(position);
        }
        View v = binding.getRoot();
        return v;
    }
}
