package com.propster.content;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.propster.R;
import com.propster.utils.Constants;

public class ContentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        boolean firstTime;
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            firstTime = false;
        } else {
            firstTime = extras.getBoolean(Constants.INTENT_EXTRA_CONTENT_FIRST_TIME_LOGIN);
        }

        ViewPager2 contentTabPager = findViewById(R.id.contentTabPager);
        ContentTabPageAdapter contentTabPageAdapter = new ContentTabPageAdapter(this, firstTime);
        contentTabPager.setAdapter(contentTabPageAdapter);

        TabLayout tabLayout = findViewById(R.id.contentTabLayout);
        new TabLayoutMediator(tabLayout, contentTabPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText(R.string.dashboard);
                        break;
                    case 1:
                        tab.setText(R.string.report);
                        break;
                    case 2:
                        tab.setText(R.string.manage);
                        break;
                    case 3:
                        tab.setText(R.string.community);
                        break;
                    case 4:
                        tab.setText(R.string.more);
                        break;
                }
            }
        }).attach();

        if (firstTime) {
            contentTabPager.setCurrentItem(2);
        }


    }
}
