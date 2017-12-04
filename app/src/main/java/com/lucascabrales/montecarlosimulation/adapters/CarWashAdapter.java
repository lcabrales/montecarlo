package com.lucascabrales.montecarlosimulation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lucascabrales.montecarlosimulation.R;
import com.lucascabrales.montecarlosimulation.models.CarWashResult;

import java.util.ArrayList;

/**
 * Created by lucascabrales on 11/22/17.
 */

public class CarWashAdapter extends ArrayAdapter<CarWashResult> {
    private Context mContext;
    private ArrayList<CarWashResult> mDataset;

    public CarWashAdapter(Context context, ArrayList<CarWashResult> dataSet) {
        super(context, R.layout.row_car_wash);
        mContext = context;
        mDataset = dataSet;
    }

    public void setData(ArrayList<CarWashResult> dataSet) {
        mDataset = dataSet;
        notifyDataSetChanged();
    }

    @Override
    public CarWashResult getItem(int position) {
        return mDataset.get(position);
    }

    @Override
    public int getCount() {
        return mDataset.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CarWashAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.row_car_wash, parent, false);
            holder = new CarWashAdapter.ViewHolder();
            holder.interval = (TextView) convertView.findViewById(R.id.tv_interval);
            holder.total = (TextView) convertView.findViewById(R.id.tv_vehicles_total);
            holder.wait = (TextView) convertView.findViewById(R.id.tv_vehicles_wait);

            convertView.setTag(holder);
        } else {
            holder = (CarWashAdapter.ViewHolder) convertView.getTag();
        }

        CarWashResult obj = mDataset.get(position);

        int index = position + 1;
        String indexString = "Intervalo #" + index;

        holder.interval.setText(indexString);
        holder.total.setText(String.valueOf(obj.vehicleAmount));
        holder.wait.setText(String.valueOf(obj.vehicleWait));

        return convertView;
    }

    private static class ViewHolder {
        private TextView interval;
        private TextView total;
        private TextView wait;
    }
}
