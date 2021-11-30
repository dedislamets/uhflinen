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

import static id.coba.kotlinpintar.CustomizedListView.KEY_ID;
import static id.coba.kotlinpintar.CustomizedListView.KEY_TITLE;
import static id.coba.kotlinpintar.InputDbHelper.BERAT;
import static id.coba.kotlinpintar.InputDbHelper.JENIS;
import static id.coba.kotlinpintar.InputDbHelper.NAMA_RUANGAN;
import static id.coba.kotlinpintar.InputDbHelper.NO_REQUEST;
import static id.coba.kotlinpintar.InputDbHelper.QTY;
import static id.coba.kotlinpintar.InputDbHelper.STATUS_REQUEST;

/**
 * Created by lbx on 2017/3/13.
 */
public class RequestDetailAdapter extends BaseAdapter {

    //    private List<EpcDataModel> list ;
//    public ArrayList<HashMap> list;
    private ArrayList<HashMap<String, String>> list;
    Activity activity;

    public RequestDetailAdapter(Activity activity, ArrayList<HashMap<String, String>> list) {
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
                convertView = inflater.inflate(R.layout.item_request, null);
//                convertView = LayoutInflater.from(activity).inflate(R.layout.item_epc, null);
                holder.tvRuangan = (TextView) convertView.findViewById(R.id.textView_ruangan);
                holder.tvNo = (TextView) convertView.findViewById(R.id.textView_id);
                holder.tvRequest = (TextView) convertView.findViewById(R.id.textView_epc);
                holder.tvJenis = (TextView) convertView.findViewById(R.id.textView_barang);
                holder.tvQty = (TextView) convertView.findViewById(R.id.textView_qty);
                holder.tvStatus = (TextView) convertView.findViewById(R.id.textView_status);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            HashMap map = list.get(position);
            if (list != null && !list.isEmpty()) {

                int id = position + 1;
                holder.tvNo.setText("" + id);
                holder.tvRuangan.setText("" + map.get(NAMA_RUANGAN));
                holder.tvRequest.setText("" + map.get(NO_REQUEST));
                holder.tvJenis.setText("" + map.get(JENIS));
                holder.tvQty.setText("" + map.get(QTY));

            }

            String sts = map.get(STATUS_REQUEST).toString();
            if(sts.equals("Pending")){
                holder.tvStatus.setText("Pdg");
            }else {
                holder.tvStatus.setText("Don");
            }


        }catch (Exception e){

        }

        return convertView;
    }

    private class ViewHolder {
        TextView tvRuangan ;
        TextView tvNo ;
        TextView tvRequest;
        TextView tvJenis;
        TextView tvQty;
        TextView tvStatus;
    }
}
