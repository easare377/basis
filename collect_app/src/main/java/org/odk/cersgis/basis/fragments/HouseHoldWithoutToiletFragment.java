package org.odk.cersgis.basis.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emma.general_backend_library.HttpRequest.ApiRequestAsync;
import com.emma.general_backend_library.HttpRequest.ApiRequestListener;
import com.emma.general_backend_library.HttpRequest.RequestAsyncTaskResponse;

import org.odk.cersgis.basis.R;
import org.odk.cersgis.basis.adapters.HouseholdWithoutToiletAdapter;
import org.odk.cersgis.basis.databinding.FragmentHouseHoldWithoutToiletBinding;
import org.odk.cersgis.basis.fragments.viewmodels.HouseHoldWithoutToiletViewModel;
import org.odk.cersgis.basis.fragments.viewmodels.HouseholdReportViewModel;
import org.odk.cersgis.basis.models.HouseHoldWithoutToilet;
import org.odk.cersgis.basis.models.Uris;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HouseHoldWithoutToiletFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HouseHoldWithoutToiletFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "phoneNumber";
    // TODO: Rename and change types of parameters
    private String phoneNumber;
    private HouseHoldWithoutToiletViewModel houseHoldWithoutToiletViewModel;
    private boolean requestComplete;
    public HouseHoldWithoutToiletFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param phoneNumber Parameter 1.
     * @return A new instance of fragment HouseHoldWithoutToiletFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HouseHoldWithoutToiletFragment newInstance(String phoneNumber) {
        HouseHoldWithoutToiletFragment fragment = new HouseHoldWithoutToiletFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, phoneNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            phoneNumber = getArguments().getString(ARG_PARAM1);
            //requestHouseholdReport();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_house_hold_without_toilet, container, false);
        FragmentHouseHoldWithoutToiletBinding binding = FragmentHouseHoldWithoutToiletBinding.bind(v);
        houseHoldWithoutToiletViewModel = new HouseHoldWithoutToiletViewModel() {
            @Override
            public void onCurrentIndexChanged(int currentIndex) {
                setNewAdapter(getCommunityDataMap(), currentIndex);
            }

            @Override
            public void onSetHouseHoldWithoutToiletsMap(HashMap<String, ArrayList<HouseholdReportViewModel>> communityDataMap) {
                if (getCurrentIndex() != 0)
                    setCurrentIndex(0);
                else
                    setNewAdapter(communityDataMap, getCurrentIndex());
            }


            private void setNewAdapter(HashMap<String, ArrayList<HouseholdReportViewModel>> communityDataMap, int currentIndex) {
                String communityName = communityDataMap.keySet().toArray()[currentIndex].toString();
                ArrayList<HouseholdReportViewModel> houseHoldWithoutToiletList = communityDataMap.get(communityName);
                HouseholdReportViewModel[] householdReportViewModels = new HouseholdReportViewModel[houseHoldWithoutToiletList.size()];
                householdReportViewModels = houseHoldWithoutToiletList.toArray(householdReportViewModels);
                HouseholdWithoutToiletAdapter householdWithoutToiletAdapter =
                        new HouseholdWithoutToiletAdapter(getContext(),R.layout.layout_household_without_toilet,
                                householdReportViewModels);
                binding.householdWithoutToiletListView.setAdapter(householdWithoutToiletAdapter);
            }
        };
        binding.setHhWithoutToiletVm(houseHoldWithoutToiletViewModel);
        houseHoldWithoutToiletViewModel.setBusy(true);
        requestHouseholdReport();
       return v;
    }

    private void requestHouseholdReport() {
        ApiRequestAsync apiRequest = new ApiRequestAsync(new ApiRequestListener() {
            @Override
            public void processFinish(RequestAsyncTaskResponse requestAsyncTaskResponse, Exception e) {
                processResponse(requestAsyncTaskResponse, e);
            }

            @Override
            public void processFinish(RequestAsyncTaskResponse requestAsyncTaskResponse, Exception e, Object o) {

            }
        }, 20000);
        apiRequest.downloadObject(Uris.getHouseHoldReportUrl() + phoneNumber);
    }

    private void processResponse(RequestAsyncTaskResponse response, Exception e) {
        houseHoldWithoutToiletViewModel.setBusy(false);
        //signInViewModel.setBusy(false);
        if (e != null) {
            // showNoInternetErrorToast();
            getActivity().finish();
            return;
        }
        try {
            if (response.getStatusCode() == 200) {
                HouseHoldWithoutToilet[] houseHoldWithoutToilets = response.getResponseObject(HouseHoldWithoutToilet[].class);
                HashMap<String, ArrayList<HouseholdReportViewModel>> householdWithoutToiletMap = new HashMap<>();
                for (HouseHoldWithoutToilet houseHoldWithoutToilet : houseHoldWithoutToilets) {
                    ArrayList<HouseholdReportViewModel> houseHoldWithoutToiletList;
                    if(householdWithoutToiletMap.containsKey(houseHoldWithoutToilet.getCommnunity())){
                        houseHoldWithoutToiletList = householdWithoutToiletMap.get(houseHoldWithoutToilet.getCommnunity());
                    }else{
                        houseHoldWithoutToiletList = new ArrayList<>();
                    }
                    HouseholdReportViewModel householdReportViewModel =
                            new HouseholdReportViewModel(houseHoldWithoutToilet.getStatus(),
                                    houseHoldWithoutToilet.getHousehold_headname());
                    houseHoldWithoutToiletList.add(householdReportViewModel);
                    householdWithoutToiletMap.put(houseHoldWithoutToilet.getCommnunity(), houseHoldWithoutToiletList);
                }
                houseHoldWithoutToiletViewModel.setCommunityDataMap(householdWithoutToiletMap);
            } else {
                // showNoInternetErrorToast();
                getActivity().finish();
            }
        } catch (Exception exception) {
            // showNoInternetErrorToast();
            getActivity().finish();
        }
    }


}