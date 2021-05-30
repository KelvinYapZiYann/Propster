package com.propster.content;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.propster.utils.Constants;

public class ContentTabPageAdapter extends FragmentStateAdapter {

    private final FragmentActivity activity;
    private int role = -1;
    private final boolean firstTime;

    private ContentHomeFragment contentHomeFragment;
    private ContentReportFragment contentReportFragment;
    private ContentManageFragment contentManageFragment;
    private ContentCommunityFragment contentCommunityFragment;
    private ContentMoreFragment contentMoreFragment;

    public ContentTabPageAdapter(@NonNull FragmentActivity fragmentActivity, boolean firstTime) {
        super(fragmentActivity);
        this.activity = fragmentActivity;
        this.firstTime = firstTime;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return this.contentReportFragment = new ContentReportFragment();
            case 2:
                return this.contentManageFragment = new ContentManageFragment(this, this.firstTime);
            case 3:
                return this.contentCommunityFragment = new ContentCommunityFragment();
            case 4:
                return this.contentMoreFragment = new ContentMoreFragment(this.activity, this);
            default:
                return this.contentHomeFragment = new ContentHomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public int getRole() {
        if (this.role == -1) {
            SharedPreferences sharedPreferences = this.activity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            this.role = sharedPreferences.getInt(Constants.SHARED_PREFERENCES_ROLE, Constants.ROLE_TENANT);
        }
        return this.role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public void reloadContentFragments() {
//        FragmentTransaction fragmentTransaction = this.activity.getSupportFragmentManager().beginTransaction();
        if (this.contentHomeFragment != null) {
            this.contentHomeFragment.reloadFragment();
//            fragmentTransaction.detach(this.contentHomeFragment);
//            fragmentTransaction.attach(this.contentHomeFragment = new ContentHomeFragment());
        }
        if (this.contentReportFragment != null) {
            this.contentReportFragment.reloadFragment();
//            fragmentTransaction.detach(this.contentReportFragment);
//            fragmentTransaction.attach(this.contentReportFragment  = new ContentReportFragment());
        }
        if (this.contentManageFragment != null) {
            this.contentManageFragment.reloadFragment();
//            fragmentTransaction.detach(this.contentManageFragment);
//            fragmentTransaction.attach(this.contentManageFragment = new ContentManageFragment(this, this.firstTime));
        }
        if (this.contentCommunityFragment != null) {
            this.contentCommunityFragment.reloadFragment();
//            fragmentTransaction.detach(this.contentCommunityFragment);
//            fragmentTransaction.attach(this.contentCommunityFragment = new ContentCommunityFragment());
        }
        if (this.contentMoreFragment != null) {
            this.contentMoreFragment.reloadFragment();
//            fragmentTransaction.detach(this.contentMoreFragment);
//            fragmentTransaction.attach(this.contentMoreFragment = new ContentMoreFragment());
        }
//        fragmentTransaction.commit();
    }
}
