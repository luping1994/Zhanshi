package net.suntrans.zhanshi.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxFragment;

import net.suntrans.zhanshi.R;
import net.suntrans.zhanshi.activity.XenonActivity;
import net.suntrans.zhanshi.api.RetrofitHelper;
import net.suntrans.zhanshi.bean.DeviceEntity;
import net.suntrans.zhanshi.bean.SceneEntity;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Looney on 2017/7/20.
 */

public class XenonFragment extends RxFragment {


    private String id;
    private SwipeRefreshLayout refreshLayout;

    public static final XenonFragment newInstance(String channel_type, String id) {
        XenonFragment fragment = new XenonFragment();
        Bundle bundle = new Bundle();
        bundle.putString("channel_type",channel_type);
        bundle.putString("id",id);
        fragment.setArguments(bundle);
        return fragment;
    }



    private RecyclerView recyclerView;
    private List<DeviceEntity.ChannelInfo> datas;
    private XenonAdapter adapter;
    private Observable<DeviceEntity> getDataOb;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_xenon, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        datas = new ArrayList<>();
        id = getArguments().getString("id");

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshlayout);
        adapter = new XenonAdapter(R.layout.item_xenon, datas);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getActivity(), XenonActivity.class);
                intent.putExtra("xenonAddr",datas.get(position).getXenon_addr());
                intent.putExtra("vtype", "3");
                intent.putExtra("number","4" );
                intent.putExtra("addr","aaaadd09");
                intent.putExtra("status", datas.get(position).getStatus());
                intent.putExtra("channel_id","1" );
                intent.putExtra("name",datas.get(position).getName());
                startActivity(intent);
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
    }


    private class XenonAdapter extends BaseQuickAdapter<DeviceEntity.ChannelInfo, BaseViewHolder> {

        public XenonAdapter(@LayoutRes int layoutResId, @Nullable List<DeviceEntity.ChannelInfo> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, DeviceEntity.ChannelInfo item) {
            helper.setText(R.id.sceneName,item.getName());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        if (getDataOb == null)
            getDataOb = RetrofitHelper.getApi().getRoomXenon(id)
                    .compose(this.<DeviceEntity>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        getDataOb.subscribe(new Subscriber<DeviceEntity>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                if (refreshLayout!=null)
                    refreshLayout.setRefreshing(false);
            }

            @Override
            public void onNext(DeviceEntity result) {
                if (refreshLayout!=null)
                    refreshLayout.setRefreshing(false);
                datas.clear();
                datas.addAll(result.data.lists);
                adapter.notifyDataSetChanged();
            }
        });
    }

}
