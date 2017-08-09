package net.suntrans.zhanshi.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import net.suntrans.zhanshi.R;
import net.suntrans.zhanshi.fragment.EnvFragment;
import net.suntrans.zhanshi.fragment.SceneFragment;
import net.suntrans.zhanshi.fragment.SocketFragment;
import net.suntrans.zhanshi.fragment.XenonFragment;
import net.suntrans.zhanshi.fragment.ZhaomingFragment;
import net.suntrans.zhanshi.websocket.WebSocketService;

import static android.support.design.widget.TabLayout.GRAVITY_FILL;
import static android.support.design.widget.TabLayout.MODE_FIXED;
import static android.support.design.widget.TabLayout.MODE_SCROLLABLE;

/**
 * Created by Looney on 2017/7/23.
 */

public class RoomDetailActivity extends BasedActivity {

    public WebSocketService.ibinder binder;
    private TextView title;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (WebSocketService.ibinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    private String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        initView();
    }

    private void initView() {

        id = getIntent().getStringExtra("id");
        title = (TextView) findViewById(R.id.title);
        title.setText(getIntent().getStringExtra("name"));
        Intent intent = new Intent();
        intent.setClass(this, WebSocketService.class);
        this.bindService(intent, connection, Context.BIND_AUTO_CREATE);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabMode(MODE_FIXED);
        tabLayout.setTabGravity(GRAVITY_FILL);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        findViewById(R.id.fanhui).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    class PagerAdapter extends FragmentPagerAdapter {
        private Fragment[] fragments;

        private final String[] title = new String[]{"场景", "照明","插座","氙气灯","环境"};

        public PagerAdapter(FragmentManager fm) {
            super(fm);
            SceneFragment fragment =  SceneFragment.newInstance("1",id);
            ZhaomingFragment fragment2 =  ZhaomingFragment.newInstance("2",id);
            SocketFragment fragment3 =  SocketFragment.newInstance("2",id);
            XenonFragment fragment4 =  XenonFragment.newInstance("2",id);
            EnvFragment fragment5 =  EnvFragment.newInstance("2",id);
            fragments =new Fragment[]{fragment,fragment2,fragment3,fragment4,fragment5};
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return title.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(connection);

        super.onDestroy();
    }
}
