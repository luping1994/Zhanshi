package net.suntrans.zhanshi.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle.components.support.RxFragment;

import net.suntrans.zhanshi.R;
import net.suntrans.zhanshi.adapter.AnalysisAdapter;
import net.suntrans.zhanshi.utils.UiUtils;
import net.suntrans.zhanshi.views.OffsetDecoration;

/**
 * Created by Looney on 2017/7/27.
 */

public class EnergyAnalysisFragment extends RxFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_anslysis,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        AnalysisAdapter analysisAdapter = new AnalysisAdapter(getActivity());

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new OffsetDecoration(UiUtils.dip2px(2)));

        recyclerView.setAdapter(analysisAdapter);
    }
}
