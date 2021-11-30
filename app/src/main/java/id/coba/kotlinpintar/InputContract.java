package id.coba.kotlinpintar;

import android.provider.BaseColumns;


public class InputContract {
    public static final String DB_NAME = "com.example.db";
    public static final int DB_VERSION = 3;

    public class TaskEntry implements BaseColumns {
        public static final String TABLE = "linen_kotor";
        public static final String TABLE_DETAIL = "linen_kotor_detail";

        public static final String NO_TRANSAKSI = "transaksi";
        public static final String TANGGAL = "tanggal";
        public static final String STATUS = "status";
        public static final String SYNC = "sync";
        public static final String PIC = "pic";
        public static final String EPC = "epc";
        public static final String ITEM = "barang";
        public static final String ROOM = "ruangan";
        public static final String CURRENT_INSERT = "timestamp";
    }
}