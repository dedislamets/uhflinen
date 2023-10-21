package id.coba.kotlinpintar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class NotifikasiAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;

    public NotifikasiAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;

        if (convertView == null) {
            vi = inflater.inflate(R.layout.list_notifikasi, null);

            TextView id = vi.findViewById(R.id.id_notifikasi);
            TextView short_msg = vi.findViewById(R.id.short_msg);
            TextView waktu = vi.findViewById(R.id.waktu);
            TextView page_url = vi.findViewById(R.id.page_url);

            HashMap<String, String> song = new HashMap<String, String>();
            song = data.get(position);


            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            try {
                Date d = sdf.parse(song.get("TANGGAL"));
                sdf.applyPattern("d MMM yyyy, hh:mm");
                waktu.setText(sdf.format(d));
            } catch (ParseException ex) {
                //Log.v("Exception", ex.getLocalizedMessage());
            }

            id.setText(song.get("ID"));
            short_msg.setText(song.get("SHORT"));
            page_url.setText(song.get("URL"));
            if(song.get("DIBACA").equals(0) || song.get("DIBACA").equals("0")){
                vi.setBackgroundColor(Color.parseColor("#e2e2e2"));
            }
        }


        return vi;
    }

}