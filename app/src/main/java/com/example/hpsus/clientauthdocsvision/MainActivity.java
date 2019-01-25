package com.example.hpsus.clientauthdocsvision;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.example.hpsus.clientauthdocsvision.Presenter.MainPresenter;
import com.rengwuxian.materialedittext.MaterialEditText;

import fr.ganfra.materialspinner.MaterialSpinner;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    private static final String TAG = "MainActivity";

    private MainContract.Presenter mPresenter;

    private ScrollView sView;
    private LinearLayout llView;
    private MaterialEditText edtAdressServer;
    private MaterialSpinner spNameBase;
    private MaterialEditText edtLogin;
    private MaterialEditText edtPassword;
    private Button btnIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPresenter = new MainPresenter(this);
        mPresenter.onCreate();

        sView = (ScrollView) findViewById(R.id.sView);
        llView = (LinearLayout) findViewById(R.id.llView);
        edtAdressServer = (MaterialEditText) findViewById(R.id.edtAdressServer);
        spNameBase = (MaterialSpinner) findViewById(R.id.spNameBase);
        edtLogin = (MaterialEditText) findViewById(R.id.edtLogin);
        edtPassword = (MaterialEditText) findViewById(R.id.edtPassword);
        btnIn = (Button) findViewById(R.id.btnIn);

        spNameBase.setAdapter(mPresenter.addAdapter());

        spNameBase.setEnabled(false);
        edtLogin.setEnabled(false);
        edtPassword.setEnabled(false);
        btnIn.setEnabled(false);

        edtAdressServer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mPresenter.getSyncMessageInfo();
                    spNameBase.setEnabled(true);
                    Log.d(TAG, "no hasFocus");
                }
            }
        });
        edtAdressServer.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    mPresenter.getSyncMessageInfo();
                    spNameBase.setEnabled(true);
                     Log.d(TAG, "keyCode ok");
                    return true;
                }
                return false;
            }
        });
        spNameBase.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected ok");

                if(position==0){
                    edtLogin.setEnabled(true);
                    edtPassword.setEnabled(true);
                    btnIn.setEnabled(true);
                }else{
                    edtLogin.setEnabled(false);
                    edtPassword.setEnabled(false);
                    btnIn.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "onNothingSelected ok");
            }
        });
        btnIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onBtnInClicked();
            }
        });
    }

    @Override
    public void showText() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
        Log.d(TAG, "onDestroy()");
    }
}
