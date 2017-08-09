/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.suntrans.zhanshi.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.suntrans.zhanshi.R;


public class AnalysisAdapter extends RecyclerView.Adapter<AnalysisAdapter.ViewHolder> {


    private final String[] items = new String[]{"区域排行", "分类排行", "时段排行", "人均分类排行"};
    private final int[] colors = new int[]{R.color.theme_blue_background, R.color.theme_green_background,
            R.color.theme_purple_background, R.color.theme_red_background};
    private final int[] colorsTx = new int[]{R.color.theme_blue_primary, R.color.theme_green_primary,
            R.color.theme_purple_primary,R.color.theme_red_primary};

    public AnalysisAdapter(Activity mActivity) {
        this.mActivity = mActivity;
    }

    private Activity mActivity;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mActivity.getApplicationContext()).inflate(R.layout.item_category, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(items[position]);
        holder.title.setBackgroundResource(colorsTx[position]);
        holder.img.setBackgroundResource(colors[position]);
    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.category_icon);
            title = (TextView) itemView.findViewById(R.id.category_title);
        }
    }
}
