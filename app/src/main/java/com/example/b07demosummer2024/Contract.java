package com.example.b07demosummer2024;

public interface Contract {
    interface View {
        void setString(String string);
    }

    interface Model {
        interface OnFinishedListener {
            void onFinished(String string);
        }
        void getNextCourse(Contract.Model.OnFinishedListener onFinishedListener);
    }

    interface Presenter {
        void onButtonClick();
    }
}
