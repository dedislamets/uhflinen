package id.coba.kotlinpintar.Adapter;

import static id.coba.kotlinpintar.InputDbHelper.DEPARTMENT;
import static id.coba.kotlinpintar.InputDbHelper.EMAIL;
import static id.coba.kotlinpintar.InputDbHelper.NAMA_USER;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import id.coba.kotlinpintar.Model.MenuModel;
import id.coba.kotlinpintar.R;

public class MenuAdapter extends ArrayAdapter<MenuModel> {
    private ArrayList<MenuModel> dataSet;
    private Context mContext;


    public static class ViewHolder {
        public TextView txtTitle;
        public TextView txtDescription;
        public ImageView imgIcon;
        public Switch UnSwitch;
    }

    public MenuAdapter(ArrayList<MenuModel> data, Context context) {
        super(context, -1, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public int getViewTypeCount() {
        return super.getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private int lastPosition = -1;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final MenuModel dataModel = getItem(position);
        final ViewHolder viewHolder;
        final SharedPreferences prefMode;
        prefMode = mContext.getSharedPreferences("MODE", Context.MODE_PRIVATE);
        final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final String desc = dataModel.getDescription();

        final Boolean isActive = dataModel.getIsValid();

        viewHolder = new ViewHolder();
        if(!TextUtils.isEmpty(desc)){
            convertView = inflater.inflate(R.layout.list_setup, parent, false);
        }else{
            convertView = inflater.inflate(R.layout.list_setup_divider, parent, false);
        }

        if(isActive != null){
            convertView = inflater.inflate(R.layout.list_setup_with_switch, parent, false);
            viewHolder.UnSwitch = (Switch) convertView.findViewById(R.id.unswitch);
            viewHolder.UnSwitch.setChecked(prefMode.getBoolean("MODE", false));

            viewHolder.UnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.e("Checked",""+ isChecked);

                    SharedPreferences.Editor editor = prefMode.edit();
                    editor.putBoolean("MODE", isChecked);
                    //editor.apply();
                    editor.commit();
                }
            });
        }

        viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.firstLine);
//        viewHolder.txtDescription = (TextView) convertView.findViewById(R.id.secondLine);
        viewHolder.imgIcon = (ImageView) convertView.findViewById(R.id.icon);

        convertView.setTag(viewHolder);

        lastPosition = position;
        viewHolder.txtTitle.setText(dataModel.getTitle());
//        viewHolder.txtDescription.setText(dataModel.getDescription());
        viewHolder.imgIcon.setImageResource(dataModel.getIcon());

        return convertView;
    }
}
