package com.zzdayss.yunyou;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zzdayss.yunyou.dao.UserDao;
import com.zzdayss.yunyou.entity.Trip;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TripActivity extends Activity {

    private SharedPreferences sp;
    private ListView triplist;
    private String userAccount;
    private List<Map<String,Object>> lists;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        triplist = findViewById(R.id.trip_list);

        sp = getSharedPreferences("Personal", MODE_PRIVATE);
        userAccount = sp.getString("userAccount","");
        trip();
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
            final ListView triplist=findViewById(R.id.trip_list);
            if(msg.what==1){
                SimpleAdapter simpleAdapter=new SimpleAdapter(TripActivity.this, lists,R.layout.adapter_trips,new String[]{"getDeparture","getDestination","getDate"},new int[]{R.id.name,R.id.address,R.id.date});
                triplist.setAdapter(simpleAdapter);
            }
        }
    };
    public void tripadd(View view){
        startActivity(new Intent(getApplicationContext(), TripaddActivity.class));
    }
}
