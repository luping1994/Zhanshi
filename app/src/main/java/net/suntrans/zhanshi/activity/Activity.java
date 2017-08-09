package net.suntrans.zhanshi.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by Looney on 2017/7/27.
 */

public class Activity extends BasedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.app.FragmentManager manager = getFragmentManager();
//        manager.beginTransaction().add()
    }
}
