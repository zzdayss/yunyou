package com.zzdayss.yunyou;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.zzdayss.yunyou.dao.UserDao;

import java.util.Calendar;

public class TripaddActivity  extends Activity{

    private TextView date,time;
    private EditText departure,destination;
    private SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tripadd);
        departure = findViewById(R.id.departure);
        destination = findViewById(R.id.destination);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        sp = getSharedPreferences("Personal", MODE_PRIVATE);
        Calendar cal=Calendar.getInstance();
        int y=cal.get(Calendar.YEAR);
        int m=cal.get(Calendar.MONTH);
        int d=cal.get(Calendar.DATE);
        int h=cal.get(Calendar.HOUR_OF_DAY);
        int mi=cal.get(Calendar.MINUTE);
        int s=cal.get(Calendar.SECOND);
        date.setText(y+"年"+m+"月"+d+"日");
        time.setText(h+"时"+mi+"分"+s+"秒");
    }

    public void submit(View view){
        String userAccount = sp.getString("userAccount","");
        String departurecontext = (departure.getText().toString());
        String destinationcontext = (destination.getText().toString());
        String date1 = (date.getText().toString()+time.getText().toString());
        new Thread(() -> {
            int msg = 0;
            UserDao userDao = new UserDao();
            if("".equals(departurecontext)||"".equals(destinationcontext)){
                msg = 0;
            } else{
                boolean flag = userDao.tripadd(userAccount,departurecontext,destinationcontext,date1);
                Log.d("tripadd","departurecontext:::"+departurecontext);
                Log.d("tripadd","destinationcontext:::"+destinationcontext);
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
                Toast.makeText(getApplicationContext(),"添加失败,请填写完整信息",Toast.LENGTH_LONG).show();
            } else if(msg.what == 1) {
                Toast.makeText(getApplicationContext(),"添加成功",Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(),TripActivity.class));
            }else if(msg.what == 2) {
                Toast.makeText(getApplicationContext(),"添加失败,连接数据库出错",Toast.LENGTH_LONG).show();
            }
        }
    };



    public void adddate(View view){
        showdate();
    }
    private void showdate() {
        Calendar calendar = Calendar.getInstance();//调用Calendar类获取年月日
        int  mYear = calendar.get(Calendar.YEAR);//年
        int  mMonth = calendar.get(Calendar.MONTH);//月份要加一个一，这个值的初始值是0。不加会日期会少一月。
        int  mDay = calendar.get(Calendar.DAY_OF_MONTH);//日
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                date.setText(i + "年" + (i1+1) + "月" + i2 + "日");//当选择完后将时间显示,记得月份i1加一
            }
        }, mYear,mMonth, mDay);//将年月日放入DatePickerDialog中，并将值传给参数
        datePickerDialog.show();//显示dialog
    }

    public void addtime(View view){
        showtime();
    }

    public void showtime(){
        //获取日历的一个实例，里面包含了当前的时分秒
        Calendar calendar=Calendar.getInstance();
        //构建一个时间对话框，该对话框已经集成了时间选择器
        //TimePickerDialog的第二个构造参数指定了事件监听器
        TimePickerDialog dialog=new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener(){
            //一旦点击对话框上的确定按钮，触发该方法
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                time.setText(i+"时"+i1+"分");//获取时间对话框设定的小时和分钟数
            }
        },calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);//true表示使用二十四小时制
        //把时间对话框显示在界面上
        dialog.show();
    }




}