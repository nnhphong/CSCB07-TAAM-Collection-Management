package com.example.b07demosummer2024;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoginModel implements Contract.Model {
    FirebaseDatabase db = FirebaseDatabase.getInstance("https://cscb07-taam-management-default-rtdb.firebaseio.com/");
    DatabaseReference ref = db.getReference("/user_info");

    public LoginModel() {}

    public Task<User> login(String username, String password) {
        TaskCompletionSource<User> tcs = new TaskCompletionSource<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User matched_user = null;
                for (DataSnapshot users : snapshot.getChildren()) {
                    User user = users.getValue(User.class);
                    if (user == null) {
                        continue;
                    }
                    if (!user.getUsername().isEmpty() && !username.isEmpty() &&
                            !Objects.equals(user.getUsername(), username)) {
                        continue;
                    }
                    if (!user.getPassword().isEmpty() && !password.isEmpty() &&
                            !Objects.equals(user.getPassword(), password)) {
                        continue;
                    }
                    matched_user = user;
                    break;
                }
                tcs.setResult(matched_user);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        return tcs.getTask();
    }
}
