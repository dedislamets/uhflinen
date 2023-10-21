package id.coba.kotlinpintar;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.google.gson.Gson;
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
import id.coba.kotlinpintar.Component.APIError;
import id.coba.kotlinpintar.Component.LoadingDialog;
import id.coba.kotlinpintar.Dto.Barang;
import id.coba.kotlinpintar.Dto.BarangHeader;
import id.coba.kotlinpintar.Dto.JumlahCuci;
import id.coba.kotlinpintar.Dto.LinenRusak;
import id.coba.kotlinpintar.Dto.Serial;
import id.coba.kotlinpintar.Helper.Helper;
import id.coba.kotlinpintar.Rest.ApiClient;
import id.coba.kotlinpintar.Rest.ApiInterface;
import id.coba.kotlinpintar.Rest.Objek;
import retrofit2.Call;
import retrofit2.Callback;

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
    private EditText txtScan;
    private LinearLayout lscan;

    private Set<String> epcSet = null ; //store different EPC
//    private List<HashMap> listEpc = null;//EPC list
    private HashMap mapEpc; //store EPC position
    private EPCRegadapter adapter ;//epc list adapter

    private boolean isMulti = false ;// multi mode flag
    private int allCount = 0 ;// inventory count

    private long lastTime =  0L ;
    private TextView txtTambah ;
    private InputDbHelper mHelper;
    private ArrayList<String> listSql = new ArrayList<String>();
    private ArrayAdapter adapterSql = null;
    private List kontenList = new ArrayList<>();
    final Calendar myCalendar = Calendar.getInstance();
    public Spinner spinner;
    public Spinner id_jenis;
    private SimpleCursorAdapter adapterRuangan ;
    private SimpleCursorAdapter adapterJenis ;
    private SharedPreferences prefMode;
    private LinearLayout empty_layout;
    private ImageView btnUlangi ;
    ApiInterface apiInterface;
    ProgressBar mProgressBar;
    private ImageView btnCekData;
    private TextView txtCekData;
    private boolean doneCheck = false ;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    String epc = msg.getData().getString("epc");
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
                        listEpc.add(mapEpc);
                        adapter = new EPCRegadapter(getActivity(), listEpc);
                        lvEpc.setAdapter(adapter);
                        Helper.getListViewSize(lvEpc);

                        Util.play(1, 0);
                        Activity_Register_Linen.mSetEpcs=epcSet;
                        tvTagSum.setText("" + listEpc.size());
                    }else{
                        if (epcSet.contains(epc.replace("\n",""))) {//set already exit
//                            position = mapEpc.get(epc);
//                            EpcDataModel epcOld = listEpc.get(position);
//                            listEpc.set(position, epcOld);
                            for (HashMap num : listEpc) {
                                if (num.get("epc") == epc){
                                    num.put("status", status);
                                }
                            }
                        }else{

                            mapEpc = new HashMap();
                            epcSet.add(epc);
                            mapEpc.put(epc, listEpc.size());
                            mapEpc.put("epc", epc);
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
                        Helper.getListViewSize(lvEpc);

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
        prefMode = getContext().getSharedPreferences("MODE", Context.MODE_PRIVATE);
        view= inflater.inflate(R.layout.insert_master_barang, null);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
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
        btnUlangi = (ImageView) view.findViewById(R.id.btnUlangi) ;
        btnSimpan = (Button) view.findViewById(R.id.button_simpan) ;
        btnCekData = (ImageView) view.findViewById(R.id.btnCekData) ;
        txtCekData = (TextView) view.findViewById(R.id.txtCekData) ;
        btnSync = (Button) view.findViewById(R.id.button_sync) ;
        btnRemove = (Button) view.findViewById(R.id.button_remove) ;
        edittext= (EditText) view.findViewById(R.id.tanggal);

        noTransaksi= (EditText) view.findViewById(R.id.no_transaksi);
        tgl= (EditText) view.findViewById(R.id.tanggal);
        spinner= (Spinner) view.findViewById(R.id.spinner1);
        id_jenis= (Spinner) view.findViewById(R.id.spinner2);
        lscan = (LinearLayout) view.findViewById(R.id.lscan) ;
        txtTambah = (TextView) view.findViewById(R.id.txtTambah) ;
        empty_layout= (LinearLayout)view.findViewById(R.id.layout_empty);
        lvEpc.setEmptyView(empty_layout);

        lvEpc.setFocusable(false);
        lvEpc.setClickable(false);
        lvEpc.setItemsCanFocus(false);
        lvEpc.setScrollingCacheEnabled(false);
        lvEpc.setOnItemClickListener(null);
        btnStart.setOnClickListener(this);
        btnUlangi.setOnClickListener(this);
        btnSimpan.setOnClickListener(this);
        edittext.setOnClickListener(this);
        txtScan= (EditText) view.findViewById(R.id.textView_scan);
        txtScan.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_ENTER)) {
                    scanning(txtScan.getText().toString().replace("\n",""), false);
                }
                return false;
            }
        });
        btnCekData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cekData();
            }
        });
        txtCekData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cekData();
            }
        });
        txtTambah.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                scanning(txtScan.getText().toString().replace("\n",""), false);
            }
        });

        if(prefMode.getBoolean("MODE", false) == true){
            lscan.setVisibility(GONE);
            btnStart.setEnabled(true);
        }else{
            btnStart.setEnabled(false);
        }

        mHelper = new InputDbHelper(getActivity());

        updateLabel();
        loadspinner();

        String no = getArguments().getString("no_transaksi");

        if (no != null){
            updateUI(no);
        }

    }

    private void cekData(){
        btnCekData.setEnabled(false);
        if (listEpc != null) {
            for (HashMap num : listEpc) {
                scanning(num.get("epc").toString(), true);
            }
        }
        doneCheck = true;
    }

    private void scanning(String epc, Boolean isCheck){
        txtScan.setText("");
        Message msg = new Message() ;
        msg.what = 1 ;
        Bundle b = new Bundle();
        b.putString("epc", epc);
        b.putString("exist", "0");


        if (isCheck){
            //mProgressBar.setVisibility(VISIBLE);
            try {
                Call<Serial> call2 = apiInterface.infoSerial(epc);
                call2.enqueue(new Callback<Serial>() {
                    @Override
                    public void onResponse(Call<Serial> call, retrofit2.Response<Serial> response) {
                        Serial userList = response.body();
                        if(userList != null) {
                            List<Serial.Datum> datumList = userList.data;
                            for (Serial.Datum datum : datumList) {
                                b.putString("status", "Terdaftar");
                            }
                        }else{
                            b.putString("status", "-");
                        }

                        msg.setData(b);
                        handler.sendMessage(msg);

                        //mProgressBar.setVisibility(GONE);
                        btnCekData.setEnabled(true);
                    }
                    @Override
                    public void onFailure(Call<Serial> call, Throwable t) {
                        Log.d("onFailure: ", t.getMessage());
                        call.cancel();
                        txtScan.setText("");
                        Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        //mProgressBar.setVisibility(GONE);
                        btnCekData.setEnabled(true);
                    }
                });
            }catch (Exception ex) {
                Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                //mProgressBar.setVisibility(GONE);
                btnCekData.setEnabled(true);
            }
        }else{
            b.putString("rssi", "");
            b.putString("item", "");
            b.putString("berat", "0");
            b.putString("exist", "");

            msg.setData(b);
            handler.sendMessage(msg);
            //mProgressBar.setVisibility(GONE);
            btnCekData.setEnabled(true);
        }


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
                        for (Serial.Datum datum : datumList) {
                            b.putString("status", "Terdaftar");
                        }
                    }else{
                        b.putString("status", "-");
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

        }catch (Exception ex){
            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
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

                            Message msg = new Message();
                            msg.what = 1;
                            Bundle b = new Bundle();
                            b.putString("epc", epc);

                            Call<Serial> call2 = apiInterface.infoSerial(epc);
                            call2.enqueue(new Callback<Serial>() {
                                @Override
                                public void onResponse(Call<Serial> call, retrofit2.Response<Serial> response) {
                                    Serial userList = response.body();
                                    if(userList != null) {
                                        List<Serial.Datum> datumList = userList.data;
                                        for (Serial.Datum datum : datumList) {
                                            b.putString("status", "Terdaftar");
                                        }
                                    }else{
                                        b.putString("status", "-");
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
                if(Activity_Register_Linen.mUhfrManager == null){
                    showToast("Fungsi ini berlaku hanya untuk handheld");
                }else{
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
                }

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
            case R.id.btnUlangi:
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
                if(!doneCheck){
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Anda belum cek data !!");
                    alertDialog.setMessage("Mencoba cek data ?");

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            cekData();
                        }
                    });

                    alertDialog.show();
                }else{
                    final String tanggal = ((EditText) getActivity().findViewById(R.id.tanggal)).getText().toString();
                    Cursor qc = adapterRuangan.getCursor();
                    final String ruang = qc.getString(qc.getColumnIndex("nama_ruangan"));
                    Cursor qjenis = adapterJenis.getCursor();
                    final String id_jenis = qjenis.getString(qjenis.getColumnIndex("id_jenis"));

                    if(lvEpc.getCount() <=0){
                        Toast.makeText(getActivity(),"Belum ada data linen di scan.!",Toast.LENGTH_SHORT).show();
                        break;
                    }

                    View parentView = null;

                    BarangHeader brg = new BarangHeader();
                    for (int i = 0; i < lvEpc.getCount(); i++) {
                        parentView = getViewByPosition(i, lvEpc);

                        final String epcName = ((TextView) parentView.findViewById(R.id.textView_epc)).getText().toString();
                        final String status = ((TextView) parentView.findViewById(R.id.status_epc)).getText().toString();

                        if(status != "Terdaftar") {

                            Barang brg_detail = new Barang();

                            brg_detail.serial = epcName;
                            brg_detail.id_jenis = id_jenis;
                            brg_detail.nama_ruangan = ruang;
                            brg_detail.tanggal_register = tanggal;
                            brg.detail.add(i,brg_detail);
                        }
                    }

                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Yakin akan di simpan?");
                    alertDialog.setMessage("Linen ini akan didaftarkan.");

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            final LoadingDialog loadingDialog = new LoadingDialog(getActivity());
                            loadingDialog.startLoadingDialog();
                            Call<BarangHeader> call2 = apiInterface.saveBarang(brg);
                            call2.enqueue(new Callback<BarangHeader>() {
                                @Override
                                public void onResponse(Call<BarangHeader> call, retrofit2.Response<BarangHeader> response) {
                                    System.out.println(response);
                                    BarangHeader defaultResponse = response.body();
                                    if(defaultResponse == null && response.errorBody() != null){
                                        APIError message = new Gson().fromJson(response.errorBody().charStream(), APIError.class);
                                        Toast.makeText(getContext(), message.getMessage(), Toast.LENGTH_LONG).show();
                                    }else{
                                        if(defaultResponse.Error == false){
                                            showToast("Sukses Menyimpan !!");
                                            Intent intent = new Intent(getActivity(), ListRegisterLinen.class);
                                            startActivity(intent);
                                        }
                                    }
                                    loadingDialog.dismissDialog();
                                }
                                @Override
                                public void onFailure(Call<BarangHeader> call, Throwable t) {
                                    call.cancel();
                                    loadingDialog.dismissDialog();
                                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    alertDialog.show();
                }

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
        //SQLiteDatabase db = mHelper.getReadableDatabase();

        String noTrans = noTransaksi.getText().toString();
        btnSimpan.setVisibility(VISIBLE);
        btnStart.setVisibility(VISIBLE);
        btnClear.setVisibility(VISIBLE);

        if(no != ""){
            noTrans = no;
            btnSimpan.setVisibility(GONE);
            btnStart.setVisibility(GONE);
            btnClear.setVisibility(GONE);
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
        if(Activity_Register_Linen.mSetEpcs != null){
            Activity_Register_Linen.mSetEpcs.clear();
        }
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
            /*@Override
            public boolean isEnabled(int position) {
                return position != 0;
            }*/

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
//                if (position == 0) {
//                    tv.setTextColor(Color.GRAY);
//                } else {
//                    tv.setTextColor(Color.BLACK);
//                }
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
            /*@Override
            public boolean isEnabled(int position) {
                return position != 0;
            }*/

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
//                if (position == 0) {
//                    tv.setTextColor(Color.GRAY);
//                } else {
//                    tv.setTextColor(Color.BLACK);
//                }
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
