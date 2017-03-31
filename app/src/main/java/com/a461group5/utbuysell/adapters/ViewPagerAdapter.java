package com.a461group5.utbuysell.adapters;

import android.graphics.pdf.PdfDocument;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;
import com.a461group5.utbuysell.fragments.PageFragment;

import java.util.ArrayList;

/**
 *
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<PageFragment> fragments = new ArrayList<>();
    private PageFragment currentFragment;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);

        fragments.clear();
        fragments.add(PageFragment.newInstance(0));
        fragments.add(PageFragment.newInstance(1));
        fragments.add(PageFragment.newInstance(2));
        fragments.add(PageFragment.newInstance(3));
    }

    @Override
    public PageFragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            currentFragment = ((PageFragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    /**
     * Get the current fragment
     */
    public PageFragment getCurrentFragment() {
        return currentFragment;
    }
}