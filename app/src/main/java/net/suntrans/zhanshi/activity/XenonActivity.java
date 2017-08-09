package net.suntrans.zhanshi.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;


import net.suntrans.zhanshi.R;
import net.suntrans.zhanshi.bean.CmdMsg;
import net.suntrans.zhanshi.bean.CmdMsg1;
import net.suntrans.zhanshi.bean.Config;
import net.suntrans.zhanshi.utils.LogUtil;
import net.suntrans.zhanshi.utils.ParseCMD;
import net.suntrans.zhanshi.utils.RxBus;
import net.suntrans.zhanshi.utils.UiUtils;
import net.suntrans.zhanshi.views.GradeBar;
import net.suntrans.zhanshi.views.LoadingDialog;
import net.suntrans.zhanshi.views.ScrollChildSwipeRefreshLayout;
import net.suntrans.zhanshi.views.SwitchButton;
import net.suntrans.zhanshi.websocket.WebSocketService;
import net.suntrans.zhanshi.websocket.WebSocketService2;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class XenonActivity extends RxAppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private WebSocketService2.ibinder binder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (WebSocketService2.ibinder) service;
//            System.out.println("绑定服务成功!");
           handler.postDelayed(new Runnable() {
               @Override
               public void run() {
                   getState();
               }
           },500);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    private WebSocketService.ibinder binder2;
    private ServiceConnection connection2 = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder2 = (WebSocketService.ibinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    private SwitchButton xianqideng;
    private SwitchButton dianyuan;
    private String xenonAddr;
    private String addr;
    private String number;
    private String status;
    private LoadingDialog dialog;
    private String channel_id;
    private Subscription subscribe;
    private Subscription subscribe1;
    private GradeBar gradeBar;
    private TextView grade;
    private String[] grades;
    private ScrollChildSwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xenon);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getStringExtra("name"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        xianqideng = (SwitchButton) findViewById(R.id.xianqideng);
//        dianyuan = (SwitchButton) findViewById(R.id.dianyuan);
        xianqideng.setOnCheckedChangeListener(this);
//        dianyuan.setOnCheckedChangeListener(this);

//        findViewById(R.id.llDianyuan).setOnClickListener(this);
//        findViewById(R.id.llXianqideng).setOnClickListener(this);
        refreshLayout = (ScrollChildSwipeRefreshLayout) findViewById(R.id.refreshlayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getState();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                },500);
            }
        });
        xenonAddr = getIntent().getStringExtra("xenonAddr");
        addr = getIntent().getStringExtra("addr");
        number = getIntent().getStringExtra("number");
        status = getIntent().getStringExtra("status");
        channel_id = getIntent().getStringExtra("channel_id");
//
//        if (status.equals("1")) {
//            dianyuan.setChecked(true);
//        } else {
//            dianyuan.setChecked(false);
//        }

        subscribe = RxBus.getInstance().toObserverable(CmdMsg1.class)
                .compose(this.<CmdMsg1>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CmdMsg1>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(CmdMsg1 cmdMsg) {
                        if (cmdMsg.status == 1) {
                            parseMsg1(cmdMsg.msg);
                        }
                    }
                });

        subscribe1 = RxBus.getInstance().toObserverable(CmdMsg.class)
                .compose(this.<CmdMsg>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CmdMsg>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(CmdMsg cmdMsg) {
                        if (cmdMsg.status == 1) {
//                            parseMsg(cmdMsg.msg);

                        }
                    }
                });

        Intent intent = new Intent();
        intent.setClass(this, WebSocketService2.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);


        Intent intent1 = new Intent();
        intent1.setClass(this, WebSocketService.class);
        bindService(intent1, connection2, Context.BIND_AUTO_CREATE);
//
        gradeBar = (GradeBar) findViewById(R.id.bar);
        grade = (TextView) findViewById(R.id.grade);
        grades = getResources().getStringArray(R.array.textarray);
        gradeBar.setOnGradeChangedListener(new GradeBar.OnGradeChangedListener() {
            @Override
            public void onGradeChanged(int index) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("action", "adjust");
//                    jsonObject.put("user_id", "123");
                    jsonObject.put("addr", xenonAddr);
                    jsonObject.put("level", index);
                    binder.sendOrder(jsonObject.toString());
                    showWaitDialog();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dissMissDialog();
                        }
                    }, 2000);
                } catch (Exception e) {
                    UiUtils.showToast("错误");
                    e.printStackTrace();
                }
