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
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zzdayss.yunyou.dao.UserDao;

/**
 * function：连接页面加载首页
 */
public class LoginActivity extends Activity {
    private static final String TAG = "mysql-party-LoginActivity";
    private String realCode;
    public String userName = null;
    private EditText EditTextAccount,EditTextPassword;
    private EditText mEtloginactivityPhonecodes;
    private ImageView mIvloginactivityShowcode;
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        //将验证码用图片的形式显示出来
        mIvloginactivityShowcode.setImageBitmap(Code.getInstance().createBitmap());
        realCode = Code.getInstance().getCode().toLowerCase();
        sp = getSharedPreferences("Personal", MODE_PRIVATE);
        setStatusBar();
    }

    private void initView() {
        // 初始化控件
        mEtloginactivityPhonecodes = findViewById(R.id.et_loginactivity_phoneCodes);
        mIvloginactivityShowcode = findViewById(R.id.iv_loginactivity_showCode);

    }

    public void reg(View view){
        startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
    }


    /**
     * function: 登录
     * */
    public void login(View view){

        EditTextAccount = findViewById(R.id.uesrAccount);
        EditTextPassword = findViewById(R.id.userPassword);

        new Thread(() -> {
            UserDao userDao = new UserDao();
            int msg = userDao.login(EditTextAccount.getText().toString(),EditTextPassword.getText().toString());
            userName = userDao.finduserName(EditTextAccount.getText().toString());
            hand1.sendEmptyMessage(msg);
        }).start();

    }

    public void ChangeCode(View view){
        mIvloginactivityShowcode.setImageBitmap(Code.getInstance().createBitmap());
        realCode = Code.getInstance().getCode().toLowerCase();
    }
    @SuppressLint("HandlerLeak")
    final Handler hand1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_LONG).show();
            } else if (msg.what == 1) {
                String phoneCode = mEtloginactivityPhonecodes.getText().toString().toLowerCase();
                if (phoneCode.equals(realCode)) {
                    sp.edit().putString("userAccount",EditTextAccount.getText().toString()).apply();
                    sp.edit().putString("userPassword",EditTextPassword.getText().toString()).apply();
                    sp.edit().putString("getuserName",userName).apply();

                    Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                }else{
                    Toast.makeText(getApplicationContext(), "验证码错误!", Toast.LENGTH_LONG).show();
                }
            } else if (msg.what == 2) {
                Toast.makeText(getApplicationContext(), "密码错误", Toast.LENGTH_LONG).show();
            } else if (msg.what == 3) {
                Toast.makeText(getApplicationContext(), "账号不存在", Toast.LENGTH_LONG).show();
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