package alarmclock.app.com.alarmclock.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.angads25.toggle.LabeledSwitch;
import com.github.angads25.toggle.interfaces.OnToggledListener;

import java.util.ArrayList;
import java.util.HashMap;

import alarmclock.app.com.alarmclock.R;
import alarmclock.app.com.alarmclock.model.ItemAlarm;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 5/9/2018.
 */

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmHolder> {

    private Context context;
    private ArrayList<ItemAlarm> itemAlarms;
    private ArrayList<ItemAlarm> itemAlarmsRemove;
    private OnItemClick onItemClick;
    HashMap<ItemAlarm, Runnable> pendingRunnables = new HashMap<>();
    public AlarmAdapter(Context context, ArrayList<ItemAlarm> itemAlarms, OnItemClick onItemClick) {
        this.context = context;
        this.itemAlarms = itemAlarms;
        this.onItemClick = onItemClick;
        itemAlarmsRemove = new ArrayList<>();
    }

    @Override
    public AlarmHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alarm_clock, parent, false);
        return new AlarmAdapter.AlarmHolder(view);
    }

    @Override
    public void onBindViewHolder(AlarmHolder holder, int position) {
        ItemAlarm itemAlarm = itemAlarms.get(position);
        if (itemAlarm != null) {
            String h = itemAlarm.getHour();
            if (h.length() == 1) {
                h = "0" + h;
            }
            String m = itemAlarm.getMinute();
            if (m.length() == 1) {
                m = "0" + m;
            }
            holder.tvTime.setText(h + ":" + m);// + " " + itemAlarm.getFormat());
            holder.tvTitle.setText(itemAlarm.getTitle());
            String result = getStringRepeat(itemAlarm);
            holder.tvRepeat.setText(result);
            holder.switchOnOff.setOn(itemAlarm.getStatus().equals("0") ? true : false);
            holder.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick.OnClickItemAlarmDelete(itemAlarm, position);
                }
            });
            holder.layoutMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick.OnClickItemAlarm(itemAlarm, position);
                }
            });

            holder.switchOnOff.setOnToggledListener(new OnToggledListener() {
                @Override
                public void onSwitched(LabeledSwitch labeledSwitch, boolean isOn) {
                    onItemClick.OnClickItemSwitchChange(itemAlarm, position, isOn);
                }
            });
            if (itemAlarmsRemove.contains(itemAlarm)) {
                /** {show swipe layout} and {hide regular layout} */
                holder.viewBackground.setVisibility(View.VISIBLE);
                holder.viewForeground.setVisibility(View.GONE);


                holder.undo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        undoOpt(itemAlarm);
                        onItemClick.OnClickItemUndo(itemAlarm, position);

                    }
               });
            } else {
                /** {show regular layout} and {hide swipe layout} */
                holder.viewBackground.setVisibility(View.GONE);
                holder.viewForeground.setVisibility(View.VISIBLE);
            }

        }

    }
    private void undoOpt(ItemAlarm itemAlarm) {
        Runnable pendingRemovalRunnable = pendingRunnables.get(itemAlarm);
        pendingRunnables.remove(itemAlarm);
        if (pendingRemovalRunnable != null)
            handler.removeCallbacks(pendingRemovalRunnable);
        itemAlarmsRemove.remove(itemAlarm);
        // this will rebind the row in "normal" state
        notifyItemChanged(itemAlarms.indexOf(itemAlarm));
    }
    @Override
    public int getItemCount() {
        return itemAlarms.size();
    }

    public void removeItem(int adapterPosition) {
        itemAlarms.remove(adapterPosition);
        notifyDataSetChanged();
    }

    public void restoreItem(ItemAlarm deletedItem, int deletedIndex) {
    }
    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec
    private Handler handler = new Handler();
    public void pendingRemoval(int position) {

        final ItemAlarm data = itemAlarms.get(position);
        if (!itemAlarmsRemove.contains(data)) {
            itemAlarmsRemove.add(data);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the data
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    remove(itemAlarms.indexOf(data));
                    onItemClick.OnClickItemAlarmDelete(data, position);
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(data, pendingRemovalRunnable);
        }
    }
    public void remove(int position) {
        ItemAlarm data = itemAlarms.get(position);
        if (itemAlarmsRemove.contains(data)) {
            itemAlarmsRemove.remove(data);
        }
        if (itemAlarms.contains(data)) {
            itemAlarms.remove(position);
            notifyItemRemoved(position);
        }
    }

    public boolean isPendingRemoval(int position) {
        ItemAlarm data = itemAlarms.get(position);
        return itemAlarmsRemove.contains(data);
    }
    public class AlarmHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.layoutMain)
        View layoutMain;

        @BindView(R.id.tvTime)
        TextView tvTime;
        @BindView(R.id.tvRepeat)
        TextView tvRepeat;

        @BindView(R.id.tvTitle)
        TextView tvTitle;

        @BindView(R.id.viewBackground)
        public View viewBackground;

        @BindView(R.id.viewForeground)
        public View viewForeground;

        @BindView(R.id.imgDelete)
        ImageView imgDelete;

        @BindView(R.id.switchOnOff)
        LabeledSwitch switchOnOff;

        @BindView(R.id.undo)
        TextView undo;

        public AlarmHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClick {
        void OnClickItemAlarm(ItemAlarm itemAlarm, int position);

        void OnClickItemAlarmDelete(ItemAlarm itemAlarm, int position);

        void OnClickItemUndo(ItemAlarm itemAlarm, int position);

        void OnClickItemSwitchChange(ItemAlarm itemAlarm, int position, boolean checked);
    }

    private String getStringRepeat(ItemAlarm itemAlarm) {
        String result = "";
        if (itemAlarm.getRepeatMo() == 1) {
            result += "Mon";
        }
        if (itemAlarm.getRepeatTu() == 1) {
            result += " Tue";
        }
        if (itemAlarm.getRepeatWe() == 1) {
            result += " Wed";
        }
        if (itemAlarm.getRepeatTh() == 1) {
            result += " Thu";
        }
        if (itemAlarm.getRepeatFr() == 1) {
            result += " Fri";
        }
        if (itemAlarm.getRepeatSa() == 1) {
            result += " Sat";
        }
        if (itemAlarm.getRepeatSu() == 1) {
            result += " Sun";
        }
        return result.trim();
    }
}
