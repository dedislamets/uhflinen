package id.coba.kotlinpintar;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.Fragment;

import com.BRMicro.Tools;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.uhf.api.cls.Reader.TAGINFO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import id.coba.kotlinpintar.Adapter.CariRoomAdapter;
import id.coba.kotlinpintar.Rest.Objek;

import static id.coba.kotlinpintar.InputDbHelper.ID_BARANG;
import static id.coba.kotlinpintar.InputDbHelper.TABLE_BARANG;
import static id.coba.kotlinpintar.Rest.ApiClient.BASE_URL;

public class Fragment_Register_Linen extends Fragment implements OnCheckedChangeListener,OnClickListener{
    private View view;// this fragment UI
    private TextView tvTagSum ;//tag sum text view
    private ListView lvEpc;// epc list view
    private Button btnStart ;//inventory button
    private Button btnClear ;
    private Button btnSimpan ;
    private Button btnSync ;
    private Button btnRemove ;
    private EditText edittext;
    private EditText noTransaksi;
    private EditText tgl;
    private CheckBox checkMulti ;
    private ArrayList<HashMap> listEpc;

    private Set<String> epcSet = null ; //store different EPC
//    private List<HashMap> listEpc = null;//EPC list
    private HashMap mapEpc; //store EPC position
    private EPCRegadapter adapter ;//epc list adapter

    private boolean isMulti = false ;// multi mode flag
    private int allCount = 0 ;// inventory count

    private long lastTime =  0L ;
    private InputDbHelper mHelper;
    private ArrayList<String> listSql = new ArrayList<String>();
    private ArrayAdapter adapterSql = null;
    private List kontenList = new ArrayList<>();
    final Calendar myCalendar = Calendar.getInstance();
    public Spinner spinner;
    public Spinner id_jenis;
    private SimpleCursorAdapter adapterRuangan ;
    private SimpleCursorAdapter adapterJenis ;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    String epc = msg.getData().getString("epc");
                    String rssi = msg.getData().getString("rssi");
                    String item = msg.getData().getString("item");
                    String status = msg.getData().getString("status");
                    if (epc == null || epc.length() == 0) {
                        return ;
                    }
                    int position ;
                    allCount++ ;

                    if (epcSet == null) {//first add
                        epcSet = new HashSet<String>();
//                      listEpc = new ArrayList<EpcDataModel>();
                        listEpc = new ArrayList<HashMap>();
                        mapEpc = new HashMap();

                        epcSet.add(epc);
                        mapEpc.put(epc, 0);
                        mapEpc.put("epc", epc);
                        mapEpc.put("rssi", rssi);
                        mapEpc.put("item", item);
                        mapEpc.put("status", status);
                        listEpc.add(mapEpc);
                        adapter = new EPCRegadapter(getActivity(), listEpc);
                        lvEpc.setAdapter(adapter);

                        Util.play(1, 0);
                        Activity_Register_Linen.mSetEpcs=epcSet;
                    }else{
                        if (epcSet.contains(epc)) {//set already exit
//                            position = mapEpc.get(epc);
//                            EpcDataModel epcOld = listEpc.get(position);
//                            listEpc.set(position, epcOld);
                        }else{

                            mapEpc = new HashMap();
                            epcSet.add(epc);
                            mapEpc.put(epc, listEpc.size());
                            mapEpc.put("epc", epc);
                            mapEpc.put("rssi", rssi);
                            mapEpc.put("item", item);
                            mapEpc.put("status", status);
                            listEpc.add(mapEpc);

                            Activity_Register_Linen.mSetEpcs = epcSet;
                            if(System.currentTimeMillis() - lastTime > 100){
                                lastTime = System.currentTimeMillis() ;
                                Util.play(1, 0);
                            }

                        }


                        tvTagSum.setText("" + listEpc.size());
                        adapter.notifyDataSetChanged();

                    }

