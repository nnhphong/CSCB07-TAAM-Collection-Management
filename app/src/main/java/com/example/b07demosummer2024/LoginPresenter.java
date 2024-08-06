package com.example.b07demosummer2024;

import androidx.annotation.NonNull;

import java.util.List;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class LoginPresenter implements Contract.Presenter{
    private LoginModel model;
    private LoginView view;

    public LoginPresenter(LoginModel model, LoginView view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void onButtonClick(String username, String password) {
        Task<List<User>> task = model.login(username, password);
        task.addOnCompleteListener(new OnCompleteListener<List<User>>() {
            @Override
            public void onComplete(@NonNull Task<List<User>> task) {
                List<User> res = task.getResult();
                if (res.isEmpty()) {
                    view.onFailure();
                }
                else {
                    view.onSuccess();
                }
            }
        });
    }
}