//                System.out.println("调光等级为:" + grades[index]);
                grade.setText(grades[index]);
            }
        });
    }

    private void showWaitDialog() {
        if (dialog == null) {
            dialog = new LoadingDialog(XenonActivity.this, R.style.loading_dialog);
            dialog.setCancelable(false);
            dialog.setWaitText("请稍后...");
        }
        dialog.show();
    }

    private void dissMissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
        UiUtils.showToast("设备不在线");
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getState() {
        final JSONObject jsonObject = new JSONObject();
        final JSONObject jsonObject2 = new JSONObject();
        try {
            jsonObject.put("action", "rlevel");
            jsonObject.put("addr", xenonAddr);
            jsonObject2.put("action", "rstatus");
            jsonObject2.put("addr", xenonAddr);
            if (binder == null)
                return;
//            binder.sendOrder(jsonObject.toString());
//            binder.sendOrder(jsonObject.toString());
//            binder.sendOrder(jsonObject.toString());
            binder.sendOrder(jsonObject.toString());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    binder.sendOrder(jsonObject2.toString());
//                    binder.sendOrder(jsonObject2.toString());
//                    binder.sendOrder(jsonObject2.toString());
//                    binder.sendOrder(jsonObject2.toString());
//                    binder.sendOrder(jsonObject2.toString());

                }
            }, 500);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseMsg1(String msg) {
        LogUtil.i("XenonActivity:" + msg);
        try {
            JSONObject jsonObject = new JSONObject(msg);
            int code = jsonObject.getInt("code");
            if (code == 200) {
                JSONObject result = jsonObject.getJSONObject("result");
                String addr = result.getString("addr");
                if (addr.equals(xenonAddr)) {
//
//                    if (result.has("statuss")) {
//                        if (result.getInt("status") == 1) {
//                            xianqideng.setChecked(true);
////                            root.setBackgroundResource(R.drawable.light_bg);
//                        } else {
//                            xianqideng.setChecked(false);
////                            root.setBackgroundResource(R.drawable.light_off);
//
//                        }
//                    } else if (result.has("level")) {
//                        int level = result.getInt("level");
//                        if (level >= 0 && level <= 7) {
//                            gradeBar.setmCurrentIndex(level);
//                            grade.setText(grades[level]);
//                        }
//                    }else
                    if (result.has("command")) {
                        String status = result.getString("command");
                        xianqideng.setChecked(status.equals("1") ? true : false);
                    } else if (result.has("level")) {
                        int level = Integer.parseInt(result.getString("level"));
                        System.out.println("level="+level);
                        if (level > 0 && level <= 7) {

                            gradeBar.setmCurrentIndex(level);
                            grade.setText((level + 1) + "级");
                        }
                    } else if (result.has("status")) {
                        String status = result.getString("status");
                        xianqideng.setChecked(status.equals("1") ? true : false);
                    }
                    handler.removeCallbacksAndMessages(null);
                    refreshLayout.setRefreshing(false);

                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }

            } else {
            }
        } catch (
                Exception e)

        {
            e.printStackTrace();
        }

    }

    private void parseMsg(String msg1) {
        LogUtil.i("XenonActivity:" + msg1);

        try {
            JSONObject jsonObject = new JSONObject(msg1);
            String code = jsonObject.getString("code");
            String device = jsonObject.getString("device");
            if (!device.equals(Config.STSLC_6) && !device.equals(Config.STSLC_10))
                return;
            if (code.equals("200")) {
                JSONObject result = jsonObject.getJSONObject("result");
                int channel = result.getInt("channel");
                int command = result.getInt("command");
                String addr1 = result.getString("addr");
                Map<String, String> map = ParseCMD.check((short) channel, (short) command);
                if (addr.equals(addr1)) {
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        String number1 = entry.getKey();
                        String status = entry.getValue();
                        if (number1.equals(number)) {
                            if (status.equals("1")) {
                                dianyuan.setChecked(true);
                            } else {
                                dianyuan.setChecked(true);
                            }
                        }
                    }
                    handler.removeCallbacksAndMessages(null);

                    dialog.dismiss();
                }
            } else {
                dialog.dismiss();

                UiUtils.showToast("设备不在线");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onDestroy() {
        if (connection != null) {
            unbindService(connection);
        }
        if (connection2 != null) {
            unbindService(connection2);
        }
        handler.removeCallbacksAndMessages(null);

        if (!subscribe.isUnsubscribed()) {
            subscribe.unsubscribe();
        }
        if (!subscribe1.isUnsubscribed()) {
            subscribe1.unsubscribe();
        }
        super.onDestroy();

    }


    @Override
    public void onClick(View v) {

    }

    private Handler handler = new Handler();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.dianyuan:

                if (!dianyuan.isChecked()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("device", Config.STSLC_10);
                        jsonObject.put("action", "switch");
//                        jsonObject.put("user_id", "123");

                        jsonObject.put("channel_id", Integer.valueOf(channel_id));

                        jsonObject.put("command", dianyuan.isChecked() ? 0 : 1);

                        binder2.sendOrder(jsonObject.toString());
                        showWaitDialog();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dissMissDialog();
                            }
                        }, 2000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                    new AlertDialog.Builder(this).setTitle("警告")
                            .setMessage("我们不建议这样做,是否关闭电源?")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog1, int which) {

                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("device", Config.STSLC_10);
                                        jsonObject.put("action", "switch");
                                        jsonObject.put("user_id", "123");

                                        jsonObject.put("channel_id", Integer.valueOf(channel_id));
                                        jsonObject.put("command", dianyuan.isChecked() ? 0 : 1);
                                        binder2.sendOrder(jsonObject.toString());
                                        showWaitDialog();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                            }
                                        }, 2000);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }).setNegativeButton("取消", null).create().show();
                }

                break;
            case R.id.xianqideng:
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("action", "switch");
                    jsonObject.put("addr", xenonAddr);
                    jsonObject.put("command", xianqideng.isChecked() ? 1 : 0);
                    binder.sendOrder(jsonObject.toString());
                    showWaitDialog();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (dialog!=null){
                                dialog.dismiss();
                                UiUtils.showToast("设备不在线");
                            }
                            xianqideng.toggle();
                        }
                    }, 2000);
                } catch (Exception e) {
                    UiUtils.showToast("错误");
                    e.printStackTrace();
                }
                break;
        }
    }
}
