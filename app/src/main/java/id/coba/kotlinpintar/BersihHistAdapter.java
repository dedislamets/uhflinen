package id.coba.kotlinpintar;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static id.coba.kotlinpintar.InputDbHelper.TABLE_BERSIH;
import static id.coba.kotlinpintar.InputDbHelper.TABLE_BERSIH_DETAIL;

public class BersihHistAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    private InputDbHelper mHelper;

    public BersihHistAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        mHelper = new InputDbHelper(a);
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            vi = inflater.inflate(R.layout.list_history, null);

        TextView title = (TextView)vi.findViewById(R.id.title); // title
        TextView artist = (TextView)vi.findViewById(R.id.artist); // artist name
        TextView duration = (TextView)vi.findViewById(R.id.duration); // duration
        TextView status_sync = (TextView)vi.findViewById(R.id.status_sync);
        TextView kategori = (TextView)vi.findViewById(R.id.kategori);
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image);

        ImageView imgdone =(ImageView)vi.findViewById(R.id.imgOK);
        ImageView imgrusak=(ImageView)vi.findViewById(R.id.imgRusak);
        ImageView imgadd=(ImageView)vi.findViewById(R.id.imgAdd);

        HashMap<String, String> song = new HashMap<String, String>();
        song = data.get(position);

        final String no_transaksi = song.get(CustomizedListView.KEY_TITLE);
        title.setText(song.get(CustomizedListView.KEY_TITLE));
        artist.setText(song.get(CustomizedListView.KEY_ARTIST) );
//        duration.setText(song.get(CustomizedListView.KEY_DURATION));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            Date d = sdf.parse(song.get(CustomizedListView.KEY_DURATION));
            sdf.applyPattern("d MMM yyyy, hh:mm");
            duration.setText(sdf.format(d));
        } catch (ParseException ex) {
            //Log.v("Exception", ex.getLocalizedMessage());
        }
//        kategori.setText(song.get("kategori"));

        String sync_sat = song.get("status_sync");
        if( sync_sat.equals("0")){
            status_sync.setText("Not Sync");
            status_sync.setTextColor(Color.RED);
        }else {
            status_sync.setText("Sync");
            status_sync.setTextColor(Color.GREEN);
        }

        String done = song.get("ok");
        String rusak = song.get("rusak");
        String baru = song.get("baru");
        if(done != null) {
            if (done.equals("OK")) {
                imgdone.setVisibility(View.VISIBLE);
            }
        }
        imgrusak.setVisibility(View.GONE);
        if(rusak != null){
            if(rusak.equals("RUSAK")){
                imgrusak.setVisibility(View.VISIBLE);
            }
        }
        if(baru != null){
            if(baru.equals("BARU")){
                imgadd.setVisibility(View.VISIBLE);
            }
        }


        Button btnHapus = (Button) vi.findViewById(R.id.btnHapus);

        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
                alertDialog.setTitle("Yakin akan di hapus ?");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yakin", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = mHelper.getWritableDatabase();
                        String [] whereArgs = {no_transaksi};
                        String whereClause = InputContract.TaskEntry.NO_TRANSAKSI+"=?";
                        db.delete(TABLE_BERSIH, whereClause,whereArgs);
                        db.delete(TABLE_BERSIH_DETAIL, whereClause,whereArgs);

                        ContentValues values_kotor = new ContentValues();
                        values_kotor.put(InputContract.TaskEntry.STATUS, 0);
                        db.update(InputContract.TaskEntry.TABLE,values_kotor,whereClause, whereArgs );

                        db.close();
                        Intent intent = new Intent(activity, ListBersihActivity.class);
                        activity.startActivity(intent);
                    }
                });
                alertDialog.show();
            }
        });

        return vi;
    }

}
