package com.zzdayss.yunyou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.services.help.Tip;
import com.zzdayss.yunyou.R;

import java.util.List;

/**
 * 输入提示adapter，展示item名称和地址
 * Created by ligen on 16/11/25.
 */
public class InputTipsAdapter extends BaseAdapter {
    private Context mContext;
    private List<Tip> mListTips;

    public InputTipsAdapter(Context context, List<Tip> tipList) {
        mContext = context;
        mListTips = tipList;
    }

    @Override
    public int getCount() {
        if (mListTips != null) {
            return mListTips.size();
        }
        return 0;
    }


    @Override
    public Object getItem(int i) {
        if (mListTips != null) {
            return mListTips.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder holder;
        if (view == null) {
            holder = new Holder();
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_inputtips, null);
            holder.place = (ImageView) view.findViewById(R.id.place);
            holder.mName = (TextView) view.findViewById(R.id.name);
            holder.mAddress = (TextView) view.findViewById(R.id.adress);
            view.setTag(holder);
        } else{
            holder = (Holder)view.getTag();
        }
        if(mListTips == null){
            return view;
        }

        holder.mName.setText(mListTips.get(i).getName());
        String address = mListTips.get(i).getAddress();
        if(address == null || address.equals("")){
            holder.mAddress.setVisibility(View.GONE);
        }else{
            holder.mAddress.setVisibility(View.VISIBLE);
            holder.mAddress.setText(address);
        }

        return view;
    }

    class Holder {
        ImageView place;
        TextView mName;
        TextView mAddress;
    }
}
