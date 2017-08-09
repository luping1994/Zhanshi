package net.suntrans.zhanshi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;

import net.suntrans.zhanshi.MainActivity;
import net.suntrans.zhanshi.R;
import net.suntrans.zhanshi.adapter.JianceActivityAdapter;
import net.suntrans.zhanshi.adapter.MainActivityAdapter;
import net.suntrans.zhanshi.api.RetrofitHelper;
import net.suntrans.zhanshi.bean.AmeterEntity;
import net.suntrans.zhanshi.bean.RoomEntity;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.R.attr.data;
import static net.suntrans.zhanshi.R.id.recyclerView;

/**
 * Created by Looney on 2017/7/26.
 */

public class JianceActivity extends BasedActivity {
    private List<AmeterEntity.Ameter> datas = new ArrayList<>();
    private JianceActivityAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jiance);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new JianceActivityAdapter(R.layout.item_main, datas);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent();
                intent.putExtra("id", datas.get(position).id);
                intent.putExtra("vtype", datas.get(position).vtype);
                intent.putExtra("name", datas.get(position).title);
                intent.putExtra("sno", datas.get(position).sno);
                if (datas.get(position).vtype.equals("1")) {

                    intent.setClass(JianceActivity.this, JianceDetailActivity.class);
                } else {
                    intent.setClass(JianceActivity.this, JianceDetail3Activity.class);

                }
                startActivity(intent);
            }
        });
        findViewById(R.id.fanhui).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }


    private void getData() {
        RetrofitHelper.getApi().getAmmeter()
                .compose(this.<AmeterEntity>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AmeterEntity>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(AmeterEntity ameterEntity) {
                        datas.clear();
                        datas.addAll(ameterEntity.data.lists);
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
