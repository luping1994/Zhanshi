package net.suntrans.zhanshi.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxFragment;

import net.suntrans.zhanshi.R;
import net.suntrans.zhanshi.api.RetrofitHelper;
import net.suntrans.zhanshi.bean.SceneEntity;
import net.suntrans.zhanshi.bean.SixEntity;
import net.suntrans.zhanshi.utils.LogUtil;
import net.suntrans.zhanshi.views.ScrollChildSwipeRefreshLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Looney on 2017/7/20.
 */

public class EnvFragment extends RxFragment {
    LinearLayout rootLL;

    TextView time;
    private DisplayMetrics displayMetrics = new DisplayMetrics();
    private int Pwidth;
    private String id;
    private ScrollChildSwipeRefreshLayout refreshLayout;

    public static final EnvFragment newInstance(String channel_type, String id) {
        EnvFragment fragment = new EnvFragment();
        Bundle bundle = new Bundle();
        bundle.putString("channel_type",channel_type);
        bundle.putString("id",id);
        fragment.setArguments(bundle);
        return fragment;
    }



    private RecyclerView recyclerView;
    private SixEntity.SixDetailData datas;
    private SceneAdapter adapter;
    private Observable<SixEntity> getDataOb;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_env, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        rootLL = (LinearLayout) view.findViewById(R.id.rootLL);
        time = (TextView) view.findViewById(R.id.time);
        id = getArguments().getString("id");
        refreshLayout = (ScrollChildSwipeRefreshLayout) view.findViewById(R.id.refreshlayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);//获取屏幕大小的信息

