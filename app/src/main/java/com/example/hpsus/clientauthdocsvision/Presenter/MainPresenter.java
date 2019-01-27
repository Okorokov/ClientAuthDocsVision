package com.example.hpsus.clientauthdocsvision.Presenter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.hpsus.clientauthdocsvision.Common.Common;
import com.example.hpsus.clientauthdocsvision.MainContract;
import com.example.hpsus.clientauthdocsvision.Model.AuthMessage;
import com.example.hpsus.clientauthdocsvision.Model.Databases;
import com.example.hpsus.clientauthdocsvision.Model.SyncMessageResult;
import com.example.hpsus.clientauthdocsvision.Repository.MainRepository;
import com.example.hpsus.clientauthdocsvision.Retrofit.IOpenDocsVisionApi;
import com.example.hpsus.clientauthdocsvision.Retrofit.RetrofitClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

public class MainPresenter implements MainContract.Presenter {

    private static final String TAG = "MainPresenter";

    private MainContract.View mView;
    private MainContract.Repository mRepository;
    private ArrayAdapter<String> mAdapter;
    private List<String> mItems;
    private CompositeDisposable mCompositedisposable;
    private IOpenDocsVisionApi mService;
    private UUID mNuidRandom;
    private List<Databases> mDatabasesList = new ArrayList<>();
    private TreeSet<String> mTSNameBase = new TreeSet<>();
    private String mTenantId;
    private List<AuthMessage> mAuthMessagesList = new ArrayList<>();
    private String nAdressServer;
    private boolean flErrorAddrSer = false;

    public MainPresenter(MainContract.View mView) {
        this.mView = mView;
        this.mRepository = new MainRepository();
        Log.d(TAG, "Constructor");
    }

    @Override
    public void onBtnInClicked(String login, String password) {
        boolean flLogin = !TextUtils.isEmpty(login) && login.length() > 0;
        boolean flPassword = !TextUtils.isEmpty(password) && password.length() > 0;
        boolean flTenantId = !TextUtils.isEmpty(mTenantId) && mTenantId.length() > 0;
        if (flLogin && flPassword && flTenantId) {
            getSyncMessageAuthInfo();
        }
        if (!flLogin) {
            mView.onSetErrorLogin(Common.ERROR_LOGIN);
        }
        if (!flPassword) {
            mView.onSetErrorPassword(Common.ERROR_PASSWORD);
        }
        if (!flTenantId) {
            mView.onSetErrorBaseName(Common.ERROR_BASE);
        }

    }

    @Override
    public void onCreate() {
        mCompositedisposable = new CompositeDisposable();
        mView.onSetEnabled(View.GONE, true, true, true, true);
    }

    @Override
    public void onDestroy() {
        mCompositedisposable.dispose();
    }

    @Override
    public ArrayAdapter<String> addAdapter() {
        mItems = new ArrayList<>();
        mAdapter = new ArrayAdapter<String>((Context) mView, android.R.layout.simple_spinner_item, mItems);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return mAdapter;
    }

