package com.cataldo.chris.homeautomationcontroller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Chris on 4/30/2016.
 */
public class SecurityAlertsAdapter extends ArrayAdapter<SecurityAlert> {
    // View lookup cache
    private static class ViewHolder {
        TextView headerText;
        TextView zone;
        TextView action;
        TextView time;
    }

    Context context;

    public SecurityAlertsAdapter(Context context, ArrayList<SecurityAlert> devices) {
        super(context, 0, devices);
        this.context = context;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        View vi = view;             // trying to reuse a recycled view
        ViewHolder holder;

        if (vi == null) {
            //The view is not a recycled one: we have to inflate
            LayoutInflater inflater = LayoutInflater.from(getContext());
            vi = inflater.inflate(R.layout.alert_row, parent, false);
            holder = new ViewHolder();

            holder.headerText = vi.findViewById(R.id.txtHeader);
            holder.zone = vi.findViewById(R.id.zone);
            holder.action = vi.findViewById(R.id.action);
            holder.time = vi.findViewById(R.id.time);

            vi.setTag(holder);
        } else {
            // View recycled !
            // no need to inflate
            // no need to findViews by id
            holder = (ViewHolder) vi.getTag();
        }

        // Get the data item for this position
        final SecurityAlert alert = getItem(position);
        boolean containsHeader = position == 0;
        if(position > 0) {
            SecurityAlert alertLast = getItem(position-1);
            if(!alert.date.equals(alertLast.date)) {
                containsHeader = true;
            }
        }

        if(containsHeader) {
            String dateString = alert.date;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date convertedDate = dateFormat.parse(dateString);
                SimpleDateFormat dateFormatOut = new SimpleDateFormat("MMMM d, yyyy");
                holder.headerText.setText(dateFormatOut.format(convertedDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
           // holder.headerText.setText("blah");
            holder.headerText.setVisibility(View.VISIBLE);
        } else {
            holder.headerText.setVisibility(View.GONE);
        }

        // Populate the data into the template view using the data object
        holder.zone.setText(alert.zone);
        holder.action.setText(alert.action);

        // Format the time
        String timeString = alert.time;
        SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");
        try {
            Date convertedTime = dateFormatter.parse(timeString);
            // Get time from date
            SimpleDateFormat timeFormatOut = new SimpleDateFormat("h:mm a");
            holder.time.setText(timeFormatOut.format(convertedTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return vi;
    }
}