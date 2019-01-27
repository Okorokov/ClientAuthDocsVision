package com.example.hpsus.clientauthdocsvision;

import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;

public interface MainContract {
    interface View {
        void onAuthResult(String message);

        void onSetEnabled(int setVis, Boolean spNameBaseSE,
                          Boolean edtLoginSE,
                          Boolean edtPasswordSE,
                          Boolean btnInSE);

        void onSetErrorAddrSer(String message);

        void onSetErrorLogin(String message);

        void onSetErrorPassword(String message);

        void onSetErrorBaseName(String message);
    }

    interface Presenter {

        void onBtnInClicked(String login, String password);

        void onCreate();

        void onDestroy();

        ArrayAdapter<String> addAdapter();

        void getSyncMessageInfo();

        void getSyncMessageAuthInfo();

        void setPositionUuid(int position, String nameBase);

        void adrrSerhasFocus(boolean hasFocus, String addrSer);

        void adrrSeronKey(int keyCode, KeyEvent event, String addrSer);
    }

    interface Repository {
        //на перспективу
    }
}
