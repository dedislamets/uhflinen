package id.coba.kotlinpintar;

import android.app.ActionBar;
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
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.Fragment;

import com.BRMicro.Tools;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.uhf.api.cls.Reader.TAGINFO;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
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

import static android.app.Activity.RESULT_OK;
import static id.coba.kotlinpintar.InputDbHelper.BERAT;
import static id.coba.kotlinpintar.InputDbHelper.JENIS;
import static id.coba.kotlinpintar.InputDbHelper.KATEGORI;
import static id.coba.kotlinpintar.InputDbHelper.KELUAR;
import static id.coba.kotlinpintar.InputDbHelper.NAMA_RUANGAN;
import static id.coba.kotlinpintar.InputDbHelper.NO_REFERENSI;
import static id.coba.kotlinpintar.InputDbHelper.NO_REQUEST;
import static id.coba.kotlinpintar.InputDbHelper.QTY;
import static id.coba.kotlinpintar.InputDbHelper.READY;
import static id.coba.kotlinpintar.InputDbHelper.SERIAL;
import static id.coba.kotlinpintar.InputDbHelper.TABLE_BARANG;
import static id.coba.kotlinpintar.InputDbHelper.TABLE_BERSIH_DETAIL;
import static id.coba.kotlinpintar.InputDbHelper.TABLE_JENIS_BARANG;
import static id.coba.kotlinpintar.InputDbHelper.TABLE_KELUAR;
import static id.coba.kotlinpintar.InputDbHelper.TABLE_KELUAR_DETAIL;
import static id.coba.kotlinpintar.InputDbHelper.TABLE_REQUEST_DETAIL;
import static id.coba.kotlinpintar.InputDbHelper.TOTAL_BERAT;
import static id.coba.kotlinpintar.InputDbHelper.TOTAL_QTY;
import static id.coba.kotlinpintar.Rest.ApiClient.BASE_URL;

public class Fragment_Keluar extends Fragment implements OnCheckedChangeListener,OnClickListener{
    private View view;// this fragment UI
    private TextView tvTagSum ;
    private TextView tvTagSumBerat ;
    private ListView lvEpc;
    private ListView lvRequest;
    private Button btnStart ;//inventory button
    private Button btnClear ;// clear button
    private Button btnSimpan ;
    private ImageButton btnReff ;
    private EditText edittext;
    private EditText noTransaksi;
    private EditText txtScan;
    private EditText tgl;
    private EditText txtReferensi;
    private Spinner spinner_pic;
    private Spinner spinner_ruangan;
    private ArrayList<HashMap> listEpc;
    private ArrayList<HashMap> listRequest;
    private LinearLayout lscan;

    private Set<String> epcSet = null ;
    //    private List<HashMap> listEpc = null;//EPC list
    private HashMap mapEpc;
    private HashMap mapEpc_request;
    private EPCKeluarAdapter adapter ;
    private KeluarRequestAdapter adapter_request ;

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
    private SimpleCursorAdapter adapterRuangan ;
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
                        listEpc = new ArrayList<HashMap>();
                        mapEpc = new HashMap();
                        if(listRequest == null)
                            listRequest = new ArrayList<HashMap>();
                        epcSet.add(epc);
                        mapEpc.put(epc, 0);
                        mapEpc.put("epc", epc);
                        mapEpc.put("rssi", rssi);
                        mapEpc.put("item", item);
                        mapEpc.put("berat", berat);
                        mapEpc.put("exist", exist);

                        listEpc.add(mapEpc);
                        hitungBerat(berat);

                        for (HashMap num_req : listRequest) {
                            if (item.equals(num_req.get("jenis"))) {
                                num_req.put("ready",1);
                            }
                        }

                        adapter = new EPCKeluarAdapter(getActivity(), listEpc);
                        adapter_request = new KeluarRequestAdapter(getActivity(), listRequest, listEpc);
                        lvEpc.setAdapter(adapter);
                        lvRequest.setAdapter(adapter_request);

