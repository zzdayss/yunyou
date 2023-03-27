package com.zzdayss.yunyou.utils;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * function： 数据库工具类，连接数据库用
 */
public class JDBCUtils {
    private static final String TAG = "mysql-party-JDBCUtils";

    private static String driver = "com.mysql.jdbc.Driver";// MySql驱动

    private static String dbName = "yunyou";// 数据库名称

    private static String user = "yunyou";// 用户名

    private static String password = "123456";// 密码

    public static Connection getConn(){


        Connection connection = null;
        try{
            Class.forName(driver);// 动态加载类
            String ip = "192.168.1.10";// 写成本机地址，不能写成localhost，同时手机和电脑连接的网络必须是同一个

            // 尝试建立到给定数据库URL的连接
            connection = DriverManager.getConnection("jdbc:mysql://" + ip + ":3306/" + dbName,
                    user, password);
        }catch (Exception e){
            e.printStackTrace();
        }
        return connection;
    }
}
