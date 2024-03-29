package id.coba.kotlinpintar;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.gson.Gson;
import com.uhf.api.cls.Reader.TAGINFO;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static id.coba.kotlinpintar.InputDbHelper.BERAT;
import static id.coba.kotlinpintar.InputDbHelper.CATATAN;
import static id.coba.kotlinpintar.InputDbHelper.DEFECT;
import static id.coba.kotlinpintar.InputDbHelper.JML_CUCI;
import static id.coba.kotlinpintar.InputDbHelper.NAMA_RUANGAN;
import static id.coba.kotlinpintar.InputDbHelper.NO_REFERENSI;
import static id.coba.kotlinpintar.InputDbHelper.TABLE_BARANG;
import static id.coba.kotlinpintar.InputDbHelper.TABLE_JENIS_BARANG;
import static id.coba.kotlinpintar.InputDbHelper.TABLE_KELUAR;
import static id.coba.kotlinpintar.InputDbHelper.TABLE_KELUAR_DETAIL;
import static id.coba.kotlinpintar.InputDbHelper.TABLE_RUSAK;
import static id.coba.kotlinpintar.InputDbHelper.TABLE_RUSAK_DETAIL;
import static id.coba.kotlinpintar.Rest.ApiClient.BASE_URL;

import id.coba.kotlinpintar.Component.APIError;
import id.coba.kotlinpintar.Component.LoadingDialog;
import id.coba.kotlinpintar.Dto.DataKotor;
import id.coba.kotlinpintar.Dto.DataRusak;
import id.coba.kotlinpintar.Dto.Delete;
import id.coba.kotlinpintar.Dto.JumlahCuci;
import id.coba.kotlinpintar.Dto.KotorListResponse;
import id.coba.kotlinpintar.Dto.LastHistory;
import id.coba.kotlinpintar.Dto.LinenKeluar;
import id.coba.kotlinpintar.Dto.LinenKeluarDetail;
import id.coba.kotlinpintar.Dto.LinenKotorDetail;
import id.coba.kotlinpintar.Dto.LinenRusak;
import id.coba.kotlinpintar.Dto.LinenRusakDetail;
import id.coba.kotlinpintar.Dto.RusakListResponse;
import id.coba.kotlinpintar.Dto.Serial;
import id.coba.kotlinpintar.Helper.Helper;
import id.coba.kotlinpintar.Rest.ApiClient;
import id.coba.kotlinpintar.Rest.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;

public class Fragment_Rusak extends Fragment implements OnCheckedChangeListener,OnClickListener{
    private View view;// this fragment UI
    private TextView tvTagSum ;
    private TextView txtTambah ;
    private TextView tvTagSumBerat ;
    private ListView lvEpc;// epc list view
    private Button btnStart ;
    private Button btnDelete ;
    private ImageView btnUlangi ;// clear button
    private Button btnSimpan ;
    private EditText edittext;
    private EditText noTransaksi;
    private EditText tgl;
    private EditText txtCatatan;
    private Spinner spinner_pic;
    private Spinner spinner_defect;
    private ArrayList<HashMap> listEpc;
    private EditText txtScan;
    private LinearLayout empty_layout;
    private Set<String> epcSet = null ;
    //    private List<HashMap> listEpc = null;//EPC list
    private HashMap mapEpc;
    private EPCRusakAdapter adapter ;

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
    private SimpleCursorAdapter adapterDefect ;
    private SimpleCursorAdapter adapterRuangan ;
    private SharedPreferences prefMode;
    ApiInterface apiInterface;
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
                    String cuci = msg.getData().getString("cuci");
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
                        mapEpc.put("cuci", cuci);

                        listEpc.add(mapEpc);
                        hitungBerat(berat);

                        adapter = new EPCRusakAdapter(getActivity(), listEpc);
                        lvEpc.setAdapter(adapter);
                        Helper.getListViewSize(lvEpc);

