package com.example.b07demosummer2024;

import android.net.Uri;
import android.provider.ContactsContract;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Collections;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import data.StringFilter;

public class DBOperation {
    private DatabaseReference ref;
    private StorageReference mediaRef;

    public DBOperation(DatabaseReference ref) {
        this.ref = ref;
    }

    public DBOperation(DatabaseReference ref, StorageReference mediaRef) {
        this.ref = ref;
        this.mediaRef = mediaRef;
    }

    public Task<List<String>> getCategories() {
        List<String> categories = new ArrayList<>();
        categories.add("");
        TaskCompletionSource<List<String>> tcs = new TaskCompletionSource<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Item item = data.getValue(Item.class);
                    if (item == null) {
                        System.out.println("Item is null!");
                        continue;
                    }

                    if (!categories.contains(item.getCategory())) {
                        categories.add(item.getCategory());
                    }
                }

                tcs.setResult(categories);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("WTF");
            }
        });

        return tcs.getTask();
    }

    public Task<List<String>> getPeriods() {
        List<String> periods = new ArrayList<>();
        periods.add("");
        TaskCompletionSource<List<String>> tcs = new TaskCompletionSource<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Item item = data.getValue(Item.class);
                    if (item == null) {
                        System.out.println("Item is null!");
                        continue;
                    }

                    if (!periods.contains(item.getPeriod())) {
                        periods.add(item.getPeriod());
                    }
                }

                tcs.setResult(periods);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return tcs.getTask();
    }

    public UploadTask addImage(Uri selectedMedia, AddItemFragment fragment, int lotNumber) {
        // Todo: check if mediaRef null
        String id = "id" + lotNumber;
        StorageReference media = mediaRef.child(id);
        UploadTask uploadTask = media.putFile(selectedMedia);

        return uploadTask;
    }
  
    public Task<Void> addItem(Item item, AddItemFragment fragment) {
        String id = "id" + item.getLotNumber();

        // Only store these fields in the database
        Map<String, Object> itemMap = new HashMap<>();
        itemMap.put("lotNumber", item.getLotNumber());
        itemMap.put("name", item.getName());
        itemMap.put("category", item.getCategory());
        itemMap.put("period", item.getPeriod());
        itemMap.put("description", item.getDescription());
        itemMap.put("mediaLink", item.getMediaLink());
        itemMap.put("mediaType", item.getMediaType());

        return ref.child(id).setValue(itemMap).addOnCompleteListener(task -> {
        });
    }

    private boolean isMatchedItem(Item item, Item criteria) {
        if (criteria.getLotNumber() != null && item.getLotNumber() != null &&
                !Objects.equals(item.getLotNumber(), criteria.getLotNumber())) {
            return false;
        }

        if (!criteria.getName().isEmpty() && !item.getName().isEmpty() &&
                !StringFilter.matchByRegex(item.getName(), criteria.getName())) {
            return false;
        }
        if (!criteria.getCategory().isEmpty() &&
                !Objects.equals(criteria.getCategory(), item.getCategory())) {
            return false;
        }
        if (!criteria.getPeriod().isEmpty() &&
                !Objects.equals(criteria.getPeriod(), item.getPeriod())) {
            return false;
        }
        return true;
    }

    public Task<List<Item>> searchItem(Item criteria) {
        List<Item> filteredItem = new ArrayList<>();
        TaskCompletionSource<List<Item>> tcs = new TaskCompletionSource<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Item item = data.getValue(Item.class);
                    if (item == null) {
                        System.out.println("Item is null!");
                        continue;
                    }

                    if (isMatchedItem(item, criteria)) {
                        filteredItem.add(item);
                    }
                }

                tcs.setResult(filteredItem);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return tcs.getTask();
    }

    public Task<Void> removeImage(Item item) {
        String mediaLink = item.getMediaLink();

        return mediaRef.child(mediaLink).delete().addOnCompleteListener(task -> {
        });
    }

    public Task<Void> removeItem(Item item) {
        String id = "id" + item.getLotNumber();

        return ref.child(id).removeValue().addOnCompleteListener(task -> {
        });
    }

    public void loadItems(List<Item> items, ItemAdapter itemAdapter) {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Item item = data.getValue(Item.class);
                    if (item != null) {
                        items.add(item);
                    }
                }
                Collections.sort(items);
                itemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("WTF");
            }
        });
    }
}
