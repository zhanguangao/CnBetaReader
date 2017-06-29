package org.zreo.cnbetareader.Adapters;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import java.util.List;

/**
 * Created by Admin on 2015/7/30.
 */
public class Information_theme_Adapter extends FragmentPagerAdapter {
    private List<Fragment>fragments;
    private List<View>views;
    public Information_theme_Adapter(FragmentManager fm,List<Fragment>fragments) {
        super(fm);
        this.fragments=fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
