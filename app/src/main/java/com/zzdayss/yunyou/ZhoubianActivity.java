package com.zzdayss.yunyou;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zzdayss.yunyou.adapter.InputTipsAdapter;
import com.zzdayss.yunyou.adapter.ZhoubianAdapter;
import com.zzdayss.yunyou.dao.UserDao;
import com.zzdayss.yunyou.entity.Trip;
import com.zzdayss.yunyou.util.Constants;
import com.zzdayss.yunyou.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ZhoubianActivity extends Activity implements PoiSearch.OnPoiSearchListener, AdapterView.OnItemClickListener {
    private List<PoiBean> mData = null;
    private TextView Local;
    private ListView zhoubianlist;
    private PoiSearch.Query query;
    private ZhoubianAdapter poiAdapter;
    private String Localaddress,getuserName,userAccount;
    private Double Locallatitude;
    private Double Locallongitude;
    private Context mContext;
    //POI搜索对象
    private PoiSearch poiSearch;
    private SharedPreferences sp;
    private LinearLayout login;
    private List<PoiItem> poiItems;
    private List<Map<String,Object>> lists;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhoubian);
        sp = getSharedPreferences("Personal", MODE_PRIVATE);
        Local = findViewById(R.id.Local);
        mContext = this;
        zhoubianlist = (ListView) findViewById(R.id.zhoubian_list);
        zhoubianlist.setOnItemClickListener(this);
        login = findViewById(R.id.login);
        getuserName = sp.getString("getuserName","");
        Localaddress = sp.getString("Localaddress","");
        userAccount = sp.getString("userAccount","");
        Locallatitude = Double.valueOf(sp.getString("latitude",""));;
        Locallongitude = Double.valueOf(sp.getString("longitude",""));
        Local.setText("当前地址："+Localaddress);
        bottom();
        setStatusBar();
        zhoubianPOI("餐饮服务");
        if("".equals(getuserName)){
            zhoubianlist.setVisibility(View.INVISIBLE);
            login.setVisibility(View.VISIBLE);
        }else{
            zhoubianlist.setVisibility(View.VISIBLE);
            login.setVisibility(View.INVISIBLE);
        }
    }

    public void foodPOI(View view){
        zhoubianPOI("餐饮服务");
    }
    public void hotelPOI(View view){
        zhoubianPOI("住宿服务");
    }
    public void tourPOI(View view){
        zhoubianPOI("风景名胜");
    }
    public void playPOI(View view){
        zhoubianPOI("体育休闲服务");
    }
    public void trip(View view){
        trip();
    }
    public void zhoubianPOI(String type) {
        //构造query对象
        query = new PoiSearch.Query(type, "", "");
        // 设置每页最多返回多少条poiitem
        query.setPageSize(50);
        //设置查询页码
        query.setPageNum(1);
        //构造 PoiSearch 对象
        try {
            poiSearch = new PoiSearch(this, query);
        } catch (AMapException e) {
            e.printStackTrace();
        }
        //设置搜索回调监听
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(Locallatitude, Locallongitude), 5000, true));//
        //发起搜索附近POI异步请求
        poiSearch.searchPOIAsyn();
    }

    //回调的方法
    @Override
    public void onPoiItemSearched(PoiItem arg0, int arg1) {

    }

    @Override
    public void onPoiSearched(PoiResult result, int arg1) {
        if (arg1 == 1000) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiItems = result.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    mData = new LinkedList<PoiBean>();
                    if (poiItems != null && poiItems.size() > 0) {
                        for (int i = 0; i < poiItems.size(); i++) {
                            PoiItem poiItem = poiItems.get(i);   //写一个bean，作为数据存储
                            PoiBean bean = new PoiBean(Locallongitude,Locallatitude,poiItem.getTitle(),poiItem.getSnippet());
                            bean.setLatitude(Locallatitude);
                            bean.setLongitude(Locallongitude);
                            bean.setTitle(poiItem.getTitle());
                            bean.setSnippet(poiItem.getAdName()+poiItem.getSnippet());
                            Log.d("zhoubian","list:"+poiItem.getTitle()+","+poiItem.getProvinceName()+","
                                    +poiItem.getCityName()+","
                                    +poiItem.getAdName()+","//区
                                    +poiItem.getSnippet()+"\n");
                            mData.add(bean);
                            }
                        poiAdapter = new ZhoubianAdapter(mData, mContext);
                        zhoubianlist.setAdapter(poiAdapter);
                        poiAdapter.notifyDataSetChanged();
                        } else {
                        ToastUtil.showerror(this, arg1);
                    }

//                        mAdapter.notifyDataSetChanged();  //解析成功更新list布局

                    }
                }
            }
        }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (poiAdapter != null) {

            Intent intent = new Intent();
            Double mDatagetLatitude = poiItems.get(i).getLatLonPoint().getLatitude();
            Double mDatagetLongitude = poiItems.get(i).getLatLonPoint().getLongitude();
            String Titlezb = poiItems.get(i).getTitle();
            String Snippetzb = poiItems.get(i).getSnippet();
            sp.edit().putString("Titlezb", Titlezb).apply();
            sp.edit().putString("Snippetzb", Snippetzb).apply();
            sp.edit().putString("mDatagetLatitude", String.valueOf(mDatagetLatitude)).apply();
            sp.edit().putString("mDatagetLongitude", String.valueOf(mDatagetLongitude)).apply();
            sp.edit().putInt("zhoubian", 103).apply();
            setResult(MainActivity.RESULT_CODE_ZHOUBIAN, intent);
            this.finish();
//            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
    }
    /**
     * function: 登录
     * */
    public void zhoubianlogin(View view){
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
    }

    public void bottom(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.botton_navigation);
        bottomNavigationView.setSelectedItemId(R.id.zhoubian);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuitem -> {
            switch (menuitem.getItemId()) {
                case R.id.zhoubian:
                    return true;
                case R.id.my:
                    startActivity(new Intent(getApplicationContext(), MyActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.main:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
        });
    }

    //是否使用特殊的标题栏背景颜色，android5.0以上可以设置状态栏背景色，如果不使用则使用透明色值
    protected boolean useThemestatusBarColor = false;
    //是否使用状态栏文字和图标为暗色，如果状态栏采用了白色系，则需要使状态栏和图标为暗色，android6.0以上可以设置
    protected boolean useStatusBarColor = true;
    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //根据上面设置是否对状态栏单独设置颜色
            if (useThemestatusBarColor) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.colortheme));//设置状态栏背景色
            } else {
                getWindow().setStatusBarColor(Color.TRANSPARENT);//透明
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        } else {
            Toast.makeText(this, "低于4.4的android系统版本不存在沉浸式状态栏", Toast.LENGTH_SHORT).show();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && useStatusBarColor) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    public void trip(){
        new Thread(() -> {
            UserDao userDao = new UserDao();
            List<Trip> trip = userDao.gettrip(userAccount);
            lists = new ArrayList<>();
            for(int i= 0;i<trip.size();i++){
                Map<String,Object> map =new HashMap<>();
                map.put("getDeparture",trip.get(i).getDeparture());
                map.put("getDestination",trip.get(i).getDestination());
                map.put("getDate",trip.get(i).getDate());
                lists.add(map);
                Log.d("Trip","gettrip: "+ trip.get(i).getDeparture()+trip.get(i).getDestination()+trip.get(i).getDate());
                Log.d("Trip","tripAdapterTTTT: "+ lists);

            }
//                将从数据库拿到的list1对象传给message再由handler传出，再在handler中处理，可进行更新UI
//                新建一个message对象，尽量不要直接new，而是用这种方法，因为有内存的问题存在
            Message message=Message.obtain();
//                        设置message的辨认码，这里设为1
            message.what=1;
//                        把刚才接收到的list1赋给message.obj对象
            message.obj=lists;
//                        通过handler将携带数据的message传出去，传到handler中
            handler.sendMessage(message);
        }).start();
    }
    @SuppressLint("HandlerLeak")
    final Handler handler =new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            final ListView triplist=findViewById(R.id.zhoubian_list);
            if(msg.what==1){
                SimpleAdapter simpleAdapter=new SimpleAdapter(ZhoubianActivity.this, lists,R.layout.adapter_trips,new String[]{"getDeparture","getDestination","getDate"},new int[]{R.id.name,R.id.address,R.id.date});
                triplist.setAdapter(simpleAdapter);
            }
        }
    };
}
