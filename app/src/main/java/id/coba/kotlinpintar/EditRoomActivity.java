package id.coba.kotlinpintar;

import android.content.Intent;
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

public class EditRoomActivity extends AppCompatActivity {
    EditText edtId, edtNama, edtNomor;
    Button btUpdate, btDelete, btBack;
    ApiInterface mApiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_room_activity);
        edtId = (EditText) findViewById(R.id.edtId);
        edtNama = (EditText) findViewById(R.id.edtNama);
        Intent mIntent = getIntent();
        edtId.setText(mIntent.getStringExtra("Id"));
        edtId.setTag(edtId.getKeyListener());
        edtId.setKeyListener(null);
        edtNama.setText(mIntent.getStringExtra("Nama"));
        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        btUpdate = (Button) findViewById(R.id.btUpdate2);
        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<PostPutDelRoom>updateRoomCall = mApiInterface.putRoom(edtId.getText().toString(),edtNama.getText().toString());

                updateRoomCall.enqueue(new Callback<PostPutDelRoom>() {
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
        btDelete = (Button) findViewById(R.id.btDelete2);
        btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtId.getText().toString().trim().isEmpty()==false){
                    Call<PostPutDelRoom> deleteKontak = mApiInterface.deleteRoom(edtId.getText().toString());
                    deleteKontak.enqueue(new Callback<PostPutDelRoom>() {
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
                }else{
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                }
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
