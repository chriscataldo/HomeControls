package com.cataldo.chris.homeautomationcontroller;

/**
 * Created by Chris on 4/18/2016.
 */


import android.content.Context;

import androidx.appcompat.app.AlertDialog;

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

    private final Context context;

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
        boolean containsHeader = false;
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


        /* Getting the toggle button corresponding to the clicked item */
        final ToggleButton button = holder.switchSetting;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newState = button.isChecked() ? "on" : "off";
                String commandString = "&command=set&device=" + device.device + "&state=" + newState;
                ApiConnection connection = new ApiConnection(context);
                JSONObject data = connection.retrieveData(commandString);
                if(data != null) {
                    try {
                        String result = data.getString("status");
                        String message = "";
                        if (result.equals("success")) {
                            message = "Confirmed.";
                            device.setDeviceStatus(newState);
                        } else {
                            message = "There was a problem.";
                        }

                        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.show();
                    } catch (JSONException e) {
                        connection.showErrorAlert("Invalid Json Response");
                    }
                }
            }
        });

        return vi;
    }
}




