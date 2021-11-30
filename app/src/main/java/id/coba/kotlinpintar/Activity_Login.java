package id.coba.kotlinpintar;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.coba.kotlinpintar.Model.Login;
import id.coba.kotlinpintar.Model.LoginModel;
import id.coba.kotlinpintar.Model.PostPutDelRoom;
import id.coba.kotlinpintar.Rest.ApiClient;
import id.coba.kotlinpintar.Rest.ApiInterface;
import id.coba.kotlinpintar.Rest.LoginInterface;
import id.coba.kotlinpintar.Rest.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;

import static id.coba.kotlinpintar.InputDbHelper.BASE_URL;
import static id.coba.kotlinpintar.InputDbHelper.DEPARTMENT;
import static id.coba.kotlinpintar.InputDbHelper.EMAIL;
import static id.coba.kotlinpintar.InputDbHelper.ID_USER;
import static id.coba.kotlinpintar.InputDbHelper.NAMA_USER;
import static id.coba.kotlinpintar.InputDbHelper.PASSWORD;
import static id.coba.kotlinpintar.InputDbHelper.TABLE_BARANG;
import static id.coba.kotlinpintar.InputDbHelper.TABLE_LOGIN;
import static id.coba.kotlinpintar.InputDbHelper.TOKEN;
import static id.coba.kotlinpintar.InputDbHelper.WEB_URL;

public class Activity_Login extends AppCompatActivity {
    private static final int REQUEST_CODE = 10;
    EditText username, password;
    Button btnLogin, exit;
    InputDbHelper db;
    SharedPreferences sharedpreferences;
    LoginInterface mApiInterface;
    private InputDbHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        mHelper = new InputDbHelper(this);
        setContentView(R.layout.activity_login);
        db = new InputDbHelper(this);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        btnLogin = (Button)findViewById(R.id.login);
        exit = (Button)findViewById(R.id.exit);
        sharedpreferences = getSharedPreferences("LOGIN", this.MODE_PRIVATE);
        db.createLogin();
        mApiInterface = ApiClient.getClient().create(LoginInterface.class);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameKey = username.getText().toString();
                String passwordKey = password.getText().toString();
                if(validate()) {
                    getDataLogin();
                }

            }
        });
    }


    public boolean validate() {
        boolean valid = true;
        String UserName = username.getText().toString();
        String Password = password.getText().toString();

        if (UserName.isEmpty()) {
            valid = false;
            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Login.this);
            builder.setMessage("Email tidak boleh kosong!")
                    .setNegativeButton("Retry", null).create().show();
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(UserName).matches()) {
            valid = false;
            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Login.this);
            builder.setMessage("Email tidak valid!")
                    .setNegativeButton("Retry", null).create().show();
        }

        if (Password.isEmpty()) {
            valid = false;
            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Login.this);
            builder.setMessage("Password tidak boleh kosong!")
                    .setNegativeButton("Retry", null).create().show();
        } else {
            if (Password.length() < 5) {
                valid = false;
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Login.this);
                builder.setMessage("Password harus lebih dari 5 karakter!")
                        .setNegativeButton("Retry", null).create().show();
            }
        }


        return valid;
    }

    private void getDataLogin() {
        Call<LoginModel>getData = mApiInterface.getLogin(username.getText().toString(),password.getText().toString());

        getData.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, retrofit2.Response<LoginModel> response) {
                if(response.body() == null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Login.this);
                    builder.setMessage("Username atau Password Anda salah!")
                            .setNegativeButton("Retry", null).create().show();
                    return;
                }
                if(response.isSuccessful()){
                    LoginModel loginList = response.body();
                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    editor.putString(NAMA_USER, loginList.getNama_user());
                    editor.putString(DEPARTMENT, loginList.getGroup());
                    editor.putString(EMAIL, loginList.getEmail());
                    editor.putInt(ID_USER, loginList.getId_user());
                    editor.putBoolean("isLogin", true);
                    editor.commit();

                    SQLiteDatabase db = mHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(ID_USER, loginList.getId_user());
                    values.put(NAMA_USER, loginList.getNama_user());
                    values.put(EMAIL, loginList.getEmail());
                    values.put(PASSWORD, password.getText().toString());
                    values.put(DEPARTMENT, loginList.getGroup());
                    db.insertWithOnConflict(TABLE_LOGIN, null, values, SQLiteDatabase.CONFLICT_REPLACE);

                    String token = FirebaseInstanceId.getInstance().getToken();
                    pushToken(loginList.getId_user(),token);

                    Toast.makeText(getApplicationContext(), "Selamat Datang ", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Activity_Login.this, MainActivity.class);
                    intent.putExtra("email", username.getText().toString());
                    startActivityForResult(intent, REQUEST_CODE);
                }


            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {


                Boolean res = db.periksaUser(username.getText().toString(),password.getText().toString());
                if (res){

                    Cursor cursor  =  db.getUser(username.getText().toString(),password.getText().toString());
                    if (cursor.moveToFirst()) {
                        SharedPreferences.Editor editor = sharedpreferences.edit();

                        editor.putString(NAMA_USER, cursor.getString(cursor.getColumnIndex(NAMA_USER)));
                        editor.putString(DEPARTMENT, cursor.getString(cursor.getColumnIndex(DEPARTMENT)));
                        editor.putString(EMAIL, cursor.getString(cursor.getColumnIndex(EMAIL)));
                        editor.putBoolean("isLogin", true);
                        editor.commit();
                    }


                    Toast.makeText(getApplicationContext(), "Selamat Datang ", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Activity_Login.this, MainActivity.class);
                    intent.putExtra("email", username.getText().toString());
                    startActivityForResult(intent, REQUEST_CODE);
                }else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Login.this);
                    builder.setMessage("Username atau Password Anda salah!")
                            .setNegativeButton("Retry", null).create().show();
                }
            }
        });
    }

    private void pushToken(final Integer id_user,final String token) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiClient.BASE_URL+ "token",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {

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
                params.put(ID_USER, id_user.toString());
                params.put(TOKEN, token);
                return params;
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}
