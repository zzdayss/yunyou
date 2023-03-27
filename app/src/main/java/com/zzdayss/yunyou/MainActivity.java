package com.zzdayss.yunyou;

import static com.amap.api.services.core.ServiceSettings.updatePrivacyAgree;
import static com.amap.api.services.core.ServiceSettings.updatePrivacyShow;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zzdayss.yunyou.dao.UserDao;
import com.zzdayss.yunyou.overlay.PoiOverlay;
import com.zzdayss.yunyou.util.Constants;
import com.zzdayss.yunyou.util.ToastUtil;

import java.util.List;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends Activity implements
        GeocodeSearch.OnGeocodeSearchListener,
        AMap.OnMapClickListener,
//        AMap.OnMarkerClickListener,
//        AMap.InfoWindowAdapter,
        View.OnClickListener,
        PoiSearch.OnPoiSearchListener {


    private MapView mapView;//地图页面
    private AMap aMap = null;//地图控制器
    public String city;//城市
    public String cityCode;//城市码
    public WeatherSearchQuery Weatherquery;//天气查询
    public WeatherSearch weathersearch= null;//天气搜索
    public String Temperature;//温度
    public String getHumidity;//湿度
    private FloatingActionButton getLocal;
    private String Title;
    private String Snippet;
    private String Localaddress;
    private String Titlezb;
    private String Snippetzb;
    private int getrenshu;
    private double latitude;
    private double longitude;
    private TextView tvContent,Weather,Renshu;//文本
    public AMapLocationClient mLocationClient = null;//声明AMapLocationClient类对象
    public AMapLocationClientOption mLocationOption = null;//声明AMapLocationClientOption对象
    private GeocodeSearch geocodeSearch;//地理编码搜索
    private static final int PARSE_SUCCESS_CODE = 1000;//解析成功标识码
    private static final int REQUEST_PERMISSIONS = 9527;//请求权限码
    private String POI_SEARCH_TYPE = "汽车服务|汽车销售|" +
            "//汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|" +
            "//住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|" +
            "//金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施";



    private TextView mKeywordsTextView;
    private ProgressDialog progDialog = null;// 搜索时进度条
    private String mKeyWords = "";// 要输入的poi搜索关键字
    private int currentPage = 1;
    private PoiResult poiResult; // poi返回的结果
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索
    private Marker mPoiMarker;
    private Marker mapMarker;
    private ImageView mCleanKeyWords;
    public static final int REQUEST_CODE = 100;
    public static final int RESULT_CODE_INPUTTIPS = 101;
    public static final int RESULT_CODE_KEYWORDS = 102;
    public static final int RESULT_CODE_ZHOUBIAN = 103;
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        updatePrivacyShow(this,true,true);
        updatePrivacyAgree(this,true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences("Personal", MODE_PRIVATE);
        mapView = findViewById(R.id.map);//定义了一个地图view1
        mapView.onCreate(savedInstanceState);//此方法须覆写，虚拟机需要在很多情况下保存地图绘制的当前状态。

        tvContent = findViewById(R.id.tv_content);
        Weather = findViewById(R.id.Weather);
        Renshu = findViewById(R.id.Renshu);
        getLocal = findViewById(R.id.GetLocal);

        if (aMap == null) {
            aMap = mapView.getMap();//初始化地图控制器对象
        }

        initLocation();
        checkingAndroidVersion();
        landian();
        GetOnceLocation();
        WeatherSearchQuery();
        setStatusBar();
        bottom();

        getLocal.setOnClickListener(v -> {
            GetOnceLocation();
            landian();
        });
        try {
            geocodeSearch = new GeocodeSearch(this);
        } catch (AMapException e) {
            e.printStackTrace();
        }
        geocodeSearch.setOnGeocodeSearchListener(this);

        mCleanKeyWords = (ImageView) findViewById(R.id.clean_keywords);
        mCleanKeyWords.setOnClickListener(this);
        mKeywordsTextView = (TextView) findViewById(R.id.main_keywords);
        mKeywordsTextView.setOnClickListener(this);
        mKeyWords = "";


    }

//    /**
//     * 设置页面监听
//     */
//    private void setUpMap() {
//        aMap.setOnMarkerClickListener(this);// 添加点击marker监听事件
//        aMap.setInfoWindowAdapter(this);// 添加显示infowindow监听事件
//        aMap.getUiSettings().setRotateGesturesEnabled(false);
//    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setMessage("正在搜索:\n" + mKeyWords);
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery(String keywords) {
        showProgressDialog();// 显示进度框
        currentPage = 1;
        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query = new PoiSearch.Query(keywords, POI_SEARCH_TYPE, "");
        // 设置每页最多返回多少条poiitem
        query.setPageSize(50);
        // 设置查第一页
        query.setPageNum(currentPage);

        try {
            poiSearch = new PoiSearch(this, query);
        } catch (AMapException e) {
            e.printStackTrace();
        }
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

//    @Override
//    public boolean onMarkerClick(Marker marker) {
//        marker.showInfoWindow();
//        return false;
//    }

//    @Override
//    public View getInfoContents(Marker marker) {
//        return null;
//    }
//
//    @Override
//    public View getInfoWindow(final Marker marker) {
//        View view = getLayoutInflater().inflate(R.layout.poikeywordsearch_uri,
//                null);
//        TextView title = (TextView) view.findViewById(R.id.title);
//        title.setText(marker.getTitle());
//
//        TextView snippet = (TextView) view.findViewById(R.id.snippet);
//        snippet.setText("地址："+marker.getSnippet());
//        return view;
//    }

    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    private void showSuggestCity(List<SuggestionCity> cities) {
        String infomation = "搜索不到，推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            infomation += "城市名称:" + cities.get(i).getCityName() + "\n";
        }
        ToastUtil.show(MainActivity.this, infomation);

    }

    /**
     * POI信息查询回调方法
     */
    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        dissmissProgressDialog();// 隐藏对话框
        if (rCode == 1000) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

                    if (poiItems != null && poiItems.size() > 0) {
                        aMap.clear();// 清理之前的图标
                        PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
                        poiOverlay.removeFromMap();
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();
                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        showSuggestCity(suggestionCities);
                    } else {
                        ToastUtil.show(MainActivity.this,
                                R.string.no_result);
                    }
                }
            } else {
                ToastUtil.show(MainActivity.this,
                        R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this, rCode);
        }

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    /**
     * 输入提示activity选择结果后的处理逻辑
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CODE_INPUTTIPS && data
                != null) {
            aMap.clear();
            Tip tip = data.getParcelableExtra(Constants.EXTRA_TIP);
            if (tip.getPoiID() == null || tip.getPoiID().equals("")) {
                doSearchQuery(tip.getName());
            } else {
                addTipMarker(tip);
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("经度：").append((double) Math.round(tip.getPoint().getLongitude() * 1000) / 1000).append("    ");
                stringBuffer.append("纬度：").append((double) Math.round(tip.getPoint().getLatitude() * 1000) / 1000).append("\n");
                stringBuffer.append("地点：").append(Title).append("\n");
                stringBuffer.append("地址：").append(Snippet).append("\n");
                tvContent.setText(stringBuffer.toString());
            }
            mKeywordsTextView.setText(tip.getName());
            if(!tip.getName().equals("")){
                mCleanKeyWords.setVisibility(View.VISIBLE);
            }
        } else if (resultCode == RESULT_CODE_KEYWORDS && data != null) {
            aMap.clear();
            String keywords = data.getStringExtra(Constants.KEY_WORDS_NAME);
            if(keywords != null && !keywords.equals("")){
                doSearchQuery(keywords);
            }
            mKeywordsTextView.setText(keywords);
            if(!keywords.equals("")){
                mCleanKeyWords.setVisibility(View.VISIBLE);
            }
        } else if(resultCode == RESULT_CODE_ZHOUBIAN){
            Double mDatagetLatitude = Double.valueOf(sp.getString("mDatagetLatitude",""));
            Double mDatagetLongitude = Double.valueOf(sp.getString("mDatagetLongitude",""));
            Titlezb = sp.getString("Titlezb","");
            Snippetzb = sp.getString("Snippetzb","");
            Log.d("Main","latlng:"+mDatagetLongitude);
            LatLng latLng = new LatLng(mDatagetLatitude,mDatagetLongitude);
            bottom();
            aMap.clear();
            if(latLng != null){
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("经度：").append((double) Math.round(mDatagetLongitude * 1000) / 1000).append("    ");
                stringBuffer.append("纬度：").append((double) Math.round(mDatagetLatitude * 1000) / 1000).append("\n");
                stringBuffer.append("地点：").append(Titlezb).append("\n");
                stringBuffer.append("地址：").append(Snippetzb).append("\n");
                tvContent.setText(stringBuffer.toString());
                addMarker(latLng);
            }else{
                showMsg("定位失败");
            }
        }
    }

    /**
     * 用marker展示输入提示list选中数据
     *
     * @param tip
     */
    private void addTipMarker(Tip tip) {
        if (tip == null) {
            return;
        }
        mPoiMarker = aMap.addMarker(new MarkerOptions());
        LatLonPoint point = tip.getPoint();
        if (point != null) {
            LatLng markerPosition = new LatLng(point.getLatitude(), point.getLongitude());
            mPoiMarker.setPosition(markerPosition);
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 17));
        }
        mPoiMarker.setTitle(tip.getName());
        mPoiMarker.setSnippet(tip.getAddress());
        Title = tip.getName();
        Snippet = tip.getAddress();
    }

    /**
     * 用marker展示输入提示list选中数据
     *
     * @param
     */
    private void addMarker(LatLng latLng) {
        mPoiMarker = aMap.addMarker(new MarkerOptions());
        if (latLng != null) {
            mPoiMarker.setPosition(latLng);
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        }
        mPoiMarker.setTitle("地点："+Titlezb);
        mPoiMarker.setSnippet("地址："+Snippetzb);
    }


    /**
     * 点击事件回调方法
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_keywords:
                Intent intent = new Intent(this, InputTipsActivity.class);
                intent.putExtra("City", city);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.clean_keywords:
                mKeywordsTextView.setText("");
                aMap.clear();
                mCleanKeyWords.setVisibility(View.GONE);
            default:
                break;
        }
    }




































    public void landian(){
        //定义一个UiSettings对象
        UiSettings mUiSettings = aMap.getUiSettings();//实例化UiSettings类对象
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        // 自定义定位蓝点图标（此处替换成自己的icon图标就可以了）
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.gps));
        myLocationStyle.radiusFillColor(0);
        myLocationStyle.strokeColor(0);
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(false);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        aMap.setOnMapClickListener(this);
        mUiSettings.setScaleControlsEnabled(true);
        //隐藏缩放按钮
        mUiSettings.setZoomControlsEnabled(true);
    }

    public void bottom(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.botton_navigation);
        bottomNavigationView.setSelectedItemId(R.id.main);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuitem -> {
            switch (menuitem.getItemId()){
                case R.id.main:
                    return true;
                case R.id.zhoubian:
                    Intent intent = new Intent(this, ZhoubianActivity.class);
                    startActivityForResult(intent, REQUEST_CODE);
                    overridePendingTransition(0,0);
                    return true;
                case R.id.my:
                    startActivity(new Intent(getApplicationContext(), MyActivity.class));
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });
    }

    public void WeatherSearchQuery(){
//        Weatherquery = new WeatherSearchQuery("东莞", WeatherSearchQuery.WEATHER_TYPE_LIVE);
        try {
            weathersearch = new WeatherSearch(this);
        } catch (AMapException e) {
            e.printStackTrace();
        }
        weathersearch.setOnWeatherSearchListener(new WeatherSearch.OnWeatherSearchListener() {
            @Override
            public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int i) {
                if (i == 1000) {
                    if (localWeatherLiveResult != null&&localWeatherLiveResult.getLiveResult() != null) {
                        LocalWeatherLive weatherlive = localWeatherLiveResult.getLiveResult();
                        Temperature = weatherlive.getTemperature();
                        getHumidity = weatherlive.getHumidity();
                        sp.edit().putString("Temperature",Temperature).apply();
                        sp.edit().putString("getHumidity",getHumidity).apply();
                        String Weatherstring = "温度：" + Temperature + "°          " +
                                "湿度：" + getHumidity + "%\n";
                        Weather.setText(Weatherstring);
                    }
                }
            }
            @Override
            public void onWeatherForecastSearched(LocalWeatherForecastResult forecastResult, int i) {
            }
        });
        weathersearch.setQuery(Weatherquery);
        weathersearch.searchWeatherAsyn();
    }



    /**
     * 初始化定位
     */
    private void initLocation() {
        //初始化定位
        try {
            mLocationClient = new AMapLocationClient(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //设置定位回调监听
        mLocationClient.setLocationListener(mAMapLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置定位请求超时时间，单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);
        //关闭缓存机制，高精度定位会产生缓存。
        mLocationOption.setLocationCacheEnable(false);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //设置是否允许模拟位置,默认为true，允许模拟位置
        mLocationOption.setMockEnable(true);
    }

    /**
     * 停止定位
     */
    public void deactivate() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }
    /**
     * 检查Android版本
     */
    private void checkingAndroidVersion() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //Android6.0及以上先获取权限再定位
            requestPermission();
        }else {
            //Android6.0以下直接定位
            mLocationClient.startLocation();
        }
    }
    public void GetOnceLocation(){
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }


    private void updateMapLocation(LatLng latLng){
        CameraPosition cameraPosition = new CameraPosition(latLng,16,0,0);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        aMap.animateCamera(cameraUpdate);
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        aMap.addMarker(new MarkerOptions().position(latLng).snippet("经度："+convertToLatLonPoint(latLng).getLongitude()+"\n\n纬度："+convertToLatLonPoint(latLng).getLatitude()));
    }


    /**
     * 坐标转地址
     * @param regeocodeResult
     * @param rCode
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int rCode) {
//解析result获取地址描述信息
        if(rCode == PARSE_SUCCESS_CODE){
            RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
            //显示解析后的地址
            GeocodeQuery query = new GeocodeQuery(regeocodeAddress.getFormatAddress(),"city");
            geocodeSearch.getFromLocationNameAsyn(query);
        }else {
            showMsg("获取地址失败");
        }
    }

    /**
     * 地址转坐标
     * @param geocodeResult
     * @param rCode
     */
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int rCode) {
        if (rCode == PARSE_SUCCESS_CODE) {
            List<GeocodeAddress> geocodeAddressList = geocodeResult.getGeocodeAddressList();
            if(geocodeAddressList!=null && geocodeAddressList.size()>0){
                LatLonPoint latLonPoint = geocodeAddressList.get(0).getLatLonPoint();
                String Address = geocodeAddressList.get(0).getFormatAddress();
                mapMarker.setSnippet(Address);
                String city = geocodeAddressList.get(0).getCity();
                Weatherquery = new WeatherSearchQuery(city, WeatherSearchQuery.WEATHER_TYPE_LIVE);
                weathersearch.setQuery(Weatherquery);
                weathersearch.searchWeatherAsyn();
                //显示解析后的坐标
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("经度：").append((double) Math.round(latLonPoint.getLongitude() * 1000) / 1000).append("    ");
                stringBuffer.append("纬度：").append((double) Math.round(latLonPoint.getLatitude() * 1000) / 1000).append("\n");
                stringBuffer.append("地址：").append(Address).append("\n");
                tvContent.setText(stringBuffer.toString());

            }

        } else {
            showMsg("获取坐标失败");
        }
    }

    /**
     * 地图单击事件
     * @param latLng
     */
    public void onMapClick(LatLng latLng) {
        Title = null;
        Snippet = null;
        latlonToAddress(latLng);
        mapMarker = aMap.addMarker(new MarkerOptions());
        mapMarker.setPosition(latLng);
    }

    /**
     * 接收异步返回的定位结果
     *
     * @param aMapLocation
     */
    AMapLocationListener mAMapLocationListener = new AMapLocationListener() {
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //地址
                    Localaddress = aMapLocation.getAddress();
                    Log.d("MainActivity","地址信息："+Localaddress);
                    Log.d("MainActivity","GPS状态："+aMapLocation.getGpsAccuracyStatus());
                    //赋值
                    city = aMapLocation.getCity();
                    cityCode = aMapLocation.getCityCode();
                    latitude = (double)Math.round(aMapLocation.getLatitude()*1000)/1000;
                    longitude = (double)Math.round(aMapLocation.getLongitude()*1000)/1000;
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("经度：").append(longitude).append("    ");
                    stringBuffer.append("纬度：").append(latitude).append("\n");
                    stringBuffer.append("地址：").append(Localaddress == null ? "地址加载异常" : Localaddress).append("\n");
                    tvContent.setText(stringBuffer.toString());
                    Log.d("MainActivity", stringBuffer.toString());
                    LatLng latLng = new LatLng(latitude,longitude);
                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                    Weatherquery = new WeatherSearchQuery(city, WeatherSearchQuery.WEATHER_TYPE_LIVE);

                    Log.d("MainActivity",city);
                    weathersearch.setQuery(Weatherquery);
                    weathersearch.searchWeatherAsyn();
                    sp.edit().putString("city",city).apply();
                    sp.edit().putString("Localaddress",Localaddress).apply();
                    sp.edit().putString("latitude",String.valueOf(latitude)).apply();
                    sp.edit().putString("longitude",String.valueOf(longitude)).apply();

                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        }
    };

    /**
     * 通过经纬度获取地址
     * @param latLng
     */
    private void latlonToAddress(LatLng latLng) {
        //位置点  通过经纬度进行构建
        LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
        //逆编码查询  第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 5, GeocodeSearch.AMAP);
        //异步获取地址信息
        geocodeSearch.getFromLocationAsyn(query);
    }

    /**
     * 动态请求权限
     */
    @AfterPermissionGranted(REQUEST_PERMISSIONS)
    private void requestPermission() {
        String[] permissions = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        if (EasyPermissions.hasPermissions(this, permissions)) {
            //true 有权限 开始定位
            showMsg("已获得权限，可以定位啦！");
            mLocationClient.startLocation();
        } else {
            //false 无权限
            EasyPermissions.requestPermissions(this, "需要权限", REQUEST_PERMISSIONS, permissions);

        }
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

//    public void gotoNavi(View view) {
//        startActivity(new Intent(this,SearchActivity.class));
//    }
    /**
     * 把LatLng对象转化为LatLonPoint对象
     */
    public static LatLonPoint convertToLatLonPoint(LatLng latLng) {
        return new LatLonPoint(latLng.latitude, latLng.longitude);
    }

    /**
     * 把LatLonPoint对象转化为LatLon对象
     */
    public static LatLng convertToLatLng(LatLonPoint latLonPoint) {
        return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
    }

    /**
     * 请求权限结果
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //设置权限请求结果
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        if(grantResults!=null && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //授予权限 执行逻辑
            mLocationClient.startLocation();
            landian();
        }else{
            //未授予权限进行提示
            showMsg("没有权限，无法使用");

        }
    }



    public void jingweidu(View view){
        if(tvContent.getVisibility()==View.INVISIBLE){
            tvContent.setVisibility(View.VISIBLE);
        }else {
            tvContent.setVisibility(View.INVISIBLE);
        }

    }

    public void wenshidu(View view){
        if(Weather.getVisibility()==View.INVISIBLE){
            Weather.setVisibility(View.VISIBLE);
        }else {
            Weather.setVisibility(View.INVISIBLE);
        }
    }
    public void renshu(View view){
        new Thread(() -> {
            UserDao userDao = new UserDao();
            getrenshu = userDao.getrenshu();
            Log.d("Main","renshu:"+getrenshu);
            Renshu.setText("已登录的在线用户人数："+getrenshu);
        }).start();

        if(Renshu.getVisibility()==View.INVISIBLE){
            Renshu.setVisibility(View.VISIBLE);

        }else {
            Renshu.setVisibility(View.INVISIBLE);
        }
//        startActivity(new Intent(getApplicationContext(), renshu.class));
    }
    /**
     * Toast提示
     * @param msg 提示内容
     */
    private void showMsg(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }



}