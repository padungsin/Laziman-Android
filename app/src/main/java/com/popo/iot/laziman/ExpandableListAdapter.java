package com.popo.iot.laziman;

import java.util.Calendar;
import java.util.List;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.popo.iot.laziman.util.GsonUtil;
import com.popo.laziman.cloud.iot.DeviceRegistryImpl;
import com.popo.laziman.cloud.iot.model.Command;
import com.popo.laziman.cloud.iot.model.CustomDevice;
import com.popo.laziman.cloud.iot.model.CustomGateway;
import com.popo.laziman.cloud.iot.model.CustomState;
import com.popo.laziman.cloud.iot.model.DeviceMode;


public class ExpandableListAdapter  extends BaseExpandableListAdapter {
    private static final String TAG = ExpandableListAdapter.class.getSimpleName();
    private Context _context;
    private List<CustomGateway> _listDataHeader; // header titles


    // child data in format of header title, child title
    //private HashMap<String, List<CustomDevice>> _listDataChild;



    public ExpandableListAdapter(Context context) throws Exception{
        this._context = context;
        this._listDataHeader = MainActivity.gateways;
    }

    public void refresh(){
        this._listDataHeader = MainActivity.gateways;
        notifyDataSetChanged();

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

        if(device.getState() == null || device.getState().getState().equals(CustomState.off)){
            deviceControlSwitch.setChecked(false);
        }else{
            deviceControlSwitch.setChecked(true);
        }


        deviceControlSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                new SendCommandTask(device, buttonView, isChecked).execute();
            }

        });


        System.out.println("======================== refresh child view of " +this._listDataHeader.get(groupPosition).getDeviceId() + " =============================");

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


        if(gateway.getDeviceId().equals("-1")){
            ((ImageView) convertView.findViewById(R.id.iv_gateway)).setImageResource(R.drawable.ic_lightning);
        }else{
            ((ImageView) convertView.findViewById(R.id.iv_gateway)).setImageResource(R.drawable.ic_gateway);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(gateway.getDeviceName());

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
                command.setMode(DeviceMode.manual);
                Calendar calendar = Calendar.getInstance();
                command.setCommandDate(calendar.getTime());
                calendar.add(Calendar.MINUTE, 1);

                if (isChecked) {
                    command.setState(CustomState.on);
                } else {
                    command.setState(CustomState.off);
                }


                DeviceRegistryImpl.sendCommand(device.getDeviceId(), MainActivity.cloudConfig.projectId, MainActivity.cloudConfig.cloudRegion, MainActivity.cloudConfig.registryId, GsonUtil.toJson(command));
            }catch(Exception e){
                return  "false";
            }
            return "true";
        }

        protected void onPostExecute(String result)  {

            buttonView.setEnabled(Boolean.valueOf(result));


        }

    }

}
