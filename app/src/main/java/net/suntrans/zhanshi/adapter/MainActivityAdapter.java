package net.suntrans.zhanshi.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import net.suntrans.zhanshi.R;
import net.suntrans.zhanshi.bean.RoomEntity;

import java.util.List;

/**
 * Created by Looney on 2017/7/23.
 */

public class MainActivityAdapter extends BaseQuickAdapter<RoomEntity.Room,BaseViewHolder>{

    public MainActivityAdapter(@LayoutRes int layoutResId, @Nullable List<RoomEntity.Room> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RoomEntity.Room item) {
        helper.setText(R.id.nameCH,item.name);
        helper.setText(R.id.nameEN,item.name_en);
    }
}
