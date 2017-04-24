package com.docreminder.app.listing;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.docreminder.R;
import com.docreminder.models.ReminderModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * <h1>ReminderListingAdapter  @{@link RecyclerView.Adapter}</h1>
 *
 * @author Narendra Singh
 * @version 1.0
 * @since 24-04-2017
 */


public class ReminderListingAdapter extends RecyclerView.Adapter<ReminderListingAdapter.ReminderListingViewHolder> {
    private ArrayList<ReminderModel> mReminders = new ArrayList<>();
    private ItemClickListener mItemClickListener;

    public void setData(ArrayList<ReminderModel> reminders) {
        mReminders.clear();
        mReminders.addAll(reminders);
        notifyDataSetChanged();
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    @Override
    public ReminderListingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder_listing, parent, false);
        return new ReminderListingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReminderListingViewHolder holder, int position) {
        holder.bind(mReminders.get(position), mItemClickListener);
    }

    @Override
    public int getItemCount() {
        return mReminders.size();
    }


    public interface ItemClickListener {
        void onItemClick(ReminderModel reminderModel);
    }

    public static class ReminderListingViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDoctorName;
        public TextView tvPatientName;
        public TextView tvAppointmentTime;

        public ReminderListingViewHolder(View itemView) {
            super(itemView);
            initView();
        }

        private void initView() {
            tvDoctorName = (TextView) itemView.findViewById(R.id.tv_doctor);
            tvPatientName = (TextView) itemView.findViewById(R.id.tv_patient);
            tvAppointmentTime = (TextView) itemView.findViewById(R.id.tv_time);
        }

        public void bind(final ReminderModel reminderModel, final ItemClickListener itemClickListener) {
            tvDoctorName.setText(reminderModel.getDoctorName());
            tvPatientName.setText(reminderModel.getPatientName());

            Date date = new Date(reminderModel.getAppointmentTime());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault());
            String formattedDate = dateFormat.format(date);

            tvAppointmentTime.setText(formattedDate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(reminderModel);
                    }
                }
            });
        }

    }
}
