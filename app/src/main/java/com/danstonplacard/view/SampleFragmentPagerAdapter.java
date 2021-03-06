package com.danstonplacard.view;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.danstonplacard.view.fragment.PageFragment;
import com.danstonplacard.view.fragment.inventaire.RootInventaireFragment;
import com.danstonplacard.view.fragment.ldc.RootLDCFragment;

/**
 * Adapter for TabLayout
 */
public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {

    //final int PAGE_COUNT = 4;
    final int PAGE_COUNT = 2;
    //private String tabTitle[]= new String[] {"Inventaire", "Listes", "Recettes", "Promotions"};
    private String tabTitle[]= new String[] {"Inventaire", "Listes"};

    public SampleFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        FragmentManager mFragmentManager = fm;
        Context mContext = context;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i)
        {
            case 0:
                return RootInventaireFragment.newInstance("PARAM");
            case 1:
                return RootLDCFragment.newInstance("PARAM");
            default :
                return PageFragment.newInstance(i+1);
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    public CharSequence getPageTitle(int i) {
        return null; // This will not print the page title in the tab bar
    }
}
