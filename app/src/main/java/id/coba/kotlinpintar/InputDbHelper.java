package id.coba.kotlinpintar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static id.coba.kotlinpintar.InputContract.TaskEntry.CURRENT_INSERT;
import static id.coba.kotlinpintar.InputContract.TaskEntry.EPC;
import static id.coba.kotlinpintar.InputContract.TaskEntry.NO_TRANSAKSI;
import static id.coba.kotlinpintar.InputContract.TaskEntry.STATUS;


public class InputDbHelper extends SQLiteOpenHelper {
    public static final String COLUMN_ID = "id";

    public static final String TABLE_RUANGAN = "ruangan";
    public static final String ID_RUANGAN = "id";
    public static final String NAMA_RUANGAN = "nama_ruangan";

    public static final String TABLE_BARANG = "barang";
    public static final String ID_BARANG = "id";
    public static final String SERIAL = "serial";
    public static final String ID_JENIS = "id_jenis";
    public static final String TANGGAL = "tanggal_register";

    public static final String TABLE_JENIS_BARANG = "jenis_barang";
    public static final String BERAT = "berat";
    public static final String JENIS = "jenis";

    public static final String TABLE_PIC = "pic";
    public static final String ID_USER = "id_user";
    public static final String NAMA_USER = "nama_user";

    public static final String TABLE_BERSIH = "linen_bersih";
    public static final String TOTAL_BERAT = "total_berat";
    public static final String TOTAL_BERAT_REAL = "total_berat_real";
    public static final String TOTAL_QTY = "total_qty";
    public static final String KATEGORI = "kategori";

    public static final String TABLE_BERSIH_DETAIL = "linen_bersih_detail";
    public static final String STATUS_LINEN = "status_linen";
    public static final String CHECKED = "checked";
    public static final String KELUAR = "keluar";

    public static final String TABLE_KATEGORI = "tb_kategori";
    public static final String ID_KATEGORI = "id_kategori";

    public static final String TABLE_KELUAR = "linen_keluar";
    public static final String NO_REFERENSI = "no_referensi";
    public static final String KOTOR = "kotor";

    public static final String TABLE_KELUAR_DETAIL = "linen_keluar_detail";

    public static final String TABLE_RUSAK = "linen_rusak";
    public static final String CATATAN = "catatan";
    public static final String DEFECT = "defect";
    public static final String READY = "qty_keluar";
    public static final String FLAG_AMBIL = "flag_ambil";

    public static final String TABLE_RUSAK_DETAIL = "linen_rusak_detail";
    public static final String JML_CUCI = "jml_cuci";

    public static final String TABLE_REQUEST = "request_linen";
    public static final String NO_REQUEST = "no_request";
    public static final String TGL_REQUEST = "tgl_request";
    public static final String REQUESTOR = "requestor";
    public static final String TOTAL_REQUEST = "total_request";
    public static final String STATUS_REQUEST = "status_request";

    public static final String TABLE_REQUEST_DETAIL = "request_linen_detail";
    public static final String QTY = "qty";

    public static final String TABLE_SETTING = "setting";
    public static final String BASE_URL = "url";
    public static final String WEB_URL = "web";

    public static final String TABLE_DEFECT = "tb_defect";
    public static final String ID_DEFECT = "id_defect";

    public static final String HARGA = "harga";
    public static final String JENIS_MEDIS = "fmedis";
    public static final String JENIS_INFEKSIUS = "finfeksius";
    public static final String SPESIFIKASI = "spesifikasi";

    public static final String TABLE_HISTORY = "linen_history";
    public static final String TABLE_NOTIFIKASI = "tb_notifikasi";
    public static final String ID_NOTIFIKASI = "id_notifikasi";
    public static final String SHORT_MSG = "short_msg";
    public static final String LONG_MSG = "long_msg";
    public static final String DIBACA = "read";
    public static final String PAGE_URL = "url";

    public static final String TABLE_LOGIN = "tb_users";
    public static final String DEPARTMENT = "department";
    public static final String PASSWORD = "password";
    public static final String EMAIL = "email";
    public static final String TOKEN = "token";

    public static final String TABLE_INFEKSIUS = "tb_infeksius";
    public static final String NAMA_INFEKSIUS = "infeksius";
    public static final String ID_INFEKSIUS = "id_infeksius";


