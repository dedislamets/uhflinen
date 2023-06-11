package id.coba.kotlinpintar;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import static id.coba.kotlinpintar.InputDbHelper.JENIS;
import static id.coba.kotlinpintar.InputDbHelper.NAMA_RUANGAN;
import static id.coba.kotlinpintar.InputDbHelper.NO_REQUEST;
import static id.coba.kotlinpintar.InputDbHelper.QTY;
import static id.coba.kotlinpintar.InputDbHelper.STATUS_REQUEST;

/**
 * Created by lbx on 2017/3/13.
 */
public class KeluarRequestAdapter extends BaseAdapter {

    //    private List<EpcDataModel> list ;
    public ArrayList<HashMap> list;
    public ArrayList<HashMap> list_request;
    private HashMap mapEpc;
//    private ArrayList<HashMap<String, String>> list;
    Activity activity;

    public KeluarRequestAdapter(Activity activity, ArrayList<HashMap> list, ArrayList<HashMap> list_request) {
        super();
        this.activity = activity;
        this.list = list ;
        this.list_request = list_request ;
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
                convertView = inflater.inflate(R.layout.item_request_keluar, null);
                holder.tvNo = (TextView) convertView.findViewById(R.id.textView_id);
                holder.tvJenis = (TextView) convertView.findViewById(R.id.textView_jenis);
                holder.tvQty = (TextView) convertView.findViewById(R.id.textView_qty);
                holder.tvReady = (TextView) convertView.findViewById(R.id.textView_ready);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            HashMap map = list.get(position);
            if (list != null && !list.isEmpty()) {

                int id = position + 1;
                holder.tvNo.setText("" + id);
                holder.tvJenis.setText("" + map.get("jenis"));
                holder.tvQty.setText("" + map.get("qty"));
                holder.tvReady.setText("" + map.get("ready"));
//                int qty = 0;
//                if(map.get("ready") != null){
//                    qty = Integer.parseInt( map.get("ready").toString());
//                }
//                int x =0;
//                for (HashMap map_request : list_request) {
//                    if (map.get("jenis").equals(map_request.get("item"))) {
//                        qty++;
//                        holder.tvReady.setText( String.valueOf(qty) );
//                        list_request.get(x).put("ready", qty);
//                    }
//                }
            }


        }catch (Exception e){
            Log.e(e.getMessage(), "getView: " );
        }

        return convertView;
    }

    private class ViewHolder {
        TextView tvNo ;
        TextView tvJenis;
        TextView tvQty;
        TextView tvReady;
    }
}
