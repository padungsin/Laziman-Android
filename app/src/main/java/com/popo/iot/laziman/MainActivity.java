package com.popo.iot.laziman;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.services.cloudiot.v1.model.DeviceRegistry;

import com.google.api.services.cloudiot.v1.model.DeviceState;
import com.popo.iot.laziman.callback.DeviceCallback;
import com.popo.iot.laziman.util.GsonUtil;
import com.popo.iot.laziman.util.Message;
import com.popo.iot.laziman.data.Configuration;
import com.popo.iot.laziman.data.ConfigurationDataAdapter;
import com.popo.laziman.cloud.iot.CloudConfig;
import com.popo.laziman.cloud.iot.DeviceRegistryImpl;
import com.popo.laziman.cloud.iot.MqttMobileImpl;
import com.popo.laziman.cloud.iot.model.Command;
import com.popo.laziman.cloud.iot.model.CustomDevice;
import com.popo.laziman.cloud.iot.model.CustomGateway;
import com.popo.laziman.cloud.iot.model.CustomState;
import com.popo.laziman.cloud.iot.model.DeviceMode;
import com.popo.laziman.cloud.iot.model.DeviceType;


import org.eclipse.paho.android.service.MqttAndroidClient;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();


    //public static Configuration configuration;
    private ConfigurationDataAdapter dataAdapter;
    private CoordinatorLayout coordinatorLayout;
    private View contentSettingView;
    private View contentHomeView;
    private View previousView;
    private View currentView;

    private boolean cloudEnable;
    private String taskCommand;

    private static final String TASK_COMMAND_INITIAL_CHECK_REGISTRY = "initialCheckRegistry";
    private static final String TASK_COMMAND_UPDATE_LOCAL_REGISTRY = "updateLocalRegistry";
    private static final String TASK_COMMAND_SEND_DEVICE_COMMAND = "sendDeviceCommand";


    //devices
    public static CloudConfig cloudConfig;
    public static Configuration configuration;

    //device list page
    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    public static List<CustomGateway> gateways = new ArrayList<>();


    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);




        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cloudEnable){
                    Snackbar.make(view, "Cloud enabled. Ready for services!!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else{
                    Snackbar.make(view, "Cloud disabled. Please check Internet or Registration!!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.content_main);
        contentSettingView  = getLayoutInflater().inflate(R.layout.content_setting, coordinatorLayout, false);
        //contentHomeView  = getLayoutInflater().inflate(R.layout.content_home, coordinatorLayout, false);

        contentHomeView = findViewById(R.id.content_home);
        //contentSettingView = findViewById(R.id.content_setting);

        currentView = contentHomeView;



        loadConfiguration();
        Message.message(getApplicationContext(), configuration.toString());


    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            //CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.content_main);
            coordinatorLayout.removeView(currentView);
            previousView = currentView;
            currentView = contentSettingView;
            coordinatorLayout.removeAllViewsInLayout();
            coordinatorLayout.addView(currentView);

            findViewById(R.id.txtRegistryError).setVisibility(View.INVISIBLE);

            ((EditText)findViewById(R.id.txtRegistryId)).setText(configuration.getRegistryId());
            findViewById(R.id.btnSaveSetting).setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(configuration.getRegistryId().equals(((EditText)findViewById(R.id.txtRegistryId)).getText().toString())){
                        Message.message(getApplicationContext(), "Registry is not changed.");
                        return;
                    }
                    //configuration.setRegistryId(((EditText)findViewById(R.id.txtRegistryId)).getText().toString());

                    taskCommand = TASK_COMMAND_UPDATE_LOCAL_REGISTRY;
                    new GoogleCloudTask().execute();
                }
            });

            findViewById(R.id.btnCloseSetting).setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    coordinatorLayout.removeAllViewsInLayout();
                    coordinatorLayout.addView(previousView);
                    currentView = previousView;
                }
            });

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void loadConfiguration()
    {
        dataAdapter = new ConfigurationDataAdapter(getApplicationContext());
        configuration = dataAdapter.getData();
        if(configuration == null){
            configuration = new Configuration();
            configuration.setProjectId("smartmanipulator");
            configuration.setCloudRegion("asia-east1");
            configuration.setRegistryId("my-registry");
            configuration.setPrivateKeyFile("rsa_private_pkcs8");
            configuration.setAlgorithm("RS256");
            configuration.setMqttBridgeHost("mqtt.googleapis.com");
            configuration.setMqttBridgePort(8883);
            configuration.setServiceAccount("smartmanipulator@appspot.gserviceaccount.com");
            configuration.setGoogleAPICredential("smartmanipulator-9299ef822e64.json");
            configuration.setMyId("PO-Android");
            dataAdapter.insertData(configuration);
            cloudConfig = configuration.createCloudConfig();

            setCloudStatus();
            return;
        }


        cloudConfig = configuration.createCloudConfig();

        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(getAssets().open("smartmanipulator-9299ef822e64.json"), "UTF-8"));
            StringBuilder credentialBuilder = new StringBuilder();
            for (String line; (line = r.readLine()) != null; ) {
                credentialBuilder.append(line).append('\n');
            }

            cloudConfig.googleAPICredential = credentialBuilder.toString();
        }catch (Exception e){
            //Message.message(getApplicationContext(), "Error loading json: " + e);
        }


        try {

            InputStream in = getAssets().open("rsa_private_pkcs8");
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len;

            // read bytes from the input stream and store them in buffer
            while ((len = in.read(buffer)) != -1) {
                // write bytes from the buffer into output stream
                os.write(buffer, 0, len);
            }


            cloudConfig.privateKey = os.toByteArray();
        }catch (Exception e){
            e.printStackTrace();
        }

        //check registry is correct then device function enable
        taskCommand = TASK_COMMAND_INITIAL_CHECK_REGISTRY;
        new GoogleCloudTask().execute();

    }


    private class GoogleCloudTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // Create Show ProgressBar
        }

        protected String doInBackground(String... urls)   {

            //Looper.prepare();
            if (Looper.myLooper() == null) { Looper.prepare(); }
            try {
                if(taskCommand.equals(TASK_COMMAND_UPDATE_LOCAL_REGISTRY)){
                    return backgroundUpdateRegistry();
                }else if(taskCommand.equals(TASK_COMMAND_INITIAL_CHECK_REGISTRY)){
                    return backgroundInitialCloudCheck();
                }
            }catch (Exception e){
                //Message.message(getApplicationContext(), "error: " + e);
                //e.printStackTrace();
                return "false";
            }
            return "true";
        }

        protected void onPostExecute(String result)  {
            // Dismiss ProgressBar
            // updateWebView(result);
            try {
                if (taskCommand.equals(TASK_COMMAND_UPDATE_LOCAL_REGISTRY)) {
                    postUpdateLocalRegistryTask(result);

                } else if (taskCommand.equals(TASK_COMMAND_INITIAL_CHECK_REGISTRY)) {
                    postInitialCloudCheck(result);
                }
            }catch (Exception e){
                Message.message(getApplicationContext(), "" + e);
            }

        }

    }

    //background Task
    private String backgroundInitialCloudCheck() throws Exception{

        DeviceRegistry registry = DeviceRegistryImpl.getRegistry(configuration.getProjectId(), configuration.getCloudRegion(), configuration.getRegistryId());
        if(registry == null){
            return "false";
        }

        return "true";
    }
    private String backgroundUpdateRegistry() throws Exception{

        DeviceRegistry registry = DeviceRegistryImpl.getRegistry(configuration.getProjectId(), configuration.getCloudRegion(), ((EditText)findViewById(R.id.txtRegistryId)).getText().toString());

        if(registry == null){
            return "false";
        }
        configuration.setRegistryId(registry.getId());
        dataAdapter.update(configuration);

        return "true";
    }


    //in progress Task

    //post background Task
    private void postInitialCloudCheck(String result) throws  Exception{

        cloudEnable = Boolean.valueOf(result);
        setCloudStatus();
        if(!cloudEnable){
            return;
        }

        fetchDeviceList();

    }

    private void postUpdateLocalRegistryTask(String result) throws Exception{
        cloudEnable = Boolean.valueOf(result);
        setCloudStatus();

        TextView message = (TextView)findViewById(R.id.txtRegistryError);
        if(cloudEnable){
            coordinatorLayout.removeAllViewsInLayout();
            currentView = previousView;
            previousView = contentHomeView;
            coordinatorLayout.addView(currentView);
            message.setVisibility(View.INVISIBLE);



        }else{

            message.setText("Register Id not found in system!!");
            message.setTextColor(Color.parseColor("red"));
            message.setVisibility(View.VISIBLE);
        }
    }




    private void fetchDeviceList() throws  Exception{



        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        new RefreshDeviceTask().execute();

                    }
                });
            }
        };
        timer.schedule(task, 0, 20000); //it executes this every 1000ms

    }


    private void setCloudStatus(){

        FloatingActionButton btnCloudStatus = (FloatingActionButton)findViewById(R.id.fab);

        if(cloudEnable){
            btnCloudStatus.setImageResource(R.drawable.ic_cloud_enable);
            btnCloudStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("green")));
        }else{
            btnCloudStatus.setImageResource(R.drawable.ic_cloud_enable);
            btnCloudStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("red")));
        }

    }
    public void initialDeviceListPage() throws  Exception{


        //find old setting


         listAdapter = new ExpandableListAdapter(this);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvDeviceControl);

        // setting list adapter
        expListView.setAdapter(listAdapter);


        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        ((CustomGateway)listAdapter.getGroup(groupPosition)).getDeviceName() + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });



        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        ((CustomGateway)listAdapter.getGroup(groupPosition)).getDeviceName() + " Collapsed",
                        Toast.LENGTH_SHORT).show();

            }
        });
    }



    private class RefreshDeviceTask extends AsyncTask<String, Void, String> {


        public  RefreshDeviceTask(){

        }

        @Override
        protected void onPreExecute() {
            // Create Show ProgressBar
        }

        protected String doInBackground(String... urls)   {
            try {


                gateways = DeviceRegistryImpl.listGateways(MainActivity.configuration.getProjectId(), MainActivity.configuration.getCloudRegion(), MainActivity.configuration.getRegistryId());

                HashMap<String, String> deviceHashMap = new HashMap<String, String>();
                for (CustomGateway gateway:gateways) {
                    gateway.setDevices(DeviceRegistryImpl.listDevicesForGateway(MainActivity.configuration.getProjectId(), MainActivity.configuration.getCloudRegion(), MainActivity.configuration.getRegistryId(),gateway.getDeviceId()));

                    for (CustomDevice device: gateway.getDevices()) {

                        deviceHashMap.put(device.getDeviceId(), device.getDeviceId());
                    }
                }

                //Direct control device
                List<CustomDevice> allDevices = DeviceRegistryImpl.listNonGatewayDevices(MainActivity.configuration.getProjectId(), MainActivity.configuration.getCloudRegion(), MainActivity.configuration.getRegistryId());


                List<CustomDevice> directControlDevices = new ArrayList<>();
                for (CustomDevice device: allDevices) {

                    if(!deviceHashMap.containsKey(device.getDeviceId())){
                        directControlDevices.add(device);
                    }
                }

                if(directControlDevices.size() > 0) {
                    CustomGateway directControlGateway = new CustomGateway();
                    directControlGateway.setDeviceId("-1");
                    directControlGateway.setDeviceName("Direct control device");

                    directControlGateway.setDevices(directControlDevices);

                    gateways.add(directControlGateway);

                }


            }catch(Exception e){
                return  "false";
            }
            return "true";
        }

        protected void onPostExecute(String result)  {

            try {
                if(listAdapter == null) {
                    initialDeviceListPage();
                }else{
                    listAdapter.refresh();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }

}
