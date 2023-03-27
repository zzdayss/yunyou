package com.zzdayss.yunyou.dao;

import android.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.zzdayss.yunyou.entity.Trip;
import com.zzdayss.yunyou.entity.User;
import com.zzdayss.yunyou.utils.JDBCUtils;

/**
 * author: yan
 * date: 2022.02.17
 * **/
public class UserDao {

    private static final String TAG = "mysql-party-UserDao";

    /**
     * function: 登录
     * */
    public int login(String userAccount, String userPassword){

        HashMap<String, Object> map = new HashMap<>();
        // 根据数据库名称，建立连接
        Connection connection = JDBCUtils.getConn();
        Log.e(TAG,"connection1是：" + connection);
        int msg = 0;
        try {
            // mysql简单的查询语句。这里是根据user表的userAccount字段来查询某条记录
            String sql = "select * from user where userAccount = ?";
            String loginsuccess = "update user set state = 1 where userAccount = ?";
            if (connection != null){// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null){
                    Log.e(TAG,"账号：" + userAccount);
                    //根据账号进行查询
                    ps.setString(1, userAccount);
                    // 执行sql查询语句并返回结果集
                    ResultSet rs = ps.executeQuery();
                    int count = rs.getMetaData().getColumnCount();
                    //将查到的内容储存在map里
                    while (rs.next()){
                        // 注意：下标是从1开始的
                        for (int i = 1;i <= count;i++){
                            String field = rs.getMetaData().getColumnName(i);
                            map.put(field, rs.getString(field));
                        }
                    }

                    if (map.size()!=0){
                        StringBuilder s = new StringBuilder();
                        //寻找密码是否匹配
                        for (String key : map.keySet()){
                            if(key.equals("userPassword")){
                                if(userPassword.equals(map.get(key))){
                                    PreparedStatement ps1 = connection.prepareStatement(loginsuccess);
                                    ps1.setString(1, userAccount);
                                    int rs1 = ps1.executeUpdate();
                                    if(rs1 == 1) {
                                        msg = 1;            //密码正确
                                    }else{
                                        msg = 4;
                                    }
                                }
                                else
                                    msg = 2;            //密码错误
                                break;
                            }
                            connection.close();
                            ps.close();
                        }
                    }else {
                        Log.e(TAG, "查询结果为空");
                        msg = 3;
                    }
                }else {
                    msg = 0;
                }
            }else {
                msg = 0;
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "异常login：" + e.getMessage());
            msg = 0;
        }
        return msg;
    }

    public void logout(String userAccount) {
        Connection connection = JDBCUtils.getConn();
        String logout = "update user set state = 0 where userAccount = ?";
        try {
            if (connection != null) {// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(logout);
                if (ps != null) {
                    Log.e(TAG, "账号：" + userAccount);
                    //根据账号进行查询
                    ps.setString(1, userAccount);
                    // 执行sql查询语句并返回结果集
                    ps.executeUpdate();
                    //将查到的内容储存在map里

                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public int getrenshu(){
        Connection connection = JDBCUtils.getConn();
        String logout = "select count(*) from user where state = 1";
        int count = 0;
        try {
            if (connection != null) {// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(logout);
                if (ps != null) {
                    // 执行sql查询语句并返回结果集
                    ResultSet rs = ps.executeQuery();
                    while(rs.next()){
                        count = rs.getInt(1);
                    }

                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return count;
    }


    /**
     * function: 注册
     * */
    public boolean register(User user){
        HashMap<String, Object> map = new HashMap<>();
        // 根据数据库名称，建立连接
        Connection connection = JDBCUtils.getConn();
        Log.e(TAG,"connection2是：" + connection);

        try {
            String sql = "insert into user(userAccount,userPassword,userName) values (?,?,?)";
            if (connection != null){// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null){

                    //将数据插入数据库
                    ps.setString(1,user.getUserAccount());
                    ps.setString(2,user.getUserPassword());
                    ps.setString(3,user.getUserName());

                    // 执行sql查询语句并返回结果集
                    int rs = ps.executeUpdate();
                    if(rs>0)
                        return true;
                    else
                        return false;
                }else {
                    return  false;
                }
            }else {
                return  false;
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "异常register：" + e.getMessage());
            return false;
        }

    }

    /**
     * function: 添加行程
     * */
    public boolean tripadd(String userAccount, String departure, String destination, String date){
        // 根据数据库名称，建立连接
        Connection connection = JDBCUtils.getConn();
        try {
            String sql = "insert into trip(userAccount,departure,destination,date) values (?,?,?,?)";
            if (connection != null){// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null){
                    //将数据插入数据库
                    ps.setString(1,userAccount);
                    ps.setString(2,departure);
                    ps.setString(3,destination);
                    ps.setString(4,date);

                    // 执行sql查询语句并返回结果集
                    int rs = ps.executeUpdate();
                    if(rs>0)
                        return true;
                    else
                        return false;
                }else {
                    return  false;
                }
            }else {
                return  false;
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "异常register：" + e.getMessage());
            return false;
        }

    }
    /**
     * function: 根据账号进行查询行程
     *
     * @return*/
    public List gettrip(String userAccount) {

        // 根据数据库名称，建立连接
        Connection connection = JDBCUtils.getConn();
        List<Trip> tripdata = new LinkedList<Trip>();
        try {
            String sql = "select * from trip where userAccount = ?";
            if (connection != null){// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    ps.setString(1, userAccount);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        //注意：下标是从1开始
                        String departure = rs.getString(3);
                        String destination = rs.getString(4);
                        String date = rs.getString(5);
                        Trip trip = new Trip(departure, destination, date);
                        trip.setDeparture(departure);
                        trip.setDestination(destination);
                        trip.setDate(date);
                        tripdata.add(trip);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "异常findUser：" + e.getMessage());
            return null;
        }
        return tripdata;
    }
    /**
     * function: 反馈
     * */
    public boolean feedback(String userAccount,String content,String Stars){
        // 根据数据库名称，建立连接
        Connection connection = JDBCUtils.getConn();
        try {
            String sql = "insert into feedback(userAccount,content,Stars) values (?,?,?)";
            if (connection != null){// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null){
                    //将数据插入数据库
                    ps.setString(1,userAccount);
                    ps.setString(2,content);
                    ps.setString(3,Stars);

                    // 执行sql查询语句并返回结果集
                    int rs = ps.executeUpdate();
                    if(rs>0)
                        return true;
                    else
                        return false;
                }else {
                    return  false;
                }
            }else {
                return  false;
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "异常register：" + e.getMessage());
            return false;
        }

    }

    /**
     * function: 根据账号进行查找该用户是否存在
     * */
    public User findUser(String userAccount) {

        // 根据数据库名称，建立连接
        Connection connection = JDBCUtils.getConn();
        Log.e(TAG,"connection3是：" + connection);
        User user = null;
        try {
            String sql = "select * from user where userAccount = ?";
            if (connection != null){// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    ps.setString(1, userAccount);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        //注意：下标是从1开始
                        int id = rs.getInt(1);
                        String userAccount1 = rs.getString(2);
                        String userPassword = rs.getString(3);
                        String userName = rs.getString(4);
                        user = new User(id, userAccount1, userPassword, userName);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "异常findUser：" + e.getMessage());
            return null;
        }
        return user;
    }
    public String finduserName(String userAccount){
        String userName = null;
        try{
            String sql = "select * from user where userAccount ="+userAccount+"";
            Connection conn = new JDBCUtils().getConn();
            Log.e(TAG,"connection2是：" + conn);
            PreparedStatement parstmt = conn.prepareStatement(sql);
            ResultSet rs = parstmt.executeQuery();
            while(rs.next()) {
                userName = rs.getString("userName");
            }
            conn.close();
            parstmt.close();
            rs.close();
        }catch(SQLException e) {
            e.printStackTrace();
        }
        return userName;
    }
}
