package id.coba.kotlinpintar.Component;

import static id.coba.kotlinpintar.Fragment_Kotor.BROADCAST_LVEPC;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.BRMicro.Tools;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.uhf.api.cls.Reader;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import id.coba.kotlinpintar.Dto.LinenKotor;
import id.coba.kotlinpintar.Dto.Serial;
import id.coba.kotlinpintar.EPCustadapter;
import id.coba.kotlinpintar.KotorActivity;
import id.coba.kotlinpintar.ListBersihActivityRecycle;
import id.coba.kotlinpintar.R;
import id.coba.kotlinpintar.Rest.ApiClient;
import id.coba.kotlinpintar.Rest.ApiInterface;
import id.coba.kotlinpintar.Util;
import retrofit2.Call;
import retrofit2.Callback;

public class BottomSheetDialog extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;
    private LinearLayout lscan;
    private EditText txtScan;
    private View view;
    private int allCount = 0 ;
    private Set<String> epcSet = null ;
    private HashMap mapEpc;
    private ArrayList<HashMap> listEpc;
    private ArrayList<HashMap> listEpcParam;
    private EPCustadapter adapter ;
    private ListView lvEpc;
    private TextView tvTagSum ;
    private TextView tvTagSumBerat ;
    private EditText tvTagSumBeratReal ;
    private SharedPreferences prefMode;
    private Button btnStart ;
    private Button btnCekData;
    private Button btnClear ;
    private ImageView btnClose;
    private TextView txtTambah;
    ApiInterface apiInterface;
    private boolean keyControl = true;
    private boolean isRunning = false ;
    private boolean doneCheck = false ;
    private boolean isStart = false ;
    private boolean isMulti = false ;
    private Toast toast;
    private  long startTime = 0 ;
    private boolean keyUpFalg= true;
    String epc ;
    private boolean f1hidden = false;
    private LinearLayout empty_layout;
    private Boolean isEdit = false;
    ProgressBar mProgressBar;
