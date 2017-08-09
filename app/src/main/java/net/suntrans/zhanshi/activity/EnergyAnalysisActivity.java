package net.suntrans.zhanshi.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import net.suntrans.zhanshi.R;
import net.suntrans.zhanshi.fragment.EnergyAnalysisFragment;

import static net.suntrans.zhanshi.R.id.toolbar;

/**
 * Created by Looney on 2017/7/26.
 */

public class EnergyAnalysisActivity extends BasedActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_fenxi);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("能耗分析");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        EnergyAnalysisFragment fragment = new EnergyAnalysisFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content,fragment).commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
