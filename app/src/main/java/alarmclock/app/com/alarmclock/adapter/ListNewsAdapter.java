package alarmclock.app.com.alarmclock.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

import alarmclock.app.com.alarmclock.R;
import alarmclock.app.com.alarmclock.model.News;
import alarmclock.app.com.alarmclock.util.Constant;

/**
 * Created by Administrator on 6/19/2018.
 */

public class ListNewsAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<News> data;

    public ListNewsAdapter(Activity a, ArrayList<News> d) {
        activity = a;
        data = d;
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ListNewsViewHolder holder = null;
        if (convertView == null) {
            holder = new ListNewsViewHolder();
            convertView = LayoutInflater.from(activity).inflate(
                    R.layout.item_news, parent, false);
            holder.galleryImage = (ImageView) convertView.findViewById(R.id.galleryImage);
            holder.author = (TextView) convertView.findViewById(R.id.author);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.sdetails = (TextView) convertView.findViewById(R.id.sdetails);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            convertView.setTag(holder);
        } else {
            holder = (ListNewsViewHolder) convertView.getTag();
        }
        News news = data.get(position);

        try {
            holder.author.setText(news.getAuthor());
            holder.title.setText(news.getTitle());
            holder.time.setText(news.getPublishedAt());
            holder.sdetails.setText(news.getDescription());

            if (news.getUrlToImage().length() < 5) {
                holder.galleryImage.setVisibility(View.GONE);
            } else {
                Glide.with(activity)
                        .load(news.getUrlToImage())
                        .into(holder.galleryImage);
            }
        } catch (Exception e) {
        }
        return convertView;
    }
}

class ListNewsViewHolder {
    ImageView galleryImage;
    TextView author, title, sdetails, time;
}