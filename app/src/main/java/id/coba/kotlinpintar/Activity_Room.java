package id.coba.kotlinpintar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.List;

import id.coba.kotlinpintar.Adapter.RoomAdapter;
import id.coba.kotlinpintar.Model.CobaModel;
import id.coba.kotlinpintar.Model.GetRoom;
import id.coba.kotlinpintar.Model.Room;
import id.coba.kotlinpintar.Rest.ApiClient;
import id.coba.kotlinpintar.Rest.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_Room extends AppCompatActivity {

    Button btIns;
    ApiInterface mApiInterface;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public static Activity_Room ma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);


        btIns = (Button) findViewById(R.id.btIns);
        btIns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Activity_Room.this, InsertRoomActivity.class));
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        ma=this;
        refresh();
    }

    public void refresh() {
        Call<CobaModel>kontakCall = mApiInterface.getRoom();

        kontakCall.enqueue(new Callback<CobaModel>() {
            @Override
            public void onResponse(Call<CobaModel> call, Response<CobaModel> response) {
                Log.d("tes","resss");
//                List<Room> RoomList = response.body().getListDataRoom();
//                Log.d("Retrofit Get", "Jumlah data Ruangan: " +
//                        String.valueOf(RoomList.size()));
//                mAdapter = new RoomAdapter(RoomList);
//                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<CobaModel> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                System.out.println(t.fillInStackTrace());
            }
        });
    }
}
