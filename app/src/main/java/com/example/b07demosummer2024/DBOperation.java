package com.example.b07demosummer2024;

import android.provider.ContactsContract;
import android.widget.EditText;

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
    /*
    public Task<Void> removeItem(Item item, RemoveItemFragment fragment) {
        String id = "id" + item.getLotNumber();

        return ref.child(id).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                fragment.displayToast("Item deleted successfully");
            } else {
                fragment.displayToast("Failed to delete item");
            }
        });
    }
    */

    public Task<List<User>> login(EditText username, EditText password) {
        TaskCompletionSource<List<User>> tcs = new TaskCompletionSource<>();
        List<User> user_list = new ArrayList<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot users : snapshot.getChildren()) {
                    User user = users.getValue(User.class);
                    if (user == null) {
                        continue;
                    }
                    if (!user.getUsername().isEmpty() && !username.getText().toString().isEmpty() &&
                            !Objects.equals(user.getUsername(), username.getText().toString())) {
                        continue;
                    }
                    if (!user.getPassword().isEmpty() && !password.getText().toString().isEmpty() &&
                            !Objects.equals(user.getPassword(), password.getText().toString())) {
                        continue;
                    }
                    user_list.add(user);
                }
                tcs.setResult(user_list);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        return tcs.getTask();
    }
}
