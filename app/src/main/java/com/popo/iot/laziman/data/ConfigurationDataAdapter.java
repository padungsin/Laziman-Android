package com.popo.iot.laziman.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.popo.laziman.cloud.iot.CloudConfig;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationDataAdapter {

    private ConfigurationDataHelper dataHelper;
    public ConfigurationDataAdapter(Context context)
    {
        dataHelper = new ConfigurationDataHelper(context);
    }
/*
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
 */
    public void insertData(Configuration config)
    {
        SQLiteDatabase dbb = dataHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConfigurationDataHelper.projectId, config.getProjectId());
        contentValues.put(ConfigurationDataHelper.cloudRegion, config.getCloudRegion());
        contentValues.put(ConfigurationDataHelper.registryId, config.getRegistryId());
        contentValues.put(ConfigurationDataHelper.mqttBridgeHost, config.getMqttBridgeHost());
        contentValues.put(ConfigurationDataHelper.mqttBridgePort, config.getMqttBridgePort());
        contentValues.put(ConfigurationDataHelper.serviceAccount, config.getServiceAccount());
        contentValues.put(ConfigurationDataHelper.privateKeyFile, config.getPrivateKeyFile());
        contentValues.put(ConfigurationDataHelper.algorithm, config.getAlgorithm());
        contentValues.put(ConfigurationDataHelper.googleAPICredential, config.getGoogleAPICredential());
        contentValues.put(ConfigurationDataHelper.myId, config.getMyId());
        long id = dbb.insert(ConfigurationDataHelper.TABLE_NAME, null , contentValues);

        config.setId(id);

    }

    public List<Configuration> getAllData()
    {
        SQLiteDatabase db = dataHelper.getWritableDatabase();
        String[] columns = {ConfigurationDataHelper.UID,ConfigurationDataHelper.projectId,ConfigurationDataHelper.cloudRegion, ConfigurationDataHelper.registryId,ConfigurationDataHelper.mqttBridgeHost,ConfigurationDataHelper.mqttBridgePort, ConfigurationDataHelper.serviceAccount,ConfigurationDataHelper.privateKeyFile,ConfigurationDataHelper.algorithm, ConfigurationDataHelper.googleAPICredential , ConfigurationDataHelper.myId };
        Cursor cursor =db.query(ConfigurationDataHelper.TABLE_NAME,columns,null,null,null,null,null);

        List<Configuration> configs = new ArrayList<Configuration>();

        while (cursor.moveToNext())
        {
            Configuration config = new Configuration();
            config.setId(cursor.getInt(cursor.getColumnIndex(ConfigurationDataHelper.UID)));
            config.setProjectId(cursor.getString(cursor.getColumnIndex(ConfigurationDataHelper.projectId)));
            config.setCloudRegion(cursor.getString(cursor.getColumnIndex(ConfigurationDataHelper.cloudRegion)));
            config.setRegistryId(cursor.getString(cursor.getColumnIndex(ConfigurationDataHelper.registryId)));
            config.setMqttBridgeHost(cursor.getString(cursor.getColumnIndex(ConfigurationDataHelper.mqttBridgeHost)));
            config.setMqttBridgePort(cursor.getInt(cursor.getColumnIndex(ConfigurationDataHelper.mqttBridgePort)));
            config.setServiceAccount(cursor.getString(cursor.getColumnIndex(ConfigurationDataHelper.serviceAccount)));
            config.setPrivateKeyFile(cursor.getString(cursor.getColumnIndex(ConfigurationDataHelper.privateKeyFile)));
            config.setAlgorithm(cursor.getString(cursor.getColumnIndex(ConfigurationDataHelper.algorithm)));
            config.setGoogleAPICredential(cursor.getString(cursor.getColumnIndex(ConfigurationDataHelper.googleAPICredential)));
            config.setMyId(cursor.getString(cursor.getColumnIndex(ConfigurationDataHelper.myId)));

            configs.add(config);
        }
        return configs;
    }

    public Configuration getData()
    {
        SQLiteDatabase db = dataHelper.getWritableDatabase();
        String[] columns = {ConfigurationDataHelper.UID,ConfigurationDataHelper.projectId,ConfigurationDataHelper.cloudRegion, ConfigurationDataHelper.registryId,ConfigurationDataHelper.mqttBridgeHost,ConfigurationDataHelper.mqttBridgePort, ConfigurationDataHelper.serviceAccount,ConfigurationDataHelper.privateKeyFile,ConfigurationDataHelper.algorithm, ConfigurationDataHelper.googleAPICredential, ConfigurationDataHelper.myId };
        Cursor cursor =db.query(ConfigurationDataHelper.TABLE_NAME,columns,null,null,null,null,null);



        if (cursor.moveToNext())
        {
            Configuration config = new Configuration();
            config.setId(cursor.getInt(cursor.getColumnIndex(ConfigurationDataHelper.UID)));
            config.setProjectId(cursor.getString(cursor.getColumnIndex(ConfigurationDataHelper.projectId)));
            config.setCloudRegion(cursor.getString(cursor.getColumnIndex(ConfigurationDataHelper.cloudRegion)));
            config.setRegistryId(cursor.getString(cursor.getColumnIndex(ConfigurationDataHelper.registryId)));
            config.setMqttBridgeHost(cursor.getString(cursor.getColumnIndex(ConfigurationDataHelper.mqttBridgeHost)));
            config.setMqttBridgePort(cursor.getInt(cursor.getColumnIndex(ConfigurationDataHelper.mqttBridgePort)));
            config.setServiceAccount(cursor.getString(cursor.getColumnIndex(ConfigurationDataHelper.serviceAccount)));
            config.setPrivateKeyFile(cursor.getString(cursor.getColumnIndex(ConfigurationDataHelper.privateKeyFile)));
            config.setAlgorithm(cursor.getString(cursor.getColumnIndex(ConfigurationDataHelper.algorithm)));
            config.setGoogleAPICredential(cursor.getString(cursor.getColumnIndex(ConfigurationDataHelper.googleAPICredential)));
            config.setMyId(cursor.getString(cursor.getColumnIndex(ConfigurationDataHelper.myId)));
            return config;
        }
        return null;
    }

    public  int delete(String registryId)
    {
        SQLiteDatabase db = dataHelper.getWritableDatabase();
        String[] whereArgs ={registryId};

        int count =db.delete(ConfigurationDataHelper.TABLE_NAME ,ConfigurationDataHelper.registryId+" = ?",whereArgs);
        return  count;
    }

    public int update(Configuration config)
    {
        SQLiteDatabase db = dataHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConfigurationDataHelper.projectId, config.getProjectId());
        contentValues.put(ConfigurationDataHelper.cloudRegion, config.getCloudRegion());
        contentValues.put(ConfigurationDataHelper.registryId, config.getRegistryId());
        contentValues.put(ConfigurationDataHelper.mqttBridgeHost, config.getMqttBridgeHost());
        contentValues.put(ConfigurationDataHelper.mqttBridgePort, config.getMqttBridgePort());
        contentValues.put(ConfigurationDataHelper.serviceAccount, config.getServiceAccount());
        contentValues.put(ConfigurationDataHelper.privateKeyFile, config.getPrivateKeyFile());
        contentValues.put(ConfigurationDataHelper.algorithm, config.getAlgorithm());
        contentValues.put(ConfigurationDataHelper.googleAPICredential, config.getGoogleAPICredential());
        contentValues.put(ConfigurationDataHelper.myId, config.getMyId());

        String[] whereArgs = {String.valueOf(config.getId())};
        int count = db.update(ConfigurationDataHelper.TABLE_NAME,contentValues, ConfigurationDataHelper.UID+" = ?",whereArgs );
        return count;
    }

}
