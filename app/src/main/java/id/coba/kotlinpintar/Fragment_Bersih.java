package id.coba.kotlinpintar;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.Fragment;

import com.BRMicro.Tools;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.uhf.api.cls.Reader.TAGINFO;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
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

import static id.coba.kotlinpintar.InputDbHelper.BERAT;
import static id.coba.kotlinpintar.InputDbHelper.CHECKED;
import static id.coba.kotlinpintar.InputDbHelper.KATEGORI;
import static id.coba.kotlinpintar.InputDbHelper.STATUS_LINEN;
import static id.coba.kotlinpintar.InputDbHelper.TABLE_BARANG;
import static id.coba.kotlinpintar.InputDbHelper.TABLE_BERSIH;
import static id.coba.kotlinpintar.InputDbHelper.TABLE_BERSIH_DETAIL;
import static id.coba.kotlinpintar.InputDbHelper.TABLE_JENIS_BARANG;
import static id.coba.kotlinpintar.InputDbHelper.TOTAL_BERAT;
import static id.coba.kotlinpintar.InputDbHelper.TOTAL_QTY;
import static id.coba.kotlinpintar.Rest.ApiClient.BASE_URL;

import id.coba.kotlinpintar.Component.APIError;
import id.coba.kotlinpintar.Component.LoadingDialog;
import id.coba.kotlinpintar.Dto.BersihListResponse;
import id.coba.kotlinpintar.Dto.DataBersih;
import id.coba.kotlinpintar.Dto.DataKotor;
import id.coba.kotlinpintar.Dto.KotorListResponse;
import id.coba.kotlinpintar.Dto.LinenBersih;
import id.coba.kotlinpintar.Dto.LinenBersihDetail;
import id.coba.kotlinpintar.Dto.LinenKotor;
import id.coba.kotlinpintar.Dto.LinenKotorDetail;
import id.coba.kotlinpintar.Dto.Serial;
import id.coba.kotlinpintar.Rest.ApiClient;
import id.coba.kotlinpintar.Rest.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;

public class Fragment_Bersih extends Fragment implements OnCheckedChangeListener,OnClickListener{
    private View view;// this fragment UI
    private TextView tvTagSum ;
    private TextView tvTagSumScan ;
    private TextView tvTagSumBerat ;
    private ListView lvEpc;
    private Button btnStart ;//inventory button
    private ImageView btnClear ;// clear button
    private Button btnSimpan ;
    private Button btnSync ;
    private Button btnRemove ;
    private EditText edittext;
    private TextView noTransaksi;
    private TextView txtKategori;
    private TextView txtTambah ;
    private EditText tgl;
    private TextView tgl_kotor;
    private TextView jam_kotor;
    private TextView pic_kotor;
    private Spinner spinner_pic;
    private EditText person;
    private EditText txtScan;
    private CheckBox checkMulti ;//multi model check box
    private ArrayList<HashMap> listEpc;
    private ArrayList<HashMap> listEpc_scan;
    private LinearLayout lscan;
    private Set<String> epcSet = null ; //store different EPC
    private Set<String> epcSet_scan = null ;
    private HashMap mapEpc; //store EPC position
    private EPCBersihadapter adapter ;//epc list adapter
    private boolean isMulti = false ;// multi mode flag
    private int allCount = 0 ;// inventory count
    private long lastTime =  0L ;// record play sound time
    private InputDbHelper mHelper;
    private ArrayList<String> listSql = new ArrayList<String>();
    private ArrayAdapter adapterSql = null;
    private List kontenList = new ArrayList<>();
    final Calendar myCalendar = Calendar.getInstance();
    private Toolbar toolbar;
    private SimpleCursorAdapter adapterPIC ;
    private String DATA_SAVED_BROADCAST = "id.coba.datasaved";
    MaterialAlertDialogBuilder progress;
    private SharedPreferences prefMode;
    ApiInterface apiInterface;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    String epc = msg.getData().getString("epc");
                    String rssi = msg.getData().getString("rssi");
                    String item = msg.getData().getString("item");
                    String berat = msg.getData().getString("berat");
                    if (epc == null || epc.length() == 0) {
                        return ;
                    }
                    int position ;
                    allCount++ ;
                    if (epcSet_scan == null) {//first add
                        epcSet_scan = new HashSet<String>();
                        mapEpc = new HashMap();

                        epcSet_scan.add(epc);
                        mapEpc.put(epc, 0);
                        mapEpc.put("epc", epc);
                        mapEpc.put("rssi", rssi);
                        mapEpc.put("item", item);
                        mapEpc.put("berat", berat);

                        listEpc_scan.add(mapEpc);

                        adapter = new EPCBersihadapter(getActivity(), listEpc, listEpc_scan);
                        lvEpc.setAdapter(adapter);

                        Util.play(1, 0);
                        BersihActivity.mSetEpcs=epcSet_scan;

                    }else{
                        if (epcSet_scan.contains(epc.replace("\n",""))) {//set already exit
                        }else{

                            mapEpc = new HashMap();
                            epcSet_scan.add(epc);
                            mapEpc.put(epc, listEpc_scan.size());
                            mapEpc.put("epc", epc);
                            mapEpc.put("rssi", rssi);
                            mapEpc.put("item", item);
                            mapEpc.put("berat", berat);

                            listEpc_scan.add(mapEpc);
                            Util.play(1, 0);
                            BersihActivity.mSetEpcs = epcSet_scan;
                            if(System.currentTimeMillis() - lastTime > 100){
                                lastTime = System.currentTimeMillis() ;
                            }
                        }

                        if (epcSet.contains(epc.replace("\n",""))) {

                        }else{
                            mapEpc = new HashMap();
                            epcSet.add(epc);
                            mapEpc.put(epc, listEpc.size());
                            mapEpc.put("epc", epc);
                            mapEpc.put("rssi", rssi);
                            mapEpc.put("item", item);
                            mapEpc.put("berat", berat);
                            mapEpc.put("baru", 1);
                            mapEpc.put("rusak", 0);

                            listEpc.add(mapEpc);
                        }

                        tvTagSum.setText("" + listEpc.size());
                        tvTagSumScan.setText("" + listEpc_scan.size());
                        adapter.notifyDataSetChanged();
                    }

