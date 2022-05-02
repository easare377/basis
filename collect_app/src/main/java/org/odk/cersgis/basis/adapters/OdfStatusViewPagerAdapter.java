package org.odk.cersgis.basis.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class OdfStatusViewPagerAdapter extends FragmentStateAdapter {

    private List<Fragment> fragmentList = new ArrayList<>();
//    private List<String> fragmentTitles = new ArrayList<>();
    private String phoneNumber;
    private int fragmentCounter;

    public OdfStatusViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, String phoneNumber) {
        super(fragmentActivity);
        this.phoneNumber = phoneNumber;
    }


//    public OdfStatusViewPagerAdapter(@NonNull FragmentManager fm) {
//        super(fm);
//    }

//    @NonNull
//    @Override
//    public Fragment getItem(int position) {
//        return fragmentList.get(position);
//    }
//
//    @Override
//    public int getCount() {
//        return fragmentList.size();
//    }
//
//    @Nullable
//    @Override
//    public CharSequence getPageTitle(int position) {
//        return fragmentTitles.get(position);
//    }

    public void addFragment(Fragment fragment) {
        fragmentList.add(fragment);
        //fragmentTitles.add(title);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // fragmentCounter++;
        return fragmentList.get(position);
        //return OdfStatusFragment.newInstance(phoneNumber);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}
