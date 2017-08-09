package net.suntrans.zhanshi.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import net.suntrans.zhanshi.R;
import net.suntrans.zhanshi.bean.AmeterEntity;
import net.suntrans.zhanshi.bean.RoomEntity;

import java.util.List;

/**
 * Created by Looney on 2017/7/23.
 */

public class JianceActivityAdapter extends BaseQuickAdapter<AmeterEntity.Ameter,BaseViewHolder>{

    public JianceActivityAdapter(@LayoutRes int layoutResId, @Nullable List<AmeterEntity.Ameter> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AmeterEntity.Ameter item) {
        helper.setText(R.id.nameCH,item.title);
        helper.getView(R.id.nameEN).setVisibility(View.GONE);
    }
}
