package id.coba.kotlinpintar;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import id.coba.kotlinpintar.Rest.ApiClient;
import id.coba.kotlinpintar.Rest.ApiInterface;
import id.coba.kotlinpintar.Sync.MyServiceSyncAdapter;

import static id.coba.kotlinpintar.InputDbHelper.BASE_URL;
import static id.coba.kotlinpintar.InputDbHelper.TABLE_SETTING;
import static id.coba.kotlinpintar.InputDbHelper.WEB_URL;

public class SetupActivity extends AppCompatActivity {
    EditText edtBase, edtWeb;
    Button btInsert, btBack;
    ApiInterface mApiInterface;
    InputDbHelper mHelper;
    String currentBase;
    String webBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        edtBase = (EditText) findViewById(R.id.txtBase);
        edtWeb = (EditText) findViewById(R.id.webBase);

        mHelper = new InputDbHelper(this);
        Cursor cursor  =  mHelper.getSetting();
        if (cursor.moveToFirst()) {
            currentBase = cursor.getString(cursor.getColumnIndex(BASE_URL));
            webBase = cursor.getString(cursor.getColumnIndex(WEB_URL));
            edtBase.setText(currentBase);
            edtWeb.setText(webBase);
        }

        getSupportActionBar().setTitle("Master Setting");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        btInsert = (Button) findViewById(R.id.button_simpan);
        btInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values_header = new ContentValues();

                values_header.put(BASE_URL,   edtBase.getText().toString());
                values_header.put(WEB_URL,   edtWeb.getText().toString());

                SQLiteDatabase db = mHelper.getWritableDatabase();
//                db.insertWithOnConflict(TABLE_SETTING, null, values_header, SQLiteDatabase.CONFLICT_REPLACE);

                String whereClause = BASE_URL+"=?";
                String [] whereArgs = {currentBase};
                db.updateWithOnConflict(TABLE_SETTING,values_header,whereClause,whereArgs,SQLiteDatabase.CONFLICT_REPLACE);
                Toast.makeText(getApplicationContext(),"Berhasil disimpan.!",Toast.LENGTH_SHORT).show();
            }
        });

//        MyServiceSyncAdapter.initializeSyncAdapter(getApplicationContext());
    }

    @Override public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
