package id.coba.kotlinpintar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.BRMicro.Tools;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.uhf.api.cls.Reader.TAGINFO;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
//import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.text.DecimalFormat;

import static id.coba.kotlinpintar.InputDbHelper.BERAT;
import static id.coba.kotlinpintar.InputDbHelper.JENIS_INFEKSIUS;
import static id.coba.kotlinpintar.InputDbHelper.KATEGORI;
import static id.coba.kotlinpintar.InputDbHelper.KELUAR;
import static id.coba.kotlinpintar.InputDbHelper.KOTOR;
import static id.coba.kotlinpintar.InputDbHelper.TABLE_BARANG;
import static id.coba.kotlinpintar.InputDbHelper.TABLE_BERSIH_DETAIL;
import static id.coba.kotlinpintar.InputDbHelper.TABLE_JENIS_BARANG;
import static id.coba.kotlinpintar.InputDbHelper.TABLE_KELUAR_DETAIL;
import static id.coba.kotlinpintar.InputDbHelper.TOTAL_BERAT;
import static id.coba.kotlinpintar.InputDbHelper.TOTAL_BERAT_REAL;
import static id.coba.kotlinpintar.InputDbHelper.TOTAL_QTY;
import static id.coba.kotlinpintar.Rest.ApiClient.BASE_URL;

public class Fragment_Kotor extends Fragment implements OnCheckedChangeListener,OnClickListener{
    private View view;// this fragment UI
    private TextView tvTagSum ;
    private TextView tvTagSumBerat ;
    private EditText tvTagSumBeratReal ;
    private ListView lvEpc;// epc list view
    private Button btnStart ;//inventory button
    private Button btnClear ;// clear button
    private Button btnSimpan ;
    private Button btnSync ;
    private Button btnRemove ;
    private EditText edittext;
    private EditText txtScan;
    private EditText noTransaksi;
    private EditText tgl;
    private Spinner spinner_pic;
    private Spinner spinner_kategori;
    private Spinner spinner_infeksius;
    private EditText person;
    private CheckBox checkMulti ;//multi model check box
    private ArrayList<HashMap> listEpc;
    private LinearLayout lscan;

    private Set<String> epcSet = null ;
//    private List<HashMap> listEpc = null;//EPC list
    private HashMap mapEpc;
    private EPCustadapter adapter ;

    private boolean isMulti = false ;
    private int allCount = 0 ;

    private long lastTime =  0L ;
    private InputDbHelper mHelper;
    private ArrayList<String> listSql = new ArrayList<String>();
    private ArrayAdapter adapterSql = null;
    private List kontenList = new ArrayList<>();
    final Calendar myCalendar = Calendar.getInstance();
    private Toolbar toolbar;
    private SimpleCursorAdapter adapterPIC ;
    private SimpleCursorAdapter adapterKategori ;
    private SimpleCursorAdapter adapterInfeksius ;
    private SharedPreferences prefMode;
    //handler
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    String epc = msg.getData().getString("epc");
                    String rssi = msg.getData().getString("rssi");
                    String item = msg.getData().getString("item");
                    String berat = msg.getData().getString("berat");
                    String exist = msg.getData().getString("exist");
                    if (epc == null || epc.length() == 0) {
                        return ;
                    }
                    int position ;
                    allCount++ ;

                    if (berat == null) {
                        berat = "0";
                    }
                    if (item == null) {
                        item = "Tidak Terdaftar!";
                    }

