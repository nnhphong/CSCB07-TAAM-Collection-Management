package com.example.b07demosummer2024;

import android.provider.ContactsContract;

import androidx.annotation.NonNull;

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

    public void addItem(Item item) {

    }

    public List<Item> searchAndDisplay(Item criteria) {
        List<Item> filteredItem = new ArrayList<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot id : snapshot.getChildren()) {
                    for (DataSnapshot data: id.getChildren()) {
                        Item item = data.getValue(Item.class);
                        if (item == null) {
                            System.out.println("Item is null!");
                            continue;
                        }
//                        System.out.println(criteria.getLotNumber() + " " + item.getLotNumber());
                        if (criteria.getLotNumber() != null && item.getLotNumber() != null &&
                                !Objects.equals(item.getLotNumber(), criteria.getLotNumber())) {
                            continue;
                        }
//                        System.out.println(criteria.getName() + " " + item.getName());
                        if (!criteria.getName().isEmpty() && !item.getName().isEmpty() &&
                                !Objects.equals(criteria.getName(), item.getName())) {
                            continue;
                        }
//                        System.out.println(criteria.getCategory() + " " + item.getCategory());
                        if (!criteria.getCategory().equals("None") &&
                                !Objects.equals(criteria.getCategory(), item.getCategory())) {
                            continue;
                        }
//                        System.out.println(criteria.getPeriod() + " " + item.getPeriod());
                        if (!criteria.getPeriod().equals("None") &&
                                !Objects.equals(criteria.getPeriod(), item.getPeriod())) {
                            continue;
                        }
                        filteredItem.add(item);
                    }
                }

                for (Item item : filteredItem) {
                    System.out.println(item.getLotNumber() + " " + item.getName() + " " + item.getCategory() + " " + item.getPeriod());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("WTF");
            }
        });

        return filteredItem;
    }

    public void removeItem(Item item) {

    }
}
