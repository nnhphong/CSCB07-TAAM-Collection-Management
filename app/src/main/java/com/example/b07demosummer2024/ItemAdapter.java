package com.example.b07demosummer2024;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.FirebaseStorage;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private final List<Item> itemList;
    private Context context;

    public ItemAdapter(List<Item> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
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
//        Glide.with(context).load(item.getMediaLink()).into(holder.itemImageView);
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://cscb07-taam-management.appspot.com");
        StorageReference fileRef = storage.getReference("/").child(item.getMediaLink());

        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // `uri` is the download URL for the file
                String url = uri.toString();

                // If the file is an image, load it into the ImageView using Glide
                Glide.with(context).load(url).into(holder.itemImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                System.out.println("Loading failure");
            }
        });

        // adding to initialize the state of the checkbox and changing if it has been selected
        holder.itemCheckedBox.setChecked(item.isSelected());

        holder.itemCheckedBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setSelected(!item.isSelected());
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public List<Item> getSelected() {
        List<Item> selected = new ArrayList<>();

        for (Item item : itemList) {
            if (item.isSelected()) {
                selected.add(item);
            }
        }

        return selected;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemTextViewLotNumber, itemTextViewName, itemTextViewPeriod, itemTextViewDescription;
        ImageView itemImageView;
        CheckBox itemCheckedBox;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTextViewLotNumber = itemView.findViewById(R.id.itemTextViewLotNumber);
            itemTextViewName = itemView.findViewById(R.id.itemTextViewName);
            itemTextViewPeriod = itemView.findViewById(R.id.itemTextViewPeriod);
            itemTextViewDescription = itemView.findViewById(R.id.itemTextViewDescription);
            itemImageView = itemView.findViewById(R.id.itemImageView);
            itemCheckedBox = itemView.findViewById(R.id.itemCheckBox);
        }
    }
}
