package com.example.moneytrackapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IconGridAdapter extends RecyclerView.Adapter<IconGridAdapter.ViewHolder> {

    private final Context context;
    private final List<Integer> icons;
    private int selectedPosition = -1;
    private OnIconClickListener listener;

    public IconGridAdapter(Context context, List<Integer> icons) {
        this.context = context;
        this.icons = icons;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_icon_circle, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int iconRes = icons.get(position);
        holder.icon.setImageResource(iconRes);

        if (selectedPosition == position) {
            holder.icon.setBackgroundResource(R.drawable.bg_dashed_circle);
        } else {
            holder.icon.setBackground(null);
        }

        holder.itemView.setOnClickListener(v -> {
            if (selectedPosition == position) {
                return;
            }

            int prevPosition = selectedPosition;
            selectedPosition = position;

            if (prevPosition != -1) notifyItemChanged(prevPosition);
            notifyItemChanged(position);

            if (listener != null) {
                listener.onIconClick(iconRes);
            }
        });
    }

    @Override
    public int getItemCount() {
        return icons.size();
    }

    public void setSelectedIcon(int iconRes) {
        int prevSelected = selectedPosition;

        for (int i = 0; i < icons.size(); i++) {
            if (icons.get(i) == iconRes) {
                selectedPosition = i;
                break;
            }
        }

        if (prevSelected != -1) notifyItemChanged(prevSelected);
        if (selectedPosition != -1) notifyItemChanged(selectedPosition);
    }

    public int getSelectedIconRes() {
        return selectedPosition >= 0 ? icons.get(selectedPosition) : -1;
    }

    public interface OnIconClickListener {
        void onIconClick(int iconRes);
    }

    public void setOnIconClickListener(OnIconClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon_image);
        }
    }
}
