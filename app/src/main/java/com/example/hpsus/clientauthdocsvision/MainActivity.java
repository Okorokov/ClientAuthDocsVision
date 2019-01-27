package com.example.hpsus.clientauthdocsvision;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

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
    private ProgressBar pbGetBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPresenter = new MainPresenter(this);

        sView = (ScrollView) findViewById(R.id.sView);
        llView = (LinearLayout) findViewById(R.id.llView);
        edtAdressServer = (MaterialEditText) findViewById(R.id.edtAdressServer);
        spNameBase = (MaterialSpinner) findViewById(R.id.spNameBase);
        edtLogin = (MaterialEditText) findViewById(R.id.edtLogin);
        edtPassword = (MaterialEditText) findViewById(R.id.edtPassword);
        btnIn = (Button) findViewById(R.id.btnIn);
        pbGetBase = (ProgressBar) findViewById(R.id.pbGetBase);

        spNameBase.setAdapter(mPresenter.addAdapter());

        mPresenter.onCreate();

        edtAdressServer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mPresenter.adrrSerhasFocus(hasFocus, edtAdressServer.getText().toString());
            }
        });
        edtAdressServer.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                mPresenter.adrrSeronKey(keyCode, event, edtAdressServer.getText().toString());
                return false;
            }
        });
        spNameBase.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPresenter.setPositionUuid(position, parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "onNothingSelected ok");
            }
        });
        btnIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onBtnInClicked(edtLogin.getText().toString(), edtPassword.getText().toString());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    @Override
    public void onAuthResult(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSetEnabled(int setVis, Boolean spNameBaseSE, Boolean edtLoginSE, Boolean edtPasswordSE, Boolean btnInSE) {
        pbGetBase.setVisibility(setVis);
        spNameBase.setEnabled(spNameBaseSE);
        edtLogin.setEnabled(edtLoginSE);
        edtPassword.setEnabled(edtPasswordSE);
        btnIn.setEnabled(btnInSE);
    }

    @Override
    public void onSetErrorAddrSer(String message) {
        edtAdressServer.setError(message);

    }

    @Override
    public void onSetErrorLogin(String message) {
        edtLogin.setError(message);
    }

    @Override
    public void onSetErrorPassword(String message) {
        edtPassword.setError(message);
    }

    @Override
    public void onSetErrorBaseName(String message) {
        spNameBase.setError(message);
    }


}
