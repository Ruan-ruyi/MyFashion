package com.example.myfashion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<Notification> mList;

    public NotificationAdapter(List<Notification> list) { this.mList = list; }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification n = mList.get(position);
        holder.tvTitle.setText(n.getTitle());
        holder.tvContent.setText(n.getContent());
        holder.tvTime.setText(n.getTime());
    }

    @Override
    public int getItemCount() { return mList == null ? 0 : mList.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_noti_title);
            tvContent = itemView.findViewById(R.id.tv_noti_content);
            tvTime = itemView.findViewById(R.id.tv_noti_time);
        }
    }
}