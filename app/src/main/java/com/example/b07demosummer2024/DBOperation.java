package com.example.b07demosummer2024;

import android.provider.ContactsContract;

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
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class DBOperation {
    private DatabaseReference ref;
    public DBOperation(DatabaseReference ref) {
        this.ref = ref;
    }

    public void addItem(Item item) {

    }

    private String buildRegex(String find) {
        String [] words = find.split(" ");
        StringBuilder regex = new StringBuilder("\\b(");
        for (String word : words) {
            regex.append("\\w*").append(word).append("\\w*");
            if (word != words[words.length - 1]) {
                regex.append("|");
            }
        }
        regex.append(")\\b");
        return regex.toString();
    }

    private Boolean matchByRegex(String target, String find) {
        String regex = buildRegex(find);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(target);
        while (matcher.find()) {
            // if there is at least one instance, return True
            return true;
        }
        return false;
    }

    public Task<List<Item>> searchItem(Item criteria) {
        List<Item> filteredItem = new ArrayList<>();
        TaskCompletionSource<List<Item>> tcs = new TaskCompletionSource<>();

//        System.out.println("Result for matchByRegex(): " + matchByRegex("Brass", "Tang Brass"));
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
                            !matchByRegex(item.getName(), criteria.getName())) {
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
