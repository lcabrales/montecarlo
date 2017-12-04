package com.lucascabrales.montecarlosimulation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.lucascabrales.montecarlosimulation.R;
import com.lucascabrales.montecarlosimulation.models.MainMenu;

import java.util.ArrayList;

/**
 * Created by lucascabrales on 12/3/17.
 */

public class NavMenuAdapter extends ArrayAdapter<MainMenu> {
    private Context mContext;
    private ArrayList<MainMenu> mDataset;

    public NavMenuAdapter(Context context) {
        super(context, R.layout.nav_menu_row);
        mContext = context;
        mDataset = MainMenu.getList();
    }

    public void setData(ArrayList<MainMenu> dataSet) {
        mDataset = dataSet;
        notifyDataSetChanged();
    }

    @Override
    public MainMenu getItem(int position) {
        return mDataset.get(position);
    }

    @Override
    public int getCount() {
        return mDataset.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.nav_menu_row, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.icon = (IconTextView) convertView.findViewById(R.id.icon);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MainMenu obj = mDataset.get(position);
        holder.title.setText(obj.title);
        holder.icon.setText("{" + obj.image + "}");

        return convertView;
    }

    private static class ViewHolder {
        private TextView title;
        private IconTextView icon;
    }
}
