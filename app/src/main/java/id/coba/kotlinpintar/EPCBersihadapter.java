package id.coba.kotlinpintar;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class EPCBersihadapter extends BaseAdapter {
    public ArrayList<HashMap> list;
    public ArrayList<HashMap> list2;
    Activity activity;
    private TextView tvTagSumBerat ;

    public EPCBersihadapter(Activity activity, ArrayList<HashMap> list, ArrayList<HashMap> list2) {
        super();
        this.activity = activity;
        this.list = list ;
        this.list2 = list2 ;
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
            View fragmnt;
            LayoutInflater inflater =  activity.getLayoutInflater();
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_bersih_check_new, null);
//                convertView = LayoutInflater.from(activity).inflate(R.layout.item_epc, null);
                holder.tVRssi = (TextView) convertView.findViewById(R.id.textView_rssi);
                holder.tvEpc = (TextView) convertView.findViewById(R.id.textView_epc);
                holder.tvId = (TextView) convertView.findViewById(R.id.textView_id);
                holder.tvItem = (TextView) convertView.findViewById(R.id.textView_barang);
                holder.tvBerat = (TextView) convertView.findViewById(R.id.textView_berat);
                holder.tvCheck = (CheckBox) convertView.findViewById(R.id.chk_check);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            HashMap map = list.get(position);

            if (list != null && !list.isEmpty()) {
                int id = position + 1;
                holder.tvId.setText("" + id);
                holder.tvEpc.setText("" + map.get("epc"));
                holder.tVRssi.setText("" + map.get("rssi"));
                holder.tvItem.setText("" + map.get("item"));
                holder.tvBerat.setText("" + map.get("berat"));

                holder.tvCheck.setChecked(false);


                for (HashMap num_scan : list2) {
                    if (map.get("epc").equals(num_scan.get("epc"))) {
                        holder.tvCheck.setChecked(true);
                        map.put("checked", "1");
                    }
                }

                if(map.get("checked").equals("1") || map.get("checked").equals(1)){
                    holder.tvCheck.setChecked(true);
                }

                if(map.get("baru").equals(1) || map.get("baru").equals("1")){
                    parent.getChildAt(position).setBackgroundColor(Color.YELLOW);
                }
                if(map.get("rusak").equals(1) || map.get("rusak").equals("1")){
                    parent.getChildAt(position).setBackgroundColor(Color.RED);
                }

            }


        }catch (Exception e){
            Log.e("adapter", e.getMessage());
        }

        return convertView;
    }

    private class ViewHolder {
        TextView tVRssi;
        TextView tvEpc ;
        TextView tvId ;
        TextView tvItem;
        TextView tvBerat;
        CheckBox tvCheck;
    }
}