                    if (epcSet == null) {//first add
                        epcSet = new HashSet<String>();
//                        listEpc = new ArrayList<EpcDataModel>();
                        listEpc = new ArrayList<HashMap>();
                        mapEpc = new HashMap();

                        epcSet.add(epc);
                        mapEpc.put(epc, 0);
                        mapEpc.put("epc", epc);
                        mapEpc.put("rssi", rssi);
                        mapEpc.put("item", item);
                        mapEpc.put("berat", berat);
                        mapEpc.put("exist", exist);

                        listEpc.add(mapEpc);

                        hitungBerat(berat);
//                        epcSet.add(epc);
//                        mapEpc.put(epc, 0);
//                        EpcDataModel epcTag = new EpcDataModel() ;
//                        epcTag.setepc(epc);
//                        epcTag.setrssi(rssi);
//                        listEpc.add(epcTag);
//                        adapter = new EPCadapter(getActivity(), listEpc);
//                        lvEpc.setAdapter(adapter);

                         adapter = new EPCustadapter(getActivity(), listEpc);
                         lvEpc.setAdapter(adapter);

                        Util.play(1, 0);
                        KotorActivity.mSetEpcs=epcSet;
                    }else{
                        if (epcSet.contains(epc.replace("\n",""))) {//set already exit
//                            position = mapEpc.get(epc);
//                            EpcDataModel epcOld = listEpc.get(position);
//                            listEpc.set(position, epcOld);
                        }else{
//                            epcSet.add(epc);
//                            mapEpc.put(epc, listEpc.size());
//                            EpcDataModel epcTag = new EpcDataModel() ;
//                            epcTag.setepc(epc);
//                            epcTag.setrssi(rssi);
//                            listEpc.add(epcTag);
                            mapEpc = new HashMap();
                            epcSet.add(epc);
                            mapEpc.put(epc, listEpc.size());
                            mapEpc.put("epc", epc);
                            mapEpc.put("rssi", rssi);
                            mapEpc.put("item", item);
                            mapEpc.put("berat", berat);
                            mapEpc.put("exist", exist);

                            listEpc.add(mapEpc);

                            hitungBerat(berat);

                            KotorActivity.mSetEpcs = epcSet;
                            if(System.currentTimeMillis() - lastTime > 100){
                                lastTime = System.currentTimeMillis() ;
                                Util.play(1, 0);
                            }

                        }
                        tvTagSum.setText("" + listEpc.size());
                        adapter.notifyDataSetChanged();
                        txtScan.setText("");
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
        view= inflater.inflate(R.layout.fragment_kotor, null);
        prefMode = getContext().getSharedPreferences("MODE", Context.MODE_PRIVATE);
        initView();


        IntentFilter filter = new IntentFilter() ;
        filter.addAction("android.rfid.FUN_KEY");
        getActivity().registerReceiver(keyReceiver, filter) ;

        return view/*super.onCreateView(inflater, container, savedInstanceState)*/;
    }

    private void hitungBerat(String berat){
        if(berat == null){
            berat = "0";
        }
        double tb =Float.parseFloat(tvTagSumBerat.getText().toString());
        Float litersOfPetrol=Float.parseFloat(berat);
        tb += litersOfPetrol;
        tvTagSumBerat.setText( String.format("%.1f", tb) );
        tvTagSumBeratReal.setText( String.format("%.1f", tb) );
    }

