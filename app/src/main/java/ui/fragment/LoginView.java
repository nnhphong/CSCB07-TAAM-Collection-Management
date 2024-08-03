package ui.fragment;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import cscb07.taam_project.R;

public class LoginView extends Fragment {
    private EditText usernameTxt, passwordTxt;
    private Button login_button;
    private CheckBox togglePasswordVisibility;
    private boolean isPasswordVisible = false;
    private LoginPresenter presenter;

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
            } else {
                passwordTxt.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            passwordTxt.setSelection(passwordTxt.length());
        });

        presenter = new LoginPresenter(new LoginModel(), this);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameTxt.getText().toString();
                String password = passwordTxt.getText().toString();
                presenter.onButtonClick(username, password);
            }
        });
        return view;
    }

    public void onSuccess() {
        Toast.makeText(getActivity(), "Log In Successful", Toast.LENGTH_SHORT).show();
        loadFragment(new HomeFragment(true));
    }

    public void onFailure() {
        Toast.makeText(getActivity(), "Incorrect credentials", Toast.LENGTH_SHORT).show();
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
