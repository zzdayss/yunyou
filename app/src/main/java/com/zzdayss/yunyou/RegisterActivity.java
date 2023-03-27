package com.zzdayss.yunyou;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zzdayss.yunyou.dao.UserDao;
import com.zzdayss.yunyou.entity.User;

import java.util.Objects;

/**
 * function：连接注册页面
 */
public class RegisterActivity extends Activity {
    EditText userAccount = null;
    EditText userPassword = null;
    EditText userName = null;
    private String realCode;
    private EditText mEtRegisteractivityPhonecodes;
    private ImageView mIvRegisteractivityShowcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userAccount = findViewById(R.id.userAccount);
        userPassword = findViewById(R.id.userPassword);
        userName = findViewById(R.id.userName);
        initView();
        setStatusBar();
        //将验证码用图片的形式显示出来
        mIvRegisteractivityShowcode.setImageBitmap(Code.getInstance().createBitmap());
        realCode = Code.getInstance().getCode().toLowerCase();


    }
    private void initView() {
        // 初始化控件
        mEtRegisteractivityPhonecodes = findViewById(R.id.et_registeractivity_phoneCodes);
        mIvRegisteractivityShowcode = findViewById(R.id.iv_registeractivity_showCode);

    }
    public void ChangeCode(View view){
        mIvRegisteractivityShowcode.setImageBitmap(Code.getInstance().createBitmap());
        realCode = Code.getInstance().getCode().toLowerCase();
    }
    public void register(View view){

        String userAccount1 = userAccount.getText().toString();
        String userPassword1 = userPassword.getText().toString();
        String userName1 = userName.getText().toString();


        User user = new User();

        user.setUserAccount(userAccount1);
        user.setUserPassword(userPassword1);
        user.setUserName(userName1);

        new Thread(() -> {

            int msg = 0;

            UserDao userDao = new UserDao();
            String phoneCode = mEtRegisteractivityPhonecodes.getText().toString().toLowerCase();
            User uu = userDao.findUser(user.getUserAccount());
            if(uu != null){
                msg = 1;
            }else if("".equals(userAccount1)||"".equals(userPassword1)||"".equals(userName1)){
                msg = 3;
            }else if(!phoneCode.equals(realCode)){
                msg = 4;
            }
            else{
                boolean flag = userDao.register(user);
                if(flag){
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
                Toast.makeText(getApplicationContext(),"注册失败",Toast.LENGTH_LONG).show();
            } else if(msg.what == 1) {
                Toast.makeText(getApplicationContext(),"该账号已经存在，请换一个账号",Toast.LENGTH_LONG).show();
            }  else if(msg.what == 3) {
            Toast.makeText(getApplicationContext(),"请将所有信息填写完整",Toast.LENGTH_LONG).show();
            } else if(msg.what == 4) {
                Toast.makeText(getApplicationContext(), "验证码错误!", Toast.LENGTH_LONG).show();
            } else if(msg.what == 2) {
                Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                //将想要传递的数据用putExtra封装在intent中
                intent.putExtra("a", "注册");
                setResult(RESULT_CANCELED, intent);
                finish();
            }

        }
    };
    public void login(View view){
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
        this.finish();
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
