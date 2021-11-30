package id.coba.kotlinpintar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class KeluarAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;

    public KeluarAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_rusak, null);

        TextView title = (TextView)vi.findViewById(R.id.title); // title
        TextView artist = (TextView)vi.findViewById(R.id.artist); // artist name
        TextView duration = (TextView)vi.findViewById(R.id.duration); // duration
        TextView status_sync = (TextView)vi.findViewById(R.id.status_sync);
        TextView ruangan = (TextView)vi.findViewById(R.id.kategori);

        HashMap<String, String> song = new HashMap<String, String>();
        song = data.get(position);

        // Setting all values in listview
        title.setText(song.get(CustomizedListView.KEY_TITLE));
        artist.setText(song.get(CustomizedListView.KEY_ARTIST));
//        duration.setText(song.get(CustomizedListView.KEY_DURATION));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            Date d = sdf.parse(song.get(CustomizedListView.KEY_DURATION));
            sdf.applyPattern("d MMM yyyy, hh:mm");
            duration.setText(sdf.format(d));
        } catch (ParseException ex) {
            //Log.v("Exception", ex.getLocalizedMessage());
        }

        ruangan.setText(song.get("ruangan")) ;
        String sync_sat = song.get("status_sync");
        if( sync_sat.equals("0")){
            status_sync.setText("Not Sync");
            status_sync.setTextColor(Color.RED);
        }else {
            status_sync.setText("Sync");
            status_sync.setTextColor(Color.GREEN);
        }
        return vi;
    }
}