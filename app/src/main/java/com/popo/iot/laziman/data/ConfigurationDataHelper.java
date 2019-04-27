package com.popo.iot.laziman.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.popo.iot.laziman.util.Message;


public class ConfigurationDataHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "configuration";    // Database Name
    public static final String TABLE_NAME = "CloudConfig";   // Table Name
    public static final int DATABASE_Version = 1;    // Database Version
    public static final String UID="_id";     // Column I (Primary Key)
    public static final String projectId= "projectId";
    public static final String cloudRegion = "cloudRegion";
    public static final String registryId= "RegistryId";
    public static final String mqttBridgeHost= "mqttBridgeHost";
    public static final String mqttBridgePort= "mqttBridgePort";
    public static final String serviceAccount= "serviceAccount";
    public static final String privateKeyFile= "privateKeyFile";
    public static final String algorithm= "algorithm";
    public static final String googleAPICredential= "googleAPICredential";
    public static final String myId= "myId";



    private Context context;

    public ConfigurationDataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_Version);
        this.context=context;
    }

    public void onCreate(SQLiteDatabase db) {

        try {
            String createTable = "CREATE TABLE " + TABLE_NAME;
            createTable += " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, ";
            createTable += projectId + " VARCHAR(50) ,";
            createTable += cloudRegion + " VARCHAR(50) ,";
            createTable += registryId + " VARCHAR(100) ,";
            createTable += mqttBridgeHost + " VARCHAR(255) ,";
            createTable += mqttBridgePort + " INTEGER ,";
            createTable += serviceAccount + " VARCHAR(255) ,";
            createTable += privateKeyFile + " VARCHAR(100) ,";
            createTable += algorithm + " VARCHAR(10) ,";
            createTable += googleAPICredential + " VARCHAR(1000) ,";
            createTable += myId + " VARCHAR(100));";

            db.execSQL(createTable);
        } catch (Exception e) {
            Message.message(context,""+e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            Message.message(context,"OnUpgrade");
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
            onCreate(db);
        }catch (Exception e) {
            Message.message(context,""+e);
        }
    }
}
