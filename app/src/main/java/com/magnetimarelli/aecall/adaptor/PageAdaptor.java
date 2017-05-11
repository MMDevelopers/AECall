package com.magnetimarelli.aecall.adaptor;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.magnetimarelli.aecall.activity.Dialler;
import com.magnetimarelli.aecall.fragment.Contacts;
import com.magnetimarelli.aecall.fragment.Dialer;
import com.magnetimarelli.aecall.fragment.RecentCall;

/**
 * Created by F49558B on 5/8/2017.
 */

public class PageAdaptor extends FragmentStatePagerAdapter {

    int mNumOfTabs;
    public Dialer tab1 = null;
    public RecentCall tab2 = null;
    public Contacts tab3 = null;

    public PageAdaptor(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                tab1 = new Dialer();
                return tab1;
            case 1:
                tab2 = new RecentCall();
                return tab2;
            case 2:
                tab3 = new Contacts();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
