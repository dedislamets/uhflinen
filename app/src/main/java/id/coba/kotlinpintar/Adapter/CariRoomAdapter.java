package id.coba.kotlinpintar.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import id.coba.kotlinpintar.R;
import id.coba.kotlinpintar.Rest.Objek;

public class CariRoomAdapter extends BaseAdapter {

    LayoutInflater layoutInflater;
    Context context;
    ArrayList<Objek> model;
    public CariRoomAdapter(Context c_contex, ArrayList<Objek> c_model){
        this.context = c_contex;
        this.model = c_model;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return model.size();
    }

    @Override
    public Object getItem(int position) {
        return model.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    TextView id,ruangan;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view1 = layoutInflater.inflate(R.layout.adapter_note_cari,parent,false);
        ruangan = view1.findViewById(R.id.text_note);

        ruangan.setText(model.get(position).getRuangan());
        return view1;
    }
}
