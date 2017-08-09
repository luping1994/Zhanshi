package net.suntrans.zhanshi;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import net.suntrans.zhanshi.activity.BasedActivity;
import net.suntrans.zhanshi.activity.RoomDetailActivity;
import net.suntrans.zhanshi.activity.SecondActivity;
import net.suntrans.zhanshi.adapter.MainActivityAdapter;
import net.suntrans.zhanshi.api.RetrofitHelper;
import net.suntrans.zhanshi.bean.RoomEntity;
import net.suntrans.zhanshi.utils.LogUtil;
import net.suntrans.zhanshi.utils.UiUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BasedActivity implements View.OnClickListener {
    private List<RoomEntity.Room> datas;
    TextView time;
    private MainActivityAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogUtil.i("mainActivity:", UiUtils.px2dip(1920) + "");
        init();

    }

    private void init() {
        Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        datas = new ArrayList<>();


        CardView nengyuan = (CardView) findViewById(R.id.nengyuan);
        CardView jiance = (CardView) findViewById(R.id.jiance);
        nengyuan.setOnClickListener(this);
        jiance.setOnClickListener(this);
        time = (TextView) findViewById(R.id.time);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {
//            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
//            time.setVisibility(View.VISIBLE);
//        } else {
//            time.setVisibility(View.INVISIBLE);
//            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
//
//        }
        adapter = new MainActivityAdapter(R.layout.item_main, datas);
        recyclerView.setAdapter(adapter);
        a.start();
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent();
                intent.putExtra("id", datas.get(position).id);
                intent.putExtra("name", datas.get(position).name);
                intent.setClass(MainActivity.this, RoomDetailActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        isRun = false;
        super.onDestroy();

    }

    private boolean isRun = true;

    private Thread a = new Thread() {
        @Override
        public void run() {
            while (isRun) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss EEEE", Locale.CHINESE);
                Date date = new Date();
                String str = sdf.format(date);

                handler.sendMessage(handler.obtainMessage(100, str));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            time.setText((String) msg.obj);
        }
    };

    //    @Override
//    protected void onPause() {
//
//        super.onPause();
//    }
//
    @Override
    protected void onResume() {
        getData();
        super.onResume();
    }

    private long[] mHits = new long[2];
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            new AlertDialog.Builder(this)
                    .setMessage("是否退出应用?")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            android.os.Process.killProcess(android.os.Process.myPid());

                        }
                    }).create().show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void getData() {
        RetrofitHelper.getApi().getHomeRoom()
                .compose(this.<RoomEntity>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RoomEntity>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        UiUtils.showToast("服务器错误");
                    }

                    @Override
                    public void onNext(RoomEntity roomEntity) {
                        if (roomEntity != null) {
                            datas.clear();
                            datas.addAll(roomEntity.data.lists);
                            adapter.notifyDataSetChanged();
                        } else {
                            UiUtils.showToast("服务器错误");

                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.nengyuan) {
            startActivity(new Intent(this, SecondActivity.class));
        }
        if (v.getId() == R.id.jiance) {

        }
    }
}
