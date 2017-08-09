package net.suntrans.zhanshi.fragment;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxFragment;

import net.suntrans.zhanshi.App;
import net.suntrans.zhanshi.R;
import net.suntrans.zhanshi.activity.RoomDetailActivity;
import net.suntrans.zhanshi.api.RetrofitHelper;
import net.suntrans.zhanshi.bean.CmdMsg;
import net.suntrans.zhanshi.bean.Config;
import net.suntrans.zhanshi.bean.ControlEntity;
import net.suntrans.zhanshi.bean.DeviceEntity;
import net.suntrans.zhanshi.utils.LogUtil;
import net.suntrans.zhanshi.utils.ParseCMD;
import net.suntrans.zhanshi.utils.RxBus;
import net.suntrans.zhanshi.utils.UiUtils;
import net.suntrans.zhanshi.views.LoadingDialog;
import net.suntrans.zhanshi.views.SwitchButton;
import net.suntrans.zhanshi.websocket.WebSocketService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Looney on 2017/7/20.
 */

public class ZhaomingFragment extends RxFragment {


    private String userid;
    private String id;

    public static final ZhaomingFragment newInstance(String channel_type, String id) {
        ZhaomingFragment fragment = new ZhaomingFragment();
        Bundle bundle = new Bundle();
        bundle.putString("channel_type",channel_type);
        bundle.putString("id",id);
        fragment.setArguments(bundle);
        return fragment;
    }

    private MyHandler mHandler;
    private RecyclerView recyclerView;
    private List<DeviceEntity.ChannelInfo> datas;
    private LoadingDialog dialog;
    private LightAdapter adapter;
    private Observable<DeviceEntity> getDataObj;
    private SwipeRefreshLayout refreshLayout;
    private TextView tips;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zhaoming,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        datas = new ArrayList<>();
        userid = App.getSharedPreferences().getString("user_id", "1");
        id = getArguments().getString("id");

        tips = (TextView) view.findViewById(R.id.tips);
        dialog = new LoadingDialog(getActivity(), R.style.loading_dialog);
        dialog.setCancelable(false);
        mHandler = new MyHandler();
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        adapter = new LightAdapter();
        recyclerView.setAdapter(adapter);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshlayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        RxBus.getInstance().toObserverable(CmdMsg.class)
                .compose(this.<CmdMsg>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
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
                            parseMsg(cmdMsg.msg);
                        }else {
                            UiUtils.showToast(cmdMsg.msg);
                        }
                    }
                });
    }
    private class LightAdapter extends RecyclerView.Adapter<LightAdapter.ViewHolder>{


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_light,parent,false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.setData(position);
        }

        @Override
        public int getItemCount() {
            return datas==null?0:datas.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            TextView name;
            SwitchButton button;
            public void setData(int position){
                button.setCheckedImmediately(datas.get(position).getStatus().equals("1")?true:false);
                name.setText(datas.get(position).getName());
            }
            public ViewHolder(View itemView) {
                super(itemView);
                button = (SwitchButton) itemView.findViewById(R.id.checkbox);
                name = (TextView) itemView.findViewById(R.id.name);
                button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        sendCmd(getAdapterPosition());
                    }
                });
            }
        }
    }



    private void sendCmd(int position) {
        if (position==-1){
            UiUtils.showToast("请不要频繁操作！");
            return;
        }
        mHandler.sendEmptyMessage(MSG_START);
        mHandler.sendMessageDelayed(Message.obtain(mHandler, MSG_CON_FAILED, "设备不在线"), 2000);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device", datas.get(position).getVtype());
            jsonObject.put("action", "switch");
            jsonObject.put("user_id", userid);

            jsonObject.put("channel_id", Integer.valueOf(datas.get(position).getId()));
//                        jsonObject.put("channel_id", "234");

            jsonObject.put("command", datas.get(position).getStatus().equals("1") ? 0 : 1);

            ((RoomDetailActivity)getActivity()).binder.sendOrder(jsonObject.toString());
//                    LogUtil.i("DeviceMainFragment", jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getData();

    }

    public void getData() {
        if (getDataObj == null) {
            getDataObj = RetrofitHelper.getApi().getRoomLight(id)
                    .compose(this.<DeviceEntity>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }

        getDataObj.subscribe(new Subscriber<DeviceEntity>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                if (refreshLayout != null) {
                    refreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onNext(DeviceEntity result) {
                if (refreshLayout != null) {
                    refreshLayout.setRefreshing(false);
                }
                if (result != null) {
                    datas.clear();
                    datas.addAll(result.data.lists);
                    adapter.notifyDataSetChanged();
                }
                if (datas.size()!=0){
                    recyclerView.setVisibility(View.VISIBLE);
                    tips.setVisibility(View.GONE);

                }else {
                    tips.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                }
            }
        });

    }


    private static final int MSG_START = 0;
    private static final int MSG_CON_SUCCESS = 1;
    private static final int MSG_CON_FAILED = 2;


    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START:
                    dialog.setWaitText("请稍后...");
                    dialog.show();
                    break;
                case MSG_CON_SUCCESS:
                    dialog.dismiss();
                    adapter.notifyDataSetChanged();
                    break;
                case MSG_CON_FAILED:
                    dialog.setWaitText((String) msg.obj);
                    UiUtils.showToast((String) msg.obj);
//                    adapter.notifyDataSetChanged();
                    mHandler.sendEmptyMessageDelayed(MSG_CON_SUCCESS, 500);
                    break;
            }
        }
    }

    private void parseMsg(String msg1) {
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
                String addr = result.getString("addr");
                Map<String, String> map = ParseCMD.check((short) channel, (short) command);
                for (int i = 0; i < datas.size(); i++) {
                    if (datas.get(i).getAddr().equals(addr)) {
                        for (Map.Entry<String, String> entry : map.entrySet()) {
                            String number = entry.getKey();
                            String status = entry.getValue();
                            if (datas.get(i).getNumber().equals(number)) {
                                datas.get(i).setStatus(status);
                            }
                        }

                    }
                }
                mHandler.removeMessages(MSG_CON_FAILED);
                mHandler.sendEmptyMessage(MSG_CON_SUCCESS);




            } else if (code.equals("404")) {
                String msg = jsonObject.getString("msg");

                mHandler.removeMessages(MSG_CON_FAILED);
                mHandler.sendMessage(Message.obtain(mHandler, MSG_CON_FAILED, msg));
            } else if (code.equals("101")) {
                String msg = jsonObject.getString("msg");

                mHandler.removeMessages(MSG_CON_FAILED);
                mHandler.sendMessage(Message.obtain(mHandler, MSG_CON_FAILED, msg));
            } else if (code.equals("403")) {
                mHandler.removeMessages(MSG_CON_FAILED);
                mHandler.sendMessage(Message.obtain(mHandler, MSG_CON_FAILED, "设备不在线"));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }


}
