package cscb07.taam_project;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class LoginFragment extends Fragment {
    private FirebaseDatabase db;
    private DatabaseReference ref;
    private EditText usernameTxt, passwordTxt;
    private Button login_button;
    private CheckBox togglePasswordVisibility;
    private boolean isPasswordVisible = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        usernameTxt = view.findViewById(R.id.username_input);
        passwordTxt = view.findViewById(R.id.password_input);
        login_button = view.findViewById(R.id.login_button);
        togglePasswordVisibility = view.findViewById(R.id.is_visible);
        // Hide the password initially
        passwordTxt.setTransformationMethod(PasswordTransformationMethod.getInstance());

        togglePasswordVisibility.setOnCheckedChangeListener((buttonView, isPasswordVisible) -> {
            if (isPasswordVisible) {
                passwordTxt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else {
                passwordTxt.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            passwordTxt.setSelection(passwordTxt.length());
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameTxt.getText().toString();
                String password = passwordTxt.getText().toString();
                db = FirebaseDatabase.getInstance("https://cscb07-taam-management-default-rtdb.firebaseio.com/");
                ref = db.getReference("/user_info");
                DBOperation op = new DBOperation(ref);
                op.login(username, password).addOnCompleteListener(new OnCompleteListener<List<User>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<User>> task) {
                        List<User> res = task.getResult();
                        if (res.isEmpty()) {
                            Toast.makeText(getActivity(),
                                    "Incorrect credentials", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getActivity(),
                                    "Log In Successful", Toast.LENGTH_LONG).show();
                            loadFragment(new HomeFragment(true));
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
