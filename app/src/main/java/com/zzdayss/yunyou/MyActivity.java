package com.zzdayss.yunyou;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zzdayss.yunyou.dao.UserDao;

/**
 * function：连接页面加载首页
 */
public class MyActivity extends Activity {
    private RelativeLayout logoutpanel;
    private LinearLayout line2;
    private TextView nickname,localcitytext,Temperaturetext,getHumiditytext;
    private SharedPreferences sp;
    private String userAccount,userPassword,getuserName,localcity,Temperature,getHumidity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        nickname = findViewById(R.id.nickname);
        localcitytext = findViewById(R.id.localcity);
        Temperaturetext = findViewById(R.id.Temperature);
        getHumiditytext = findViewById(R.id.getHumidity);

        logoutpanel = findViewById(R.id.logout_panel);
        line2 = findViewById(R.id.line2);

        sp = getSharedPreferences("Personal", MODE_PRIVATE);
        localcity = sp.getString("city","");
        Temperature = sp.getString("Temperature","");
        getHumidity = sp.getString("getHumidity","");

        userAccount = sp.getString("userAccount","");
        userPassword = sp.getString("userPassword","");
        getuserName = sp.getString("getuserName","");
        Log.d("My",userAccount+"\t"+userPassword+"\t"+getuserName);
        if("".equals(userAccount)&&"".equals(userPassword)){

        }else{
            nickname.setText(getuserName);
            nickname.setClickable(false);
            nickname.setTextColor(this.getResources().getColor(R.color.black));
        }

        localcitytext.setText(localcity);
        Temperaturetext.setText(Temperature+"℃");
        getHumiditytext.setText(getHumidity+"%");

        if("".equals(userAccount)||"".equals(userPassword)||"".equals(getuserName)) {
            logoutpanel.setVisibility(View.GONE);
            line2.setVisibility(View.GONE);
        }
        bottom();
        setStatusBar();
    }


    public void logout(View veiw){
        showMsg("退出成功");
        new Thread(() -> {
        UserDao userDao = new UserDao();
            userDao.logout(userAccount);
        }).start();
        sp.edit().remove("getuserName").apply();
        sp.edit().remove("userAccount").apply();
        sp.edit().remove("userPassword").apply();
        onCreate(null);

    }

    public void trip(View view){
        if("".equals(userAccount)&&"".equals(userPassword)&&"".equals(getuserName)){
            showMsg("请先登录");
        }else{
            startActivity(new Intent(getApplicationContext(),TripActivity.class));
        }
    }

    public void feedback(View view){
        if("".equals(userAccount)&&"".equals(userPassword)&&"".equals(getuserName)){
            showMsg("请先登录");
        }else{
            startActivity(new Intent(getApplicationContext(),feedbackActivity.class));
        }
    }

    public void register(View view){
        startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
    }


    /**
     * function: 登录
     * */
    public void loginactivity(View view){
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
    }

    /**
     * Toast提示
     * @param msg 提示内容
     */
    private void showMsg(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    public void bottom(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.botton_navigation);
        bottomNavigationView.setSelectedItemId(R.id.my);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuitem -> {
            switch (menuitem.getItemId()){
                case R.id.my:
                    return true;
                case R.id.zhoubian:
                    startActivity(new Intent(getApplicationContext(), ZhoubianActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.main:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0,0);
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

}