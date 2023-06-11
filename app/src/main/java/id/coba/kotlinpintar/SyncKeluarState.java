package id.coba.kotlinpintar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static id.coba.kotlinpintar.InputDbHelper.KATEGORI;
import static id.coba.kotlinpintar.InputDbHelper.NAMA_RUANGAN;
import static id.coba.kotlinpintar.InputDbHelper.NO_REFERENSI;
import static id.coba.kotlinpintar.InputDbHelper.TOTAL_BERAT;
import static id.coba.kotlinpintar.InputDbHelper.TOTAL_QTY;
import static id.coba.kotlinpintar.Rest.ApiClient.BASE_URL;

public class SyncKeluarState extends BroadcastReceiver {
    private Context context;
    private InputDbHelper db;


    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        db = new InputDbHelper(context);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //if there is a network
        if (activeNetwork != null) {
            //if connected to wifi or mobile data plan
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

                //getting all the unsynced names
                Cursor cursor = db.getUnsyncedKeluar();
                if (cursor.moveToFirst()) {
                    do {

                        saveHeader(
                                cursor.getInt(cursor.getColumnIndex(InputContract.TaskEntry._ID)),
                                cursor.getString(cursor.getColumnIndex(InputContract.TaskEntry.NO_TRANSAKSI)),
                                cursor.getString(cursor.getColumnIndex(InputContract.TaskEntry.TANGGAL)),
                                cursor.getString(cursor.getColumnIndex(InputContract.TaskEntry.PIC)),
                                cursor.getString(cursor.getColumnIndex(InputContract.TaskEntry.STATUS)),
                                cursor.getString(cursor.getColumnIndex(NAMA_RUANGAN)),
                                cursor.getString(cursor.getColumnIndex(NO_REFERENSI))
                        );


                        Cursor cursor_detail = db.getDetailKeluar(cursor.getString(cursor.getColumnIndex(InputContract.TaskEntry.NO_TRANSAKSI)));
                        if (cursor_detail.moveToFirst()) {
                            do {
                                saveDetail(
                                        cursor_detail.getString(cursor_detail.getColumnIndex(InputContract.TaskEntry.NO_TRANSAKSI)),
                                        cursor_detail.getString(cursor_detail.getColumnIndex(InputContract.TaskEntry.EPC))
                                );
                            } while (cursor_detail.moveToNext());
                        }

                    } while (cursor.moveToNext());
                }
            }
        }
    }

    private void saveHeader(final Integer id,final String no_transaksi, final String tanggal, final String pic, final String status, final String nama_ruangan, final String no_referensi) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, BASE_URL+ "linen_keluar",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                InputDbHelper db = new InputDbHelper(context);
                                db.updateFlagSyncKeluar(id);

                                //sending the broadcast to refresh the list
                                context.sendBroadcast(new Intent("id.coba.datasaved"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("no_transaksi", no_transaksi);
                params.put("tanggal", tanggal);
                params.put("pic", pic);
                params.put("status", "KIRIM");
                params.put("ruangan", nama_ruangan);
                params.put("no_referensi", no_referensi);
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    private void saveDetail(final String no_transaksi, final String epc) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, BASE_URL+ "linen_keluar_detail",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("no_transaksi", no_transaksi);
                params.put("epc", epc);
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

}
