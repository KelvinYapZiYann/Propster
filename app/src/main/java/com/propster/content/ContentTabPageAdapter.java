package com.propster.content;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.propster.utils.Constants;

public class ContentTabPageAdapter extends FragmentStateAdapter {

    private final Activity activity;
    private int role = -1;
    private final boolean firstTime;

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
                return new ContentReportFragment();
            case 2:
                return new ContentManageFragment(this, this.firstTime);
            case 3:
                return new ContentCommunityFragment();
            case 4:
                return new ContentMoreFragment();
            default:
                return new ContentHomeFragment();
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
}