    private void initView() {
        lvEpc = (ListView) view.findViewById(R.id.listView_data);
        btnStart = (Button) view.findViewById(R.id.button_start);
        tvTagSum = (TextView) view.findViewById(R.id.textView_tag) ;
        tvTagSumBerat = (TextView) view.findViewById(R.id.textView_total_berat) ;
        tvTagSumBeratReal = (EditText) view.findViewById(R.id.textView_total_berat_real) ;
        btnClear = (Button) view.findViewById(R.id.button_clear_epc) ;
        btnSimpan = (Button) view.findViewById(R.id.button_simpan) ;
        btnSync = (Button) view.findViewById(R.id.button_sync) ;
        btnRemove = (Button) view.findViewById(R.id.button_remove) ;
        edittext= (EditText) view.findViewById(R.id.tanggal);
        spinner_pic= view.findViewById(R.id.spinner_pic);
        spinner_kategori= view.findViewById(R.id.spinner_kategori);
        spinner_infeksius= view.findViewById(R.id.spinner_infeksius);
        txtScan= (EditText) view.findViewById(R.id.textView_scan);
        lscan = (LinearLayout) view.findViewById(R.id.lscan) ;

        noTransaksi= (EditText) view.findViewById(R.id.no_transaksi);
        setAutoNumber();

        tgl= (EditText) view.findViewById(R.id.tanggal);
//        person= (EditText) view.findViewById(R.id.pic);

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
        txtScan.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_ENTER)) {

                    String epc = txtScan.getText().toString().replace("\n","");
                    Message msg = new Message() ;
                    msg.what = 1 ;
                    Bundle b = new Bundle();
                    b.putString("epc", epc);
                    b.putString("exist", "0");

                    try {
                        SQLiteDatabase db = mHelper.getReadableDatabase();
                        String selectQuery = "SELECT A.*,B.jenis,B.berat FROM " + TABLE_BARANG + " A LEFT JOIN " + TABLE_JENIS_BARANG + " B ON A.ID_JENIS=B.ID_JENIS WHERE serial='" + epc + "'";
                        Cursor cursor_header = db.rawQuery(selectQuery, null);
                        int count = cursor_header.getCount();
                        if (count > 0) {
                            while (cursor_header.moveToNext()) {
                                String ruangan = cursor_header.getString(cursor_header.getColumnIndex(InputDbHelper.NAMA_RUANGAN));
                                String jenis = cursor_header.getString(cursor_header.getColumnIndex(InputDbHelper.JENIS));
                                String berat = cursor_header.getString(cursor_header.getColumnIndex(InputDbHelper.BERAT));

                                b.putString("rssi", ruangan);
                                b.putString("item", jenis);
                                b.putString("berat", berat);

                                String exist = "";
                                selectQuery = "SELECT b.* FROM linen_kotor a " +
                                        "JOIN linen_kotor_detail b ON a.transaksi=b.transaksi " +
                                        "WHERE STATUS='CUCI' AND epc='" + epc + "' AND a.transaksi<>'" + noTransaksi.getText().toString() + "'";
                                Cursor cursor_exist = db.rawQuery(selectQuery, null);
                                while (cursor_exist.moveToNext()) {
                                    exist = "1";
                                }
                                b.putString("exist", exist);
                            }
                        }else{
                            String ruangan = "-";
                            String jenis = "Tidak Terdaftar!";
                            String berat = "0";

                            b.putString("rssi", ruangan);
                            b.putString("item", jenis);
                            b.putString("berat", berat);
                            b.putString("exist", "");
                        }
                        msg.setData(b);
                        handler.sendMessage(msg);

                    }catch (Exception ex) {
                    }
                }
                return false;
            }
        });

        if(prefMode.getBoolean("MODE", false) == true){
            lscan.setVisibility(View.GONE);
            btnStart.setEnabled(true);
        }else{
            btnStart.setEnabled(false);
        }

        mHelper = new InputDbHelper(getActivity());
        tvTagSumBerat.setText("0");
        tvTagSum.setText("0");

        updateLabel();

        loadspinner();
        String no = getArguments().getString("no_transaksi");

        if (no != null){
            updateUI(no);
        }
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Form Linen Kotor");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

    }
    public static void showKeyboard(Activity activity) {
        if (activity != null) {
            activity.getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null) {
            activity.getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }
    private void loadspinner() {

        InputDbHelper db = new InputDbHelper(getActivity());
        final Cursor serviceCursor = db.getPICCursor();
        String[] from = {"nama_user"};
        int[] to = {android.R.id.text1};
        adapterPIC = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, serviceCursor, from, to, 0);
        adapterPIC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_pic.setAdapter(adapterPIC);

        final Cursor kategoriCursor = db.getKategoriCursor();
        String[] from_kategori = {"kategori"};
        int[] to_kategori = {android.R.id.text1};
        adapterKategori = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, kategoriCursor, from_kategori, to_kategori, 0);
        adapterKategori.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_kategori.setAdapter(adapterKategori);

        final Cursor infeksiusCursor = db.getInfeksiusCursor();
        String[] from_infeksius = {"infeksius"};
        int[] to_infeksius = {android.R.id.text1};
        adapterInfeksius = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, infeksiusCursor, from_infeksius, to_infeksius, 0);
        adapterInfeksius.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_infeksius.setAdapter(adapterInfeksius);

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
            KotorActivity.mUhfrManager.stopTagInventory();
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
        if (KotorActivity.mUhfrManager!=null) KotorActivity.mUhfrManager.setCancleInventoryFilter();
    }


    private boolean isRunning = false ;
    private boolean isStart = false ;
    String epc ;
    //inventory epc
    private Runnable inventoryTask = new Runnable() {
        @Override
        public void run() {
            while(isRunning){
                if (isStart) {
                    List<TAGINFO> list1 ;
                    if (isMulti) { // multi mode
                        list1 = KotorActivity.mUhfrManager.tagInventoryRealTime();
                    }else{
                        //sleep can save electricity
//						try {
//							Thread.sleep(250);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
                        list1 = KotorActivity.mUhfrManager.tagInventoryByTimer((short)50);
                        //inventory epc + tid
//                        list1 = KotorActivity.mUhfrManager.tagEpcTidInventoryByTimer((short) 50);
                    }
                    if (list1 != null&&list1.size()>0) {//
                        for (TAGINFO tfs:list1) {
                            byte[] epcdata = tfs.EpcId;
                            epc = Tools.Bytes2HexString(epcdata, epcdata.length);
                            int rssi = tfs.RSSI;

                            Message msg = new Message() ;
                            msg.what = 1 ;
                            Bundle b = new Bundle();
                            b.putString("epc", epc);
                            b.putString("exist", "0");

                            SQLiteDatabase db = mHelper.getReadableDatabase();
//                            String whereClause = InputDbHelper.SERIAL +"=?";
//                            String [] whereArgs = {epc};
//
//                            Cursor cursor_header = db.query(TABLE_BARANG,
//                                    new String[]{
//                                            InputDbHelper.SERIAL},
//                                    whereClause,
//                                    whereArgs,
//                                    null,
//                                    null,
//                                    null);
                            try {
                                String selectQuery = "SELECT A.*,B.jenis,B.berat FROM " + TABLE_BARANG + " A LEFT JOIN " + TABLE_JENIS_BARANG + " B ON A.ID_JENIS=B.ID_JENIS WHERE serial='" + epc + "'";
                                Cursor cursor_header = db.rawQuery(selectQuery, null);
                                int count = cursor_header.getCount();
                                if (count > 0) {
                                    while (cursor_header.moveToNext()) {
                                        String ruangan = cursor_header.getString(cursor_header.getColumnIndex(InputDbHelper.NAMA_RUANGAN));
                                        String jenis = cursor_header.getString(cursor_header.getColumnIndex(InputDbHelper.JENIS));
                                        String berat = cursor_header.getString(cursor_header.getColumnIndex(InputDbHelper.BERAT));

                                        b.putString("rssi", ruangan);
                                        b.putString("item", jenis);
                                        b.putString("berat", berat);

                                        String exist = "";
                                        selectQuery = "SELECT b.* FROM linen_kotor a " +
                                                "JOIN linen_kotor_detail b ON a.transaksi=b.transaksi " +
                                                "WHERE STATUS='CUCI' AND epc='" + epc + "' AND a.transaksi<>'" + noTransaksi.getText().toString() + "'";
                                        Cursor cursor_exist = db.rawQuery(selectQuery, null);
                                        while (cursor_exist.moveToNext()) {
                                            exist = "1";
                                        }
                                        b.putString("exist", exist);
                                    }
                                }else{
                                    String ruangan = "-";
                                    String jenis = "Tidak Terdaftar!";
                                    String berat = "0";

                                    b.putString("rssi", ruangan);
                                    b.putString("item", jenis);
                                    b.putString("berat", berat);
                                    b.putString("exist", "");
                                }
                                msg.setData(b);
                                handler.sendMessage(msg);
                            }catch (Exception ex) {
//                                Toast.makeText(getActivity(),ex.getMessage().toString(),Toast.LENGTH_LONG);
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
                if(KotorActivity.mUhfrManager == null){
                    showToast("Fungsi ini berlaku hanya untuk handheld");
                }else{

                    KotorActivity.mUhfrManager.setCancleInventoryFilter();
                    isRunning = true;
                    if (isMulti) {
                        KotorActivity.mUhfrManager.setFastMode();
                        KotorActivity.mUhfrManager.asyncStartReading();
                    }else {
                        KotorActivity.mUhfrManager.setCancleFastMode();
                    }

                    new Thread(inventoryTask).start();
                    btnStart.setText(getResources().getString(R.string.stop_inventory_epc));
                    isStart = true;
                }

            } else {
                if (isMulti)
                    KotorActivity.mUhfrManager.asyncStopReading();
                else
                    KotorActivity.mUhfrManager.stopTagInventory();
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
                deleteAll();
                break ;
            case R.id.tanggal:
                showTanggal();
                break;
            case R.id.button_simpan:
                String no_transaksi = ((EditText) getActivity().findViewById(R.id.no_transaksi)).getText().toString();
                String tanggal = ((EditText) getActivity().findViewById(R.id.tanggal)).getText().toString();
                String berat = tvTagSumBerat.getText().toString();
                String berat_real = tvTagSumBeratReal.getText().toString();
                String qty = tvTagSum.getText().toString();

                Cursor qc = adapterPIC.getCursor();
                final String pic = qc.getString(qc.getColumnIndex("nama_user"));

                Cursor qc_kategori = adapterKategori.getCursor();
                final String kategori = qc_kategori.getString(qc_kategori.getColumnIndex("kategori"));

                Cursor qc_infeksius = adapterInfeksius.getCursor();
                final String infeksius = qc_infeksius.getString(qc_infeksius.getColumnIndex("infeksius"));

                if(TextUtils.isEmpty(no_transaksi)){
                    Toast.makeText(getActivity(),"PIC Kosong",Toast.LENGTH_SHORT).show();
                    break;
                }
                if(lvEpc.getCount() <=0){
                    Toast.makeText(getActivity(),"Belum ada data linen di scan.!",Toast.LENGTH_SHORT).show();
                    break;
                }
                if(berat_real.equals("")){
                    Toast.makeText(getActivity(),"Berat Timbang harus > 0",Toast.LENGTH_SHORT).show();
                    break;
                }
                if(berat_real == ""){
                    Toast.makeText(getActivity(),"Berat Timbang tidak boleh kosong",Toast.LENGTH_SHORT).show();
                    break;
                }

                Boolean exist = false;
                SQLiteDatabase db = mHelper.getReadableDatabase();
                for (HashMap num : listEpc) {
                    if (num.get("item").equals("Tidak Terdaftar!")){
                        exist = true;
                        Toast.makeText(getActivity(),"Serial " + num.get("epc") + " belum terdaftar..!",Toast.LENGTH_SHORT).show();
                    }
                    if (num.get("exist").equals("1")) {
                        exist = true;
                        Toast.makeText(getActivity(), "Serial " + num.get("epc") + " belum di proses di transaksi lain.!", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    InputDbHelper db_store = new InputDbHelper(getActivity());
                    Cursor cursor_exist_bersih = db_store.getLastHistory( num.get("epc").toString());
                    while (cursor_exist_bersih.moveToNext()) {

                        int no = cursor_exist_bersih.getColumnIndex("transaksi");
                        int i_status = cursor_exist_bersih.getColumnIndex("FLAG");
                        int i_currenttime = cursor_exist_bersih.getColumnIndex("timestamp");

                        String noTransaksi = cursor_exist_bersih.getString(no);
                        String status = cursor_exist_bersih.getString(i_status);
                        String timestamp = cursor_exist_bersih.getString(i_currenttime);
                        if(!status.equals("keluar")){
                            exist = true;
                            Toast.makeText(getActivity(),"Serial " + num.get("epc") + " berstatus " + status + " di transaksi " + noTransaksi  + "..!",Toast.LENGTH_SHORT).show();
                        }

                        break;
                    }

                }
                if(exist) {
                    break;
                }

                View parentView = null;

                ContentValues values_header = new ContentValues();

                values_header.put(InputContract.TaskEntry.NO_TRANSAKSI,   no_transaksi);
                values_header.put(InputContract.TaskEntry.TANGGAL,   tanggal);
                values_header.put(InputContract.TaskEntry.PIC,   pic);
                values_header.put(InputContract.TaskEntry.STATUS,   "CUCI");
                values_header.put(KATEGORI,   kategori);
                values_header.put(JENIS_INFEKSIUS,   infeksius);
                values_header.put(TOTAL_QTY,   qty);
                values_header.put(TOTAL_BERAT,   berat);
                values_header.put(TOTAL_BERAT_REAL,   berat_real);
                values_header.put(InputContract.TaskEntry.CURRENT_INSERT, getDateTime());

                db.insertWithOnConflict(InputContract.TaskEntry.TABLE, null, values_header, SQLiteDatabase.CONFLICT_REPLACE);


                for (int i = 0; i < lvEpc.getCount(); i++) {
                    parentView = getViewByPosition(i, lvEpc);

                    String epcName = ((TextView) parentView.findViewById(R.id.textView_epc)).getText().toString();
                    String barang = ((TextView) parentView.findViewById(R.id.textView_barang)).getText().toString();
                    String ruangan = ((TextView) parentView.findViewById(R.id.textView_rssi)).getText().toString();

                    ContentValues values = new ContentValues();
                    values.put(InputContract.TaskEntry.NO_TRANSAKSI,   no_transaksi);
                    values.put(InputContract.TaskEntry.EPC,   epcName);
                    values.put(InputContract.TaskEntry.ITEM,   barang);
                    values.put(InputContract.TaskEntry.ROOM,   ruangan);
                    values.put(InputContract.TaskEntry.CURRENT_INSERT, getDateTime());

                    db.insertWithOnConflict(InputContract.TaskEntry.TABLE_DETAIL, null, values, SQLiteDatabase.CONFLICT_REPLACE);

                    ContentValues values_keluar = new ContentValues();
                    values_keluar.put(KOTOR,   1);

                    db.update(TABLE_KELUAR_DETAIL,
                            values_keluar,
                            InputContract.TaskEntry.EPC + "=" + epc + " and " + KOTOR + "=0",
                            null);
                }

                db.close();
                showToast("Sukses menyimpan..");
                syncOnlineDB();
                Intent intent = new Intent(getActivity(), ListKotorActivity.class);
                startActivity(intent);
                break ;
        }
    }

    public String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void syncOnlineDB(){
        InputDbHelper db = new InputDbHelper(getActivity());
        Cursor cursor = db.getUnsyncedNames();
        if (cursor.moveToFirst()) {
            do {

                saveHeader(
                        cursor.getInt(cursor.getColumnIndex(InputContract.TaskEntry._ID)),
                        cursor.getString(cursor.getColumnIndex(InputContract.TaskEntry.NO_TRANSAKSI)),
                        cursor.getString(cursor.getColumnIndex(InputContract.TaskEntry.TANGGAL)),
                        cursor.getString(cursor.getColumnIndex(InputContract.TaskEntry.PIC)),
                        cursor.getString(cursor.getColumnIndex(InputContract.TaskEntry.STATUS)),
                        cursor.getString(cursor.getColumnIndex(KATEGORI)),
                        cursor.getString(cursor.getColumnIndex(JENIS_INFEKSIUS)),
                        cursor.getString(cursor.getColumnIndex(TOTAL_BERAT)),
                        cursor.getString(cursor.getColumnIndex(TOTAL_BERAT_REAL)),
                        cursor.getString(cursor.getColumnIndex(TOTAL_QTY))
                );


                Cursor cursor_detail = db.getDetail(cursor.getString(cursor.getColumnIndex(InputContract.TaskEntry.NO_TRANSAKSI)));
                if (cursor_detail.moveToFirst()) {
                    do {
                        saveDetail(
                                cursor_detail.getString(cursor_detail.getColumnIndex(InputContract.TaskEntry.NO_TRANSAKSI)),
                                cursor_detail.getString(cursor_detail.getColumnIndex(InputContract.TaskEntry.EPC)),
                                cursor_detail.getString(cursor_detail.getColumnIndex(InputContract.TaskEntry.ROOM))
                        );
                    } while (cursor_detail.moveToNext());
                }

            } while (cursor.moveToNext());
        }
    }

    private void saveHeader(final Integer id,final String no_transaksi, final String tanggal, final String pic, final String status, final String kategori, final String infeksius, final String total_berat, final String total_berat_real, final String total_qty) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, BASE_URL+ "linen_kotor",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                InputDbHelper db = new InputDbHelper(getActivity());
                                db.updateNameStatus(id);

                                //sending the broadcast to refresh the list
                                getActivity().sendBroadcast(new Intent("id.coba.datasaved"));
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
                params.put("status", status);
                params.put("kategori", kategori);
                params.put("infeksius", infeksius);
                params.put("total_berat", total_berat);
                params.put("total_berat_real", total_berat_real);
                params.put("total_qty", total_qty);
                return params;
            }
        };

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void saveDetail(final String no_transaksi, final String epc, final String room) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, BASE_URL+ "linen_kotor_detail",
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
                params.put("room", room);
                return params;
            }
        };

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
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

    private void setAutoNumber(){
        Date dNow = new Date();
        String myFormat = "yyyyMMdd-hhmmss";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        String datetime = sdf.format(dNow);
        noTransaksi.setText("K"+ datetime);
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

        Cursor cursor_header = db.query(InputContract.TaskEntry.TABLE,
                new String[]{
                        InputContract.TaskEntry._ID,
                        InputContract.TaskEntry.NO_TRANSAKSI,
                        InputContract.TaskEntry.TANGGAL,
                        InputContract.TaskEntry.PIC,
                        KATEGORI, JENIS_INFEKSIUS, TOTAL_BERAT_REAL},
                whereClause,
                whereArgs,
                null,
                null,
                null);
        int ii = 0;
        while (cursor_header.moveToNext()) {
            int id_no_transaksi = cursor_header.getColumnIndex(InputContract.TaskEntry.NO_TRANSAKSI);
            int id_tanggal = cursor_header.getColumnIndex(InputContract.TaskEntry.TANGGAL);
            int id_pic = cursor_header.getColumnIndex(InputContract.TaskEntry.PIC);
            int id_kategori = cursor_header.getColumnIndex(KATEGORI);
            int id_infeksius = cursor_header.getColumnIndex(JENIS_INFEKSIUS);
            int id_berat_real = cursor_header.getColumnIndex(TOTAL_BERAT_REAL);


            String no_transaksi = cursor_header.getString(id_no_transaksi);
            String tanggal = cursor_header.getString(id_tanggal);
            String pic = cursor_header.getString(id_pic);
            String kategori = cursor_header.getString(id_kategori);
            String infeksius = cursor_header.getString(id_infeksius);
            String total_berat_real = cursor_header.getString(id_berat_real);

            noTransaksi.setText(no_transaksi);
            tgl.setText(tanggal);
            tvTagSumBeratReal.setText(total_berat_real);

            getIndexByString(spinner_pic,pic);
            getIndexKategori(spinner_kategori, kategori);
            getIndexInfeksius(spinner_infeksius, infeksius);
        }

        String [] whereArgsDetail = {noTrans};
//        Cursor cursor = db.query(InputContract.TaskEntry.TABLE_DETAIL,
//                new String[]{InputContract.TaskEntry._ID, InputContract.TaskEntry.EPC, InputContract.TaskEntry.ITEM, InputContract.TaskEntry.ROOM},
//                InputContract.TaskEntry.NO_TRANSAKSI+"=?",
//                whereArgsDetail,
//                null,
//                null,
//                null);

        String selectQuery = "SELECT A.*,C.jenis,C.berat " +
                "FROM " + InputContract.TaskEntry.TABLE_DETAIL + " A " +
                "JOIN " + TABLE_BARANG + " B ON A.EPC=B.SERIAL " +
                "JOIN " + TABLE_JENIS_BARANG + " C ON C.id_jenis=B.id_jenis " +
                "WHERE transaksi='" + noTrans + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

         int i = 0;
         double tb = 0;
        while (cursor.moveToNext()) {
            int id = cursor.getColumnIndex(InputContract.TaskEntry._ID);
            int epc = cursor.getColumnIndex(InputContract.TaskEntry.EPC);
            int item = cursor.getColumnIndex(InputContract.TaskEntry.ITEM);
            int room = cursor.getColumnIndex(InputContract.TaskEntry.ROOM);
            int idberat = cursor.getColumnIndex(BERAT);

            long thisId = cursor.getLong(id);
            String epcs = cursor.getString(epc);
            String barang = cursor.getString(item);
            String ruangan = cursor.getString(room);
            String berat = cursor.getString(idberat);

            mapEpc = new HashMap();
            epcSet.add(epcs);
            mapEpc.put(epcs, i);
            mapEpc.put("epc", epcs);
            mapEpc.put("rssi", ruangan);
            mapEpc.put("item", barang);
            mapEpc.put("berat", berat);
            listEpc.add(mapEpc);
            i++;

            Float litersOfPetrol=Float.parseFloat(berat);
            tb += litersOfPetrol;
            tvTagSum.setText( String.valueOf(i) );
        }

        tvTagSumBerat.setText( String.format("%.1f", tb) );

        adapter = new EPCustadapter(getActivity(), listEpc);
        lvEpc.setAdapter(adapter);

        cursor.close();
        db.close();
    }
    private void getIndexByString(Spinner spinner, String string) {
      for (int i = 0; i < spinner.getCount(); i++) {
                Cursor value = (Cursor) spinner.getItemAtPosition(i);
                String name = value.getString(value.getColumnIndex("nama_user"));
                if (name.equals(string))
                {
                    spinner.setSelection(i);
                    break;
                }
        }
    }
    private void getIndexKategori(Spinner spinner, String string) {
        for (int i = 0; i < spinner.getCount(); i++) {
            Cursor value = (Cursor) spinner.getItemAtPosition(i);
            String name = value.getString(value.getColumnIndex("kategori"));
            if (name.equals(string))
            {
                spinner.setSelection(i);
                break;
            }
        }
    }
    private void getIndexInfeksius(Spinner spinner, String string) {
        for (int i = 0; i < spinner.getCount(); i++) {
            Cursor value = (Cursor) spinner.getItemAtPosition(i);
            String name = value.getString(value.getColumnIndex("infeksius"));
            if (name.equals(string))
            {
                spinner.setSelection(i);
                break;
            }
        }
    }

    public void deleteTask() {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(InputContract.TaskEntry.TABLE,
                InputContract.TaskEntry.EPC + " = ?",
                new String[]{"Name of entry you want deleted"});
        db.close();
        updateUI("");

    }

    public void deleteAll()
    {
        SQLiteDatabase db = mHelper.getWritableDatabase();
//        db.execSQL("DROP TABLE IF EXISTS "+InputContract.TaskEntry.TABLE);
//        db.execSQL("DROP TABLE IF EXISTS "+InputContract.TaskEntry.TABLE_DETAIL);
        db.delete(InputContract.TaskEntry.TABLE, null, null);
        db.delete(InputContract.TaskEntry.TABLE_DETAIL, null, null);
        db.close();
        updateUI("");
        showToast("Sukses hapus semua tabel..");
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
        tvTagSumBerat.setText("0");
        if(KotorActivity.mSetEpcs != null) {
            KotorActivity.mSetEpcs.clear();
        }
//        lvEpc.removeAllViews();
    }

    //show tips
    private Toast toast;
    private void showToast(String info) {
        if (toast==null) toast =  Toast.makeText(getActivity(), info, Toast.LENGTH_SHORT);
        else toast.setText(info);
        toast.show();
    }
    //key receiver
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

}
