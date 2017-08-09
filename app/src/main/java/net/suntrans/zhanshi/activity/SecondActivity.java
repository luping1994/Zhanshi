package net.suntrans.zhanshi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import net.suntrans.zhanshi.R;
import net.suntrans.zhanshi.utils.UiUtils;

/**
 * Created by Looney on 2017/7/26.
 */

public class SecondActivity extends BasedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);


       findViewById(R.id.jiance).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
            startActivity(new Intent(SecondActivity.this,JianceActivity.class));
           }
       });

       findViewById(R.id.fenxi).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(SecondActivity.this,EnergyAnalysisActivity.class));
               UiUtils.showToast("暂无内容");
           }
       });

        findViewById(R.id.fanhui).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
