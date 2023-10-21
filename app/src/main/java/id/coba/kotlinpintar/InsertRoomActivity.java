package id.coba.kotlinpintar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import id.coba.kotlinpintar.Model.PostPutDelRoom;
import id.coba.kotlinpintar.Rest.ApiClient;
import id.coba.kotlinpintar.Rest.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InsertRoomActivity extends AppCompatActivity {
    EditText edtNama, edtNomor;
    Button btInsert, btBack;
    ApiInterface mApiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_room_activity);
        edtNama = (EditText) findViewById(R.id.edtNama);
        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        btInsert = (Button) findViewById(R.id.btInserting);
        btInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<PostPutDelRoom>postRoomCall = mApiInterface.postRoom(edtNama.getText().toString());
                postRoomCall.enqueue(new Callback<PostPutDelRoom>() {
                    @Override
                    public void onResponse(Call<PostPutDelRoom> call, Response<PostPutDelRoom> response) {
                        Activity_Room.ma.refresh();
                        finish();
                    }

                    @Override
                    public void onFailure(Call<PostPutDelRoom> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        btBack = (Button) findViewById(R.id.btBackGo);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_Room.ma.refresh();
                finish();
            }
        });
    }
}
