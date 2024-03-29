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
public class EPCustadapter extends BaseAdapter {

    //    private List<EpcDataModel> list ;
    public ArrayList<HashMap> list;
    Activity activity;

    public EPCustadapter(Activity activity, ArrayList<HashMap> list) {
        super();
        this.activity = activity;
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
                convertView = inflater.inflate(R.layout.item_epc, null);
//                convertView = LayoutInflater.from(activity).inflate(R.layout.item_epc, null);
                holder.tVRssi = (TextView) convertView.findViewById(R.id.textView_rssi);
                holder.tvEpc = (TextView) convertView.findViewById(R.id.textView_epc);
                holder.tvId = (TextView) convertView.findViewById(R.id.textView_id);
                holder.tvItem = (TextView) convertView.findViewById(R.id.textView_barang);
                holder.tvBerat = (TextView) convertView.findViewById(R.id.textView_berat);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


//            if (list != null && !list.isEmpty()) {
//                int id = position + 1;
//                holder.tvId.setText("" + id);
//                holder.tvEpc.setText(list.get(position).getepc());
//                holder.tVRssi.setText(list.get(position).getrssi());
//
//            }
            HashMap map = list.get(position);
            if (list != null && !list.isEmpty()) {
                int id = position + 1;
                holder.tvId.setText("" + id);
                holder.tvEpc.setText("" + map.get("epc"));
                holder.tVRssi.setText("" + map.get("rssi"));
                holder.tvItem.setText("" + map.get("item"));
                holder.tvBerat.setText("" + map.get("berat"));
            }
            String item = map.get("item").toString();
            if(item.equals("Tidak Terdaftar!")){
//                parent.getChildAt(position).setBackgroundColor(Color.RED);
            }

            if(map.get("exist").equals("1")){
//                parent.getChildAt(position).setBackgroundColor(Color.RED);
            }
        }catch (Exception e){

        }

        return convertView;
    }

    private class ViewHolder {
        TextView tVRssi;
        TextView tvEpc ;
        TextView tvId ;
        TextView tvItem;
        TextView tvBerat;
    }
}
