package id.coba.kotlinpintar;

import static android.app.ActionBar.DISPLAY_SHOW_CUSTOM;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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

import java.io.Console;
import java.text.ParseException;
import java.text.SimpleDateFormat;
//import java.time.LocalDateTime;
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

import id.coba.kotlinpintar.Component.APIError;
import id.coba.kotlinpintar.Component.BottomSheetDialog;
import id.coba.kotlinpintar.Component.LoadingDialog;
import id.coba.kotlinpintar.Dto.DataKotor;
import id.coba.kotlinpintar.Dto.DefaultResponse;
import id.coba.kotlinpintar.Dto.KotorListResponse;
import id.coba.kotlinpintar.Dto.LinenKotor;
import id.coba.kotlinpintar.Dto.LinenKotorDetail;
import id.coba.kotlinpintar.Dto.Serial;
import id.coba.kotlinpintar.Rest.ApiClient;
import id.coba.kotlinpintar.Rest.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;

public class Fragment_Kotor extends Fragment implements BottomSheetDialog.BottomSheetListener,OnCheckedChangeListener,OnClickListener{
    private View view;// this fragment UI
    private TextView tvTagSum ;
    private TextView tvTagSumBerat ;
    private TextView txtTotalLinen ;
    private TextView txtTotalBerat ;
    private TextView txtTotalBeratReal ;
    private EditText tvTagSumBeratReal ;
    private ListView lvEpc;// epc list view
    private Button btnSimpan ;
    private Button btnSync ;
    private Button btnRemove ;
    private LinearLayout panelInfo;
    private LinearLayout panelInfoBarang;
    private LinearLayout panelList;
    private LinearLayout panelForm;
    private EditText edittext;
    private EditText noTransaksi;
    private TextView txtPIC;
    private TextView txtKategori;
    private TextView txtInfeksius;
    private EditText tgl;
    private Button buttonSheet;
    private Spinner spinner_pic;
    private Spinner spinner_kategori;
    private Spinner spinner_infeksius;
    private EditText person;
    private CheckBox checkMulti ;//multi model check box
    private ArrayList<HashMap> listEpc;
    private Set<String> epcSet = null ;
    private HashMap mapEpc;
    private EPCustadapter adapter ;
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
    ApiInterface apiInterface;
    public static final String BROADCAST_LVEPC = "BROADCAST_LVEPC";

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_kotor, null);
        prefMode = getContext().getSharedPreferences("MODE", Context.MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        initView();

        return view/*super.onCreateView(inflater, container, savedInstanceState)*/;
    }



    private void initView() {
        btnSimpan = (Button) view.findViewById(R.id.button_simpan) ;
        buttonSheet = view.findViewById(R.id.button_sheet);
        btnSync = (Button) view.findViewById(R.id.button_sync) ;
        btnRemove = (Button) view.findViewById(R.id.button_remove) ;
        edittext= (EditText) view.findViewById(R.id.tanggal);
        spinner_pic= view.findViewById(R.id.spinner_pic);
        spinner_kategori= view.findViewById(R.id.spinner_kategori);
        spinner_infeksius= view.findViewById(R.id.spinner_infeksius);
        tvTagSum = (TextView) view.findViewById(R.id.textView_tag) ;
        tvTagSumBerat = (TextView) view.findViewById(R.id.textView_total_berat) ;
        tvTagSumBeratReal = (EditText) view.findViewById(R.id.textView_total_berat_real) ;
        txtPIC= (TextView) view.findViewById(R.id.txtPIC);
        txtKategori= (TextView) view.findViewById(R.id.txtKategori);
        txtInfeksius= (TextView) view.findViewById(R.id.txtInfeksius);
        txtTotalBerat= (TextView) view.findViewById(R.id.txtTotalBerat);
        txtTotalBeratReal= (TextView) view.findViewById(R.id.txtTotalBeratReal);
        txtTotalLinen= (TextView) view.findViewById(R.id.txtTotalLinen);

        panelInfo = (LinearLayout) view.findViewById(R.id.panelInfo);
        panelInfoBarang = (LinearLayout) view.findViewById(R.id.panelInfoBarang);
        panelList = (LinearLayout) view.findViewById(R.id.panelList);
        panelForm = (LinearLayout) view.findViewById(R.id.panelForm);

        lvEpc = (ListView) view.findViewById(R.id.listView);
        lvEpc.setFocusable(false);
        lvEpc.setClickable(false);
        lvEpc.setItemsCanFocus(false);
        lvEpc.setScrollingCacheEnabled(false);
        lvEpc.setOnItemClickListener(null);

        noTransaksi= (EditText) view.findViewById(R.id.no_transaksi);
        setAutoNumber();

        tgl= (EditText) view.findViewById(R.id.tanggal);


        btnSimpan.setOnClickListener(this);
        btnSync.setOnClickListener(this);
        btnRemove.setOnClickListener(this);
        edittext.setOnClickListener(this);
        buttonSheet.setOnClickListener(this);
        mHelper = new InputDbHelper(getActivity());

        updateLabel();

        loadspinner();
        String no = getArguments().getString("no_transaksi");
        panelInfo.setVisibility(View.GONE);
        panelInfoBarang.setVisibility(View.GONE);
        panelList.setVisibility(View.GONE);
        panelForm.setVisibility(View.VISIBLE);
        if (no != null){
            panelInfo.setVisibility(View.VISIBLE);
            panelInfoBarang.setVisibility(View.VISIBLE);
            panelList.setVisibility(View.VISIBLE);
            panelForm.setVisibility(View.GONE);
            updateUIBaru(no);
        }

        ((AppCompatActivity)getActivity()).getSupportActionBar().setCustomView(R.layout.abs_layout);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayOptions(androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        AppCompatTextView title = ((AppCompatActivity)getActivity()).getSupportActionBar().getCustomView().findViewById(R.id.tvTitle);
        title.setText("Form Linen Kotor");
    }
    private BroadcastReceiver bottomSheetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            String totalScan  = extras.getString("totalScan","0");
            tvTagSum.setText(totalScan) ;
            tvTagSumBerat.setText(extras.getString("totalBerat","0"));
            tvTagSumBeratReal.setText(extras.get("totalReal").toString());
            listEpc = (ArrayList<HashMap>) extras.getSerializable("listEpc");
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(bottomSheetReceiver, new IntentFilter(BROADCAST_LVEPC));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(bottomSheetReceiver);
        super.onPause();
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
        adapterPIC = new SimpleCursorAdapter(getActivity(), R.layout.spinner_dropdown_item, serviceCursor, from, to, 0);
        adapterPIC.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner_pic.setAdapter(adapterPIC);

        final Cursor kategoriCursor = db.getKategoriCursor();
        String[] from_kategori = {"kategori"};
        int[] to_kategori = {android.R.id.text1};
        adapterKategori = new SimpleCursorAdapter(getActivity(), R.layout.spinner_dropdown_item, kategoriCursor, from_kategori, to_kategori, 0);
        adapterKategori.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner_kategori.setAdapter(adapterKategori);

        final Cursor infeksiusCursor = db.getInfeksiusCursor();
        String[] from_infeksius = {"infeksius"};
        int[] to_infeksius = {android.R.id.text1};
        adapterInfeksius = new SimpleCursorAdapter(getActivity(), R.layout.spinner_dropdown_item, infeksiusCursor, from_infeksius, to_infeksius, 0);
        adapterInfeksius.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner_infeksius.setAdapter(adapterInfeksius);
    }
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        /*if (isChecked) isMulti = true;
        else isMulti = false;*/
    }

    @Override
    public void onButtonClicked(String text) {
//        txtScan.setText(text);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_start:

                break ;
            case R.id.button_clear_epc:

                break ;
            case R.id.button_sync:
                updateUI("");

                showToast("Sync sukses.");
                break ;
            case R.id.button_remove:
                deleteAll();
                break ;
            case R.id.button_sheet:
                Bundle data = new Bundle();
                data.putSerializable("listEpc", listEpc);
                data.putBoolean("edit",true);
                BottomSheetDialog bottomSheet = BottomSheetDialog.newInstance();
                bottomSheet.setCancelable(false);
                bottomSheet.setArguments(data);
                bottomSheet.show(getFragmentManager(), "exampleBottomSheet");
                break;
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
                if(listEpc.size() <=0){
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
                //SQLiteDatabase db = mHelper.getReadableDatabase();
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

                    /*InputDbHelper db_store = new InputDbHelper(getActivity());
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
                    }*/

                }
                if(exist) {
                    break;
                }

                LinenKotor kotor = new LinenKotor();
                kotor.NO_TRANSAKSI = no_transaksi;
                kotor.TANGGAL = tanggal;
                kotor.PIC = pic;
                kotor.F_INFEKSIUS = infeksius;
                kotor.KATEGORI = kategori;
                kotor.STATUS = "CUCI";
                kotor.TOTAL_BERAT = berat;
                kotor.TOTAL_BERAT_REAL = berat_real;
                kotor.TOTAL_QTY = qty;

               //View parentView = null;

                int i = 0;
                for (HashMap num : listEpc) {

                    LinenKotorDetail dtm = new LinenKotorDetail();
                    dtm.epc = num.get("epc").toString();
                    dtm.ruangan = num.get("rssi").toString();
                    dtm.no_transaksi =  no_transaksi;

                    kotor.detail.add(i,dtm);
                    i++;
                }

                System.out.println(kotor);

                final LoadingDialog loadingDialog = new LoadingDialog(getActivity());
                loadingDialog.startLoadingDialog();
                Call<LinenKotor> call2 = apiInterface.saveLinenKotor(kotor);
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
                                Intent intent = new Intent(getActivity(), ListKotorActivityRecyle.class);
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

                /*ContentValues values_header = new ContentValues();

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
                 */
                //Intent intent = new Intent(getActivity(), ListKotorActivity.class);
                //startActivity(intent);
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

    private void updateUIBaru(String no){
        epcSet = new HashSet<String>();
        listEpc = new ArrayList<HashMap>();

        String noTrans = noTransaksi.getText().toString();
        btnSimpan.setVisibility(View.VISIBLE);

        if(no != ""){
            noTrans = no;
        }

        Call<KotorListResponse> call2 = apiInterface.getKotor(no);
        call2.enqueue(new Callback<KotorListResponse>() {
            @Override
            public void onResponse(Call<KotorListResponse> call, retrofit2.Response<KotorListResponse> response) {
                ArrayList<DataKotor> dataKotor = response.body().getData();
                for (DataKotor data : dataKotor){
                    noTransaksi.setText(data.getNO_TRANSAKSI());
                    tgl.setText(data.getTANGGAL());
                    tvTagSumBeratReal.setText(data.getTOTAL_BERAT_REAL());
                    txtInfeksius.setText(data.getF_INFEKSIUS());
                    txtKategori.setText(data.getKATEGORI());
                    txtPIC.setText(data.getPIC());

                    getIndexByString(spinner_pic,data.getPIC());
                    getIndexKategori(spinner_kategori, data.getPIC());
                    getIndexInfeksius(spinner_infeksius, data.getF_INFEKSIUS());

                    int i = 0;
                    double tb = 0;

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

                    tvTagSumBerat.setText( new Formatter(Locale.ENGLISH).format("%.1f", tb).toString() );
                    txtTotalBerat.setText(new Formatter(Locale.ENGLISH).format("%.1f", tb).toString());
                    txtTotalBeratReal.setText(data.getTOTAL_BERAT_REAL());
                    txtTotalLinen.setText(String.valueOf(i) );
                    adapter = new EPCustadapter(getActivity(), listEpc);
                    lvEpc.setAdapter(adapter);

                }

            }
            @Override
            public void onFailure(Call<KotorListResponse> call, Throwable t) {
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

        if(no != ""){
            noTrans = no;
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
        //I("");
        showToast("Sukses hapus semua tabel..");
    }

    //show tips
    private Toast toast;
    private void showToast(String info) {
        if (toast==null) toast =  Toast.makeText(getActivity(), info, Toast.LENGTH_SHORT);
        else toast.setText(info);
        toast.show();
    }
    //key receiver



}
