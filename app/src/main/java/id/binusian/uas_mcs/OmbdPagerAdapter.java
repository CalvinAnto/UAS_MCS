package id.binusian.uas_mcs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

import id.binusian.uas_mcs.fragments.SavedFragment;
import id.binusian.uas_mcs.fragments.SearchFragment;

public class OmbdPagerAdapter extends FragmentStateAdapter {

    public OmbdPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public ArrayList<Fragment> fragments = new ArrayList<>();

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        fragments.add(new SearchFragment());
        fragments.add(new SavedFragment());

        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