                    break ;
            }
        }
    } ;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_bersih_new, null);
        prefMode = getContext().getSharedPreferences("MODE", Context.MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        initView();

        IntentFilter filter = new IntentFilter() ;
        filter.addAction("android.rfid.FUN_KEY");
        getActivity().registerReceiver(keyReceiver, filter) ;

        return view/*super.onCreateView(inflater, container, savedInstanceState)*/;
    }

    private void initView() {
        lvEpc = (ListView) view.findViewById(R.id.listView_data);
        lvEpc.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);
        btnStart = (Button) view.findViewById(R.id.button_start);
        tvTagSum = (TextView) view.findViewById(R.id.textView_tag) ;
        tvTagSumScan = (TextView) view.findViewById(R.id.txtDiScan) ;
        tvTagSumBerat = (TextView) view.findViewById(R.id.textView_total_berat) ;
        btnClear = (ImageView) view.findViewById(R.id.btnUlangi) ;
        btnSimpan = (Button) view.findViewById(R.id.button_simpan) ;
        edittext= (EditText) view.findViewById(R.id.tanggal);
        spinner_pic= view.findViewById(R.id.spinner_pic);
        lscan = (LinearLayout) view.findViewById(R.id.lscan) ;
        txtTambah = (TextView) view.findViewById(R.id.txtTambah) ;

        noTransaksi= (TextView) view.findViewById(R.id.no_transaksi);
        setAutoNumber();

        tgl= (EditText) view.findViewById(R.id.tanggal);
        tgl_kotor= (TextView) view.findViewById(R.id.tgl_kotor);
        jam_kotor= (TextView) view.findViewById(R.id.jam_kotor);
        pic_kotor= (TextView) view.findViewById(R.id.pic_kotor);

        btnStart.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnSimpan.setOnClickListener(this);
        edittext.setOnClickListener(this);
        txtScan= (EditText) view.findViewById(R.id.textView_scan);
        txtScan.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_ENTER)) {
                    manualscan();
                }
                return false;
            }
        });
        txtScan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int counts) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        txtTambah.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                manualscan();
            }
        });
        if(prefMode.getBoolean("MODE", false) == true){
            lscan.setVisibility(View.GONE);
            btnStart.setVisibility(View.VISIBLE);
        }else{
            btnStart.setVisibility(View.GONE);
        }

        lvEpc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                TextView b = (TextView) view.findViewById(R.id.textView_barang);
                TextView c = (TextView) view.findViewById(R.id.textView_epc);
                String nama_barang = b.getText().toString();
                String serial = c.getText().toString();

                final int pos = position;
                CharSequence[] choices = {"Tandai Linen Rusak", "Hapus dari List", "Penambahan Baru"};
                 final AlertDialog alertDialog = new MaterialAlertDialogBuilder(getActivity())
                        .setTitle("Serial : " + serial + " - " + nama_barang + ", Pilih Action?")
                        .setIcon(R.mipmap.ic_launcher)
                        .setNeutralButton("Cancel", null)
                        .setSingleChoiceItems(choices, 1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 1) {
                                    listEpc.remove(position);
                                    adapter.notifyDataSetChanged();
                                    hitungBerat();
                                    dialog.dismiss();
                                }else if(which == 2){
                                    parent.getChildAt(position).setBackgroundColor(Color.YELLOW);
                                    dialog.dismiss();
                                }else if(which == 0){
                                    mapEpc = new HashMap();
                                    mapEpc.put("epc", listEpc.get(position).get("epc"));
                                    mapEpc.put("rssi", listEpc.get(position).get("rssi"));
                                    mapEpc.put("item", listEpc.get(position).get("item"));
                                    mapEpc.put("berat", listEpc.get(position).get("berat"));
                                    mapEpc.put("rusak", 1);
                                    listEpc.set(position,mapEpc);
                                    adapter.notifyDataSetChanged();
                                    parent.getChildAt(position).setBackgroundColor(Color.RED);
                                    dialog.dismiss();
                                }
                            }
                        })
                        .show();

            }
        });
        mHelper = new InputDbHelper(getActivity());

        updateLabel();
        loadspinner();
        String no = getArguments().getString("no_transaksi");
        String hist = getArguments().getString("history");

        if (no != null){
            updateUIBaru(no, hist);
        }

        ((AppCompatActivity)getActivity()).getSupportActionBar().setCustomView(R.layout.abs_layout);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayOptions(androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        AppCompatTextView title = ((AppCompatActivity)getActivity()).getSupportActionBar().getCustomView().findViewById(R.id.tvTitle);
        title.setText("Form Linen Bersih");
    }

    private  void manualscan(){
        String epc = txtScan.getText().toString().replace("\n","");
        txtScan.setText("");
        Message msg = new Message();
        msg.what = 1;
        Bundle b = new Bundle();
        b.putString("epc", epc);
        b.putString("exist", "0");

        try {
            Call<Serial> call2 = apiInterface.infoSerial(epc);
            call2.enqueue(new Callback<Serial>() {
                @Override
                public void onResponse(Call<Serial> call, retrofit2.Response<Serial> response) {
                    Serial userList = response.body();
                    if(userList != null) {
                        List<Serial.Datum> datumList = userList.data;
                        String message = userList.message;
                        for (Serial.Datum datum : datumList) {
                            b.putString("rssi", datum.ruangan);
                            b.putString("item", datum.jenis);
                            b.putString("berat", datum.berat);
                            b.putInt("check", 1);
                        }
                        if(datumList.size() == 0){
                            b.putString("rssi", "-");
                            b.putString("item", "Tidak Terdaftar!");
                            b.putString("berat", "0");
                            b.putInt("check", 0);
                        }
                    }else{
                        b.putString("rssi", "-");
                        b.putString("item", "Tidak Terdaftar!");
                        b.putString("berat", "0");
                        b.putInt("check", 0);
                    }

                    msg.setData(b);
                    handler.sendMessage(msg);
                }
                @Override
                public void onFailure(Call<Serial> call, Throwable t) {
                    call.cancel();
                    txtScan.setText("");
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception ex) {
            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void hitungBerat(){
        double tb = 0;
        for (HashMap num : listEpc) {
            Float litersOfPetrol=Float.parseFloat(num.get("berat").toString());
            tb += litersOfPetrol;
        }
        tvTagSumBerat.setText( String.format("%.1f", tb) );
    }
    private void loadspinner() {
        InputDbHelper db = new InputDbHelper(getActivity());
        final Cursor serviceCursor = db.getPICCursor();
        String[] from = {"nama_user"};
        int[] to = {android.R.id.text1};
        adapterPIC = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, serviceCursor, from, to, 0);
        adapterPIC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_pic.setAdapter(adapterPIC);
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
            BersihActivity.mUhfrManager.stopTagInventory();
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
        if (BersihActivity.mUhfrManager!=null) BersihActivity.mUhfrManager.setCancleInventoryFilter();
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
                        list1 = BersihActivity.mUhfrManager.tagInventoryRealTime();
                    }else{
                        list1 = BersihActivity.mUhfrManager.tagInventoryByTimer((short)50);
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

                            //SQLiteDatabase db = mHelper.getReadableDatabase();
                            try {
                                Call<Serial> call2 = apiInterface.infoSerial(epc);
                                call2.enqueue(new Callback<Serial>() {
                                    @Override
                                    public void onResponse(Call<Serial> call, retrofit2.Response<Serial> response) {
                                        Serial userList = response.body();
                                        if(userList != null) {
                                            List<Serial.Datum> datumList = userList.data;
                                            String message = userList.message;
                                            for (Serial.Datum datum : datumList) {
                                                b.putString("rssi", datum.ruangan);
                                                b.putString("item", datum.jenis);
                                                b.putString("berat", datum.berat);
                                                b.putInt("check", 1);
                                            }
                                            if(datumList.size() == 0){
                                                b.putString("rssi", "-");
                                                b.putString("item", "Tidak Terdaftar!");
                                                b.putString("berat", "0");
                                                b.putInt("check", 0);
                                            }
                                        }else{
                                            b.putString("rssi", "-");
                                            b.putString("item", "Tidak Terdaftar!");
                                            b.putString("berat", "0");
                                            b.putInt("check", 0);
                                        }

                                        msg.setData(b);
                                        handler.sendMessage(msg);
                                    }
                                    @Override
                                    public void onFailure(Call<Serial> call, Throwable t) {
                                        call.cancel();
                                        txtScan.setText("");
                                        Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                                /*String selectQuery = "SELECT A.*,B.jenis,B.berat FROM " + TABLE_BARANG + " A JOIN " + TABLE_JENIS_BARANG + " B ON A.ID_JENIS=B.ID_JENIS WHERE serial='" + epc + "'";
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
                                        b.putInt("check", 1);
                                    }
                                }else{

                                    b.putString("rssi", "-");
                                    b.putString("item", "Tidak Terdaftar!");
                                    b.putString("berat", "0");
                                    b.putInt("check", 0);
                                }
                                msg.setData(b);
                                handler.sendMessage(msg);*/
                            }catch (Exception ex) {
                                Toast.makeText(getActivity(),ex.getMessage().toString(),Toast.LENGTH_LONG);
                            }
                        }
                    }
                }
            }
        }
    } ;
    private boolean keyControl = true;
    private void runInventory() {
        if (keyControl) {
            keyControl = false;
            if (!isStart) {
                if(BersihActivity.mUhfrManager == null){
                    showToast("Fungsi ini berlaku hanya untuk handheld");
                }else{
                    BersihActivity.mUhfrManager.setCancleInventoryFilter();
                    isRunning = true;
                    if (isMulti) {
                        BersihActivity.mUhfrManager.setFastMode();
                        BersihActivity.mUhfrManager.asyncStartReading();
                    }else {
                        BersihActivity.mUhfrManager.setCancleFastMode();
                    }
                    new Thread(inventoryTask).start();
                    btnStart.setText(getResources().getString(R.string.stop_inventory_epc));
                    isStart = true;
                }

            } else {
//                checkMulti.setClickable(true);
//                checkMulti.setTextColor(Color.BLACK);
                if (isMulti)
                    BersihActivity.mUhfrManager.asyncStopReading();
                else
                    BersihActivity.mUhfrManager.stopTagInventory();
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isRunning = false;
                btnStart.setText(getResources().getString(R.string.start_inventory_epc));
                isStart = false;

                View parentView = null;
                double tb = 0;
                for (int i = 0; i < lvEpc.getCount(); i++) {
                    parentView = getViewByPosition(i, lvEpc);
                    String berat = ((TextView) parentView.findViewById(R.id.textView_berat)).getText().toString();

                    Float litersOfPetrol=Float.parseFloat(berat);
                    tb += litersOfPetrol;

                }
                tvTagSumBerat.setText( String.format("%.1f", tb) );
            }
            keyControl = true;
        }
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) isMulti = true;
        else isMulti = false;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_start:
                runInventory() ;
                break ;
            case R.id.btnUlangi:
                clearEpc();
                break ;
            case R.id.button_sync:
                updateUI("","0");
                showToast("Sync sukses.");
                break ;
            case R.id.tanggal:
                showTanggal();
                break;
            case R.id.button_simpan:

                Boolean exist = false;
                /*SQLiteDatabase db = mHelper.getReadableDatabase();
                for (HashMap num : listEpc) {

                    InputDbHelper db_store = new InputDbHelper(getActivity());
                    Cursor cursor_exist_bersih = db_store.getLastHistory( num.get("epc").toString());
                    while (cursor_exist_bersih.moveToNext()) {

                        int no = cursor_exist_bersih.getColumnIndex("transaksi");
                        int i_status = cursor_exist_bersih.getColumnIndex("FLAG");

                        String noTransaksi = cursor_exist_bersih.getString(no);
                        String status = cursor_exist_bersih.getString(i_status);
                        if(!status.equals("kotor")){
                            exist = true;
                            Toast.makeText(getActivity(),"Serial " + num.get("epc") + " berstatus " + status + " di transaksi " + noTransaksi  + "..!",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }

                    String selectQuery = "SELECT b.* FROM linen_rusak a " +
                            "JOIN linen_rusak_detail b ON a.transaksi=b.transaksi " +
                            "WHERE epc='" + num.get("epc") +"' AND a.transaksi<>'" + noTransaksi.getText().toString() + "'";
                    Cursor cursor_exist_rusak = db.rawQuery(selectQuery, null);
                    while (cursor_exist_rusak.moveToNext()) {
                        exist = true;
                        int no = cursor_exist_rusak.getColumnIndex("transaksi");
                        String noTransaksi = cursor_exist_rusak.getString(no);
                        Toast.makeText(getActivity(),"Serial " + num.get("epc") + " telah berstatus rusak..!",Toast.LENGTH_SHORT).show();
                        break;
                    }
                }

                */

                Cursor qc = adapterPIC.getCursor();
                final String pic = qc.getString(qc.getColumnIndex("nama_user"));

                LinenBersih bersih = new LinenBersih();
                bersih.NO_TRANSAKSI = noTransaksi.getText().toString();
                bersih.TANGGAL = edittext.getText().toString();
                bersih.PIC = pic;
                bersih.STATUS = "BERSIH";
                bersih.TOTAL_BERAT = tvTagSumBerat.getText().toString();
                bersih.TOTAL_QTY = tvTagSum.getText().toString();

                if(listEpc.isEmpty()){
                    Toast.makeText(getActivity(),"Belum ada data linen di scan.!",Toast.LENGTH_SHORT).show();
                    break;
                }

                int i = 0;
                for (HashMap num : listEpc) {
                    String epcName = num.get("epc").toString();
                    String barang = num.get("item").toString();
                    String ruangan = num.get("rssi").toString();
                    String berat = num.get("berat").toString();
                    String checked = "0";
                    if(num.get("checked") != null){
                        checked = num.get("checked").toString();
                    }
                    String baru = "0";
                    if(num.get("baru") != null){
                        baru = num.get("baru").toString();
                    }
                    String rusak = "0";
                    if(num.get("rusak") != null){
                        rusak = num.get("rusak").toString();
                    }
                    String status = "OK";
                    if(baru.equals("1")){
                        status = "BARU";
                    }else if(rusak.equals("1")){
                        status = "RUSAK";
                    }

                    /*if(epcName.toString() == "Tidak Terdaftar!"){
                        exist = true;
                        Toast.makeText(getActivity(),"Serial " + num.get("epc") + " berstatus " + status + " di transaksi " + noTransaksi  + "..!",Toast.LENGTH_SHORT).show();
                    }*/

                    LinenBersihDetail dtm = new LinenBersihDetail();
                    dtm.epc = epcName;
                    dtm.ruangan = ruangan;
                    dtm.no_transaksi =  noTransaksi.getText().toString();
                    dtm.checked = checked;
                    dtm.status_linen = status;

                    bersih.detail.add(i,dtm);
                    i++;
                }

                if(exist){
                    break;
                }
                System.out.println(bersih);

                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Yakin akan di simpan?");
                alertDialog.setMessage("Pastikan linen sudah di cek ulang.");

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final LoadingDialog loadingDialog = new LoadingDialog(getActivity());
                        loadingDialog.startLoadingDialog();
                        Call<LinenKotor> call2 = apiInterface.saveLinenBersih(bersih);
                        call2.enqueue(new Callback<LinenKotor>() {
                            @Override
                            public void onResponse(Call<LinenKotor> call, retrofit2.Response<LinenKotor> response) {
                                System.out.println(response);
                                LinenKotor defaultResponse = response.body();
                                if(defaultResponse == null && response.errorBody() != null){
                                    APIError message = new Gson().fromJson(response.errorBody().charStream(), APIError.class);
                                    Toast.makeText(getContext(), message.getMessage(), Toast.LENGTH_LONG).show();
                                }else{
                                    if(defaultResponse.Error == false){
                                        showToast("Sukses Menyimpan !!");
                                        Intent intent = new Intent(getActivity(), ListBersihActivityRecycle.class);
                                        startActivity(intent);
                                    }
                                }
                                loadingDialog.dismissDialog();
                            }
                            @Override
                            public void onFailure(Call<LinenKotor> call, Throwable t) {
                                call.cancel();
                                loadingDialog.dismissDialog();
                                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        /*String no_transaksi = noTransaksi.getText().toString();
                        String tanggal = ((EditText) getActivity().findViewById(R.id.tanggal)).getText().toString();
                        String total_berat = ((TextView) getActivity().findViewById(R.id.textView_total_berat)).getText().toString();
                        String total_qty = ((TextView) getActivity().findViewById(R.id.textView_tag)).getText().toString();

                        Cursor qc = adapterPIC.getCursor();
                        final String pic = qc.getString(qc.getColumnIndex("nama_user"));

                        SQLiteDatabase db = mHelper.getWritableDatabase();

                        ContentValues values_header = new ContentValues();

                        values_header.put(InputContract.TaskEntry.NO_TRANSAKSI, no_transaksi);
                        values_header.put(InputContract.TaskEntry.TANGGAL, tanggal);
                        values_header.put(InputContract.TaskEntry.PIC, pic);
                        values_header.put(InputContract.TaskEntry.STATUS, "BERSIH");
                        values_header.put(TOTAL_BERAT, total_berat);
                        values_header.put(TOTAL_QTY, total_qty);
                        values_header.put(KATEGORI, "");
                        values_header.put(InputContract.TaskEntry.CURRENT_INSERT, getDateTime());

                        db.insertWithOnConflict(TABLE_BERSIH, null, values_header, SQLiteDatabase.CONFLICT_REPLACE);

                        ContentValues values_kotor = new ContentValues();
                        values_kotor.put(InputContract.TaskEntry.STATUS, 1);
                        String [] whereArgs = {no_transaksi};
                        String whereClause = InputContract.TaskEntry.NO_TRANSAKSI+"=?";
                        db.update(InputContract.TaskEntry.TABLE,values_kotor,whereClause, whereArgs );

                        for (HashMap num : listEpc) {
                            String epcName = num.get("epc").toString();
                            String barang = num.get("item").toString();
                            String ruangan = num.get("rssi").toString();
                            String berat = num.get("berat").toString();
                            String checked = "0";
                            if(num.get("checked") != null){
                                checked = num.get("checked").toString();
                            }
                            String baru = "0";
                            if(num.get("baru") != null){
                                baru = num.get("baru").toString();
                            }
                            String rusak = "0";
                            if(num.get("rusak") != null){
                                rusak = num.get("rusak").toString();
                            }
                            String status = "OK";
                            if(baru.equals("1")){
                                status = "BARU";
                            }else if(rusak.equals("1")){
                                status = "RUSAK";
                            }

                            ContentValues values = new ContentValues();
                            values.put(InputContract.TaskEntry.NO_TRANSAKSI,   no_transaksi);
                            values.put(InputContract.TaskEntry.EPC,   epcName);
                            values.put(InputContract.TaskEntry.ITEM,   barang);
                            values.put(InputContract.TaskEntry.ROOM,   ruangan);
                            values.put(BERAT, berat);
                            values.put(STATUS_LINEN, status);
                            values.put(CHECKED, checked);
                            values.put(InputContract.TaskEntry.CURRENT_INSERT, getDateTime());

                            db.insertWithOnConflict(TABLE_BERSIH_DETAIL, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                        }

                        db.close();
                        showToast("Sukses menyimpan..");

                        syncOnlinedb();

                        Intent intent = new Intent(getActivity(), ListBersihActivityRecycle.class);
                        startActivity(intent);
                        */
                    }
                });

                alertDialog.show();
//                if(TextUtils.isEmpty(no_transaksi)){
//                    Toast.makeText(getActivity(),"PIC Kosong",Toast.LENGTH_SHORT).show();
//                    break;
//                }
                break ;
        }
    }
    public String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
    private void syncOnlinedb() {
        InputDbHelper db = new InputDbHelper(getActivity());
        Cursor cursor = db.getUnsyncedBersih();
        if (cursor.moveToFirst()) {
            do {

                saveHeader(
                        cursor.getInt(cursor.getColumnIndex(InputContract.TaskEntry._ID)),
                        cursor.getString(cursor.getColumnIndex(InputContract.TaskEntry.NO_TRANSAKSI)),
                        cursor.getString(cursor.getColumnIndex(InputContract.TaskEntry.TANGGAL)),
                        cursor.getString(cursor.getColumnIndex(InputContract.TaskEntry.PIC)),
                        cursor.getString(cursor.getColumnIndex(InputContract.TaskEntry.STATUS)),
                        cursor.getString(cursor.getColumnIndex(TOTAL_BERAT)),
                        cursor.getString(cursor.getColumnIndex(TOTAL_QTY))
                );

                db.updateFlagSyncBersih(cursor.getInt(cursor.getColumnIndex(InputContract.TaskEntry._ID)));

                Cursor cursor_detail = db.getDetailBersih(cursor.getString(cursor.getColumnIndex(InputContract.TaskEntry.NO_TRANSAKSI)));
                if (cursor_detail.moveToFirst()) {
                    do {
                        saveDetail(
                                cursor_detail.getString(cursor_detail.getColumnIndex(InputContract.TaskEntry.NO_TRANSAKSI)),
                                cursor_detail.getString(cursor_detail.getColumnIndex(InputContract.TaskEntry.EPC)),
                                cursor_detail.getString(cursor_detail.getColumnIndex(InputContract.TaskEntry.ROOM)),
                                cursor_detail.getString(cursor_detail.getColumnIndex(STATUS_LINEN)),
                                cursor_detail.getString(cursor_detail.getColumnIndex(CHECKED))
                        );
                    } while (cursor_detail.moveToNext());
                }

            } while (cursor.moveToNext());
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
    private void updateUIBaru(String no, String hist ) {
        epcSet = new HashSet<String>();
        listEpc = new ArrayList<HashMap>();
        listEpc_scan = new ArrayList<HashMap>();

        List<String> myList = new ArrayList<String>();
        SQLiteDatabase db = mHelper.getReadableDatabase();

        String noTrans = noTransaksi.getText().toString();
        btnSimpan.setVisibility(View.VISIBLE);

        //btnStart.setVisibility(View.VISIBLE);
        btnClear.setVisibility(View.VISIBLE);

        if(no != ""){
            noTrans = no;
        }

        Call<KotorListResponse> call2 = apiInterface.getKotor(no);
        call2.enqueue(new Callback<KotorListResponse>() {
            @Override
            public void onResponse(Call<KotorListResponse> call, retrofit2.Response<KotorListResponse> response) {
                ArrayList<DataKotor> dataKotor = response.body().getData();
                for (DataKotor data : dataKotor){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    try {
                        Date d = sdf.parse(data.getCURRENT_INSERT());
                        sdf.applyPattern("d MMM yyyy");
                        tgl_kotor.setText(sdf.format(d));
                        sdf.applyPattern("hh:mm");
                        jam_kotor.setText(sdf.format(d));
                    } catch (ParseException ex) {
                        Log.v("Exception", ex.getLocalizedMessage());
                    }
                    noTransaksi.setText(data.getNO_TRANSAKSI());
                    pic_kotor.setText(data.getPIC());
                    //txtKategori.setText(data.getKATEGORI());

                    int i = 0;
                    double tb = 0;

                    if(hist != null && hist.equals("1")){
                        btnSimpan.setVisibility(View.GONE);
                        btnStart.setVisibility(View.GONE);
                        btnClear.setVisibility(View.GONE);
                        lscan.setVisibility(View.GONE);
                        txtScan.setEnabled(false);

                        Call<BersihListResponse> call3 = apiInterface.getBersih(no);
                        call3.enqueue(new Callback<BersihListResponse>() {
                            @Override
                            public void onResponse(Call<BersihListResponse> call, retrofit2.Response<BersihListResponse> response) {
                                ArrayList<DataBersih> dataBersih = response.body().getData();
                                for (DataBersih dbersih : dataBersih){
                                    tgl.setText(dbersih.getTANGGAL());

                                    getIndexByString(spinner_pic,dbersih.getPIC());
                                    int i = 0;
                                    double tb = 0;
                                    int iscan = 0;

                                    List<LinenBersihDetail> datumList2 = dbersih.getDetail();
                                    for (LinenBersihDetail datum2 : datumList2) {
                                        String status_linen = datum2.status_linen;
                                        String baru = "0";
                                        String rusak = "0";
                                        if(status_linen.equals("BARU")){
                                            baru = "1";
                                        }else if(status_linen.equals("RUSAK")){
                                            rusak = "1";
                                        }

                                        mapEpc = new HashMap();
                                        epcSet.add(datum2.epc);
                                        mapEpc.put(datum2.epc, i);
                                        mapEpc.put("epc", datum2.epc);
                                        mapEpc.put("rssi", datum2.ruangan);
                                        mapEpc.put("item", datum2.item);
                                        mapEpc.put("berat", datum2.berat);
                                        mapEpc.put("checked", datum2.checked);
                                        mapEpc.put("baru", baru);
                                        mapEpc.put("rusak", rusak);
                                        listEpc.add(mapEpc);
                                        i++;

                                        Float litersOfPetrol=Float.parseFloat(datum2.berat);
                                        tb += litersOfPetrol;
                                        tvTagSum.setText( String.valueOf(i) );

                                        if(Integer.parseInt(datum2.checked) == 1){
                                            iscan++;
                                            tvTagSumScan.setText( String.valueOf(iscan) );
                                        }
                                    }

                                    tvTagSumBerat.setText( String.format("%.1f", tb) );
                                    adapter = new EPCBersihadapter(getActivity(), listEpc, listEpc_scan);
                                    lvEpc.setAdapter(adapter);
                                }
                            }
                            @Override
                            public void onFailure(Call<BersihListResponse> call, Throwable t) {
                                call.cancel();
                            }
                        });
                    }else{
                        List<LinenKotorDetail> datumList = data.getDetail();
                        for (LinenKotorDetail datum : datumList) {
                            mapEpc = new HashMap();
                            epcSet.add(datum.epc);
                            mapEpc.put(datum.epc, i);
                            mapEpc.put("epc", datum.epc);
                            mapEpc.put("rssi", datum.ruangan);
                            mapEpc.put("item", datum.item);
                            mapEpc.put("berat", datum.berat);
                            listEpc.add(mapEpc);
                            i++;

                            Float litersOfPetrol=Float.parseFloat(datum.berat);
                            tb += litersOfPetrol;
                            tvTagSum.setText( String.valueOf(i) );
                        }
                        tvTagSumBerat.setText( String.format("%.1f", tb) );
                        tvTagSumScan.setText("0");
                        adapter = new EPCBersihadapter(getActivity(), listEpc, listEpc_scan);
                        lvEpc.setAdapter(adapter);
                    }
                }
            }
            @Override
            public void onFailure(Call<KotorListResponse> call, Throwable t) {
                call.cancel();
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateUI(String no, String hist ) {
        epcSet = new HashSet<String>();
        listEpc = new ArrayList<HashMap>();
        listEpc_scan = new ArrayList<HashMap>();

        List<String> myList = new ArrayList<String>();
        SQLiteDatabase db = mHelper.getReadableDatabase();

        String noTrans = noTransaksi.getText().toString();
        btnSimpan.setVisibility(View.VISIBLE);
        btnStart.setVisibility(View.VISIBLE);
        btnClear.setVisibility(View.VISIBLE);

        if(no != ""){
            noTrans = no;
        }

        String whereClause = InputContract.TaskEntry.NO_TRANSAKSI+"=?";
        String [] whereArgs = {noTrans};

        Cursor cursor_header = db.query(InputContract.TaskEntry.TABLE,
                new String[]{InputContract.TaskEntry._ID, InputContract.TaskEntry.NO_TRANSAKSI, InputContract.TaskEntry.CURRENT_INSERT, InputContract.TaskEntry.PIC, KATEGORI},
                whereClause,
                whereArgs,
                null,
                null,
                null);
        int ii = 0;
        while (cursor_header.moveToNext()) {
            int id_no_transaksi = cursor_header.getColumnIndex(InputContract.TaskEntry.NO_TRANSAKSI);
            int id_tanggal = cursor_header.getColumnIndex(InputContract.TaskEntry.CURRENT_INSERT);
            int id_pic = cursor_header.getColumnIndex(InputContract.TaskEntry.PIC);
            int id_kat = cursor_header.getColumnIndex(KATEGORI);

            String no_transaksi = cursor_header.getString(id_no_transaksi);
            String tanggal = cursor_header.getString(id_tanggal);
            String pic = cursor_header.getString(id_pic);
            String kategori = cursor_header.getString(id_kat);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            try {
                Date d = sdf.parse(tanggal);
                sdf.applyPattern("d MMM yyyy, hh:mm");
                tgl_kotor.setText(sdf.format(d));
            } catch (ParseException ex) {
                //Log.v("Exception", ex.getLocalizedMessage());
            }

            noTransaksi.setText(no_transaksi);

            pic_kotor.setText(pic);
//            txtKategori.setText(kategori);

        }

        double tb = 0;
        if(hist.equals("1")){
            btnSimpan.setVisibility(View.GONE);
            btnStart.setVisibility(View.GONE);
            btnClear.setVisibility(View.GONE);

            Cursor cursor_bersih = db.query(TABLE_BERSIH,
                    new String[]{InputContract.TaskEntry._ID, InputContract.TaskEntry.NO_TRANSAKSI, InputContract.TaskEntry.TANGGAL, InputContract.TaskEntry.PIC},
                    whereClause,
                    whereArgs,
                    null,
                    null,
                    null);
            while (cursor_bersih.moveToNext()) {
                int id_tanggal = cursor_bersih.getColumnIndex(InputContract.TaskEntry.TANGGAL);
                int id_pic = cursor_bersih.getColumnIndex(InputContract.TaskEntry.PIC);

                String tanggal = cursor_bersih.getString(id_tanggal);
                String pic = cursor_bersih.getString(id_pic);
                tgl.setText(tanggal);

                getIndexByString(spinner_pic,pic);

            }

            //Data Listview
            String selectQuery = "SELECT A.*,C.jenis,C.berat " +
                    "FROM " + TABLE_BERSIH_DETAIL + " A " +
                    "JOIN " + TABLE_BARANG + " B ON A.EPC=B.SERIAL " +
                    "JOIN " + TABLE_JENIS_BARANG + " C ON C.id_jenis=B.id_jenis " +
                    "WHERE transaksi='" + noTrans + "'";
            Cursor cursor = db.rawQuery(selectQuery, null);

            int i = 0;

            while (cursor.moveToNext()) {
                int id = cursor.getColumnIndex(InputContract.TaskEntry._ID);
                int epc = cursor.getColumnIndex(InputContract.TaskEntry.EPC);
                int item = cursor.getColumnIndex(InputContract.TaskEntry.ITEM);
                int room = cursor.getColumnIndex(InputContract.TaskEntry.ROOM);
                int idberat = cursor.getColumnIndex(BERAT);
                int status = cursor.getColumnIndex(STATUS_LINEN);
                int check = cursor.getColumnIndex(CHECKED);

                String epcs = cursor.getString(epc);
                String barang = cursor.getString(item);
                String ruangan = cursor.getString(room);
                String berat = cursor.getString(idberat);
                String status_linen = cursor.getString(status);
                String checked = cursor.getString(check);
                String baru = "0";
                String rusak = "0";
                if(status_linen.equals("BARU")){
                    baru = "1";
                }else if(status_linen.equals("RUSAK")){
                    rusak = "1";
                }

                mapEpc = new HashMap();
                epcSet.add(epcs);
                mapEpc.put(epcs, i);
                mapEpc.put("epc", epcs);
                mapEpc.put("rssi", ruangan);
                mapEpc.put("item", barang);
                mapEpc.put("berat", berat);
                mapEpc.put("checked", checked);
                mapEpc.put("baru", baru);
                mapEpc.put("rusak", rusak);
                listEpc.add(mapEpc);
                i++;

                Float litersOfPetrol=Float.parseFloat(berat);
                tb += litersOfPetrol;
                tvTagSum.setText( String.valueOf(i) );

            }
            cursor.close();
        }else{
            String selectQuery = "SELECT A.*,C.jenis,C.berat " +
                    "FROM " + InputContract.TaskEntry.TABLE_DETAIL + " A " +
                    "JOIN " + TABLE_BARANG + " B ON A.EPC=B.SERIAL " +
                    "JOIN " + TABLE_JENIS_BARANG + " C ON C.id_jenis=B.id_jenis " +
                    "WHERE transaksi='" + noTrans + "'";
            Cursor cursor = db.rawQuery(selectQuery, null);

            int i = 0;

            while (cursor.moveToNext()) {
                int id = cursor.getColumnIndex(InputContract.TaskEntry._ID);
                int epc = cursor.getColumnIndex(InputContract.TaskEntry.EPC);
                int item = cursor.getColumnIndex(InputContract.TaskEntry.ITEM);
                int room = cursor.getColumnIndex(InputContract.TaskEntry.ROOM);
                int idberat = cursor.getColumnIndex(BERAT);

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
            cursor.close();
        }


        tvTagSumBerat.setText( String.format("%.1f", tb) );

        adapter = new EPCBersihadapter(getActivity(), listEpc, listEpc_scan);
        lvEpc.setAdapter(adapter);


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
    public void deleteTask() {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(InputContract.TaskEntry.TABLE,
                InputContract.TaskEntry.EPC + " = ?",
                new String[]{"Name of entry you want deleted"});
        db.close();
        updateUI("","0");

    }
    private void clearEpc(){

        if(listEpc_scan != null)
            listEpc_scan.removeAll(listEpc_scan);
        if(epcSet_scan != null)
            epcSet_scan.clear();

        String noTrans = noTransaksi.getText().toString();
        updateUIBaru(noTrans, "0");
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
        boolean keyDown = intent.getBooleanExtra("keydown", false) ;
        if(keyUpFalg&&keyDown && System.currentTimeMillis() - startTime > 500){
            keyUpFalg = false;
            startTime = System.currentTimeMillis() ;
            if ( (keyCode == KeyEvent.KEYCODE_F1 || keyCode == KeyEvent.KEYCODE_F2
                    || keyCode == KeyEvent.KEYCODE_F3 || keyCode == KeyEvent.KEYCODE_F4 ||
                    keyCode == KeyEvent.KEYCODE_F5)) {
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

    private void saveHeader(final Integer id,final String no_transaksi, final String tanggal, final String pic, final String status,final String total_berat, final String total_qty) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, BASE_URL+ "linen_bersih",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //updating the status in sqlite

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
                params.put("total_berat", total_berat);
                params.put("total_qty", total_qty);
                return params;
            }
        };

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void saveDetail(final String no_transaksi, final String epc, final String room, final String status_linen, final String checked) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, BASE_URL+ "linen_bersih_detail",
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
                params.put("status_linen", status_linen);
                params.put("checked", checked);
                return params;
            }
        };

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }
}