                        Util.play(1, 0);
                        KeluarActivity.mSetEpcs=epcSet;
                    }else{
                        if (epcSet.contains(epc.replace("\n",""))) {//set already exit

                        }else{
                            mapEpc = new HashMap();
                            epcSet.add(epc);
                            mapEpc.put(epc, listEpc.size());
                            mapEpc.put("epc", epc);
                            mapEpc.put("rssi", rssi);
                            mapEpc.put("item", item);
                            mapEpc.put("berat", berat);
                            mapEpc.put("exist", exist);

                            listEpc.add(mapEpc);

                            for (HashMap num_req : listRequest) {
                                if (item.equals(num_req.get("jenis"))) {
                                    num_req.put("ready",Integer.parseInt(num_req.get("ready").toString())+1);
                                }
                            }
//                            adapter_request = new KeluarRequestAdapter(getActivity(), listRequest, listEpc);
//                            lvRequest.setAdapter(adapter_request);

                            hitungBerat(berat);
                            Util.play(1, 0);
                            KeluarActivity.mSetEpcs = epcSet;
                            if(System.currentTimeMillis() - lastTime > 100){
                                lastTime = System.currentTimeMillis() ;
                                Util.play(1, 0);
                            }

                        }

                        tvTagSum.setText("" + listEpc.size());

                        adapter.notifyDataSetChanged();
                        adapter_request.notifyDataSetChanged();
                        txtScan.setText("");
                    }

                    break ;
            }
        }
    } ;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_keluar, null);
        prefMode = getContext().getSharedPreferences("MODE", Context.MODE_PRIVATE);
        initView();


        IntentFilter filter = new IntentFilter() ;
        filter.addAction("android.rfid.FUN_KEY");
        getActivity().registerReceiver(keyReceiver, filter) ;

        return view;
    }


    private void hitungBerat(String berat){
        if (berat == null) {
            berat = "0";
        }
        double tb =Float.parseFloat(tvTagSumBerat.getText().toString());
        Float litersOfPetrol=Float.parseFloat(berat);
        tb += litersOfPetrol;
        tvTagSumBerat.setText( String.format("%.1f", tb) );
    }

    private void initView() {
        lvEpc = (ListView) view.findViewById(R.id.listView_data);
        lvRequest = (ListView) view.findViewById(R.id.listView_request);
        btnStart = (Button) view.findViewById(R.id.button_start);
        tvTagSum = (TextView) view.findViewById(R.id.textView_tag) ;
        tvTagSumBerat = (TextView) view.findViewById(R.id.textView_total_berat) ;
        btnClear = (Button) view.findViewById(R.id.button_clear_epc) ;
        btnSimpan = (Button) view.findViewById(R.id.button_simpan) ;
        btnReff = (ImageButton) view.findViewById(R.id.btnReff) ;
        edittext= (EditText) view.findViewById(R.id.tanggal);
        txtReferensi= (EditText) view.findViewById(R.id.referensi);
        spinner_pic= view.findViewById(R.id.spinner_pic);
        spinner_ruangan= view.findViewById(R.id.spinner_ruangan);
        lscan = (LinearLayout) view.findViewById(R.id.lscan) ;

        noTransaksi= (EditText) view.findViewById(R.id.no_transaksi);
        setAutoNumber();

        tgl= (EditText) view.findViewById(R.id.tanggal);

        lvEpc.setFocusable(false);
        lvEpc.setClickable(false);
        lvEpc.setItemsCanFocus(false);
        lvEpc.setScrollingCacheEnabled(false);
        lvEpc.setOnItemClickListener(null);
        btnStart.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnSimpan.setOnClickListener(this);
        edittext.setOnClickListener(this);
        btnReff.setOnClickListener(this);
        txtScan= (EditText) view.findViewById(R.id.textView_scan);
        txtScan.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String epc = txtScan.getText().toString().replace("\n","");
                    Message msg = new Message();
                    msg.what = 1;
                    Bundle b = new Bundle();
                    b.putString("epc", epc);

                    try {
                        SQLiteDatabase db = mHelper.getReadableDatabase();

                        String selectQuery = "SELECT A.*,B.jenis,B.berat FROM " + TABLE_BARANG + " A JOIN " + TABLE_JENIS_BARANG + " B ON A.ID_JENIS=B.ID_JENIS WHERE serial='" + epc + "'";
                        Cursor cursor_header = db.rawQuery(selectQuery, null);

                        String berat = "0";
                        String jenis = "";
                        b.putString("exist", "0");

                        int count = cursor_header.getCount();
                        if (count > 0) {
                            while (cursor_header.moveToNext()) {
                                String ruangan = cursor_header.getString(cursor_header.getColumnIndex(InputDbHelper.NAMA_RUANGAN));
                                jenis = cursor_header.getString(cursor_header.getColumnIndex(InputDbHelper.JENIS));
                                berat = cursor_header.getString(cursor_header.getColumnIndex(InputDbHelper.BERAT));

                                b.putString("rssi", ruangan);
                                b.putString("item", jenis);
                                b.putString("berat", berat);

                                String exist = "";
//                                selectQuery = "SELECT b.* FROM linen_keluar a " +
//                                        "JOIN linen_keluar_detail b ON a.transaksi=b.transaksi " +
//                                        "WHERE a.status=0 and epc='" + epc +"' AND a.transaksi<>'" + noTransaksi.getText().toString() + "'";
//                                Cursor cursor_exist = db.rawQuery(selectQuery, null);
//                                while (cursor_exist.moveToNext()) {
//                                    exist = "1";
//                                }

                                InputDbHelper db_store = new InputDbHelper(getActivity());
                                Cursor cursor_exist_bersih = db_store.getLastHistory(epc);
                                while (cursor_exist_bersih.moveToNext()) {
                                    int i_status = cursor_exist_bersih.getColumnIndex("FLAG");

                                    String status = cursor_exist_bersih.getString(i_status);
                                    if (status.equals("keluar")) {
                                        exist = "1";
                                    }
                                }
                                b.putString("exist", exist);
                            }
                        } else {
                            b.putString("rssi", "-");
                            b.putString("item", "Tidak Terdaftar!");
                            b.putString("berat", "0");
                        }
                        msg.setData(b);
                        handler.sendMessage(msg);
                    } catch (Exception ex) {
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

        updateLabel();
        loadspinner();
        String no = getArguments().getString("no_transaksi");

        if (no != null){
            updateUI(no);
        }
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Form Linen Keluar");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    private void loadspinner() {

        InputDbHelper db = new InputDbHelper(getActivity());
        final Cursor serviceCursor = db.getPICCursor();
        String[] from = {"nama_user"};
        int[] to = {android.R.id.text1};
        adapterPIC = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, serviceCursor, from, to, 0);
        adapterPIC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_pic.setAdapter(adapterPIC);

        final Cursor kategoriCursor = db.getServiceCursor();
        String[] from_ruangan = {"nama_ruangan"};
        int[] to_ruangan = {android.R.id.text1};
        adapterRuangan = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, kategoriCursor, from_ruangan, to_ruangan, 0);
        adapterRuangan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_ruangan.setAdapter(adapterRuangan);

    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isStart) {
            isStart = false;
            isRunning = false;
            KeluarActivity.mUhfrManager.stopTagInventory();
        }
        getActivity().unregisterReceiver(keyReceiver);//
    }
    @Override
    public void onPause() {
        super.onPause();
        if (isStart) {
            runInventory();
        }
    }
    private boolean f1hidden = false;
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        f1hidden = hidden;
        if (hidden) {
            if (isStart) runInventory();// stop inventory
        }
        if (KeluarActivity.mUhfrManager!=null) KeluarActivity.mUhfrManager.setCancleInventoryFilter();
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
                        list1 = KeluarActivity.mUhfrManager.tagInventoryRealTime();
                    }else{
                        //sleep can save electricity
//						try {
//							Thread.sleep(250);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
                        list1 = KeluarActivity.mUhfrManager.tagInventoryByTimer((short)50);
                        //inventory epc + tid
//                        list1 = KeluarActivity.mUhfrManager.tagEpcTidInventoryByTimer((short) 50);
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

                            SQLiteDatabase db = mHelper.getReadableDatabase();

                            String selectQuery = "SELECT A.*,B.jenis,B.berat FROM " + TABLE_BARANG + " A JOIN " + TABLE_JENIS_BARANG + " B ON A.ID_JENIS=B.ID_JENIS WHERE serial='" + epc + "'";
                            Cursor cursor_header = db.rawQuery(selectQuery, null);

                            String berat = "0";
                            String jenis = "";
                            b.putString("exist", "0");

                            int count = cursor_header.getCount();
                            if (count > 0) {
                                while (cursor_header.moveToNext()) {
                                    String ruangan = cursor_header.getString(cursor_header.getColumnIndex(InputDbHelper.NAMA_RUANGAN));
                                    jenis = cursor_header.getString(cursor_header.getColumnIndex(InputDbHelper.JENIS));
                                    berat = cursor_header.getString(cursor_header.getColumnIndex(InputDbHelper.BERAT));

                                    b.putString("rssi", ruangan);
                                    b.putString("item", jenis);
                                    b.putString("berat", berat);

                                    String exist = "";
//                                selectQuery = "SELECT b.* FROM linen_keluar a " +
//                                        "JOIN linen_keluar_detail b ON a.transaksi=b.transaksi " +
//                                        "WHERE a.status=0 and epc='" + epc +"' AND a.transaksi<>'" + noTransaksi.getText().toString() + "'";
//                                Cursor cursor_exist = db.rawQuery(selectQuery, null);
//                                while (cursor_exist.moveToNext()) {
//                                    exist = "1";
//                                }

                                    InputDbHelper db_store = new InputDbHelper(getActivity());
                                    Cursor cursor_exist_bersih = db_store.getLastHistory(epc);
                                    while (cursor_exist_bersih.moveToNext()) {
                                        int i_status = cursor_exist_bersih.getColumnIndex("FLAG");

                                        String status = cursor_exist_bersih.getString(i_status);
                                        if (status.equals("keluar")) {
                                            exist = "1";
                                        }
                                    }
                                    b.putString("exist", exist);
                                }
                            }else{
                                b.putString("rssi", "-");
                                b.putString("item", "Tidak Terdaftar!");
                                b.putString("berat", "0");
                            }
                            msg.setData(b);
                            handler.sendMessage(msg);
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
                if(KeluarActivity.mUhfrManager == null){
                    showToast("Fungsi ini berlaku hanya untuk handheld");
                }else{
                    KeluarActivity.mUhfrManager.setCancleInventoryFilter();
                    isRunning = true;
                    if (isMulti) {
                        KeluarActivity.mUhfrManager.setFastMode();
                        KeluarActivity.mUhfrManager.asyncStartReading();
                    }else {
                        KeluarActivity.mUhfrManager.setCancleFastMode();
                    }
                    new Thread(inventoryTask).start();
                    btnStart.setText(getResources().getString(R.string.stop_inventory_epc));
                    isStart = true;
                }

            } else {
//                checkMulti.setClickable(true);
//                checkMulti.setTextColor(Color.BLACK);
                if (isMulti)
                    KeluarActivity.mUhfrManager.asyncStopReading();
                else
                    KeluarActivity.mUhfrManager.stopTagInventory();
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
            case R.id.btnReff:
                if (isStart) {
                    KeluarActivity.mUhfrManager.stopTagInventory();
                }
                Intent intent = new Intent(getActivity(), CariListRequestActivity.class);
                startActivityForResult(intent,1);

                break ;
            case R.id.tanggal:
                showTanggal();
                break;
            case R.id.button_simpan:
                String no_transaksi = ((EditText) getActivity().findViewById(R.id.no_transaksi)).getText().toString();
                String tanggal = ((EditText) getActivity().findViewById(R.id.tanggal)).getText().toString();
                String no_referensi = txtReferensi.getText().toString();
                String qty = tvTagSum.getText().toString();

                Cursor qc = adapterPIC.getCursor();
                final String pic = qc.getString(qc.getColumnIndex("nama_user"));

                Cursor qc_ruangan = adapterRuangan.getCursor();
                final String ruangan = qc_ruangan.getString(qc_ruangan.getColumnIndex("nama_ruangan"));

                if(txtReferensi.getText().toString() != ""){
                    Boolean exist_req = false;
                    Boolean limit_req = false;
                    if(listRequest != null) {
                        for (HashMap num_req : listRequest) {
                            if (Integer.parseInt(num_req.get("ready").toString()) == 0) {
                                exist_req = true;
                            }
                            if (Integer.parseInt(num_req.get("ready").toString()) > Integer.parseInt(num_req.get(QTY).toString())) {
                                limit_req = true;
                            }
                        }
                        if (exist_req) {
                            Toast.makeText(getActivity(), "List Request min ready > 0 .!", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        if(limit_req){
                            Toast.makeText(getActivity(),"List Scan tidak boleh melebihi list request.!",Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }
                if(lvEpc.getCount() <=0){
                    Toast.makeText(getActivity(),"Belum ada data linen di scan.!",Toast.LENGTH_SHORT).show();
                    break;
                }

                Boolean exist = false;
                SQLiteDatabase db = mHelper.getReadableDatabase();
                for (HashMap num : listEpc) {
                    if (num.get("item").equals("Tidak Terdaftar!")){
                        exist = true;
                        Toast.makeText(getActivity(),"Serial " + num.get("epc") + " belum terdaftar..!",Toast.LENGTH_SHORT).show();
                    }
                    InputDbHelper db_store = new InputDbHelper(getActivity());
                    Cursor cursor_exist_bersih = db_store.getLastHistory( num.get("epc").toString());
                    while (cursor_exist_bersih.moveToNext()) {

                        int no = cursor_exist_bersih.getColumnIndex("transaksi");
                        int i_status = cursor_exist_bersih.getColumnIndex("FLAG");

                        String noTransaksi = cursor_exist_bersih.getString(no);
                        String status = cursor_exist_bersih.getString(i_status);
                        if(!status.equals("bersih")){
                            exist = true;
                            Toast.makeText(getActivity(),"Serial " + num.get("epc") + " berstatus " + status + " di transaksi " + noTransaksi  + "..!",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }

                    String selectQuery = "SELECT b.* FROM linen_rusak a " +
                            "JOIN linen_rusak_detail b ON a.transaksi=b.transaksi " +
                            "WHERE epc='" + num.get("epc") +"' AND a.transaksi<>'" + noTransaksi.getText().toString() + "'";;
                    Cursor cursor_exist_rusak = db.rawQuery(selectQuery, null);
                    while (cursor_exist_rusak.moveToNext()) {
                        exist = true;
                        int no = cursor_exist_rusak.getColumnIndex("transaksi");
                        String noTransaksi = cursor_exist_rusak.getString(no);
                        Toast.makeText(getActivity(),"Serial " + num.get("epc") + " telah berstatus rusak..!",Toast.LENGTH_SHORT).show();
                        break;
                    }

                    if(listRequest != null) {
                        for (HashMap num_req : listRequest) {
                            if (!num.get("item").equals(num_req.get("jenis"))) {
                                exist = true;
                                Toast.makeText(getActivity(), "Item " + num.get("item") + " tidak ada di list request..!", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
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
                values_header.put(NAMA_RUANGAN,   ruangan);
                values_header.put(NO_REFERENSI,   no_referensi);
                values_header.put(InputContract.TaskEntry.CURRENT_INSERT, getDateTime());

                db.insertWithOnConflict(TABLE_KELUAR, null, values_header, SQLiteDatabase.CONFLICT_REPLACE);


                for (int i = 0; i < lvEpc.getCount(); i++) {
                    parentView = getViewByPosition(i, lvEpc);

                    String epcName = ((TextView) parentView.findViewById(R.id.textView_epc)).getText().toString();
                    String barang = ((TextView) parentView.findViewById(R.id.textView_barang)).getText().toString();
                    String berat = ((TextView) parentView.findViewById(R.id.textView_berat)).getText().toString();

                    ContentValues values = new ContentValues();
                    values.put(InputContract.TaskEntry.NO_TRANSAKSI,   no_transaksi);
                    values.put(InputContract.TaskEntry.EPC,   epcName);
                    values.put(InputContract.TaskEntry.ITEM,   barang);
                    values.put(BERAT,   berat);
                    values.put(InputContract.TaskEntry.CURRENT_INSERT, getDateTime());

                    db.insertWithOnConflict(TABLE_KELUAR_DETAIL, null, values, SQLiteDatabase.CONFLICT_REPLACE);

                    ContentValues values_bersih = new ContentValues();
                    values_bersih.put(KELUAR,   1);

                    db.update(TABLE_BERSIH_DETAIL,
                            values_bersih,
                            InputContract.TaskEntry.EPC + "=" + epcName + " and " + KELUAR + "=0",
                            null);

                    ContentValues values_barang = new ContentValues();
                    values_barang.put(NAMA_RUANGAN,   ruangan);

                    db.update(TABLE_BARANG,
                            values_barang,
                            SERIAL + "=" + epcName ,
                            null);
                }

                db.close();
                showToast("Sukses menyimpan..");
                syncOnlineDB();
                intent = new Intent(getActivity(), ListKeluarActivity.class);
                startActivity(intent);
                break ;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String no_request = data.getStringExtra("no_request");
                txtReferensi.setText(no_request);
                loadDataRequest(no_request);
            }
        }
    }

    public String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void syncOnlineDB(){
        InputDbHelper db = new InputDbHelper(getActivity());
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
                                cursor.getString(cursor.getColumnIndex(NO_REFERENSI)),
                                cursor.getString(cursor.getColumnIndex(NAMA_RUANGAN)),
                                cursor_detail.getString(cursor_detail.getColumnIndex(InputContract.TaskEntry.EPC))
                        );
                    } while (cursor_detail.moveToNext());
                }

            } while (cursor.moveToNext());
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
                                InputDbHelper db = new InputDbHelper(getActivity());
                                db.updateFlagSyncKeluar(id);

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
                params.put("ruangan", nama_ruangan);
                params.put("no_referensi", no_referensi);
                return params;
            }
        };

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void saveDetail(final String no_transaksi,final String no_referensi, final String ruangan, final String epc) {
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
                params.put("no_referensi", no_referensi);
                params.put("ruangan", ruangan);
                params.put("epc", epc);
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
        noTransaksi.setText("OT"+ datetime);
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

    private void loadDataRequest(String no ) {
        listRequest = new ArrayList<HashMap>();
//        if(listEpc != null){
//
//        }
//        ArrayList<HashMap> listdummy = new ArrayList<HashMap>();
        List<String> myList = new ArrayList<String>();
        SQLiteDatabase db = mHelper.getReadableDatabase();

        Cursor cursor_header = db.rawQuery(
                "SELECT * " +
                        "FROM request_linen_detail " +
                        "WHERE no_request = '" + no + "' ",
                null);
        listRequest.clear();
        while (cursor_header.moveToNext()) {
            int id_jenis = cursor_header.getColumnIndex(JENIS);
            int id_qty = cursor_header.getColumnIndex(QTY);
            int id_ready = cursor_header.getColumnIndex(READY);

            String jenis = cursor_header.getString(id_jenis);
            String qty = cursor_header.getString(id_qty);
            String ready = cursor_header.getString(id_ready);

            mapEpc = new HashMap();
            mapEpc.put(JENIS, jenis);
            mapEpc.put(QTY, qty);
            mapEpc.put("ready", ready);
            listRequest.add(mapEpc);
        }

        adapter_request = new KeluarRequestAdapter(getActivity(), listRequest,listEpc);
        lvRequest.setAdapter(adapter_request);

        cursor_header.close();
        db.close();
    }

    private void updateUI(String no ) {
        epcSet = new HashSet<String>();
        listEpc = new ArrayList<HashMap>();
        listRequest = new ArrayList<HashMap>();

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

        Cursor cursor_header = db.query(TABLE_KELUAR,
                new String[]{InputContract.TaskEntry._ID, InputContract.TaskEntry.NO_TRANSAKSI, InputContract.TaskEntry.TANGGAL, InputContract.TaskEntry.PIC, NAMA_RUANGAN,NO_REFERENSI},
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
            int id_ruangan = cursor_header.getColumnIndex(NAMA_RUANGAN);
            int id_ref = cursor_header.getColumnIndex(NO_REFERENSI);

            String no_transaksi = cursor_header.getString(id_no_transaksi);
            String tanggal = cursor_header.getString(id_tanggal);
            String pic = cursor_header.getString(id_pic);
            String ruangan = cursor_header.getString(id_ruangan);
            String no_ref = cursor_header.getString(id_ref);

            noTransaksi.setText(no_transaksi);
            tgl.setText(tanggal);
            txtReferensi.setText(no_ref);

            getIndexByString(spinner_pic,pic);
            getIndexRuangan(spinner_ruangan, ruangan);
        }



        String selectQuery = "SELECT A.*,C.jenis,C.berat " +
                "FROM " + TABLE_KELUAR_DETAIL + " A " +
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
            int idberat = cursor.getColumnIndex(BERAT);

            long thisId = cursor.getLong(id);
            String epcs = cursor.getString(epc);
            String barang = cursor.getString(item);
            String berat = cursor.getString(idberat);

            mapEpc = new HashMap();
            epcSet.add(epcs);
            mapEpc.put(epcs, i);
            mapEpc.put("epc", epcs);
            mapEpc.put("item", barang);
            mapEpc.put("berat", berat);
            listEpc.add(mapEpc);
            i++;

            Float litersOfPetrol=Float.parseFloat(berat);
            tb += litersOfPetrol;
            tvTagSum.setText( String.valueOf(i) );


        }

        selectQuery = "SELECT * " +
                "FROM " + TABLE_REQUEST_DETAIL + " A " +
                "WHERE "+ NO_REQUEST +"='" + txtReferensi.getText() + "'";
        Cursor cursor_request = db.rawQuery(selectQuery, null);
        while (cursor_request.moveToNext()) {
            int idqty = cursor_request.getColumnIndex(QTY);
            int idready = cursor_request.getColumnIndex(READY);
            int idjenis = cursor_request.getColumnIndex(JENIS);
            String barang = cursor_request.getString(idjenis);
            String qty = cursor_request.getString(idqty);
            Integer ready = cursor_request.getInt(idready);

            mapEpc = new HashMap();
            mapEpc.put(JENIS, barang);
            mapEpc.put(QTY, qty);
            mapEpc.put("ready", ready);
            listRequest.add(mapEpc);
        }

        tvTagSumBerat.setText( String.format("%.1f", tb) );

        adapter = new EPCKeluarAdapter(getActivity(), listEpc);
        lvEpc.setAdapter(adapter);

        adapter_request = new KeluarRequestAdapter(getActivity(), listRequest,listEpc);
        lvRequest.setAdapter(adapter_request);

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
    private void getIndexRuangan(Spinner spinner, String string) {
        for (int i = 0; i < spinner.getCount(); i++) {
            Cursor value = (Cursor) spinner.getItemAtPosition(i);
            String name = value.getString(value.getColumnIndex("nama_ruangan"));
            if (name.equals(string))
            {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void clearEpc(){
        if (epcSet != null) {
            epcSet.removeAll(epcSet); //store different EPC
        }
        if(listEpc != null)
            listEpc.removeAll(listEpc);//EPC list
        if(mapEpc != null)
//            mapEpc.clear();
        if(adapter != null) {
            adapter.notifyDataSetChanged();
            adapter_request.notifyDataSetChanged();
        }
        allCount = 0 ;
        tvTagSum.setText("0");
        tvTagSumBerat.setText("0");
        if(KeluarActivity.mSetEpcs != null){
            KeluarActivity.mSetEpcs.clear();
        }

//        lvEpc.removeAllViews();
    }

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
