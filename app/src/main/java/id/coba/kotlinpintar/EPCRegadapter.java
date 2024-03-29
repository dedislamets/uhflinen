package id.coba.kotlinpintar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lbx on 2017/3/13.
 */
public class EPCRegadapter extends BaseAdapter {

    public ArrayList<HashMap> list;
    Activity activity;

    public EPCRegadapter(Activity activity, ArrayList<HashMap> list) {
        this.activity = activity ;
        this.list = list ;
    }
    @Override
    public int getCount() {
        return list.size() ;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            ViewHolder holder;
            LayoutInflater inflater =  activity.getLayoutInflater();
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_register, null);
                holder.tvEpc = (TextView) convertView.findViewById(R.id.textView_epc);
                holder.tvId = (TextView) convertView.findViewById(R.id.textView_id);
                holder.tvStatus = (TextView) convertView.findViewById(R.id.status_epc);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            HashMap map = list.get(position);
            if (list != null && !list.isEmpty()) {
                int id = position + 1;
                holder.tvId.setText("" + id);
                holder.tvEpc.setText("" + map.get("epc"));
                holder.tvStatus.setText("" + map.get("status"));
                if(map.get("status") == "Terdaftar"){
                    holder.tvStatus.setTextColor(Color.RED);
                }

            }
        }catch (Exception e){

        }
        return convertView;
    }

    private class ViewHolder {
        TextView tvEpc ;
        TextView tvId ;
        TextView tvStatus;
    }
}