                    break ;
            }
        }
    } ;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        Log.e("f1","create view");
        view= inflater.inflate(R.layout.insert_master_barang, null);
        initView();


        IntentFilter filter = new IntentFilter() ;
        filter.addAction("android.rfid.FUN_KEY");
        getActivity().registerReceiver(keyReceiver, filter) ;

        return view/*super.onCreateView(inflater, container, savedInstanceState)*/;
    }

    private void initView() {
        lvEpc = (ListView) view.findViewById(R.id.listView_data);
        btnStart = (Button) view.findViewById(R.id.button_start);
        tvTagSum = (TextView) view.findViewById(R.id.textView_tag) ;
        btnClear = (Button) view.findViewById(R.id.button_clear_epc) ;
        btnSimpan = (Button) view.findViewById(R.id.button_simpan) ;
        btnSync = (Button) view.findViewById(R.id.button_sync) ;
        btnRemove = (Button) view.findViewById(R.id.button_remove) ;
        edittext= (EditText) view.findViewById(R.id.tanggal);

        noTransaksi= (EditText) view.findViewById(R.id.no_transaksi);
        tgl= (EditText) view.findViewById(R.id.tanggal);
        spinner= (Spinner) view.findViewById(R.id.spinner1);
        id_jenis= (Spinner) view.findViewById(R.id.spinner2);

        lvEpc.setFocusable(false);
        lvEpc.setClickable(false);
        lvEpc.setItemsCanFocus(false);
        lvEpc.setScrollingCacheEnabled(false);
        lvEpc.setOnItemClickListener(null);
        btnStart.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnSimpan.setOnClickListener(this);
        btnSync.setOnClickListener(this);
        btnRemove.setOnClickListener(this);
        edittext.setOnClickListener(this);

        mHelper = new InputDbHelper(getActivity());

        updateLabel();
        loadspinner();

        String no = getArguments().getString("no_transaksi");

        if (no != null){
            updateUI(no);
        }

    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
//		Log.e("f1","destroy view");
        if (isStart) {
            isStart = false;
            isRunning = false;
            Activity_Register_Linen.mUhfrManager.stopTagInventory();
        }
        getActivity().unregisterReceiver(keyReceiver);//
    }
    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
