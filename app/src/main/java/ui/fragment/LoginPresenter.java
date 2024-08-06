package ui.fragment;

import androidx.annotation.NonNull;

import java.util.List;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import data.database.User;

public class LoginPresenter implements Contract.Presenter{
    private LoginModel model;
    private LoginView view;

    public LoginPresenter(LoginModel model, LoginView view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void onButtonClick(String username, String password) {
        Task<User> task = model.login(username, password);
        task.addOnCompleteListener(new OnCompleteListener<User>() {
            @Override
            public void onComplete(@NonNull Task<User> task) {
                User res = task.getResult();
                if (res == null) {
                    view.onFailure();
                }
                else {
                    view.onSuccess();
                }
            }
        });
    }
}