                        Util.play(1, 0);
                        RusakActivity.mSetEpcs=epcSet;
                    }else{
                        if (epcSet.contains(epc.replace("\n",""))) {//set already exit
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
                            mapEpc.put("berat", berat);
                            mapEpc.put("exist", exist);
                            mapEpc.put("cuci", cuci);

                            listEpc.add(mapEpc);

                            hitungBerat(berat);
                            Util.play(1, 1);
                            RusakActivity.mSetEpcs = epcSet;
                            if(System.currentTimeMillis() - lastTime > 100){
                                lastTime = System.currentTimeMillis() ;

                            }

                        }

                        tvTagSum.setText("" + listEpc.size());
                        adapter.notifyDataSetChanged();
                        Helper.getListViewSize(lvEpc);

                    }

                    break ;
            }
        }
    } ;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_rusak, null);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
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
        tvTagSumBerat.setText( new Formatter(Locale.ENGLISH).format("%.1f", tb).toString() );
    }

    private void initView() {
        lvEpc = (ListView) view.findViewById(R.id.listView_data);
        btnStart = (Button) view.findViewById(R.id.button_start);
        tvTagSum = (TextView) view.findViewById(R.id.textView_tag) ;
        tvTagSumBerat = (TextView) view.findViewById(R.id.textView_total_berat) ;
        btnUlangi = (ImageView) view.findViewById(R.id.btnUlangi) ;
        btnSimpan = (Button) view.findViewById(R.id.button_simpan) ;
        btnDelete = (Button) view.findViewById(R.id.button_delete) ;
        edittext= (EditText) view.findViewById(R.id.tanggal);
        spinner_pic= view.findViewById(R.id.spinner_pic);
        spinner_defect= view.findViewById(R.id.spinner_defect);
        txtTambah = (TextView) view.findViewById(R.id.txtTambah) ;
        empty_layout= (LinearLayout)view.findViewById(R.id.layout_empty);
        lvEpc.setEmptyView(empty_layout);

        noTransaksi= (EditText) view.findViewById(R.id.no_transaksi);
        setAutoNumber();

        tgl= (EditText) view.findViewById(R.id.tanggal);
        txtCatatan= (EditText) view.findViewById(R.id.catatan);

        lvEpc.setFocusable(false);
        lvEpc.setClickable(false);
        lvEpc.setItemsCanFocus(false);
        lvEpc.setScrollingCacheEnabled(false);
        lvEpc.setOnItemClickListener(null);
        btnStart.setOnClickListener(this);
        btnUlangi.setOnClickListener(this);
        btnSimpan.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
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
        txtTambah.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                manualscan();
            }
        });
        mHelper = new InputDbHelper(getActivity());
        tvTagSumBerat.setText("0");

        if(prefMode.getBoolean("MODE", false) == true){
            btnStart.setEnabled(true);
        }else{
            btnStart.setEnabled(false);
        }

        updateLabel();
        loadspinner();
        String no = getArguments().getString("no_transaksi");

        if (no != null){
            updateUIBaru(no);
        }
        ((AppCompatActivity)getActivity()).getSupportActionBar().setCustomView(R.layout.abs_layout);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayOptions(androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        AppCompatTextView title = ((AppCompatActivity)getActivity()).getSupportActionBar().getCustomView().findViewById(R.id.tvTitle);
        title.setText("Form Linen Rusak");

    }

    private void manualscan(){
        String epc = txtScan.getText().toString().replace("\n","");
        txtScan.setText("");
        Message msg = new Message();
        msg.what = 1;
        Bundle b = new Bundle();
        b.putString("epc", epc);

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
                            b.putString("exist", "");
                            b.putString("cuci", "0");

                            Call<JumlahCuci> callJumlahCuci = apiInterface.getJumlahCuci(epc);
                            callJumlahCuci.enqueue(new Callback<JumlahCuci>() {
                                @Override
                                public void onResponse(Call<JumlahCuci> call, retrofit2.Response<JumlahCuci> response) {
                                    JumlahCuci history = response.body();
                                    List<JumlahCuci.Datum> historyDatum = history.data;
                                    for (JumlahCuci.Datum datum : historyDatum) {
                                        b.putString("cuci", datum.jml);
                                    }
                                    msg.setData(b);
                                    handler.sendMessage(msg);
                                }

                                @Override
                                public void onFailure(Call<JumlahCuci> call3, Throwable t) {
                                    call3.cancel();
                                }
                            });
                        }
                    }else{
                        b.putString("rssi", "");
                        b.putString("item", "");
                        b.putString("berat", "0");
                        b.putString("exist", "");
                        b.putString("cuci", "0");

                        msg.setData(b);
                        handler.sendMessage(msg);
                    }
                }
                @Override
                public void onFailure(Call<Serial> call, Throwable t) {
                    call.cancel();
                    txtScan.setText("");
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception ex) {
            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        /*try {
            SQLiteDatabase db = mHelper.getReadableDatabase();

            String selectQuery = "SELECT A.*,B.jenis,B.berat FROM " + TABLE_BARANG + " A JOIN " + TABLE_JENIS_BARANG + " B ON A.ID_JENIS=B.ID_JENIS WHERE serial='" + epc + "'";
            Cursor cursor_header = db.rawQuery(selectQuery, null);

            String berat = "0";
            String jenis = "";
            b.putString("exist", "0");
            while (cursor_header.moveToNext()) {
                String ruangan = cursor_header.getString(cursor_header.getColumnIndex(InputDbHelper.NAMA_RUANGAN));
                jenis = cursor_header.getString(cursor_header.getColumnIndex(InputDbHelper.JENIS));
                berat = cursor_header.getString(cursor_header.getColumnIndex(InputDbHelper.BERAT));

                b.putString("rssi", ruangan);
                b.putString("item", jenis);
                b.putString("berat", berat);

                String exist = "";
                selectQuery = "SELECT b.* FROM linen_rusak a " +
                        "JOIN linen_rusak_detail b ON a.transaksi=b.transaksi " +
                        "WHERE epc='" + epc + "' AND a.transaksi<>'" + noTransaksi.getText().toString() + "'";
                Cursor cursor_exist = db.rawQuery(selectQuery, null);
                while (cursor_exist.moveToNext()) {
                    exist = "1";
                }
                b.putString("exist", exist);
            }

            selectQuery = "SELECT COUNT(*) jml " +
                    "FROM linen_kotor a " +
                    "JOIN linen_kotor_detail b ON a.transaksi=b.transaksi " +
                    "WHERE epc='" + epc + "'";
            Cursor cursor_jml_cuci = db.rawQuery(selectQuery, null);
            String jml_cuci = "0";
            b.putString("cuci", jml_cuci);
            while (cursor_jml_cuci.moveToNext()) {
                jml_cuci = cursor_jml_cuci.getString(cursor_jml_cuci.getColumnIndex("jml"));
                b.putString("cuci", jml_cuci);
            }
            msg.setData(b);
            handler.sendMessage(msg);
        } catch (Exception ex) {
        }*/
    }
    private void loadspinner() {

        InputDbHelper db = new InputDbHelper(getActivity());
        final Cursor serviceCursor = db.getPICCursor();
        final Cursor defectCursor = db.getDefect();

        String[] from = {"nama_user"};
        String[] from_defect = {"defect"};
        int[] to = {android.R.id.text1};

        adapterPIC = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, serviceCursor, from, to, 0);
        adapterPIC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_pic.setAdapter(adapterPIC);

        adapterDefect = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, defectCursor, from_defect, to, 0);
        adapterDefect.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_defect.setAdapter(adapterDefect);


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
            RusakActivity.mUhfrManager.stopTagInventory();
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
        if (RusakActivity.mUhfrManager!=null) RusakActivity.mUhfrManager.setCancleInventoryFilter();
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
                        list1 = RusakActivity.mUhfrManager.tagInventoryRealTime();
                    }else{
                        list1 = RusakActivity.mUhfrManager.tagInventoryByTimer((short)50);
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
                                                b.putString("exist", "");
                                                b.putString("cuci", "0");

                                                Call<JumlahCuci> callJumlahCuci = apiInterface.getJumlahCuci(epc);
                                                callJumlahCuci.enqueue(new Callback<JumlahCuci>() {
                                                    @Override
                                                    public void onResponse(Call<JumlahCuci> call, retrofit2.Response<JumlahCuci> response) {
                                                        JumlahCuci history = response.body();
                                                        List<JumlahCuci.Datum> historyDatum = history.data;
                                                        for (JumlahCuci.Datum datum : historyDatum) {
                                                            b.putString("cuci", datum.jml);
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<JumlahCuci> call3, Throwable t) {
                                                        call3.cancel();
                                                    }
                                                });
                                            }
                                        }else{
                                            b.putString("rssi", "");
                                            b.putString("item", "");
                                            b.putString("berat", "0");
                                            b.putString("exist", "");
                                            b.putString("cuci", "0");
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
                            }catch (Exception ex) {
                                Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
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
                if(RusakActivity.mUhfrManager == null){
                    showToast("Fungsi ini berlaku hanya untuk handheld");
                }else{
                    RusakActivity.mUhfrManager.setCancleInventoryFilter();
                    isRunning = true;
                    if (isMulti) {
                        RusakActivity.mUhfrManager.setFastMode();
                        RusakActivity.mUhfrManager.asyncStartReading();
                    }else {
                        RusakActivity.mUhfrManager.setCancleFastMode();
                    }
                    new Thread(inventoryTask).start();
                    btnStart.setText(getResources().getString(R.string.stop_inventory_epc));
                    isStart = true;
                }

            } else {
//                checkMulti.setClickable(true);
//                checkMulti.setTextColor(Color.BLACK);
                if (isMulti)
                    RusakActivity.mUhfrManager.asyncStopReading();
                else
                    RusakActivity.mUhfrManager.stopTagInventory();
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
                /*updateUI("");
                showToast("Sync sukses.");*/
                break ;
            case R.id.button_delete:

                AlertDialog alertDialog_delete = new AlertDialog.Builder(getActivity()).create();
                alertDialog_delete.setTitle("Yakin akan di hapus?");
                alertDialog_delete.setMessage("Linen ini akan dihapus dari linen rusak.");
                alertDialog_delete.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final LoadingDialog loadingDialog = new LoadingDialog(getActivity());
                        loadingDialog.startLoadingDialog();
                        Call<Delete> call2 = apiInterface.DeleteRusak(noTransaksi.getText().toString());
                        call2.enqueue(new Callback<Delete>() {
                            @Override
                            public void onResponse(Call<Delete> call, retrofit2.Response<Delete> response) {
                                System.out.println(response);
                                Delete defaultResponse = response.body();
                                if(defaultResponse == null && response.errorBody() != null){
                                    APIError message = new Gson().fromJson(response.errorBody().charStream(), APIError.class);
                                    Toast.makeText(getContext(), message.getMessage(), Toast.LENGTH_LONG).show();
                                }else{
                                    showToast("Sukses Menyimpan !!");
                                    Intent intent = new Intent(getActivity(), ListRusakActivityRecycle.class);
                                    startActivity(intent);
                                }
                                loadingDialog.dismissDialog();
                            }
                            @Override
                            public void onFailure(Call<Delete> call, Throwable t) {
                                call.cancel();
                                loadingDialog.dismissDialog();
                                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                alertDialog_delete.show();
                break ;
            case R.id.tanggal:
                showTanggal();
                break;
            case R.id.button_simpan:
                String no_transaksi = ((EditText) getActivity().findViewById(R.id.no_transaksi)).getText().toString();
                String tanggal = ((EditText) getActivity().findViewById(R.id.tanggal)).getText().toString();
                String catatan = ((EditText) getActivity().findViewById(R.id.catatan)).getText().toString();
                String qty = tvTagSum.getText().toString();

                Cursor qc = adapterPIC.getCursor();
                Cursor qc_def = adapterDefect.getCursor();
                final String pic = qc.getString(qc.getColumnIndex("nama_user"));
                final String defect = qc_def.getString(qc_def.getColumnIndex("defect"));

                if(TextUtils.isEmpty(no_transaksi)){
                    Toast.makeText(getActivity(),"PIC Kosong",Toast.LENGTH_SHORT).show();
                    break;
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
                    /*if (num.get("exist").equals("1") && !num.get("item").equals("Tidak Terdaftar!")) {
                        exist = true;
                        String selectQuery = "SELECT b.* FROM linen_rusak a " +
                                "JOIN linen_rusak_detail b ON a.transaksi=b.transaksi " +
                                "WHERE epc='" + num.get("epc") +"' AND a.transaksi<>'" + noTransaksi.getText().toString() + "'";
                        Cursor cursor_exist_keluar = db.rawQuery(selectQuery, null);
                        while (cursor_exist_keluar.moveToNext()) {

                            int no = cursor_exist_keluar.getColumnIndex("transaksi");

                            String noTransaksi = cursor_exist_keluar.getString(no);
                            Toast.makeText(getActivity(),"Serial " + num.get("epc") + " telah berstatus rusak di transaksi " + noTransaksi  + "..!",Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }*/

                }
                if(exist) {
                    break;
                }

                LinenRusak rusak = new LinenRusak();
                rusak.NO_TRANSAKSI = no_transaksi;
                rusak.TANGGAL = tanggal;
                rusak.PIC = pic;
                rusak.CATATAN = catatan;
                rusak.DEFECT = defect;

                View parentView = null;
                for (int i = 0; i < lvEpc.getCount(); i++) {

                    parentView = getViewByPosition(i, lvEpc);

                    String epcName = ((TextView) parentView.findViewById(R.id.textView_epc)).getText().toString();
                    String jml_cuci = ((TextView) parentView.findViewById(R.id.textView_jml_cuci)).getText().toString();

                    LinenRusakDetail dtm = new LinenRusakDetail();
                    dtm.epc = epcName;
                    dtm.no_transaksi =  no_transaksi;
                    dtm.jml_cuci = jml_cuci;
                    rusak.detail.add(i,dtm);
                }

                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Yakin akan di simpan?");
                alertDialog.setMessage("Linen ini akan didaftarkan ke linen rusak.");

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final LoadingDialog loadingDialog = new LoadingDialog(getActivity());
                        loadingDialog.startLoadingDialog();
                        Call<LinenRusak> call2 = apiInterface.saveLinenRusak(rusak);
                        call2.enqueue(new Callback<LinenRusak>() {
                            @Override
                            public void onResponse(Call<LinenRusak> call, retrofit2.Response<LinenRusak> response) {
                                System.out.println(response);
                                LinenRusak defaultResponse = response.body();
                                if(defaultResponse == null && response.errorBody() != null){
                                    APIError message = new Gson().fromJson(response.errorBody().charStream(), APIError.class);
                                    Toast.makeText(getContext(), message.getMessage(), Toast.LENGTH_LONG).show();
                                }else{
                                    if(defaultResponse.Error == false){
                                        showToast("Sukses Menyimpan !!");
                                        Intent intent = new Intent(getActivity(), ListRusakActivityRecycle.class);
                                        startActivity(intent);
                                    }
                                }
                                loadingDialog.dismissDialog();
                            }
                            @Override
                            public void onFailure(Call<LinenRusak> call, Throwable t) {
                                call.cancel();
                                loadingDialog.dismissDialog();
                                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                alertDialog.show();

                /* View parentView = null;

                ContentValues values_header = new ContentValues();

                values_header.put(InputContract.TaskEntry.NO_TRANSAKSI,   no_transaksi);
                values_header.put(InputContract.TaskEntry.TANGGAL,   tanggal);
                values_header.put(InputContract.TaskEntry.PIC,   pic);
                values_header.put(CATATAN,   catatan);
                values_header.put(DEFECT,   defect);
                values_header.put(InputContract.TaskEntry.CURRENT_INSERT, getDateTime());

                db.insertWithOnConflict(TABLE_RUSAK, null, values_header, SQLiteDatabase.CONFLICT_REPLACE);


                for (int i = 0; i < lvEpc.getCount(); i++) {
                    parentView = getViewByPosition(i, lvEpc);

                    String epcName = ((TextView) parentView.findViewById(R.id.textView_epc)).getText().toString();
                    String barang = ((TextView) parentView.findViewById(R.id.textView_barang)).getText().toString();
                    String berat = ((TextView) parentView.findViewById(R.id.textView_berat)).getText().toString();
                    String jml_cuci = ((TextView) parentView.findViewById(R.id.textView_jml_cuci)).getText().toString();

                    ContentValues values = new ContentValues();
                    values.put(InputContract.TaskEntry.NO_TRANSAKSI,   no_transaksi);
                    values.put(InputContract.TaskEntry.EPC,   epcName);
                    values.put(InputContract.TaskEntry.ITEM,   barang);
                    values.put(BERAT,   berat);
                    values.put(JML_CUCI,   jml_cuci);
                    values.put(InputContract.TaskEntry.CURRENT_INSERT, getDateTime());

                    db.insertWithOnConflict(TABLE_RUSAK_DETAIL, null, values, SQLiteDatabase.CONFLICT_REPLACE);

                }

                db.close();
                showToast("Sukses menyimpan..");
                syncOnlineDB();
                Intent intent = new Intent(getActivity(), ListRusakActivity.class);
                startActivity(intent);*/
                break ;
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
        Cursor cursor = db.getUnsyncedRusak();
        if (cursor.moveToFirst()) {
            do {

                saveHeader(
                        cursor.getInt(cursor.getColumnIndex(InputContract.TaskEntry._ID)),
                        cursor.getString(cursor.getColumnIndex(InputContract.TaskEntry.NO_TRANSAKSI)),
                        cursor.getString(cursor.getColumnIndex(InputContract.TaskEntry.TANGGAL)),
                        cursor.getString(cursor.getColumnIndex(InputContract.TaskEntry.PIC)),
                        cursor.getString(cursor.getColumnIndex(CATATAN)),
                        cursor.getString(cursor.getColumnIndex(DEFECT))
                );


                Cursor cursor_detail = db.getDetailRusak(cursor.getString(cursor.getColumnIndex(InputContract.TaskEntry.NO_TRANSAKSI)));
                if (cursor_detail.moveToFirst()) {
                    do {
                        saveDetail(
                                cursor_detail.getString(cursor_detail.getColumnIndex(InputContract.TaskEntry.NO_TRANSAKSI)),
                                cursor_detail.getString(cursor_detail.getColumnIndex(InputContract.TaskEntry.EPC)),
                                cursor_detail.getString(cursor_detail.getColumnIndex(JML_CUCI))
                        );
                    } while (cursor_detail.moveToNext());
                }

            } while (cursor.moveToNext());
        }
    }

    private void saveHeader(final Integer id,final String no_transaksi, final String tanggal, final String pic, final String catatan, final String defect) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, BASE_URL+ "linen_rusak",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                InputDbHelper db = new InputDbHelper(getActivity());
                                db.updateFlagSyncRusak(id);

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
                params.put("defect", defect);
                params.put("catatan", catatan);
                return params;
            }
        };

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void saveDetail(final String no_transaksi, final String epc, final String jml_cuci) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, BASE_URL+ "linen_rusak_detail",
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
                params.put("jml_cuci", jml_cuci);
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
        noTransaksi.setText("R"+ datetime);
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
    private void updateUIBaru(String no){
        epcSet = new HashSet<String>();
        listEpc = new ArrayList<HashMap>();

        String noTrans = noTransaksi.getText().toString();
        btnSimpan.setVisibility(View.VISIBLE);
        btnStart.setVisibility(View.VISIBLE);
        btnDelete.setVisibility(View.GONE);

        if(no != ""){
            noTrans = no;
            btnSimpan.setVisibility(View.GONE);
            btnStart.setVisibility(View.GONE);
            btnDelete.setVisibility(View.VISIBLE);
        }

        Call<RusakListResponse> call2 = apiInterface.getRusak(no);
        call2.enqueue(new Callback<RusakListResponse>() {
            @Override
            public void onResponse(Call<RusakListResponse> call, retrofit2.Response<RusakListResponse> response) {
                ArrayList<DataRusak> dataRusak = response.body().getData();
                for (DataRusak data : dataRusak){
                    noTransaksi.setText(data.getNO_TRANSAKSI());
                    tgl.setText(data.getTANGGAL());
                    txtCatatan.setText(data.getCATATAN());
                    getIndexByString(spinner_pic,data.getPIC(),"nama_user");
                    getIndexByString(spinner_defect,data.getDEFECT(),"defect");

                    int i = 0;
                    double tb = 0;

                    List<LinenRusakDetail> datumList = data.getDetail();
                    for (LinenRusakDetail datum : datumList) {
                        mapEpc = new HashMap();
                        epcSet.add(datum.epc);
                        mapEpc.put(datum.epc, i);
                        mapEpc.put("epc", datum.epc);
                        mapEpc.put("item", datum.item);
                        mapEpc.put("berat", datum.berat);
                        mapEpc.put("cuci", datum.jml_cuci);
                        listEpc.add(mapEpc);
                        i++;

                        Float litersOfPetrol=Float.parseFloat(datum.berat);
                        tb += litersOfPetrol;
                        tvTagSum.setText( String.valueOf(i) );
                    }

                    tvTagSumBerat.setText( new Formatter(Locale.ENGLISH).format("%.1f", tb).toString() );

                    adapter = new EPCRusakAdapter(getActivity(), listEpc);
                    lvEpc.setAdapter(adapter);
                    Helper.getListViewSize(lvEpc);
                }
            }
            @Override
            public void onFailure(Call<RusakListResponse> call, Throwable t) {
                call.cancel();
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateUI(String no ) {
        epcSet = new HashSet<String>();
        listEpc = new ArrayList<HashMap>();

        List<String> myList = new ArrayList<String>();
        SQLiteDatabase db = mHelper.getReadableDatabase();

        String noTrans = noTransaksi.getText().toString();
        btnSimpan.setVisibility(View.VISIBLE);
        btnStart.setVisibility(View.VISIBLE);

        if(no != ""){
            noTrans = no;
            btnSimpan.setVisibility(View.GONE);
            btnStart.setVisibility(View.GONE);
        }

        String whereClause = InputContract.TaskEntry.NO_TRANSAKSI+"=?";
        String [] whereArgs = {noTrans};

        Cursor cursor_header = db.query(TABLE_RUSAK,
                new String[]{InputContract.TaskEntry._ID, InputContract.TaskEntry.NO_TRANSAKSI, InputContract.TaskEntry.TANGGAL, InputContract.TaskEntry.PIC, CATATAN,DEFECT},
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
            int id_defect = cursor_header.getColumnIndex(DEFECT);
            int id_catatan = cursor_header.getColumnIndex(CATATAN);

            String no_transaksi = cursor_header.getString(id_no_transaksi);
            String tanggal = cursor_header.getString(id_tanggal);
            String pic = cursor_header.getString(id_pic);
            String defect = cursor_header.getString(id_defect);
            String catatan = cursor_header.getString(id_catatan);

            noTransaksi.setText(no_transaksi);
            tgl.setText(tanggal);
            txtCatatan.setText(catatan);
            getIndexByString(spinner_pic,pic,"nama_user");
            getIndexByString(spinner_defect,defect,"defect");
        }

        String selectQuery = "SELECT A.*,C.jenis,C.berat " +
                "FROM " + TABLE_RUSAK_DETAIL + " A " +
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
            int idjmlcuci = cursor.getColumnIndex(JML_CUCI);

            long thisId = cursor.getLong(id);
            String epcs = cursor.getString(epc);
            String barang = cursor.getString(item);
            String berat = cursor.getString(idberat);
            String jmlcuci = cursor.getString(idjmlcuci);

            mapEpc = new HashMap();
            epcSet.add(epcs);
            mapEpc.put(epcs, i);
            mapEpc.put("epc", epcs);
            mapEpc.put("item", barang);
            mapEpc.put("berat", berat);
            mapEpc.put("cuci", jmlcuci);
            listEpc.add(mapEpc);
            i++;

            Float litersOfPetrol=Float.parseFloat(berat);
            tb += litersOfPetrol;
            tvTagSum.setText( String.valueOf(i) );
        }

        tvTagSumBerat.setText( new Formatter(Locale.ENGLISH).format("%.1f", tb).toString() );

        adapter = new EPCRusakAdapter(getActivity(), listEpc);
        lvEpc.setAdapter(adapter);

        cursor.close();
        db.close();
    }
    private void getIndexByString(Spinner spinner, String string,String column) {
        for (int i = 0; i < spinner.getCount(); i++) {
            Cursor value = (Cursor) spinner.getItemAtPosition(i);
            String name = value.getString(value.getColumnIndex(column));
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
            mapEpc.clear(); //store EPC position
        if(adapter != null)
            adapter.notifyDataSetChanged();
        allCount = 0 ;
        tvTagSum.setText("0");
        tvTagSumBerat.setText("0");
        if(RusakActivity.mSetEpcs != null){
            RusakActivity.mSetEpcs.clear();
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
