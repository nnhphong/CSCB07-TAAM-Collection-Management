package com.example.b07demosummer2024;

import com.google.android.gms.tasks.Task;

import java.util.List;

public interface Contract {
    interface Model {
        Task<User> login(String username, String password);
    }

    interface View {
        void onSuccess();
        void onFailure();
    }

    interface Presenter {
        void onButtonClick(String username, String password);
    }
}
