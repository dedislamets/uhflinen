package id.coba.kotlinpintar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "linen_rs";

    // Table Names
    private static final String TABLE_TODO = "linen_kotor";
    private static final String TABLE_DETAIL = "linen_kotor_detail";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_HEADER = "no_transaksi";
    private static final String TANGGAL = "tanggal";
    private static final String PIC = "pic";


    // TAGS Table - column names
    private static final String KEY_DETAIL = "no_transaksi";
    private static final String EPC = "epc";
    private static final String ITEM = "barang";
    private static final String ROOM = "room";

    // Table Create Statements
    // Todo table create statement
    private static final String CREATE_TABLE_TODO = "CREATE TABLE "
            + TABLE_TODO + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_HEADER + " TEXT,"
            + TANGGAL + " DATE,"
            + PIC + " TEXT,"
            + KEY_CREATED_AT + " DATETIME" + ")";

    // Tag table create statement
    private static final String CREATE_TABLE_DETAIL = "CREATE TABLE " + TABLE_DETAIL
            + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_DETAIL + " INT,"
            + EPC + " TEXT,"
            + ITEM + " TEXT,"
            + ROOM + " TEXT,"
            + KEY_CREATED_AT + " DATETIME" + ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_TODO);
        db.execSQL(CREATE_TABLE_DETAIL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DETAIL);

        // create new tables
        onCreate(db);
    }




}

