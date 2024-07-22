package com.example.b07demosummer2024;

import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DBOperation {
    private DatabaseReference ref;
    public DBOperation(DatabaseReference ref) {
        this.ref = ref;
    }

    public Task<List<Item>> checkLotFree(Item item, AddItemFragment fragment) {
        TaskCompletionSource<List<Item>> tcs = new TaskCompletionSource<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Item> filteredItems = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Item comp = data.getValue(Item.class);

                    if (comp == null) {
                        Log.v("MainActivity", "Item is null");
                        continue;
                    }

                    Log.v("MainActivty", "" + item.getLotNumber() + " " + comp.getLotNumber() + " " + filteredItems.toString());

                    if (item.getLotNumber() != null && comp.getLotNumber() != null &&
                            item.getLotNumber().equals(comp.getLotNumber())) {
                        filteredItems.add(comp);
                    }
                }

                tcs.setResult(filteredItems);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.wtf("MainActivity", "WTF WHY IS THIS RUN");
            }
        });

        return tcs.getTask();
    }

    public Task<Void> addItem(Item item, AddItemFragment fragment) {
        String id = "id" + item.getLotNumber();

        return ref.child(id).setValue(item).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                fragment.displayToast("Item added successfully");
            } else {
                fragment.displayToast("Failed to add item");
            }
        });
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

                    if (criteria.getLotNumber() != null && item.getLotNumber() != null &&
                            !Objects.equals(item.getLotNumber(), criteria.getLotNumber())) {
                        continue;
                    }
                    if (!criteria.getName().isEmpty() && !item.getName().isEmpty() &&
                            !Objects.equals(criteria.getName(), item.getName())) {
                        continue;
                    }
                    if (!criteria.getCategory().equals("None") &&
                            !Objects.equals(criteria.getCategory(), item.getCategory())) {
                        continue;
                    }
                    if (!criteria.getPeriod().equals("None") &&
                            !Objects.equals(criteria.getPeriod(), item.getPeriod())) {
                        continue;
                    }
                    filteredItem.add(item);
                }

                tcs.setResult(filteredItem);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("WTF");
            }
        });

        return tcs.getTask();
    }

    public void removeItem(Item item) {

    }
}
