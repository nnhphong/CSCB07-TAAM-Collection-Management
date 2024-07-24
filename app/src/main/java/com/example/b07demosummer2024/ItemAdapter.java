package com.example.b07demosummer2024;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private final List<Item> itemList;

    public ItemAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.itemTextViewLotNumber.setText(item.getLotNumber().toString());
        holder.itemTextViewName.setText(item.getName());
        holder.itemTextViewPeriod.setText(item.getPeriod());
        holder.itemTextViewDescription.setText(item.getDescription());
        // holder.itemImageView.setImage(item.()); needs to implement firebase storage connection to get image
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemTextViewLotNumber, itemTextViewName, itemTextViewPeriod, itemTextViewDescription;
//        ImageView itemImageView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTextViewLotNumber = itemView.findViewById(R.id.itemTextViewLotNumber);
            itemTextViewName = itemView.findViewById(R.id.itemTextViewName);
            itemTextViewPeriod = itemView.findViewById(R.id.itemTextViewPeriod);
            itemTextViewDescription = itemView.findViewById(R.id.itemTextViewDescription);
//            itemImageView = itemView.findViewById(R.id.itemImageView);
        }
    }
}
