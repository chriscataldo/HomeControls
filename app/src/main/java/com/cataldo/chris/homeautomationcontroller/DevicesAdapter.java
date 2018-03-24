package com.cataldo.chris.homeautomationcontroller;

/**
 * Created by Chris on 4/18/2016.
 */


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class DevicesAdapter extends ArrayAdapter<Device> {
    // View lookup cache
    private static class ViewHolder {
        TextView headerText;
        TextView deviceName;
        ToggleButton switchSetting;
    }

    Context context;

    public DevicesAdapter(Context context, ArrayList<Device> devices) {
        super(context, 0, devices);
        this.context = context;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        View vi = view;             //trying to reuse a recycled view
        ViewHolder holder = null;

        if (vi == null) {
            //The view is not a recycled one: we have to inflate
            LayoutInflater inflater = LayoutInflater.from(getContext());
            vi = inflater.inflate(R.layout.device_row, parent, false);
            holder = new ViewHolder();

            holder.headerText = (TextView) vi.findViewById(R.id.txtHeader);
            holder.deviceName = (TextView) vi.findViewById(R.id.device_name);
            holder.switchSetting = (ToggleButton) vi.findViewById(R.id.device_switch);

            vi.setTag(holder);
        } else {
            // View recycled !
            // no need to inflate
            // no need to findViews by id
            holder = (ViewHolder) vi.getTag();
        }

        // Get the data item for this position
        final Device device = getItem(position);
        Boolean containsHeader = false;
        if(position == 0) containsHeader = true;
        if(position > 0) {
            Device deviceLast = getItem(position-1);
            if(!device.deviceType.equals(deviceLast.deviceType)) {
                containsHeader = true;
            }
        }

        if(containsHeader) {
            String header = device.deviceType;
            header = header.substring(0,1).toUpperCase() + header.substring(1).toLowerCase() + "s";
            holder.headerText.setText(header);
            holder.headerText.setVisibility(View.VISIBLE);
        } else {
            holder.headerText.setVisibility(View.GONE);
        }


        // Populate the data into the template view using the data object
        holder.deviceName.setText(device.deviceName);

        // set the switch to the correct position
        if(Objects.equals(device.deviceStatus, "on")) {
            holder.switchSetting.setChecked(true);
        } else {
            holder.switchSetting.setChecked(false);
        }


        /** Getting the toggle button corresponding to the clicked item */
        final ToggleButton button = holder.switchSetting;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newState = button.isChecked() ? "on" : "off";
                String jsonString = changeDeviceStatus(device.device, newState);
                try {
                    JSONObject jsonData = new JSONObject(jsonString);
                    String statusError = jsonData.getString("Error");
                    if(statusError.length() > 0) {
                        showErrorAlert(statusError);
                    } else {
                        device.setDeviceStatus(newState);
                        Toast toast= Toast.makeText(context,"Confirmed", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.show();
                    }
                } catch (JSONException e) {
                    showErrorAlert("Invalid Response");
                }
            }
        });

        return vi;
    }

    private String changeDeviceStatus(String device, String state) {
        String commandString = "&command=set&device=" + device + "&state=" + state;
        HTTPConnection connection = new HTTPConnection();
        GlobalVars mApp = ((GlobalVars)context.getApplicationContext());
        String dataUrl = "http://" + mApp.getDomain() + mApp.getHomeControlUrl() + "?" + mApp.getAuthCode() + commandString;
        return connection.getConnection(dataUrl);
    }

    private void showErrorAlert(String errorMessage) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(errorMessage);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}