//    public BottomSheetDialog (ArrayList<HashMap> params){
//        if ( params != null){
//            isEdit = true;
//            listEpcParam = params;
//        }
//    }

    public static BottomSheetDialog newInstance() {
        return new BottomSheetDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefMode = getContext().getSharedPreferences("MODE", Context.MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        IntentFilter filter = new IntentFilter() ;
        filter.addAction("android.rfid.FUN_KEY");
        getActivity().registerReceiver(keyReceiver, filter) ;
        if(getArguments().size() > 0 & getArguments().getBoolean("edit") == true && getArguments().get("listEpc") != null){
            isEdit = true;
            listEpcParam = (ArrayList<HashMap>) getArguments().get("listEpc");
        }
        getArguments().getString("listEpc");
        if (isEdit){
            for (HashMap num : listEpcParam) {
                Message msg = new Message() ;
                msg.what = 1 ;
                Bundle b = new Bundle();
                b.putString("epc", num.get("epc").toString());
                b.putString("exist", "0");
                if (num.get("rssi") == null){
                    b.putString("rssi", "-");
                }else{
                    b.putString("rssi", num.get("rssi").toString());
                }

                b.putString("item", num.get("item").toString());
                b.putString("berat", num.get("berat").toString());

                msg.setData(b);
                handler.sendMessage(msg);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);
        initView();

        return view;
    }


    @Override
    public int getTheme() {
        return R.style.BottomSheetDialog;
    }
    public interface BottomSheetListener {
        void onButtonClicked(String text);
    }

    @Override
    public void onPause() {
        if (isStart) {
            runInventory();
        }
        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (BottomSheetListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }

    @Override
    public void onDestroyView() {
        if (isStart) {
            isStart = false;
            isRunning = false;
            KotorActivity.mUhfrManager.stopTagInventory();
        }
        getActivity().unregisterReceiver(keyReceiver);//
        super.onDestroyView();
    }

    private void initView() {
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        txtScan= (EditText) view.findViewById(R.id.textView_scan);
        lscan = (LinearLayout) view.findViewById(R.id.lscan) ;
        btnStart = (Button) view.findViewById(R.id.button_start);
        btnClear = (Button) view.findViewById(R.id.button_clear_epc) ;
        btnClose = view.findViewById(R.id.btnClose) ;
        btnCekData = view.findViewById(R.id.btnCekData);
        txtTambah = view.findViewById(R.id.txtTambah);
        tvTagSum = (TextView) view.findViewById(R.id.textView_tag) ;
        tvTagSumBerat = (TextView) view.findViewById(R.id.textView_total_berat) ;
        tvTagSumBeratReal = (EditText) view.findViewById(R.id.textView_total_berat_real) ;
        lvEpc = (ListView) view.findViewById(R.id.listView_data);
        empty_layout= (LinearLayout)view.findViewById(R.id.layout_empty);
        lvEpc.setEmptyView(empty_layout);

        lvEpc.setFocusable(false);
        lvEpc.setClickable(false);
        lvEpc.setItemsCanFocus(false);
        lvEpc.setScrollingCacheEnabled(false);
        lvEpc.setOnItemClickListener(null);

        tvTagSumBerat.setText("0");
        tvTagSum.setText("0");

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!doneCheck){
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Anda belum cek data !!");
                    alertDialog.setMessage("Pastikan sudah klik cek data.");

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            cekData();
                            dismiss();
                        }
                    });

                    alertDialog.show();
                }else{
                    dismiss();
                }
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearEpc();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runInventory() ;
            }
        });

        btnCekData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cekData();
            }
        });

        txtTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanning(txtScan.getText().toString().replace("\n",""), false);
            }
        });

        txtScan.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.e("f1", String.valueOf(event.getKeyCode()));

                if ((keyCode == KeyEvent.KEYCODE_ENTER )) {
                    scanning(txtScan.getText().toString().replace("\n",""), false);
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
            mProgressBar.setVisibility(View.VISIBLE);
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
                            }
                        }else{
                            b.putString("rssi", "");
                            b.putString("item", "");
                            b.putString("berat", "0");
                            b.putString("exist", "");
                        }

                        msg.setData(b);
                        handler.sendMessage(msg);

                        mProgressBar.setVisibility(View.GONE);
                        btnCekData.setEnabled(true);
                    }
                    @Override
                    public void onFailure(Call<Serial> call, Throwable t) {
                        Log.d("onFailure: ", t.getMessage());
                        call.cancel();
                        txtScan.setText("");
                        Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.GONE);
                        btnCekData.setEnabled(true);
                    }
                });
            }catch (Exception ex) {
                Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);
                btnCekData.setEnabled(true);
            }
        }else{
            b.putString("rssi", "");
            b.putString("item", "");
            b.putString("berat", "0");
            b.putString("exist", "");

            msg.setData(b);
            handler.sendMessage(msg);
            mProgressBar.setVisibility(View.GONE);
            btnCekData.setEnabled(true);
        }


    }

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
    private Runnable inventoryTask = new Runnable() {
        @Override
        public void run() {
            while(isRunning){
                if (isStart) {
                    List<Reader.TAGINFO> list1 ;
                    if (isMulti) { // multi mode
                        list1 = KotorActivity.mUhfrManager.tagInventoryRealTime();
                    }else{
                        list1 = KotorActivity.mUhfrManager.tagInventoryByTimer((short)50);
                    }
                    if (list1 != null&&list1.size()>0) {//
                        for (Reader.TAGINFO tfs:list1) {
                            byte[] epcdata = tfs.EpcId;
                            epc = Tools.Bytes2HexString(epcdata, epcdata.length);
                            int rssi = tfs.RSSI;

                            Message msg = new Message() ;
                            msg.what = 1 ;
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
                                                b.putString("exist", "");
                                            }
                                        }else{
                                            b.putString("rssi", "");
                                            b.putString("item", "");
                                            b.putString("berat", "0");
                                            b.putString("exist", "");
                                        }

                                        msg.setData(b);
                                        handler.sendMessage(msg);
                                    }
                                    @Override
                                    public void onFailure(Call<Serial> call, Throwable t) {
                                        call.cancel();
                                        txtScan.setText("");
                                        Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
//                                      throw new RuntimeException(t.getMessage());
                                    }
                                });
                                /*String selectQuery = "SELECT A.*,B.jenis,B.berat FROM " + TABLE_BARANG + " A LEFT JOIN " + TABLE_JENIS_BARANG + " B ON A.ID_JENIS=B.ID_JENIS WHERE serial='" + epc + "'";
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
                                handler.sendMessage(msg);*/
                            }catch (Exception ex) {
                                Toast.makeText(getActivity(),ex.getMessage().toString(),Toast.LENGTH_LONG);
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
    private void showToast(String info) {
        if (toast==null) toast =  Toast.makeText(getActivity(), info, Toast.LENGTH_SHORT);
        else toast.setText(info);
        toast.show();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        f1hidden = hidden;
        if (hidden) {
            if (isStart) runInventory();// stop inventory
        }
        if (KotorActivity.mUhfrManager!=null) KotorActivity.mUhfrManager.setCancleInventoryFilter();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Intent intent = new Intent(BROADCAST_LVEPC);
        intent.putExtra("listEpc", listEpc);
        intent.putExtra("totalScan", tvTagSum.getText());
        intent.putExtra("totalBerat", tvTagSumBerat.getText());
        intent.putExtra("totalReal", tvTagSumBeratReal.getText());
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }

    private void hitungBerat(String berat){
        if(berat == null){
            berat = "0";
        }
        double tb =Float.parseFloat(tvTagSumBerat.getText().toString());
        Float litersOfPetrol=Float.parseFloat(berat);
        tb += litersOfPetrol;
        tvTagSumBerat.setText( new Formatter(Locale.ENGLISH).format("%.1f", tb).toString() );
        tvTagSumBeratReal.setText( new Formatter(Locale.ENGLISH).format("%.1f", tb).toString() );
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
    }
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

                        epcSet.add(epc);
                        mapEpc.put(epc, 0);
                        mapEpc.put("epc", epc);
                        mapEpc.put("rssi", rssi);
                        mapEpc.put("item", item);
                        mapEpc.put("berat", berat);
                        mapEpc.put("exist", exist);

                        listEpc.add(mapEpc);

                        hitungBerat(berat);
                        adapter = new EPCustadapter(getActivity(), listEpc);
                        lvEpc.setAdapter(adapter);

                        tvTagSum.setText("" + listEpc.size());
                        Util.play(1, 0);
                        KotorActivity.mSetEpcs=epcSet;
                    }else{
                        if (epcSet.contains(epc.replace("\n",""))) {//set already exit
                            //position = mapEpc.get(epc);
//                            EpcDataModel epcOld = listEpc.get(position);
//                            listEpc.set(position, epcOld);
                            for (HashMap num : listEpc) {
                                if (num.get("epc") == epc){
                                    num.put("rssi", rssi);
                                    num.put("item", item);
                                    num.put("berat", berat);
                                    num.put("exist", exist);
                                }
                            }
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

                            hitungBerat(berat);

                            KotorActivity.mSetEpcs = epcSet;

                        }
                        tvTagSum.setText("" + listEpc.size());
                        adapter.notifyDataSetChanged();
                        Util.play(1, 1);

                    }
                    break ;
            }
        }
    } ;
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
}