    public InputDbHelper(Context context) {
        super(context, InputContract.DB_NAME, null, InputContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE IF NOT EXISTS " + InputContract.TaskEntry.TABLE + "    ( " +
                InputContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NO_TRANSAKSI + " TEXT NOT NULL, " +
                InputContract.TaskEntry.TANGGAL + " DATE NOT NULL, " +
                InputContract.TaskEntry.STATUS + " TEXT NOT NULL DEFAULT 0, " +
                InputContract.TaskEntry.SYNC + " TEXT NOT NULL DEFAULT 0, " +
                InputContract.TaskEntry.PIC + " TEXT NOT NULL, " +
                KATEGORI + " TEXT NOT NULL, " +
                JENIS_INFEKSIUS + " TEXT NOT NULL, " +
                TOTAL_BERAT + " TEXT NOT NULL DEFAULT 0, " +
                TOTAL_BERAT_REAL + " TEXT NOT NULL DEFAULT 0, " +
                TOTAL_QTY + " TEXT NOT NULL DEFAULT 0, " +
                InputContract.TaskEntry.CURRENT_INSERT +  " DEFAULT CURRENT_TIMESTAMP " + ")";


        db.execSQL(createTable);
        String createTableDetail = "CREATE TABLE IF NOT EXISTS " + InputContract.TaskEntry.TABLE_DETAIL
                + "    ( " +
                InputContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NO_TRANSAKSI + " TEXT NOT NULL, " +
                InputContract.TaskEntry.EPC + " TEXT NOT NULL, " +
                InputContract.TaskEntry.ITEM + " TEXT NOT NULL, " +
                InputContract.TaskEntry.ROOM + " TEXT NOT NULL, " +
                InputContract.TaskEntry.CURRENT_INSERT +  " DEFAULT CURRENT_TIMESTAMP "+ ")";

        db.execSQL(createTableDetail);

        //Tabel Linen Bersih
        String createTableBersih = "CREATE TABLE IF NOT EXISTS " + TABLE_BERSIH + "    ( " +
                InputContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NO_TRANSAKSI + " TEXT NOT NULL, " +
                InputContract.TaskEntry.TANGGAL + " DATE NOT NULL, " +
                InputContract.TaskEntry.STATUS + " TEXT NOT NULL, " +
                InputContract.TaskEntry.SYNC + " TEXT NOT NULL DEFAULT 0, " +
                InputContract.TaskEntry.PIC + " TEXT NOT NULL, " +
                TOTAL_BERAT + " TEXT NOT NULL DEFAULT 0, " +
                TOTAL_QTY + " TEXT NOT NULL DEFAULT 0, " +
                KATEGORI + " TEXT NOT NULL DEFAULT '', " +
                InputContract.TaskEntry.CURRENT_INSERT +  " DEFAULT CURRENT_TIMESTAMP " + ")";


        db.execSQL(createTableBersih);
        String createTableBersihDetail = "CREATE TABLE IF NOT EXISTS " + TABLE_BERSIH_DETAIL
                + "    ( " +
                InputContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NO_TRANSAKSI + " TEXT NOT NULL, " +
                InputContract.TaskEntry.EPC + " TEXT NOT NULL, " +
                InputContract.TaskEntry.ITEM + " TEXT NOT NULL, " +
                InputContract.TaskEntry.ROOM + " TEXT NOT NULL, " +
                STATUS_LINEN + " TEXT NOT NULL, " +
                BERAT + " TEXT NOT NULL, " +
                CHECKED + " TEXT NOT NULL DEFAULT 0, " +
                KELUAR + " TEXT NOT NULL DEFAULT 0, " +
                InputContract.TaskEntry.CURRENT_INSERT +  " DEFAULT CURRENT_TIMESTAMP "+ ")";

        db.execSQL(createTableBersihDetail);

        //Linen Keluar
        String createTableKeluar = "CREATE TABLE IF NOT EXISTS " + TABLE_KELUAR + "    ( " +
                InputContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NO_TRANSAKSI + " TEXT NOT NULL, " +
                InputContract.TaskEntry.TANGGAL + " DATE NOT NULL, " +
                NAMA_RUANGAN + " TEXT NOT NULL, " +
                InputContract.TaskEntry.SYNC + " TEXT NOT NULL DEFAULT 0, " +
                InputContract.TaskEntry.STATUS + " TEXT NOT NULL DEFAULT 0, " +
                InputContract.TaskEntry.PIC + " TEXT NOT NULL, " +
                NO_REFERENSI + " TEXT NOT NULL DEFAULT '', " +
                InputContract.TaskEntry.CURRENT_INSERT +  " DEFAULT CURRENT_TIMESTAMP " + ")";


        db.execSQL(createTableKeluar);
        String createTableKeluarDetail = "CREATE TABLE IF NOT EXISTS " + TABLE_KELUAR_DETAIL
                + "    ( " +
                InputContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NO_TRANSAKSI + " TEXT NOT NULL, " +
                InputContract.TaskEntry.EPC + " TEXT NOT NULL, " +
                InputContract.TaskEntry.ITEM + " TEXT NOT NULL, " +
                BERAT + " TEXT NOT NULL, " +
                KOTOR + " TEXT NOT NULL DEFAULT 0, " +
                InputContract.TaskEntry.CURRENT_INSERT +  " DEFAULT CURRENT_TIMESTAMP "+ ")";

        db.execSQL(createTableKeluarDetail);

        //Linen Rusak
        String createTableRusak = "CREATE TABLE IF NOT EXISTS " + TABLE_RUSAK + "    ( " +
                InputContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NO_TRANSAKSI + " TEXT NOT NULL, " +
                InputContract.TaskEntry.TANGGAL + " DATE NOT NULL, " +
                InputContract.TaskEntry.SYNC + " TEXT NOT NULL DEFAULT 0, " +
                InputContract.TaskEntry.PIC + " TEXT NOT NULL, " +
                DEFECT + " TEXT NOT NULL DEFAULT 'SOBEK', " +
                CATATAN + " TEXT NOT NULL DEFAULT '', " +
                InputContract.TaskEntry.CURRENT_INSERT +  " DEFAULT CURRENT_TIMESTAMP " + ")";


        db.execSQL(createTableRusak);
        String createTableRusakDetail = "CREATE TABLE IF NOT EXISTS " + TABLE_RUSAK_DETAIL
                + "    ( " +
                InputContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NO_TRANSAKSI + " TEXT NOT NULL, " +
                InputContract.TaskEntry.EPC + " TEXT NOT NULL, " +
                InputContract.TaskEntry.ITEM + " TEXT NOT NULL, " +
                JML_CUCI + " TEXT NOT NULL DEFAULT 0, " +
                BERAT + " TEXT NOT NULL DEFAULT 0, " +
                InputContract.TaskEntry.CURRENT_INSERT +  " DEFAULT CURRENT_TIMESTAMP "+ ")";

        db.execSQL(createTableRusakDetail);

        //Linen Request
        String createTableRequest = "CREATE TABLE IF NOT EXISTS " + TABLE_REQUEST + "    ( " +
                InputContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NO_REQUEST + " TEXT NOT NULL, " +
                TGL_REQUEST + " DATE NOT NULL, " +
                NAMA_RUANGAN + " TEXT NOT NULL, " +
                REQUESTOR + " TEXT NOT NULL, " +
                TOTAL_REQUEST + " TEXT NOT NULL DEFAULT '0', " +
                STATUS_REQUEST + " TEXT NOT NULL DEFAULT 'Pending', " +
                InputContract.TaskEntry.CURRENT_INSERT +  " DEFAULT CURRENT_TIMESTAMP " + ")";


        db.execSQL(createTableRequest);
        String createTableRequestDetail = "CREATE TABLE IF NOT EXISTS " + TABLE_REQUEST_DETAIL
                + "    ( " +
                InputContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NO_REQUEST + " TEXT NOT NULL, " +
                JENIS + " TEXT NOT NULL, " +
                QTY + " TEXT NOT NULL DEFAULT 0, " +
                READY + " TEXT NOT NULL DEFAULT 0, " +
                FLAG_AMBIL + " TEXT NOT NULL DEFAULT 0, " +
                InputContract.TaskEntry.CURRENT_INSERT +  " DEFAULT CURRENT_TIMESTAMP "+ ")";

        db.execSQL(createTableRequestDetail);

        //Master Ruangan
        String createTableRuangan = "CREATE TABLE IF NOT EXISTS " + TABLE_RUANGAN
                + "    ( " +
                ID_RUANGAN + " INTEGER PRIMARY KEY, " +
                JENIS_INFEKSIUS + " TEXT NOT NULL DEFAULT 'Infeksius', " +
                NAMA_RUANGAN + " TEXT NOT NULL " + ")";
        db.execSQL(createTableRuangan);

        String createTableJenisBarang = "CREATE TABLE IF NOT EXISTS " + TABLE_JENIS_BARANG
                + "    ( " +
                ID_JENIS+ " INTEGER PRIMARY KEY, " +
                JENIS + " TEXT NOT NULL, " +
                HARGA + " TEXT NOT NULL DEFAULT 0, " +
                JENIS_MEDIS + " TEXT NOT NULL DEFAULT 'Medis', " +
                SPESIFIKASI + " TEXT NOT NULL DEFAULT '', " +
                BERAT + " TEXT NOT NULL " + ")";
        db.execSQL(createTableJenisBarang);

        String createTablePIC = "CREATE TABLE IF NOT EXISTS " + TABLE_PIC
                + "    ( " +
                ID_USER + " INTEGER PRIMARY KEY, " +
                NAMA_USER + " TEXT NOT NULL " +")";
        db.execSQL(createTablePIC);

        String createTableDefect = "CREATE TABLE IF NOT EXISTS " + TABLE_DEFECT
                + "    ( " +
                ID_DEFECT + " INTEGER PRIMARY KEY, " +
                DEFECT + " TEXT NOT NULL " +")";
        db.execSQL(createTableDefect);

        String createTableKategori = "CREATE TABLE IF NOT EXISTS " + TABLE_KATEGORI
                + "    ( " +
                ID_KATEGORI + " INTEGER PRIMARY KEY, " +
                KATEGORI + " TEXT NOT NULL " + ")";
        db.execSQL(createTableKategori);

        String createTableInfeksius = "CREATE TABLE IF NOT EXISTS " + TABLE_INFEKSIUS
                + "    ( " +
                ID_INFEKSIUS + " INTEGER PRIMARY KEY, " +
                NAMA_INFEKSIUS + " TEXT NOT NULL " + ")";
        db.execSQL(createTableInfeksius);

        String createTableSetting = "CREATE TABLE IF NOT EXISTS " + TABLE_SETTING
                + "    ( " +
                BASE_URL + " TEXT NOT NULL, " +
                WEB_URL + " TEXT NOT NULL " +")";
        db.execSQL(createTableSetting);

        String createTableNotifikasi = "CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFIKASI
                + "    ( " +
                ID_NOTIFIKASI + " INTEGER PRIMARY KEY, " +
                SHORT_MSG + " TEXT NOT NULL, " +
                LONG_MSG + " TEXT NOT NULL, " +
                DIBACA + " TEXT NOT NULL, " +
                PAGE_URL + " TEXT NOT NULL, " +
                InputContract.TaskEntry.CURRENT_INSERT +  " DEFAULT CURRENT_TIMESTAMP "+ ")";
        db.execSQL(createTableNotifikasi);

        String createTableLogin = "CREATE TABLE IF NOT EXISTS " + TABLE_LOGIN
                + "    ( " +
                ID_USER + " INTEGER PRIMARY KEY, " +
                NAMA_USER + " TEXT NOT NULL, " +
                EMAIL + " TEXT NOT NULL, " +
                PASSWORD + " TEXT NOT NULL, " +
                DEPARTMENT + " TEXT NOT NULL, " +
                InputContract.TaskEntry.CURRENT_INSERT +  " DEFAULT CURRENT_TIMESTAMP "+ ")";
        db.execSQL(createTableLogin);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + InputContract.TaskEntry.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + InputContract.TaskEntry.TABLE_DETAIL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BARANG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JENIS_BARANG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PIC);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BERSIH);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BERSIH_DETAIL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KATEGORI);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INFEKSIUS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KELUAR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KELUAR_DETAIL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RUSAK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RUSAK_DETAIL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REQUEST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REQUEST_DETAIL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEFECT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFIKASI);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        onCreate(db);
    }

    public boolean addName(String no_transaksi, String tanggal, String PIC) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(NO_TRANSAKSI, no_transaksi);
        contentValues.put(InputContract.TaskEntry.TANGGAL, tanggal);
        contentValues.put(InputContract.TaskEntry.PIC, PIC);


        db.insert(InputContract.TaskEntry.TABLE, null, contentValues);
        db.close();
        return true;
    }

    public boolean updateNameStatus(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(InputContract.TaskEntry.SYNC, 1);

        int result = 0;
        result = db.update(InputContract.TaskEntry.TABLE, contentValues, InputContract.TaskEntry._ID + "=" + id, null);
        db.close();

        return true;
    }
    public boolean updateSudahDibaca(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DIBACA, 1);

        int result = 0;
        result = db.update(TABLE_NOTIFIKASI, contentValues, ID_NOTIFIKASI + "=" + id, null);
        db.close();

        return true;
    }
    public boolean updateFlagSyncBersih(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(InputContract.TaskEntry.SYNC, 1);

        int result = 0;
        result = db.update(TABLE_BERSIH, contentValues, InputContract.TaskEntry._ID + "=" + id, null);
        db.close();

        return true;
    }

    public boolean updateFlagSyncKeluar(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(InputContract.TaskEntry.SYNC, 1);

        db.update(TABLE_KELUAR, contentValues, InputContract.TaskEntry._ID + "=" + id, null);
        db.close();

        return true;
    }

    public boolean updateFlagSyncRusak(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(InputContract.TaskEntry.SYNC, 1);

        int result = 0;
        result = db.update(TABLE_RUSAK, contentValues, InputContract.TaskEntry._ID + "=" + id, null);
        db.close();

        return true;
    }

    /*
     * this method will give us all the name stored in sqlite
     * */
    public Cursor getNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + InputContract.TaskEntry.TABLE + " ORDER BY " + COLUMN_ID + " ASC;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    /*
     * this method is for getting all the unsynced name
     * so that we can sync it with database
     * */
    public Cursor getUnsyncedNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + InputContract.TaskEntry.TABLE + " WHERE " + InputContract.TaskEntry.SYNC + " = 0 ;";
        return db.rawQuery(sql, null);

