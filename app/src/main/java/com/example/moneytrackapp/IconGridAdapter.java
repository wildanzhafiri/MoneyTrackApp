package com.example.moneytrackapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class IconGridAdapter extends RecyclerView.Adapter<IconGridAdapter.ViewHolder> {

    private final Context context;
    private final List<Integer> localIcons;
    private final List<String> uploadedIconUrls;
    private int selectedPosition = -1;
    private OnIconSelectedListener listener;

    public interface OnIconSelectedListener {
        void onIconSelected(int iconRes, String imageUrl);
    }

    public IconGridAdapter(Context context, List<Integer> localIcons, List<String> uploadedIconUrls) {
        this.context = context;
        this.localIcons = localIcons;
        this.uploadedIconUrls = uploadedIconUrls != null ? uploadedIconUrls : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_icon_circle, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        boolean isLocal = position < localIcons.size();

        if (isLocal) {
            holder.icon.setImageResource(localIcons.get(position));
        } else {
            String base64 = uploadedIconUrls.get(position - localIcons.size());
            byte[] imageBytes = Base64.decode(base64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.icon.setImageBitmap(bitmap);
        }


        holder.icon.setBackgroundResource(selectedPosition == position ? R.drawable.bg_dashed_circle : 0);

        holder.itemView.setOnClickListener(v -> {
            int previous = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(previous);
            notifyItemChanged(position);

            if (listener != null) {
                if (isLocal) {
                    listener.onIconSelected(localIcons.get(position), null);
                } else {
                    listener.onIconSelected(-1, uploadedIconUrls.get(position - localIcons.size()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return localIcons.size() + uploadedIconUrls.size();
    }

    public void setOnIconSelectedListener(OnIconSelectedListener listener) {
        this.listener = listener;
    }

    public void setSelectedIconFromRes(int resId) {
        for (int i = 0; i < localIcons.size(); i++) {
            if (localIcons.get(i) == resId) {
                int previous = selectedPosition;
                selectedPosition = i;
                notifyItemChanged(previous);
                notifyItemChanged(selectedPosition);
                break;
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon_image);
        }
    }
}
