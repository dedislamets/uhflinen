package id.coba.kotlinpintar.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final MenuModel dataModel = getItem(position);
        final ViewHolder viewHolder;
        final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final String desc = dataModel.getDescription();

        viewHolder = new ViewHolder();
        if(!TextUtils.isEmpty(desc)){
            convertView = inflater.inflate(R.layout.list_setup, parent, false);
        }else{
            convertView = inflater.inflate(R.layout.list_setup_divider, parent, false);
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
