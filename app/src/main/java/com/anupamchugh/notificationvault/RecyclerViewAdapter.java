package com.anupamchugh.notificationvault;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private ArrayList<NotificationModel> data;
    ClickAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitle, mBody, mTime;
        RelativeLayout relativeLayout;
        private ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            mTitle = itemView.findViewById(R.id.txtTitle);
            mBody = itemView.findViewById(R.id.txtBody);
            mTime = itemView.findViewById(R.id.timeStamp);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    public RecyclerViewAdapter(ArrayList<NotificationModel> data, ClickAdapterListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mTitle.setText(data.get(position).title);
        holder.mBody.setText(data.get(position).body);

        SimpleDateFormat spf = new SimpleDateFormat("hh:mm a");
        Date newDate = new Date(data.get(position).timeStamp);

        holder.mTime.setText(spf.format(newDate));

        Context context = holder.imageView.getContext();

        Glide.with(context)
                .asBitmap()
                .load(data.get(position).notificationIcon)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(holder.imageView);

        applyClickEvents(holder, data.get(position));
    }

    private void applyClickEvents(MyViewHolder holder, final NotificationModel model) {
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRowClicked(model);
            }
        });
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public ArrayList<NotificationModel> getData() {
        return data;
    }

    public void setData(ArrayList<NotificationModel> newData) {

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MyDiffUtilCallBack(newData, data));
        data.clear();
        this.data.addAll(newData);
        diffResult.dispatchUpdatesTo(this);
    }

    public interface ClickAdapterListener {

        void onRowClicked(NotificationModel model);
    }
}
