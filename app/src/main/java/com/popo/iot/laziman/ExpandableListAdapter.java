package com.popo.iot.laziman;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.api.services.cloudiot.v1.model.Device;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.popo.iot.laziman.callback.DeviceCallback;
import com.popo.iot.laziman.util.Message;
import com.popo.laziman.cloud.iot.DeviceRegistryImpl;
import com.popo.laziman.cloud.iot.MqttMobileImpl;
import com.popo.laziman.cloud.iot.model.Command;
import com.popo.laziman.cloud.iot.model.CustomDevice;
import com.popo.laziman.cloud.iot.model.CustomGateway;
import com.popo.laziman.cloud.iot.model.DeviceType;


public class ExpandableListAdapter  extends BaseExpandableListAdapter {
    private Context _context;
    private List<CustomGateway> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<CustomDevice>> _listDataChild;

    public ExpandableListAdapter(Context context, List<CustomGateway> gateways) {
        this._context = context;
        this._listDataHeader = gateways;

        for (CustomGateway gateway:gateways) {
            _listDataChild = new HashMap<String, List<CustomDevice>>();
            _listDataChild.put(gateway.getGatewayId(), gateway.getDevices());
        }

    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataHeader.get(groupPosition).getDevices().get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final CustomDevice device = (CustomDevice) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);

        }

        ImageView imgChild = (ImageView) convertView.findViewById(R.id.ivDevice);


        switch (device.getDeviceType()){
            case tap:imgChild.setImageResource(R.drawable.ic_tap);break;
            case light:imgChild.setImageResource(R.drawable.ic_light);break;
            case gate:imgChild.setImageResource(R.drawable.ic_gate);break;
            case door:imgChild.setImageResource(R.drawable.ic_door);break;
            case waterPump:imgChild.setImageResource(R.drawable.ic_water_pump);break;
            default:imgChild.setImageResource(R.drawable.ic_other_device);
        }


        TextView txtListChild = (TextView) convertView.findViewById(R.id.lblListItem);
        txtListChild.setText(device.getDeviceName());

        Switch deviceControlSwitch =(Switch) convertView.findViewById(R.id.deviceControlSwitch);
        deviceControlSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                new SendCommandTask(device, buttonView, isChecked).execute();
            }

        });

        //subscribe(device, deviceControlSwitch);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataHeader.get(groupPosition).getDevices().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        CustomGateway gateway = (CustomGateway) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }


        if(gateway.getGatewayId().equals("-1")){
            ((ImageView) convertView.findViewById(R.id.iv_gateway)).setImageResource(R.drawable.ic_lightning);
        }else{
            ((ImageView) convertView.findViewById(R.id.iv_gateway)).setImageResource(R.drawable.ic_gateway);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(gateway.getGatewayName());

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    private class SendCommandTask extends AsyncTask<String, Void, String> {

        private  CompoundButton buttonView;
        private boolean isChecked;
        private CustomDevice device;

        public  SendCommandTask(CustomDevice device, CompoundButton buttonView, boolean isChecked){
            this.device = device;
            this.buttonView = buttonView;
            this.isChecked = isChecked;

        }

        @Override
        protected void onPreExecute() {
            // Create Show ProgressBar
        }

        protected String doInBackground(String... urls)   {

            try {
                Command command = new Command();
                command.setDeviceId(device.getDeviceId());
                command.setType(Command.CommandType.control);
                Calendar calendar = Calendar.getInstance();
                command.setRequestTime(calendar.getTime());
                calendar.add(Calendar.MINUTE, 1);
                command.setExpireTime(calendar.getTime());


                if (isChecked) {
                    command.setControl(Command.ControlType.on);
                } else {
                    command.setControl(Command.ControlType.off);
                }
                Gson gson = new GsonBuilder()
                        .enableComplexMapKeySerialization()
                        .serializeNulls()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .setPrettyPrinting()
                        .setVersion(1.0)
                        .create();

                DeviceRegistryImpl.sendCommand(device.getDeviceId(), MainActivity.cloudConfig.projectId, MainActivity.cloudConfig.cloudRegion, MainActivity.cloudConfig.registryId, gson.toJson(command));
            }catch(Exception e){
                e.printStackTrace();
            }
            return "true";
        }

        protected void onPostExecute(String result)  {


        }

    }


    public String subscribe(CustomDevice device, Switch deviceControlSwitch)   {

        try {


            System.out.println(device.getDeviceName() + " Waiting for state!!");
            DeviceCallback callback = new DeviceCallback();
             MqttMobileImpl.getInstance(MainActivity.me, new DeviceCallback()).listenForEvent(device);

            System.out.println("=========================================================");
        }catch(Exception e){
            e.printStackTrace();
        }
        return "true";
    }
}
