package com.example.ringtest;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.pedro.library.AutoPermissions;

import me.relex.circleindicator.CircleIndicator3;

public class UserGuide extends FragmentActivity {
    private static final int NUM_PAGES = 3;
    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;
    private CircleIndicator3 mIndicator;
    private Button finish_btn;

    private boolean is_first;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_guide);

        mContext = this;

        is_first = PreferenceManager.getBoolean(mContext, "is_first");

        finish_btn = findViewById(R.id.btn_finish);

        finish_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                PreferenceManager.setBoolean(mContext, "is_first", false);
                permissionCheck();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Instantiate a ViewPager2 and a PagerAdapter.
        viewPager = findViewById(R.id.viewpager);
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageTransformer(new ZoomOutPageTransformer());

        mIndicator = findViewById(R.id.indicator);
        mIndicator.setViewPager(viewPager);
        mIndicator.createIndicators(NUM_PAGES,0);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    viewPager.setCurrentItem(position);
                }
            }

            // 페이지 선택되어있는 부분 확인
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mIndicator.animatePageSelected(position%NUM_PAGES);
                if(position == NUM_PAGES - 1){
                    finish_btn.setVisibility(View.VISIBLE);
                }
                else{
                    finish_btn.setVisibility(View.INVISIBLE);
                }
            }
        });

        //가이드를 끝냈을때
        if(!is_first){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //권한을 허용 했을 경우
        if(requestCode == 1){
            int length = permissions.length;
            for (int i = 0; i < length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    // 동의
                    Log.d("MainActivity","권한 허용 : " + permissions[i]);
                }
            }
        }
    }


    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {

        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        // Fragment 생성부분
        @Override
        public Fragment createFragment(int position) {
            if(position == 0){

                return new FragmentFirst();
            }
            else if(position == 1){

                return new FragementSecond();
            }
            else{
                return new FragmentThird();
            }
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }

    }

    private void permissionCheck() {
        AutoPermissions.Companion.loadAllPermissions(this,101); // 권한 설정 오픈소스

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE); // 상태 표시줄 알림을 위한 작업
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(new NotificationChannel("default", "기본 채널",
                    NotificationManager.IMPORTANCE_DEFAULT));
        }
    }
}