//		Log.e("f1","pause");
        if (isStart) {
            runInventory();
        }
    }
    private boolean f1hidden = false;
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        f1hidden = hidden;
//		Log.e("hidden", "hide"+hidden) ;
        if (hidden) {
            if (isStart) runInventory();// stop inventory
        }
        if (Activity_Register_Linen.mUhfrManager!=null) Activity_Register_Linen.mUhfrManager.setCancleInventoryFilter();
    }


    private boolean isRunning = false ;
    private boolean isStart = false ;
    String epc ;
    private Runnable inventoryTask = new Runnable() {
        @Override
        public void run() {
            while(isRunning){
                if (isStart) {
                    List<TAGINFO> list1 ;
                    if (isMulti) { // multi mode
                        list1 = Activity_Register_Linen.mUhfrManager.tagInventoryRealTime();
                    }else{
                        //sleep can save electricity
//						try {
//							Thread.sleep(250);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
                        list1 = Activity_Register_Linen.mUhfrManager.tagInventoryByTimer((short)50);

                    }
                    if (list1 != null&&list1.size()>0) {//
                        for (TAGINFO tfs:list1) {
                            byte[] epcdata = tfs.EpcId;
                            epc = Tools.Bytes2HexString(epcdata, epcdata.length);
                            int rssi = tfs.RSSI;

                            SQLiteDatabase db = mHelper.getReadableDatabase();
                            String whereClause = InputDbHelper.SERIAL +"=?";
                            String [] whereArgs = {epc};

                            Cursor cursor_header = db.query(TABLE_BARANG,
                                    new String[]{
                                            InputDbHelper.SERIAL},
                                    whereClause,
                                    whereArgs,
                                    null,
                                    null,
                                    null);
                            int ii = 0;
                            while (cursor_header.moveToNext()) {
                                int id_serial = cursor_header.getColumnIndex(InputDbHelper.SERIAL);
                                String no_serial = cursor_header.getString(id_serial);

                                Message msg = new Message() ;
                                msg.what = 1 ;
                                Bundle b = new Bundle();
                                b.putString("epc", epc);
                                b.putString("status", "Terdaftar");
                                msg.setData(b);
                                handler.sendMessage(msg);
                            }

                            if(cursor_header.getCount()<=0){
                                Message msg = new Message() ;
                                msg.what = 1 ;
                                Bundle b = new Bundle();
                                b.putString("epc", epc);
                                b.putString("status", "-");
                                msg.setData(b);
                                handler.sendMessage(msg);
                            }


                        }
                    }
                    //inventory epc + tid
//                    if (list1 != null && list1.size() > 0) {
//                        for (TAGINFO tfs : list1) {
//                            byte[] epcdata = tfs.EpcId;
//                            epc = Tools.Bytes2HexString(epcdata, epcdata.length);
//                            int rssi = tfs.RSSI;
//                            String tid = Tools.Bytes2HexString(tfs.EmbededData, tfs.EmbededDatalen);
//                            Log.e("Huang, Fragment1", "tid = " + tid);
//                            Message msg = new Message();
//                            msg.what = 1;
//                            Bundle b = new Bundle();
//                            b.putString("epc", epc);
////                            b.putString("rssi", rssi + "");
//                            msg.setData(b);
//                            handler.sendMessage(msg);
//                        }
//                    }
                }
            }
        }
    } ;
    private boolean keyControl = true;
    private void runInventory() {
        if (keyControl) {
            keyControl = false;
            if (!isStart) {
                Activity_Register_Linen.mUhfrManager.setCancleInventoryFilter();
                isRunning = true;
                if (isMulti) {
                    Activity_Register_Linen.mUhfrManager.setFastMode();
                    Activity_Register_Linen.mUhfrManager.asyncStartReading();
                }else {
                    Activity_Register_Linen.mUhfrManager.setCancleFastMode();
                }
                new Thread(inventoryTask).start();
                btnStart.setText(getResources().getString(R.string.stop_inventory_epc));
                isStart = true;
            } else {
                if (isMulti)
                    Activity_Register_Linen.mUhfrManager.asyncStopReading();
                else
                    Activity_Register_Linen.mUhfrManager.stopTagInventory();
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isRunning = false;
                btnStart.setText(getResources().getString(R.string.start_inventory_epc));
                isStart = false;
            }
            keyControl = true;
        }
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // TODO Auto-generated method stub
        if (isChecked) isMulti = true;
        else isMulti = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_start:
                runInventory() ;
                break ;
            case R.id.button_clear_epc:
                clearEpc();
                break ;
            case R.id.button_sync:
                updateUI("");
                showToast("Sync sukses.");
                break ;
            case R.id.button_remove:

                break ;
            case R.id.tanggal:
                showTanggal();
                break;
            case R.id.button_simpan:

                final String tanggal = ((EditText) getActivity().findViewById(R.id.tanggal)).getText().toString();
                Cursor qc = adapterRuangan.getCursor();
                final String ruang = qc.getString(qc.getColumnIndex("nama_ruangan"));
                Cursor qjenis = adapterJenis.getCursor();
                final String id_jenis = qjenis.getString(qjenis.getColumnIndex("id_jenis"));

//                Toast.makeText(getActivity(),ruang,Toast.LENGTH_SHORT).show();

//                if(TextUtils.isEmpty(pic)){
//                    Toast.makeText(getActivity(),"PIC Kosong",Toast.LENGTH_SHORT).show();
//                    break;
//                }

                if(lvEpc.getCount() <=0){
                    Toast.makeText(getActivity(),"Belum ada data linen di scan.!",Toast.LENGTH_SHORT).show();
                    break;
                }


                SQLiteDatabase db = mHelper.getWritableDatabase();
                View parentView = null;


                for (int i = 0; i < lvEpc.getCount(); i++) {
                    parentView = getViewByPosition(i, lvEpc);

                    final String epcName = ((TextView) parentView.findViewById(R.id.textView_epc)).getText().toString();
                    final String status = ((TextView) parentView.findViewById(R.id.status_epc)).getText().toString();

                    if(status != "Terdaftar") {
                        ContentValues values = new ContentValues();
                        values.put(InputDbHelper.SERIAL, epcName);
                        values.put(InputDbHelper.TANGGAL, tanggal);
                        values.put(InputDbHelper.NAMA_RUANGAN, ruang);
                        values.put(InputDbHelper.ID_JENIS, id_jenis);

                        db.insertWithOnConflict(TABLE_BARANG, null, values, SQLiteDatabase.CONFLICT_REPLACE);

                        StringRequest request = new StringRequest(
                                Request.Method.POST,
                                BASE_URL + "barang",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            String status_kirim = jsonObject.getString("status");
                                            if (status_kirim.equals("200")) {
                                                Toast.makeText(getActivity(), "Data $epcName tersimpan", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getActivity(), "Data gagal", Toast.LENGTH_SHORT).show();
                                    }
                                }
                        ) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("serial", epcName);
                                params.put("tanggal_register", tanggal);
                                params.put("nama_ruangan", ruang);
                                params.put("id_jenis", id_jenis);
                                return params;
                            }
                        };

                        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                        requestQueue.add(request);
                    }
                }

                db.close();
                showToast("Sukses menyimpan..");
                Intent intent = new Intent(getActivity(), ListRegisterLinen.class);
                startActivity(intent);
                break ;
        }
    }
    private void showTanggal(){
        new DatePickerDialog(getActivity(), date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }
    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        edittext.setText(sdf.format(myCalendar.getTime()));
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition
                + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    private void updateUI(String no ) {
        epcSet = new HashSet<String>();
//                        listEpc = new ArrayList<EpcDataModel>();
        listEpc = new ArrayList<HashMap>();

        List<String> myList = new ArrayList<String>();
        SQLiteDatabase db = mHelper.getReadableDatabase();

        String noTrans = noTransaksi.getText().toString();
        btnSimpan.setVisibility(View.VISIBLE);
        btnStart.setVisibility(View.VISIBLE);
        btnClear.setVisibility(View.VISIBLE);

        if(no != ""){
            noTrans = no;
            btnSimpan.setVisibility(View.GONE);
            btnStart.setVisibility(View.GONE);
            btnClear.setVisibility(View.GONE);
        }

        String whereClause = InputContract.TaskEntry.NO_TRANSAKSI+"=?";
        String [] whereArgs = {noTrans};

//        Cursor cursor_header = db.query(InputContract.TaskEntry.TABLE,
//                new String[]{InputContract.TaskEntry._ID, InputContract.TaskEntry.NO_TRANSAKSI, InputContract.TaskEntry.TANGGAL, InputContract.TaskEntry.PIC},
//                whereClause,
//                whereArgs,
//                null,
//                null,
//                null);
//        int ii = 0;
//        while (cursor_header.moveToNext()) {
//            int id_no_transaksi = cursor_header.getColumnIndex(InputContract.TaskEntry.NO_TRANSAKSI);
//            int id_tanggal = cursor_header.getColumnIndex(InputContract.TaskEntry.TANGGAL);
//            int id_pic = cursor_header.getColumnIndex(InputContract.TaskEntry.PIC);
//
//            String no_transaksi = cursor_header.getString(id_no_transaksi);
//            String tanggal = cursor_header.getString(id_tanggal);
//            String pic = cursor_header.getString(id_pic);
//
//            noTransaksi.setText(no_transaksi);
//            tgl.setText(tanggal);
//
//        }
//
//        String [] whereArgsDetail = {noTrans};
//        Cursor cursor = db.query(InputContract.TaskEntry.TABLE_DETAIL,
//                new String[]{InputContract.TaskEntry._ID, InputContract.TaskEntry.EPC, InputContract.TaskEntry.ITEM, InputContract.TaskEntry.ROOM},
//                InputContract.TaskEntry.NO_TRANSAKSI+"=?",
//                whereArgsDetail,
//                null,
//                null,
//                null);
//         int i = 0;
//        while (cursor.moveToNext()) {
//            int id = cursor.getColumnIndex(InputContract.TaskEntry._ID);
//            int epc = cursor.getColumnIndex(InputContract.TaskEntry.EPC);
//            int item = cursor.getColumnIndex(InputContract.TaskEntry.ITEM);
//            int room = cursor.getColumnIndex(InputContract.TaskEntry.ROOM);
//
//            long thisId = cursor.getLong(id);
//            String epcs = cursor.getString(epc);
//            String barang = cursor.getString(item);
//            String ruangan = cursor.getString(room);
//
//            mapEpc = new HashMap();
//            epcSet.add(epcs);
//            mapEpc.put(epcs, i);
//            mapEpc.put("epc", epcs);
//            mapEpc.put("rssi", ruangan);
//            mapEpc.put("item", barang);
//            listEpc.add(mapEpc);
//            i++;
//            tvTagSum.setText( String.valueOf(i) );
//        }
//
//
//        adapter = new EPCRegadapter(getActivity(), listEpc);
//        lvEpc.setAdapter(adapter);
//
//        cursor.close();
//        db.close();
    }

    private void clearEpc(){
        if (epcSet != null) {
            epcSet.removeAll(epcSet); //store different EPC
        }
        if(listEpc != null)
            listEpc.removeAll(listEpc);//EPC list
        if(mapEpc != null)
            mapEpc.clear(); //store EPC position
        if(adapter != null)
            adapter.notifyDataSetChanged();
        allCount = 0 ;
        tvTagSum.setText("0");
        Activity_Register_Linen.mSetEpcs.clear();
    }


    private Toast toast;
    private void showToast(String info) {
        if (toast==null) toast =  Toast.makeText(getActivity(), info, Toast.LENGTH_SHORT);
        else toast.setText(info);
        toast.show();
    }

    private  long startTime = 0 ;
    private boolean keyUpFalg= true;
    private BroadcastReceiver keyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (f1hidden) return;
            int keyCode = intent.getIntExtra("keyCode", 0) ;
            if(keyCode == 0){//H941
                keyCode = intent.getIntExtra("keycode", 0) ;
            }
//            Log.e("key ","keyCode = " + keyCode) ;
            boolean keyDown = intent.getBooleanExtra("keydown", false) ;
//			Log.e("key ", "down = " + keyDown);
            if(keyUpFalg&&keyDown && System.currentTimeMillis() - startTime > 500){
                keyUpFalg = false;
                startTime = System.currentTimeMillis() ;
                if ( (keyCode == KeyEvent.KEYCODE_F1 || keyCode == KeyEvent.KEYCODE_F2
                        || keyCode == KeyEvent.KEYCODE_F3 || keyCode == KeyEvent.KEYCODE_F4 ||
                        keyCode == KeyEvent.KEYCODE_F5)) {
//                Log.e("key ","inventory.... " ) ;
                    runInventory();
                }
                return ;
            }else if (keyDown){
                startTime = System.currentTimeMillis() ;
            }else {
                keyUpFalg = true;
            }

        }
    } ;

    private void loadspinner() {
        InputDbHelper db = new InputDbHelper(getActivity());
        final Cursor serviceCursor = db.getServiceCursor();
        String[] from = {"nama_ruangan"};
        int[] to = {android.R.id.text1};
        adapterRuangan = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, serviceCursor, from, to, 0){
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinner.setAdapter(adapterRuangan);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//                Object valueID = parent.getSelectedItemId();
//                edtSp1.setText(String.valueOf(valueID));
//
//                Cursor qc = adapterRuangan.getCursor();
//                POST_NAMA_PERANGKAT = qc.getString(qc.getColumnIndex("nama_perangkat"));
//                edtSp2.setText(POST_NAMA_PERANGKAT);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String[] from_jenis = {"jenis"};
        final Cursor jenisCursor = db.getJenisBarang();
        adapterJenis = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, jenisCursor, from_jenis, to, 0){
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        id_jenis.setAdapter(adapterJenis);
        id_jenis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
