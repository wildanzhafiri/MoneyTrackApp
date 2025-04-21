package com.example.moneytrackapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ManageCategoryAdapter extends RecyclerView.Adapter<ManageCategoryAdapter.ViewHolder> {

    private final Context context;
    private final List<Category> categoryList;
    private OnStartDragListener dragStartListener;

    public ManageCategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    public interface OnStartDragListener {
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }

    public void setDragStartListener(OnStartDragListener listener) {
        this.dragStartListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_manage_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageCategoryAdapter.ViewHolder holder, int position) {
        Category item = categoryList.get(position);
        holder.name.setText(item.getName());
        holder.icon.setImageResource(item.getIconRes());

        holder.edit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditCategoryActivity.class);
            intent.putExtra("mode", "edit");
            intent.putExtra("category_name", item.getName());
            intent.putExtra("icon_res", item.getIconRes());
            context.startActivity(intent);
        });

        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context, "Kategori: " + item.getName(), Toast.LENGTH_SHORT).show();
        });

        holder.dragHandle.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN && dragStartListener != null) {
                dragStartListener.onStartDrag(holder);
            }
            return false;
        });


    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name, edit;
        ImageView dragHandle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.category_icon);
            name = itemView.findViewById(R.id.category_name);
            edit = itemView.findViewById(R.id.edit_button);
            dragHandle = itemView.findViewById(R.id.drag_handle);
        }
    }
}
