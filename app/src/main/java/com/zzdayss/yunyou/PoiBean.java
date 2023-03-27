package com.zzdayss.yunyou;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.services.help.Tip;
import com.zzdayss.yunyou.R;

import java.util.List;

/**
 * 输入提示adapter，展示item名称和地址
 * Created by ligen on 16/11/25.
 */
public class PoiBean {
    private double longitude;//经度
    private double latitude;//纬度
    private String title;//信息标题
    private String snippet;//信息内容
    public PoiBean(double lon, double lat, String title, String snippet){
        this.longitude = lon;
        this.latitude = lat;
        this.title = title;
        this.snippet = snippet;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }
}
