package org.odk.cersgis.basis.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emma.general_backend_library.HttpRequest.ApiRequestAsync;
import com.emma.general_backend_library.HttpRequest.ApiRequestListener;
import com.emma.general_backend_library.HttpRequest.RequestAsyncTaskResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.odk.cersgis.basis.R;
import org.odk.cersgis.basis.adapters.OdfStatusDataAdapter;
import org.odk.cersgis.basis.databinding.FragmentOdfStatusBinding;
import org.odk.cersgis.basis.fragments.viewmodels.OdfStatusViewModel;
import org.odk.cersgis.basis.models.Uris;
import org.slf4j.event.KeyValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OdfStatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OdfStatusFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "phoneNumber";
    //private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String phoneNumber;
    //private String mParam2;

    private OdfStatusViewModel odfStatusViewModel;

    public OdfStatusFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param phoneNumber Parameter 1.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OdfStatusFragment newInstance(String phoneNumber) {
        OdfStatusFragment fragment = new OdfStatusFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, phoneNumber);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            phoneNumber = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_odf_status, container, false);
        FragmentOdfStatusBinding binding = FragmentOdfStatusBinding.bind(v);
        odfStatusViewModel = new OdfStatusViewModel() {

            @Override
            public void onSetCommunityDataList(ArrayList<HashMap<String, String>> communityDataList) {
                if (getCurrentIndex() != 0)
                    setCurrentIndex(0);
                else
                    setNewAdapter(communityDataList, getCurrentIndex());
            }

            @Override
            public void onCurrentIndexChanged(int currentIndex) {
                setNewAdapter(getCommunityDataList(), currentIndex);
            }

            private void setNewAdapter(ArrayList<HashMap<String, String>> communityDataList, int currentIndex) {
                KeyValuePair[] odfStatusData = new KeyValuePair[communityDataList.get(currentIndex).size()];
                Set<Map.Entry<String, String>> entries = getCommunityDataList().get(getCurrentIndex()).entrySet();
                int i = 0;
                for (Map.Entry<String, String> entry : entries) {
                    odfStatusData[i] = new KeyValuePair(entry.getKey(), entry.getValue());
                    i++;
                }
                OdfStatusDataAdapter odfStatusDataAdapter =
                        new OdfStatusDataAdapter(OdfStatusFragment.this.getContext(),
                                R.layout.layout_odf_status_item, odfStatusData);
                binding.odfStatusListView.setAdapter(odfStatusDataAdapter);
            }
        };
        binding.setOdfStatusVm(odfStatusViewModel);
        odfStatusViewModel.setBusy(true);
        requestOdfStatus();
        // Inflate the layout for this fragment
        return v;
    }

    private void requestOdfStatus() {
        ApiRequestAsync apiRequest = new ApiRequestAsync(new ApiRequestListener() {
            @Override
            public void processFinish(RequestAsyncTaskResponse requestAsyncTaskResponse, Exception e) {
                processOdfStatusResponse(requestAsyncTaskResponse, e);
            }

            @Override
            public void processFinish(RequestAsyncTaskResponse requestAsyncTaskResponse, Exception e, Object o) {

            }
        }, 20000);
        apiRequest.downloadObject(Uris.getOdfStatusUrl() + phoneNumber);
    }

    private void processOdfStatusResponse(RequestAsyncTaskResponse response, Exception e) {
        odfStatusViewModel.setBusy(false);
        //signInViewModel.setBusy(false);
        if (e != null) {
            // showNoInternetErrorToast();
            getActivity().finish();
            return;
        }
        try {
            if (response.getStatusCode() == 200) {
                JsonArray odfStatusResponse = response.getResponseObject(JsonArray.class);
                ArrayList<HashMap<String, String>> communityDataList = new ArrayList<>();
                for (JsonElement jsonElement : odfStatusResponse) {
                    HashMap<String, String> odfStatusMap = new HashMap<>();
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    Set<Map.Entry<String, JsonElement>> entries = jsonObject.entrySet();//will return members of your object
                    for (Map.Entry<String, JsonElement> entry : entries) {
                        String key = entry.getKey();
                        String value = entry.getValue().getAsString();
                        odfStatusMap.put(key, value);
                        odfStatusMap.toString();
                    }
                    communityDataList.add(odfStatusMap);
                }
                setupViewModel(communityDataList);
            } else {
                // showNoInternetErrorToast();
                getActivity().finish();
            }
        } catch (Exception exception) {
            // showNoInternetErrorToast();
            getActivity().finish();
        }
    }

    private void setupViewModel(ArrayList<HashMap<String, String>> communityDataList) {
        odfStatusViewModel.setCommunityDataList(communityDataList);
    }
}