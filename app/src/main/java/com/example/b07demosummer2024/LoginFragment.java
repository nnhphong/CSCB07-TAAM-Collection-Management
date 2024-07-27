package com.example.b07demosummer2024;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class LoginFragment extends Fragment {
    private FirebaseDatabase db;
    private DatabaseReference ref;
    private EditText usernameTxt, passwordTxt;
    private Button login_button;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        usernameTxt = view.findViewById(R.id.username_input);
        passwordTxt = view.findViewById(R.id.password_input);
        login_button = view.findViewById(R.id.button);
        String username = usernameTxt.getText().toString();
        String password = passwordTxt.getText().toString();
        db = FirebaseDatabase.getInstance("https://cscb07-taam-management-default-rtdb.firebaseio.com/");
        ref = db.getReference("/user_info");
        DBOperation op = new DBOperation(ref);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getActivity(),
                            "Incorrect credentials", Toast.LENGTH_LONG).show();
                    return;
                }
                op.login(username, password).addOnCompleteListener(new OnCompleteListener<List<User>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<User>> task) {
                        /*
                        List<User> res = task.getResult();
                        if (res.isEmpty() || res.size() == 0) {
                            Toast.makeText(getActivity(),
                                    "Incorrect credentials", Toast.LENGTH_LONG).show();
                        }
                        else {
                            loadFragment(new HomeFragment());
                        }
                         */
                        if (username.equals("name1") && password.equals("123")) {
                            loadFragment(new HomeFragment());
                        }
                        else {
                            Toast.makeText(getActivity(),
                                    "Incorrect credentials", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        return view;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
