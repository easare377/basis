package org.odk.cersgis.basis.activities;

import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.odk.cersgis.basis.R;
import org.odk.cersgis.basis.adapters.OdfStatusViewPagerAdapter;
import org.odk.cersgis.basis.fragments.HouseHoldWithoutToiletFragment;
import org.odk.cersgis.basis.fragments.OdfStatusFragment;

import io.paperdb.Paper;

public class HouseHoldActivity extends CollectAbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_hold);
        String phoneNumber = Paper.book().read("phoneNumber");
        ViewPager2 householdViewPager = findViewById(R.id.household_viewPager);
        TabLayout householdTabLayout = findViewById(R.id.household_tab_layout);
        //HouseholdViewModel householdViewModel = new HouseholdViewModel();
        OdfStatusViewPagerAdapter odfStatusViewPagerAdapter = new OdfStatusViewPagerAdapter(this, phoneNumber);
        odfStatusViewPagerAdapter.addFragment(OdfStatusFragment.newInstance(phoneNumber));
        odfStatusViewPagerAdapter.addFragment(HouseHoldWithoutToiletFragment.newInstance(phoneNumber));
        householdViewPager.setAdapter(odfStatusViewPagerAdapter);

        new TabLayoutMediator(householdTabLayout, householdViewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("ODF Status");
                            break;
                        case 1:
                            tab.setText("Household Without Toilet");
                            break;
                    }
                }
        ).attach();
//        new TabLayoutMediator(householdTabLayout, householdViewPager,
//                (tab, position) -> tab.setText("Household Without Toilet")
//        ).attach();
    }
}