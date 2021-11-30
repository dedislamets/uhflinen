package id.coba.kotlinpintar.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import id.coba.kotlinpintar.EditRoomActivity;
import id.coba.kotlinpintar.Model.Room;
import id.coba.kotlinpintar.R;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.MyViewHolder>{
    List<Room> mRoomList;

    public RoomAdapter(List <Room> RoomList) {
        mRoomList = RoomList;
    }

    @Override
    public MyViewHolder onCreateViewHolder (ViewGroup parent,int viewType){
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_list, parent, false);
        MyViewHolder mViewHolder = new MyViewHolder(mView);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder (MyViewHolder holder,final int position){
        holder.mTextViewId.setText("id = " + mRoomList.get(position).getId());
        holder.mTextViewNama.setText("ruangan = " + mRoomList.get(position).getRoom());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(view.getContext(), EditRoomActivity.class);
                mIntent.putExtra("id", mRoomList.get(position).getId());
                mIntent.putExtra("ruangan", mRoomList.get(position).getRoom());
                view.getContext().startActivity(mIntent);
            }
        });
    }

    @Override
    public int getItemCount () {
        return mRoomList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextViewId, mTextViewNama;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTextViewId = (TextView) itemView.findViewById(R.id.tvId);
            mTextViewNama = (TextView) itemView.findViewById(R.id.tvNama);
        }
    }
}