//        Cursor c = db.query(
//                InputContract.TaskEntry.TABLE,
//                null,
//                null,
//                null,
//                null, null, null
//        );
//
//        return c;
    }

    public Cursor getUnsyncedBersih() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_BERSIH + " WHERE " + InputContract.TaskEntry.SYNC + " = 0 ;";
        return db.rawQuery(sql, null);
    }

    public Cursor getUnsyncedKeluar() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_KELUAR + " WHERE " + InputContract.TaskEntry.SYNC + " = 0 ;";
        return db.rawQuery(sql, null);
    }
    public Cursor getUnsyncedRusak() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_RUSAK + " WHERE " + InputContract.TaskEntry.SYNC + " = 0 ;";
        return db.rawQuery(sql, null);
    }

    public Cursor getServiceCursor() {
        String selectQuery = "SELECT id as _id,* FROM " + TABLE_RUANGAN ;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }
    public Cursor deleteKotor() {
        String selectQuery = "DELETE FROM " + InputContract.TaskEntry.TABLE + " WHERE sync=1 and julianday(timestamp) - julianday('now', 'localtime')  > -4 " ;
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(selectQuery, null);
    }
    public Cursor deleteKotorAll() {
        String selectQuery = "DELETE FROM " + InputContract.TaskEntry.TABLE + " WHERE sync=1 " ;
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(selectQuery, null);
    }
    public Cursor deleteBersih() {
        String selectQuery = "DELETE FROM " + TABLE_BERSIH + " WHERE sync=1 and julianday(timestamp) - julianday('now', 'localtime')  > -4 " ;
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(selectQuery, null);
    }
    public Cursor deleteBersihAll() {
        String selectQuery = "DELETE FROM " + TABLE_BERSIH + " WHERE sync=1 " ;
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(selectQuery, null);
    }
    public Cursor deleteRusak() {
        String selectQuery = "DELETE FROM " + TABLE_RUSAK + " WHERE sync=1 and julianday(timestamp) - julianday('now', 'localtime')  > -4 " ;
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(selectQuery, null);
    }

    public Cursor deleteRusakAll() {
        String selectQuery = "DELETE FROM " + TABLE_RUSAK + " WHERE sync=1 " ;
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(selectQuery, null);
    }

    public Cursor deleteKeluarAll() {
        String selectQuery = "DELETE FROM " + TABLE_KELUAR + " WHERE sync=1 " ;
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(selectQuery, null);
    }
    public Cursor deleteKeluar() {
        String selectQuery = "DELETE FROM " + TABLE_KELUAR + " WHERE sync=1 and julianday(timestamp) - julianday('now', 'localtime')  > -4 " ;
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(selectQuery, null);
    }
    public Cursor deleteRequest() {
        String selectQuery = "DELETE FROM " + TABLE_REQUEST + " WHERE  julianday(timestamp) - julianday('now', 'localtime')  > -30 " ;
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(selectQuery, null);
    }
    public Cursor deleteRequestAll() {
        String selectQuery = "DELETE FROM " + TABLE_REQUEST  ;
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(selectQuery, null);
    }
    public Cursor getKotor(String noTrans) {
        String selectQuery = "SELECT * FROM " + InputContract.TaskEntry.TABLE + " WHERE transaksi='" + noTrans + "'" ;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }
    public Cursor getRusak(String noTrans) {
        String selectQuery = "SELECT * FROM " + TABLE_RUSAK + " WHERE transaksi='" + noTrans + "'" ;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }
    public Cursor getRequest(String noTrans) {
        String selectQuery = "SELECT * FROM " + TABLE_REQUEST + " WHERE no_request='" + noTrans + "'" ;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }
    public Cursor getKeluar(String noTrans) {
        String selectQuery = "SELECT * FROM " + TABLE_KELUAR + " WHERE transaksi='" + noTrans + "'" ;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }
    public Cursor getBersih(String noTrans) {
        String selectQuery = "SELECT * FROM " + TABLE_BERSIH + " WHERE transaksi='" + noTrans + "'" ;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }
    public Cursor getDetailEpcBersih(String noTrans, String epc) {
        String selectQuery = "SELECT * FROM " + TABLE_BERSIH_DETAIL + " WHERE transaksi='" + noTrans + "' and epc='" + epc +"'" ;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }

    public Cursor getDetailEpcKeluar(String noTrans, String epc) {
        String selectQuery = "SELECT * FROM " + TABLE_KELUAR_DETAIL + " WHERE transaksi='" + noTrans + "' and epc='" + epc +"'" ;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }

    public Cursor getDetailEpcRequest(String noTrans, String jenis) {
        String selectQuery = "SELECT * FROM " + TABLE_REQUEST_DETAIL + " WHERE no_request='" + noTrans + "' and jenis='" + jenis +"'" ;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }

    public Cursor getDetailEpcRusak(String noTrans, String epc) {
        String selectQuery = "SELECT * FROM " + TABLE_RUSAK_DETAIL + " WHERE transaksi='" + noTrans + "' and epc='" + epc +"'" ;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }

    public Cursor getDetailEpc(String noTrans, String epc) {
        String selectQuery = "SELECT * FROM " + InputContract.TaskEntry.TABLE_DETAIL + " WHERE transaksi='" + noTrans + "' and epc='" + epc +"'" ;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }
    public Cursor getNotifikasi(int noTrans) {
        String selectQuery = "SELECT * FROM " + TABLE_NOTIFIKASI + " WHERE id_notifikasi=" + noTrans ;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }

    public Cursor deleteDetailEpc(String noTrans, String epc) {
        String selectQuery = "DELETE FROM " + InputContract.TaskEntry.TABLE_DETAIL + " WHERE transaksi='" + noTrans + "' and epc='" + epc +"'" ;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }
    public Cursor deleteDetailEpcBersih(String noTrans, String epc) {
        String selectQuery = "DELETE FROM " + TABLE_BERSIH_DETAIL + " WHERE transaksi='" + noTrans + "' and epc='" + epc +"'" ;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }

    public Cursor deleteDetailEpcKeluar(String noTrans, String epc) {
        String selectQuery = "DELETE FROM " + TABLE_KELUAR_DETAIL + " WHERE transaksi='" + noTrans + "' and epc='" + epc +"'" ;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }
    public Cursor deleteDetailEpcRequest(String noTrans, String jenis) {
        String selectQuery = "DELETE FROM " + TABLE_REQUEST_DETAIL + " WHERE no_request='" + noTrans + "' and jenis='" + jenis +"'" ;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }
    public Cursor deleteDetailEpcRusak(String noTrans, String epc) {
        String selectQuery = "DELETE FROM " + TABLE_RUSAK_DETAIL + " WHERE transaksi='" + noTrans + "' and epc='" + epc +"'" ;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }

    public Cursor getDetail(String noTrans) {
        String selectQuery = "SELECT * FROM " + InputContract.TaskEntry.TABLE_DETAIL + " WHERE transaksi='" + noTrans + "'" ;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }
    public Cursor getDetailBersih(String noTrans) {
        String selectQuery = "SELECT * FROM " + TABLE_BERSIH_DETAIL + " WHERE transaksi='" + noTrans + "'" ;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }
    public Cursor getDetailKeluar(String noTrans) {
        String selectQuery = "SELECT * FROM " + TABLE_KELUAR_DETAIL + " WHERE transaksi='" + noTrans + "'" ;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }
    public Cursor getDetailRusak(String noTrans) {
        String selectQuery = "SELECT * FROM " + TABLE_RUSAK_DETAIL + " WHERE transaksi='" + noTrans + "'" ;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }

    public Cursor getPICCursor() {
        String selectQuery = "SELECT id_user as _id,* FROM " + TABLE_PIC ;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }
    public Cursor getKategoriCursor() {
        String selectQuery = "SELECT id_kategori as _id,* FROM " + TABLE_KATEGORI ;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }
    public Cursor getInfeksiusCursor() {
        String selectQuery = "SELECT id_infeksius as _id,* FROM " + TABLE_INFEKSIUS ;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }

    public Cursor getJenisBarang() {
        String selectQuery = "SELECT id_jenis as _id,* FROM " + TABLE_JENIS_BARANG ;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }
    public Cursor getDefect() {
        String selectQuery = "SELECT id_defect as _id,* FROM " + TABLE_DEFECT ;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }
    public Cursor getSetting() {
        createSetting();

        String selectQuery = "SELECT * FROM " + TABLE_SETTING;
        SQLiteDatabase db_read = this.getReadableDatabase();
        return db_read.rawQuery(selectQuery, null);
    }

    public Cursor getUser(String email, String password) {
        createSetting();

        String selectQuery = "SELECT " + NAMA_USER + "," + ID_USER + "," + DEPARTMENT + ","+ EMAIL + " FROM " + TABLE_LOGIN + " WHERE " + EMAIL + "='" + email + "' AND password='" + password + "'";
        SQLiteDatabase db_read = this.getReadableDatabase();
        return db_read.rawQuery(selectQuery, null);
    }

    public boolean periksaUser(String username, String password){
        String[] columns = { NAMA_USER, ID_USER,DEPARTMENT, EMAIL };
        SQLiteDatabase db = getReadableDatabase();
        String selection = EMAIL + "=?" + " AND " + PASSWORD + "=?";
        String[] selectionArgs = { username, password };
        Cursor cursor = db.query(TABLE_LOGIN,columns,selection,selectionArgs,null,null,null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        if(count>0)
            return  true;
        else
            return  false;
    }
    public int hitungNotif(){
        String[] columns = { ID_NOTIFIKASI, SHORT_MSG,DIBACA };
        SQLiteDatabase db = getReadableDatabase();
        String selection = DIBACA + "=?" ;
        String[] selectionArgs = { "0"};
        Cursor cursor = db.query(TABLE_NOTIFIKASI,columns,selection,selectionArgs,null,null,null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        return  count;
    }

    public void createRuangan(){
        SQLiteDatabase db = this.getWritableDatabase();
        String createTableRuangan = "CREATE TABLE IF NOT EXISTS " + TABLE_RUANGAN
                + "    ( " +
                ID_RUANGAN + " INTEGER PRIMARY KEY, " +
                JENIS_INFEKSIUS + " TEXT NOT NULL DEFAULT 'Infeksius', " +
                NAMA_RUANGAN + " TEXT NOT NULL " + ")";
        db.execSQL(createTableRuangan);
    }

    public void createInfeksius(){
        SQLiteDatabase db = this.getWritableDatabase();
        String createTableInfeksius = "CREATE TABLE IF NOT EXISTS " + TABLE_INFEKSIUS
                + "    ( " +
                ID_INFEKSIUS + " INTEGER PRIMARY KEY, " +
                NAMA_INFEKSIUS + " TEXT NOT NULL " + ")";
        db.execSQL(createTableInfeksius);
    }

    public void createNotifikasi(){
        SQLiteDatabase db = this.getWritableDatabase();
        String createTableNotifikasi = "CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFIKASI
                + "    ( " +
                ID_NOTIFIKASI + " INTEGER PRIMARY KEY, " +
                SHORT_MSG + " TEXT NOT NULL, " +
                LONG_MSG + " TEXT NOT NULL, " +
                DIBACA + " TEXT NOT NULL, " +
                PAGE_URL + " TEXT NOT NULL, " +
                InputContract.TaskEntry.CURRENT_INSERT +  " DEFAULT CURRENT_TIMESTAMP "+ ")";
        db.execSQL(createTableNotifikasi);
    }
    public void createLogin(){
        SQLiteDatabase db = this.getWritableDatabase();
        String createTableLogin = "CREATE TABLE IF NOT EXISTS " + TABLE_LOGIN
                + "    ( " +
                ID_USER + " INTEGER PRIMARY KEY, " +
                NAMA_USER + " TEXT NOT NULL, " +
                EMAIL + " TEXT NOT NULL, " +
                PASSWORD + " TEXT NOT NULL, " +
                DEPARTMENT + " TEXT NOT NULL, " +
                InputContract.TaskEntry.CURRENT_INSERT +  " DEFAULT CURRENT_TIMESTAMP "+ ")";
        db.execSQL(createTableLogin);
    }

    public void createKategori(){
        SQLiteDatabase db = this.getWritableDatabase();
        String createTableKategori = "CREATE TABLE IF NOT EXISTS " + TABLE_KATEGORI
                + "    ( " +
                ID_KATEGORI + " INTEGER PRIMARY KEY, " +
                KATEGORI + " TEXT NOT NULL " + ")";
        db.execSQL(createTableKategori);
    }

    public void createDefect(){
        SQLiteDatabase db = this.getWritableDatabase();
        String createTableDefect = "CREATE TABLE IF NOT EXISTS " + TABLE_DEFECT
                + "    ( " +
                ID_DEFECT + " INTEGER PRIMARY KEY, " +
                DEFECT + " TEXT NOT NULL " +")";
        db.execSQL(createTableDefect);
    }

    public void createSetting(){
        SQLiteDatabase db = this.getWritableDatabase();
        String createTableSetting = "CREATE TABLE IF NOT EXISTS " + TABLE_SETTING
                + "    ( " +
                BASE_URL + " TEXT NOT NULL, " +
                WEB_URL + " TEXT NOT NULL " +")";
        db.execSQL(createTableSetting);

        ContentValues values_header = new ContentValues();

        values_header.put(BASE_URL,   "https://rasb.co.id/api/");
        values_header.put(WEB_URL,   "https://rasb.co.id/");
        db.insertWithOnConflict(TABLE_SETTING,null,values_header, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public Cursor getLastHistory(String epc){
        SQLiteDatabase db = this.getWritableDatabase();

        String createTableHistory = "select * FROM (" +
                "SELECT B.transaksi,B.timestamp,epc,'kotor' as FLAG, A.STATUS , '-' as RUANGAN " +
                "FROM linen_kotor_detail B, linen_kotor A " +
                "WHERE A.transaksi=B.transaksi and epc='" + epc + "'" +
                "UNION " +
                "SELECT B.transaksi,B.timestamp,epc,'bersih' AS FLAG , A.STATUS ,'-' AS RUANGAN " +
                "FROM linen_bersih_detail B, linen_bersih A " +
                "WHERE A.transaksi=B.transaksi AND epc='" + epc + "'" +
                "UNION " +
                "SELECT B.transaksi,B.timestamp,epc,'keluar' AS FLAG , A.STATUS , A.nama_ruangan as RUANGAN " +
                "FROM linen_keluar_detail B, linen_keluar A " +
                "WHERE A.transaksi=B.transaksi and epc='" + epc + "'" +
                "UNION " +
                "SELECT B.transaksi,B.timestamp,epc,'rusak' AS FLAG ,'' AS STATUS,'-' AS RUANGAN " +
                "FROM linen_rusak_detail B, linen_rusak A " +
                "WHERE A.transaksi=B.transaksi AND epc='" + epc + "'" +
                ")history ORDER BY timestamp DESC limit 1";
        return db.rawQuery(createTableHistory, null);
    }


    public void alterTabel(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + InputContract.TaskEntry.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + InputContract.TaskEntry.TABLE_DETAIL);
//        db.execSQL("ALTER TABLE " + TABLE_NOTIFIKASI  +
//                " ADD "+ PAGE_URL + " TEXT DEFAULT '' ");
//        db.execSQL("ALTER TABLE " + TABLE_REQUEST_DETAIL  +
//                " ADD "+ FLAG_AMBIL + " INTEGER DEFAULT 0 ");
//        String createTableKeluarDetail = "CREATE TABLE IF NOT EXISTS " + TABLE_KELUAR_DETAIL
//                + "    ( " +
//                InputContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                NO_TRANSAKSI + " TEXT NOT NULL, " +
//                InputContract.TaskEntry.EPC + " TEXT NOT NULL, " +
//                InputContract.TaskEntry.ITEM + " TEXT NOT NULL, " +
//                BERAT + " TEXT NOT NULL, " +
//                KOTOR + " TEXT NOT NULL DEFAULT 0, " +
//                InputContract.TaskEntry.CURRENT_INSERT +  " DEFAULT CURRENT_TIMESTAMP "+ ")";
//
//        db.execSQL(createTableKeluarDetail);
//
//        String createTableBersihDetail = "CREATE TABLE IF NOT EXISTS " + TABLE_BERSIH_DETAIL
//                + "    ( " +
//                InputContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                NO_TRANSAKSI + " TEXT NOT NULL, " +
//                InputContract.TaskEntry.EPC + " TEXT NOT NULL, " +
//                InputContract.TaskEntry.ITEM + " TEXT NOT NULL, " +
//                InputContract.TaskEntry.ROOM + " TEXT NOT NULL, " +
//                STATUS_LINEN + " TEXT NOT NULL, " +
//                BERAT + " TEXT NOT NULL, " +
//                CHECKED + " TEXT NOT NULL DEFAULT 0, " +
//                KELUAR + " TEXT NOT NULL DEFAULT 0, " +
//                InputContract.TaskEntry.CURRENT_INSERT +  " DEFAULT CURRENT_TIMESTAMP "+ ")";
//
//        db.execSQL(createTableBersihDetail);
    }

    public void createJenisBarang(){
        SQLiteDatabase db = this.getWritableDatabase();
        String createTableRuangan = "CREATE TABLE IF NOT EXISTS " + TABLE_JENIS_BARANG
                + "    ( " +
                ID_JENIS+ " INTEGER PRIMARY KEY, " +
                JENIS + " TEXT NOT NULL, " +
                HARGA + " TEXT NOT NULL DEFAULT 0, " +
                JENIS_MEDIS + " TEXT NOT NULL DEFAULT 'Medis', " +
                SPESIFIKASI + " TEXT, " +
                BERAT + " TEXT NOT NULL " + ")";
        db.execSQL(createTableRuangan);
    }

    public void createPIC(){
        SQLiteDatabase db = this.getWritableDatabase();
        String createTableRuangan = "CREATE TABLE IF NOT EXISTS " + TABLE_PIC
                + "    ( " +
                ID_USER+ " INTEGER PRIMARY KEY, " +
                NAMA_USER + " TEXT NOT NULL " + ")";
        db.execSQL(createTableRuangan);
    }

    public void createLinenKotor(){
        SQLiteDatabase db = this.getWritableDatabase();
        String createTable = "CREATE TABLE IF NOT EXISTS " + InputContract.TaskEntry.TABLE + "    ( " +
                InputContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NO_TRANSAKSI + " TEXT NOT NULL, " +
                InputContract.TaskEntry.TANGGAL + " DATE NOT NULL, " +
                InputContract.TaskEntry.STATUS + " TEXT NOT NULL DEFAULT 0, " +
                InputContract.TaskEntry.SYNC + " TEXT NOT NULL DEFAULT 0, " +
                InputContract.TaskEntry.PIC + " TEXT NOT NULL, " +
                KATEGORI + " TEXT NOT NULL, " +
                JENIS_INFEKSIUS + " TEXT NOT NULL, " +
                TOTAL_BERAT + " TEXT NOT NULL DEFAULT 0, " +
                TOTAL_QTY + " TEXT NOT NULL DEFAULT 0, " +
                InputContract.TaskEntry.CURRENT_INSERT +  " DEFAULT CURRENT_TIMESTAMP " + ")";


        db.execSQL(createTable);
        String createTableDetail = "CREATE TABLE IF NOT EXISTS " + InputContract.TaskEntry.TABLE_DETAIL
                + "    ( " +
                InputContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NO_TRANSAKSI + " TEXT NOT NULL, " +
                InputContract.TaskEntry.EPC + " TEXT NOT NULL, " +
                InputContract.TaskEntry.ITEM + " TEXT NOT NULL, " +
                InputContract.TaskEntry.ROOM + " TEXT NOT NULL, " +
                InputContract.TaskEntry.CURRENT_INSERT +  " DEFAULT CURRENT_TIMESTAMP "+ ")";

        db.execSQL(createTableDetail);
    }

    public void createLinenBersih(){
        SQLiteDatabase db = this.getWritableDatabase();
        String createTableBersih = "CREATE TABLE IF NOT EXISTS " + TABLE_BERSIH + "    ( " +
                InputContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NO_TRANSAKSI + " TEXT NOT NULL, " +
                InputContract.TaskEntry.TANGGAL + " DATE NOT NULL, " +
                InputContract.TaskEntry.STATUS + " TEXT NOT NULL, " +
                InputContract.TaskEntry.SYNC + " TEXT NOT NULL DEFAULT 0, " +
                InputContract.TaskEntry.PIC + " TEXT NOT NULL, " +
                TOTAL_BERAT + " TEXT NOT NULL DEFAULT 0, " +
                TOTAL_QTY + " TEXT NOT NULL DEFAULT 0, " +
                KATEGORI + " TEXT NOT NULL DEFAULT '', " +
                InputContract.TaskEntry.CURRENT_INSERT +  " DEFAULT CURRENT_TIMESTAMP " + ")";


        db.execSQL(createTableBersih);
        String createTableBersihDetail = "CREATE TABLE IF NOT EXISTS " + TABLE_BERSIH_DETAIL
                + "    ( " +
                InputContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NO_TRANSAKSI + " TEXT NOT NULL, " +
                InputContract.TaskEntry.EPC + " TEXT NOT NULL, " +
                InputContract.TaskEntry.ITEM + " TEXT NOT NULL, " +
                InputContract.TaskEntry.ROOM + " TEXT NOT NULL, " +
                STATUS_LINEN + " TEXT NOT NULL, " +
                BERAT + " TEXT NOT NULL, " +
                CHECKED + " TEXT NOT NULL DEFAULT 0, " +
                KELUAR + " TEXT NOT NULL DEFAULT 0, " +
                InputContract.TaskEntry.CURRENT_INSERT +  " DEFAULT CURRENT_TIMESTAMP "+ ")";

        db.execSQL(createTableBersihDetail);
    }
    public void createLinenRequest(){
        SQLiteDatabase db = this.getWritableDatabase();
        String createTableRequest = "CREATE TABLE IF NOT EXISTS " + TABLE_REQUEST + "    ( " +
                InputContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NO_REQUEST + " TEXT NOT NULL, " +
                TGL_REQUEST + " DATE NOT NULL, " +
                NAMA_RUANGAN + " TEXT NOT NULL, " +
                REQUESTOR + " TEXT NOT NULL, " +
                TOTAL_REQUEST + " TEXT NOT NULL DEFAULT '0', " +
                STATUS_REQUEST + " TEXT NOT NULL DEFAULT 'Pending', " +
                InputContract.TaskEntry.CURRENT_INSERT +  " DEFAULT CURRENT_TIMESTAMP " + ")";


        db.execSQL(createTableRequest);
        String createTableRequestDetail = "CREATE TABLE IF NOT EXISTS " + TABLE_REQUEST_DETAIL
                + "    ( " +
                InputContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NO_REQUEST + " TEXT NOT NULL, " +
                JENIS + " TEXT NOT NULL, " +
                QTY + " TEXT NOT NULL DEFAULT 0, " +
                READY + " TEXT NOT NULL DEFAULT 0, " +
                FLAG_AMBIL + " TEXT NOT NULL DEFAULT 0, " +
                InputContract.TaskEntry.CURRENT_INSERT +  " DEFAULT CURRENT_TIMESTAMP "+ ")";

        db.execSQL(createTableRequestDetail);
    }
    public void createLinenKeluar(){
        SQLiteDatabase db = this.getWritableDatabase();
        String createTableKeluar = "CREATE TABLE IF NOT EXISTS " + TABLE_KELUAR + "    ( " +
                InputContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NO_TRANSAKSI + " TEXT NOT NULL, " +
                InputContract.TaskEntry.TANGGAL + " DATE NOT NULL, " +
                NAMA_RUANGAN + " TEXT NOT NULL, " +
                InputContract.TaskEntry.SYNC + " TEXT NOT NULL DEFAULT 0, " +
                InputContract.TaskEntry.STATUS + " TEXT NOT NULL DEFAULT 0, " +
                InputContract.TaskEntry.PIC + " TEXT NOT NULL, " +
                NO_REFERENSI + " TEXT NOT NULL DEFAULT '', " +
                InputContract.TaskEntry.CURRENT_INSERT +  " DEFAULT CURRENT_TIMESTAMP " + ")";


        db.execSQL(createTableKeluar);
        String createTableKeluarDetail = "CREATE TABLE IF NOT EXISTS " + TABLE_KELUAR_DETAIL
                + "    ( " +
                InputContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NO_TRANSAKSI + " TEXT NOT NULL, " +
                InputContract.TaskEntry.EPC + " TEXT NOT NULL, " +
                InputContract.TaskEntry.ITEM + " TEXT NOT NULL, " +
                BERAT + " TEXT NOT NULL, " +
                KOTOR + " TEXT NOT NULL DEFAULT 0, " +
                InputContract.TaskEntry.CURRENT_INSERT +  " DEFAULT CURRENT_TIMESTAMP "+ ")";

        db.execSQL(createTableKeluarDetail);

    }

    public void createLinenRusak(){
        SQLiteDatabase db = this.getWritableDatabase();
        String createTableRusak = "CREATE TABLE IF NOT EXISTS " + TABLE_RUSAK + "    ( " +
                InputContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NO_TRANSAKSI + " TEXT NOT NULL, " +
                InputContract.TaskEntry.TANGGAL + " DATE NOT NULL, " +
                InputContract.TaskEntry.SYNC + " TEXT NOT NULL DEFAULT 0, " +
                InputContract.TaskEntry.PIC + " TEXT NOT NULL, " +
                DEFECT + " TEXT NOT NULL DEFAULT 'SOBEK', " +
                CATATAN + " TEXT NOT NULL DEFAULT '', " +
                InputContract.TaskEntry.CURRENT_INSERT +  " DEFAULT CURRENT_TIMESTAMP " + ")";


        db.execSQL(createTableRusak);
        String createTableRusakDetail = "CREATE TABLE IF NOT EXISTS " + TABLE_RUSAK_DETAIL
                + "    ( " +
                InputContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NO_TRANSAKSI + " TEXT NOT NULL, " +
                InputContract.TaskEntry.EPC + " TEXT NOT NULL, " +
                InputContract.TaskEntry.ITEM + " TEXT NOT NULL, " +
                JML_CUCI + " TEXT NOT NULL DEFAULT 0, " +
                BERAT + " TEXT NOT NULL DEFAULT 0, " +
                InputContract.TaskEntry.CURRENT_INSERT +  " DEFAULT CURRENT_TIMESTAMP "+ ")";

        db.execSQL(createTableRusakDetail);

    }

    public void createLinen(){
        SQLiteDatabase db = this.getWritableDatabase();
//        String createTableRuangan = "DROP TABLE BARANG";
//        db.execSQL(createTableRuangan);
        String createTableRuangan = "CREATE TABLE IF NOT EXISTS " + TABLE_BARANG
                + "    ( " +
                SERIAL + " TEXT PRIMARY KEY NOT NULL , " +
                NAMA_RUANGAN + " TEXT NOT NULL , " +
                 ID_JENIS + " TEXT NOT NULL , " +
                TANGGAL + " DATE NOT NULL " + ")";
        db.execSQL(createTableRuangan);

    }
    public Cursor getData(String tabel, String wher){
        String selectQuery = "SELECT * FROM " + tabel + " WHERE " + wher ;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectQuery, null);

    }

}
