package com.example.skaner;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class KlasaConnection {
    String username,password,ip;
    String port="1433";
    String database="skaner";
    String ConnectionURL=null;

    public KlasaConnection() {
    }

    public KlasaConnection(String username, String password, String ip) {
        this.username = username;
        this.password = password;
        this.ip = ip;
    }

    public Connection connectionClass(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Connection connection=null;

        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectionURL="jdbc:jtds:sqlserver://"
                    +ip+":"+port+";databasename="+database+";user="+username+";password="+password+";loginTimeout=5;";
            connection= DriverManager.getConnection(ConnectionURL);
        }catch (Exception e){
            e.printStackTrace();
            Log.e("connerr",e.getMessage());
        }
        return  connection;
    }

    public Connection connectionClass(String URL){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Connection connection=null;

        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connection= DriverManager.getConnection(URL);
        }catch (Exception e){
            e.printStackTrace();
            Log.e("connerr",e.getMessage());
        }
        return  connection;
    }


    public String getConnectionURL(){
        return ConnectionURL;
    }
}
