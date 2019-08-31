package com.KidsCampus.user.kinder.NormalBoard;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.KidsCampus.user.kinder.Fragment.item_image_fragment;
import com.KidsCampus.user.kinder.R;

import java.util.ArrayList;

public class ShowImageActivity extends AppCompatActivity {

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ArrayList<String> string_list = getIntent().getStringArrayListExtra("uris");

        viewPager = findViewById(R.id.showimage_viewpager);
        viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager(), string_list));
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = new item_image_fragment();
        fragment.onDestroy();
        super.onBackPressed();
    }

    private class MyViewPagerAdapter extends FragmentPagerAdapter {
        ArrayList<String> data;
        public MyViewPagerAdapter(FragmentManager fm, ArrayList<String> data_list) {
            super(fm);
            this.data = data_list;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Fragment getItem(int i) {
            return item_image_fragment.newInstance(data.get(i));
        }
    }
}
