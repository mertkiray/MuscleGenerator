package com.musclegenerator.musclegenerator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Mert on 27.05.2017.
 */

public class InboxPager extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public InboxPager(FragmentManager fm, int NumOfTabs){
        super(fm);
        this.mNumOfTabs = NumOfTabs;

    }


    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                InboxFragment inbox = new InboxFragment();
                return inbox;
            case 1:
                FriendRequestFragment friendRequestFragment = new FriendRequestFragment();
                return friendRequestFragment;
            case 2:
                AddFriendFragment addFriendFragment = new AddFriendFragment();
                return addFriendFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