        Pwidth =displayMetrics.widthPixels;   //屏幕宽度,先锋的宽度是800px，小米2a的宽度是720px
        initView((SixEntity.SixDetailData) getActivity().getIntent().getParcelableExtra("info"));
    }


    private class SceneAdapter extends BaseQuickAdapter<SceneEntity.Scene, BaseViewHolder> {

        public SceneAdapter(@LayoutRes int layoutResId, @Nullable List<SceneEntity.Scene> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, SceneEntity.Scene item) {
            helper.setText(R.id.sceneName, item.name);
            ImageView imageView = helper.getView(R.id.sceneBg);
        }

    }

    @Override
    public void onResume() {
        getData();
        super.onResume();
    }

    private void getData() {
        if (getDataOb == null)
            getDataOb = RetrofitHelper.getApi().getRoomEnv(id)
                    .compose(this.<SixEntity>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        getDataOb.subscribe(new Subscriber<SixEntity>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                if (refreshLayout!=null){
                    refreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onNext(SixEntity result) {
                if (refreshLayout!=null){
                    refreshLayout.setRefreshing(false);
                }
                result.data.setEva();
                    initView(result.data);
            }
        });
    }



    private void initView(SixEntity.SixDetailData data) {
        if (data!=null){
            SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            String times = format.format(new Date());
            time.setText(times);
        }
        for (int i=0;i<rootLL.getChildCount();i++){
            if (i==0||i==1||i==7||i==13)
                continue;
            initView(i,data);
        }

    }

    Handler handler = new Handler();



    private void initView(int position, SixEntity.SixDetailData data) {


        TextView nameTx = (TextView) rootLL.getChildAt(position).findViewById(R.id.name);
        TextView evaluateTx = (TextView) rootLL.getChildAt(position).findViewById(R.id.evaluate);
        TextView valueTx = (TextView) rootLL.getChildAt(position).findViewById(R.id.value);
        LinearLayout layout_arrow = (LinearLayout) rootLL.getChildAt(position).findViewById(R.id.layout_arrow);
        ImageView standard = (ImageView) rootLL.getChildAt(position).findViewById(R.id.standard);   //等级划分条
        ImageView arrow = (ImageView) rootLL.getChildAt(position).findViewById(R.id.arrow);   //箭头

        switch (position) {
            case 2:
                nameTx.setText("温度");
                standard.setImageResource(R.drawable.standard_tem);
                if (data!=null){
                    valueTx.setText(data.getWendu()+"℃");
                    evaluateTx.setText(data.wenduEva);
                    LogUtil.i("SensusDetaiActivity",data.wenduPro+","+ Pwidth * data.wenduPro / 200);
                    setPading(data.wenduPro,layout_arrow,valueTx);
                }
                break;
            case 3:
                standard.setImageResource(R.drawable.standard_humidity);
                nameTx.setText("湿度");
                if (data!=null){
                    valueTx.setText(data.getShidu()+"%RH");
                    evaluateTx.setText(data.shiduEva);
                    setPading(data.shiduPro,layout_arrow,valueTx);
                }
                break;
            case 4:
                nameTx.setText("大气压");
                standard.setImageResource(R.drawable.standard_humidity);
                if (data!=null){
                    valueTx.setText(data.getDaqiya()+"kPa");
                    evaluateTx.setText(data.daqiYaEva);
                    setPading(data.daqiyaPro,layout_arrow,valueTx);
                }
                break;
            case 5:
                nameTx.setText("人员信息");
                valueTx.setVisibility(View.GONE);
                layout_arrow.setVisibility(View.INVISIBLE);
                if (data!=null){
                    evaluateTx.setText(data.getRenyuan().equals("1")?"有人":"没人");
                }
                break;
            case 6:
                nameTx.setText("光照强度");
                standard.setImageResource(R.drawable.standard_light);
                if (data!=null){
                    valueTx.setText(data.getGuangzhao()+"");
                    evaluateTx.setText(data.guanzhaoEva);
                    setPading(data.guanzhaoPro,layout_arrow,valueTx);
                }
                break;
            case 8:
                nameTx.setText("烟雾");
                standard.setImageResource(R.drawable.standard_smoke);
                if (data!=null){
                    valueTx.setText(data.getYanwu()+"ppm");
                    evaluateTx.setText(data.yanwuEva);
                    setPading(data.yanwuPro,layout_arrow,valueTx);
                }
                break;
            case 9:
                nameTx.setText("甲醛");
                standard.setImageResource(R.drawable.standard_smoke);
                if (data!=null){
                    valueTx.setText(data.getJiaquan()+"ppm");
                    evaluateTx.setText(data.jiaquanEva);
                    setPading(data.jiaquanPro,layout_arrow,valueTx);
                }
                break;
            case 10:
                nameTx.setText("PM1");
                standard.setImageResource(R.drawable.bg_standard);
                if (data!=null){
                    valueTx.setText(data.getPm1()+"ppm");
                    evaluateTx.setText(data.pm1Eva);
                    setPading(data.pm1Pro,layout_arrow,valueTx);
                }
                break;
            case 11:
                nameTx.setText("PM2.5");
                standard.setImageResource(R.drawable.bg_standard);
                if (data!=null){
                    valueTx.setText(data.getPm25()+"ppm");
                    evaluateTx.setText(data.pm25Eva);
                    setPading(data.pm25Pro,layout_arrow,valueTx);
                }
                break;
            case 12:
                nameTx.setText("PM10");
                standard.setImageResource(R.drawable.bg_standard);
                if (data!=null){
                    valueTx.setText(data.getPm10()+"ppm");
                    evaluateTx.setText(data.pm10Eva);
                    setPading(data.pm10Pro,layout_arrow,valueTx);
                }
                break;
            case 14:
                nameTx.setText("X轴角度");
                valueTx.setVisibility(View.GONE);
                layout_arrow.setVisibility(View.INVISIBLE);
                if (data!=null){
                    evaluateTx.setText(data.xEva);
                }
                break;
            case 15:
                valueTx.setVisibility(View.GONE);
                layout_arrow.setVisibility(View.INVISIBLE);
                nameTx.setText("Y轴角度");
                if (data!=null){
                    evaluateTx.setText(data.yEva);
                }
                break;
            case 16:
                valueTx.setVisibility(View.GONE);
                layout_arrow.setVisibility(View.INVISIBLE);
                nameTx.setText("水平夹角");
                if (data!=null){
                    evaluateTx.setText(data.zEva);
                }
                break;
            case 17:
                valueTx.setVisibility(View.GONE);
                layout_arrow.setVisibility(View.INVISIBLE);
                nameTx.setText("振动强度");
                if (data!=null){
                    evaluateTx.setText(data.getZhendong()+"G");
                }
                break;

        }
    }
    private void setPading(int progress,LinearLayout layout_arrow,TextView value){
        value.setVisibility(View.VISIBLE);
        layout_arrow.setVisibility(View.VISIBLE);
        layout_arrow.setPadding(Pwidth * progress / 200, 0, 0, 0);
        if(progress<50)
        {
            value.setGravity(Gravity.LEFT);
            value.setPadding(Pwidth * progress / 200, 0, 0, 0);   //设置左边距
        }
        else
        {
            value.setGravity(Gravity.RIGHT);
//            System.out.println(Pwidth);
            value.setPadding(0, 0, Pwidth * (90 - progress) / 200, 0);  //设置右边距
        }
    }

}
