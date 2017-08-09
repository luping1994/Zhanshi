package net.suntrans.zhanshi.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxFragment;


import net.suntrans.zhanshi.App;
import net.suntrans.zhanshi.R;
import net.suntrans.zhanshi.activity.RoomDetailActivity;
import net.suntrans.zhanshi.api.RetrofitHelper;
import net.suntrans.zhanshi.bean.CmdMsg;
import net.suntrans.zhanshi.bean.SceneEntity;
import net.suntrans.zhanshi.utils.ParseCMD;
import net.suntrans.zhanshi.utils.RxBus;
import net.suntrans.zhanshi.utils.UiUtils;
import net.suntrans.zhanshi.websocket.WebSocketService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.R.attr.id;

/**
 * Created by Looney on 2017/7/20.
 */

public class SceneFragment extends RxFragment {
    private String userid;
    private String id;

    public static final SceneFragment newInstance(String channel_type, String id) {
        SceneFragment fragment = new SceneFragment();
        Bundle bundle = new Bundle();
        bundle.putString("channel_type",channel_type);
        bundle.putString("id",id);
        fragment.setArguments(bundle);
        return fragment;
    }


    private Subscription subscribe;

    private RecyclerView recyclerView;
    private List<SceneEntity.Scene> datas;
    private SceneAdapter adapter;
    private Observable<SceneEntity> getDataOb;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scene, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        datas = new ArrayList<>();
        userid = App.getSharedPreferences().getString("user_id", "1");
        id = getArguments().getString("id");
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SceneAdapter(R.layout.item_scene, datas);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                excute(datas.get(position).id);

            }
        });

        subscribe = RxBus.getInstance().toObserverable(CmdMsg.class)
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
                        }
                    }
                });
    }


    private class SceneAdapter extends BaseQuickAdapter<SceneEntity.Scene, BaseViewHolder> {

        public SceneAdapter(@LayoutRes int layoutResId, @Nullable List<SceneEntity.Scene> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, SceneEntity.Scene item) {
            System.out.println(item.name);
            helper.setText(R.id.sceneName,item.name)
                    .addOnClickListener(R.id.zhixing);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getSceneData();
    }

    private void getSceneData() {
        if (getDataOb == null)
            getDataOb = RetrofitHelper.getApi().getHomeScene(id)
                    .compose(this.<SceneEntity>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        getDataOb.subscribe(new Subscriber<SceneEntity>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(SceneEntity result) {
                datas.clear();
                datas.addAll(result.data.lists);
                adapter.notifyDataSetChanged();
            }
        });
    }


    private void parseMsg(String msg1) {
//        try {
//            JSONObject jsonObject = new JSONObject(msg1);
//            String code = jsonObject.getString("code");
//            if (code.equals("200")) {
//                JSONObject result = jsonObject.getJSONObject("result");
//                int channel = result.getInt("channel");
//                int command = result.getInt("command");
//                String device = result.getString("device");
//                String addr = result.getString("addr");
//                Map<String, String> map = ParseCMD.check((short) channel, (short) command);
//
//                for (int i = 0; i < datas.size(); i++) {
//
//                }
//
//
//            } else if (code.equals("404")) {
//
//            } else if (code.equals("101")) {
//
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

    }

    @Override
    public void onDestroy() {
        if (subscribe != null) {
            if (!subscribe.isUnsubscribed()) {
                subscribe.unsubscribe();
            }
        }
        super.onDestroy();
    }

    private static long INTERVAL=1000L;
    private static long lastTime = 0;
    private void excute(String id) {
        long time = System.currentTimeMillis();
        if (time - lastTime>INTERVAL){
//            System.out.println("sb");
            lastTime =time;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("device", "scene");
                jsonObject.put("scene_id",Integer.valueOf(id));
                jsonObject.put("user_id",Integer.valueOf(userid));
                if (((RoomDetailActivity)getActivity()).binder!=null){
                    if (((RoomDetailActivity)getActivity()).binder.sendOrder(jsonObject.toString())){
                        UiUtils.showToast("已经为您执行改场景");

                    }else {
                        UiUtils.showToast("执行失败");
                    }
                }else {
                    UiUtils.showToast("执行失败");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {

            UiUtils.showToast("正在执行中");
        }
//        //{ "device": "scene", "scene_id": 1,"user_id":123}

    }
}
