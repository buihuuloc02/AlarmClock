package alarmclock.app.com.alarmclock.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import alarmclock.app.com.alarmclock.R;
import alarmclock.app.com.alarmclock.model.Photo;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 6/4/2018.
 */

public class CustomGridAdapter extends BaseAdapter {
    private ArrayList<Photo> listData;
    private LayoutInflater layoutInflater;
    private Context context;
    private OnItemClick onItemClick;

    public CustomGridAdapter(Context aContext, ArrayList<Photo> listData, OnItemClick onItemClick) {
        this.context = aContext;
        this.listData = listData;
        this.onItemClick = onItemClick;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int i) {
        return listData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_list_image, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            holder.cbSelected = (CheckBox) convertView.findViewById(R.id.cbSelected);
            holder.layoutMain = (View) convertView.findViewById(R.id.layoutMain);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Photo photo = this.listData.get(position);

        boolean selected = photo.isSelected();
        holder.cbSelected.setChecked(selected);

        Glide.with(context)
                .load(photo.getPhotoUri())
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photo.setSelected(!photo.isSelected());
                onItemClick.OnClickItem(photo, position);

            }
        });
        holder.layoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photo.setSelected(!photo.isSelected());
                onItemClick.OnClickItem(photo, position);
            }
        });
        holder.cbSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photo.setSelected(!photo.isSelected());
                onItemClick.OnClickItem(photo, position);
            }
        });
        return convertView;
    }
    public interface OnItemClick {
        void OnClickItem(Photo photo, int position);

        void OnClickCheckBox(Photo photo, int position);

    }
    static class ViewHolder {

        ImageView imageView;
        CheckBox cbSelected;
        View layoutMain;
    }
}
