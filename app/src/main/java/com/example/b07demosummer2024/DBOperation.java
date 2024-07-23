package com.example.b07demosummer2024;

import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
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
import java.util.List;
import java.util.Objects;

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

    public UploadTask addImage(Uri selectedMedia, AddItemFragment fragment, int lotNumber) {
        String id = "id" + lotNumber;
        StorageReference media = mediaRef.child(id);
        UploadTask uploadTask = media.putFile(selectedMedia);

        return uploadTask;
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
                    if (!criteria.getCategory().isEmpty() &&
                            !Objects.equals(criteria.getCategory(), item.getCategory())) {
                        continue;
                    }
                    if (!criteria.getPeriod().isEmpty() &&
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
