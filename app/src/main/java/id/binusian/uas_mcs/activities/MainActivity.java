package id.binusian.uas_mcs.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.transition.Fade;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import id.binusian.uas_mcs.OmbdPagerAdapter;
import id.binusian.uas_mcs.R;
import id.binusian.uas_mcs.fragments.SavedFragment;
import id.binusian.uas_mcs.fragments.SearchFragment;

public class MainActivity extends AppCompatActivity {

    OmbdPagerAdapter ombdPagerAdapter;
    ViewPager2 viewPager2;
    TabLayout tabLayout;

    SavedFragment savedFragment;
    SearchFragment searchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fade fade = new Fade();
        fade.excludeTarget(tabLayout, true);

        ombdPagerAdapter = new OmbdPagerAdapter(this);
        viewPager2 = findViewById(R.id.main_pager);
        viewPager2.setAdapter(ombdPagerAdapter);

        tabLayout = findViewById(R.id.main_tab_layout);
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch(position) {
                    case 0:
                        tab.setText("Search Movies");
                        break;
                    case 1:
                        tab.setText("Saved Movies");
                        break;
                }
            }
        }).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) tabLayout.setElevation(2f);
                else tabLayout.setElevation(0);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!ombdPagerAdapter.fragments.isEmpty()){
            savedFragment = (SavedFragment) ombdPagerAdapter.fragments.get(1);
            searchFragment = (SearchFragment) ombdPagerAdapter.fragments.get(0);
        }
    }
}