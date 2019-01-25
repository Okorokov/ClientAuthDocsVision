package com.example.hpsus.clientauthdocsvision;

import android.widget.ArrayAdapter;

public interface MainContract {
    interface View {
        void showText();
    }

    interface Presenter {

        void onBtnInClicked();

        void onCreate();

        void onDestroy();

        ArrayAdapter<String> addAdapter();

        void getSyncMessageInfo();
    }

    interface Repository {
        String loadMessage();
    }
}
