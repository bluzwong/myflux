package com.github.bluzwang.myflux.example;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import com.github.bluzwang.myflux_kotlin.R;

public class DemoActivity extends Activity  {


    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        viewPager = (ViewPager) findViewById(R.id.vp);

        viewPager.setAdapter(new FragmentPagerAdapter(getFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return new DemoFragment();
            }

            @Override
            public int getCount() {
                return 5;
            }
        });
    }

}
