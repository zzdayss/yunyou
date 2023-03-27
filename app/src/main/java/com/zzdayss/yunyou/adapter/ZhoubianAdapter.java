package com.zzdayss.yunyou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zzdayss.yunyou.PoiBean;
import com.zzdayss.yunyou.R;

import java.util.List;

public class ZhoubianAdapter extends BaseAdapter {
    private List<PoiBean> mData;
    private Context mContext;
    public ZhoubianAdapter(List<PoiBean> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }
    @Override
    public int getCount() {
        return mData.size();
    }
    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView =
                LayoutInflater.from(mContext).inflate(R.layout.adapter_inputtips,
                        parent, false);
        TextView title = (TextView)
                convertView.findViewById(R.id.name);
        TextView adress = (TextView)
                convertView.findViewById(R.id.adress);
        title.setText(mData.get(position).getTitle());
        adress.setText(mData.get(position).getSnippet());
        return convertView;
    }
}