    @Override
    public void getSyncMessageInfo() {
        initService(nAdressServer);
        mNuidRandom = UUID.randomUUID();
        RequestBody bodyPost = RequestBody.create(Common.JSON, JsonToStringPostBase(mNuidRandom));
        Request request = new Request.Builder()
                .url(nAdressServer + Common.URL_MESSAGE + Common.URL_POST_MESSAGE)
                .post(bodyPost)
                .build();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        Call mCall = httpClient.newCall(request);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse");
            }
        });

        try {
            Thread.sleep(1000);

            RequestBody bodyGet = RequestBody.create(Common.JSON, JsonToStringGetBase(mNuidRandom));
            mCompositedisposable.add(mService.getSyncMessage(bodyGet)
                    .timeout(1, TimeUnit.SECONDS)
                    .repeat(5)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<SyncMessageResult>>() {
                        @Override
                        public void accept(List<SyncMessageResult> syncMessageResults) throws Exception {
                            if (!syncMessageResults.isEmpty()) {
                                Log.d(TAG, "mService ok zize");
                                deserializeFromJSON(syncMessageResults);
                            }
                        }


                    }, new Consumer<Throwable>() {

                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            mView.onSetErrorAddrSer(Common.ERROR_NAME_SERVER);
                            mView.onSetEnabled(View.GONE, true, true, true, true);
                            mAdapter.clear();
                            //mItems=new ArrayList<>();
                            //mAdapter.addAll(mItems);
                            Log.d(TAG, "mService " + throwable.getMessage());
                        }
                    })
            );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void getSyncMessageAuthInfo() {
        mNuidRandom = UUID.randomUUID();
        RequestBody bodyPostAuth = RequestBody.create(Common.JSON, JsonToStringPostAuth(mNuidRandom));
        Request requestAuth = new Request.Builder()
                .url(nAdressServer + Common.URL_MESSAGE + Common.URL_POST_AUTH_MESSAGE)
                .post(bodyPostAuth)
                .build();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        Call mCall = httpClient.newCall(requestAuth);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse");
            }
        });

        try {
            Thread.sleep(500);

            RequestBody bodyGetAuth = RequestBody.create(Common.JSON, JsonToStringGetAuth(mNuidRandom));
            mCompositedisposable.add(mService.getSyncMessageAuth(bodyGetAuth)
                    //.timeout(1, TimeUnit.SECONDS)
                    //.repeat(3)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<SyncMessageResult>>() {
                        @Override
                        public void accept(List<SyncMessageResult> syncMessageResults) throws Exception {
                            if (!syncMessageResults.isEmpty()) {
                                Log.d(TAG, "mService ok zize");
                                deserializeFromAuthJSON(syncMessageResults);
                            }
                        }


                    }, new Consumer<Throwable>() {

                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.d(TAG, "mService " + throwable.getMessage());
                        }
                    })
            );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setPositionUuid(int position, String nameBase) {

        if (position >= 0) {
            for (Databases databases : mDatabasesList) {
                if (databases.getDisplayName().equals(nameBase)) {
                    mTenantId = databases.getDatabaseId();
                    Log.d(TAG, "mTenantId " + mTenantId);
                }
            }
            mView.onSetEnabled(View.GONE, true, true, true, true);
        } else {
            mTenantId = null;
        }

    }

    @Override
    public void adrrSerhasFocus(boolean hasFocus, String addrSer) {
        nAdressServer = addrSer;
        if (isValidAdressServer(nAdressServer)) {
            if (!hasFocus) {
                mView.onSetEnabled(View.VISIBLE, true, true, true, true);
                getSyncMessageInfo();
            }
        } else {
            if (flErrorAddrSer) {
                mView.onSetErrorAddrSer(Common.ERROR_NAME_SERVER);
            } else {
                flErrorAddrSer=true;
            }
        }
    }

    @Override
    public void adrrSeronKey(int keyCode, KeyEvent event, String addrSer) {
        nAdressServer = addrSer;
        if (isValidAdressServer(nAdressServer)) {
            if (event.getAction() == KeyEvent.ACTION_DOWN &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                mView.onSetEnabled(View.VISIBLE, true, true, true, true);
                getSyncMessageInfo();

            }
        } else {
            mView.onSetErrorAddrSer(Common.ERROR_NAME_SERVER);
        }
    }

    private String JsonToStringPostBase(UUID uuid) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObjectPost = new JSONObject();
        try {
            jsonObject.put("Id", uuid.toString());
            jsonObject.put("MessageType", "Dv.DatabasesRequest");
            jsonObject.put("RequestId", uuid.toString());
            jsonObject.put("SettingsHash", 0);
            jsonObject.put("Message", jsonObjectPost.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String JsonToStringGetBase(UUID uuid) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObjectPost = new JSONObject();
        try {
            jsonObject.put("RequestId", uuid.toString());
            jsonObject.put("SessionToken", jsonObjectPost);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String JsonToStringPostAuth(UUID uuid) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObjectPost = new JSONObject();
        JSONObject jsonObjectNew = new JSONObject();
        try {
            jsonObject.put("Id", uuid.toString());
            jsonObject.put("MessageType", "Dv.AuthRequest");
            jsonObject.put("RequestId", uuid.toString());

            jsonObjectPost.put("TenantId", mTenantId);
            jsonObjectPost.put("Login", "kmg04");
            jsonObjectPost.put("Password ", "kmg004");
            //jsonObjectPost.put("RefreshToken ", "");

            //jsonObjectPost.put("Thumbprint", "");

            jsonObject.put("Message", jsonObjectPost.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String JsonToStringGetAuth(UUID uuid) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObjectPost = new JSONObject();
        try {
            jsonObject.put("RequestId", uuid.toString());
            //jsonObject.put("SessionToken", jsonObjectPost);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private void deserializeFromJSON(List<SyncMessageResult> smr) {
        mTSNameBase = new TreeSet<>();
        mDatabasesList = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();

        for (SyncMessageResult syncMessageResult : smr) {
            String message = syncMessageResult.getMessage();
            JsonNode rootNode = null;
            try {
                rootNode = mapper.readValue(message, JsonNode.class);
                ArrayNode dockersArrayNode = (ArrayNode) rootNode.get("Databases");

                for (int i = 0; i <= dockersArrayNode.size() - 1; i++) {
                    Databases databases = new Databases();
                    mTSNameBase.add(dockersArrayNode.get(i).path("DisplayName").asText());
                    databases.setDatabaseId(dockersArrayNode.get(i).path("DatabaseId").asText());
                    databases.setDisplayName(dockersArrayNode.get(i).path("DisplayName").asText());
                    databases.setDefault(dockersArrayNode.get(i).path("IsDefault").asBoolean());
                    mDatabasesList.add(databases);
                }
                Iterator<String> itr = mTSNameBase.iterator();
                mItems = new ArrayList<>();

                while (itr.hasNext()) {
                    mItems.add(itr.next().toString());
                }

                //spNameBase.setEnabled(true);
                mAdapter.clear();
                mAdapter.addAll(mItems);
                mView.onSetEnabled(View.GONE, true, true, true, true);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void deserializeFromAuthJSON(List<SyncMessageResult> smr) {
        mAuthMessagesList = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();

        for (SyncMessageResult syncMessageResult : smr) {
            String message = syncMessageResult.getMessage();
            JsonNode rootNode = null;
            try {
                rootNode = mapper.readValue(message, JsonNode.class);
                AuthMessage authMessage = new AuthMessage();
                authMessage.setSessionToken(rootNode.get("SessionToken").asLong());
                authMessage.setJwtToken(rootNode.get("JwtToken").asLong());
                authMessage.setMessage(rootNode.get("Message").asText());
                mAuthMessagesList.add(authMessage);

                if (!mAuthMessagesList.isEmpty()) {
                    //mView.onAuthResult("Ошибка авторизации!");
                    if ((Long.valueOf(mAuthMessagesList.get(0).getJwtToken()) != null) & (mAuthMessagesList.get(0).getJwtToken() != 0)) {
                        mView.onAuthResult("Авторизация прошла успешно!");
                    } else {
                        mView.onAuthResult("Ошибка авторизации!");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private boolean isValidAdressServer(String mAServer) {
        if (!mAServer.isEmpty()) {
            char myChar = mAServer.charAt(0);

            if (myChar != 'h') {
                nAdressServer = "http://" + mAServer;
            }
        }
        return !TextUtils.isEmpty(nAdressServer) &&
                Patterns.WEB_URL.matcher(nAdressServer).matches() &&
                nAdressServer.length() > 7;
    }

    private void initService(String url) {
        Retrofit retrofit = new RetrofitClient().getInstance(url + Common.URL_MESSAGE);
        mService = retrofit.create(IOpenDocsVisionApi.class);
    }
}
