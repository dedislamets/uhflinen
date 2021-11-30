package id.coba.kotlinpintar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import id.coba.kotlinpintar.Adapter.CariRoomAdapter;
import id.coba.kotlinpintar.Rest.Objek;

import static id.coba.kotlinpintar.InputDbHelper.BASE_URL;

public class CariRoom extends AppCompatActivity {

    EditText cari_room;
    Button batal;
    Button reset;
    ListView listView;
    ArrayList<Objek> model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cari_room);

        batal = findViewById(R.id.keluar);
        reset = findViewById(R.id.reset);
        cari_room = findViewById(R.id.cari_room);
        listView = findViewById(R.id.list_cari);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cari_room.setText("");
            }
        });

        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cari_room.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cari_data(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    void cari_data(String text){
        String url = BASE_URL + "cari_room?ruangan=" + text;
        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            model= new ArrayList<>();
                            for(int index=0; index<jsonArray.length(); index++){
                                JSONObject getdata = jsonArray.getJSONObject(index);
                                Integer id = getdata.getInt("id");
                                String ruangan = getdata.getString("ruangan");
                                model.add(new Objek(id,ruangan));
                            }

                            CariRoomAdapter adapter = new CariRoomAdapter(getApplicationContext(), model);
                            listView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Data tersimpan", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
}
