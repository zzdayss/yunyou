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
import android.widget.Toast;

import com.zzdayss.yunyou.dao.UserDao;
import com.zzdayss.yunyou.entity.User;

public class feedbackActivity extends Activity {

    private EditText editText;
    private android.widget.RatingBar RatingBar;
    private SharedPreferences sp;
    private String userAccount,feedback,Stars;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        editText = findViewById(R.id.feedback);
        RatingBar = findViewById(R.id.ratingBar);

        sp = getSharedPreferences("Personal", MODE_PRIVATE);

        setStatusBar();

    }

    public void submit(View view){
        userAccount = sp.getString("userAccount","");

        feedback = (editText.getText().toString());
        Stars = String.valueOf(RatingBar.getRating());
        new Thread(() -> {
            int msg = 0;
            UserDao userDao = new UserDao();
            if("".equals(feedback)||feedback==null){
                msg = 0;
            } else{
                boolean flag = userDao.feedback(userAccount,feedback,Stars);
                Log.d("feed","feedback:::"+feedback);
                Log.d("feed","Stars:::"+Stars);
                if(flag){
                    msg = 1;
                }else {
                    msg = 2;
                }
            }
            hand.sendEmptyMessage(msg);

        }).start();

    }
    @SuppressLint("HandlerLeak")
    final Handler hand = new Handler()
    {
        public void handleMessage(Message msg) {
            if(msg.what == 0) {
                Toast.makeText(getApplicationContext(),"提交失败,请填写完整信息",Toast.LENGTH_LONG).show();
            } else if(msg.what == 1) {
                Toast.makeText(getApplicationContext(),"提交成功",Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(),MyActivity.class));
            }else if(msg.what == 2) {
                Toast.makeText(getApplicationContext(),"提交失败,连接数据库出错",Toast.LENGTH_LONG).show();
            }
        }
    };

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
