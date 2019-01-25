package com.example.hpsus.clientauthdocsvision.Presenter;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;


import com.example.hpsus.clientauthdocsvision.Common.Common;
import com.example.hpsus.clientauthdocsvision.MainContract;
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

    public MainPresenter(MainContract.View mView) {
        this.mView = mView;
        this.mRepository = new MainRepository();
        Log.d(TAG, "Constructor");
    }

    @Override
    public void onBtnInClicked() {

    }

    @Override
    public void onCreate() {
        mCompositedisposable = new CompositeDisposable();
        Retrofit retrofit = new RetrofitClient().getInstance(Common.API_SERVERA_DEFAULT);

        mService = retrofit.create(IOpenDocsVisionApi.class);
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
        RequestBody bodyPost = RequestBody.create(Common.JSON, JsonToStringPostBase());
        Request request = new Request.Builder()
                .url(Common.API_SERVERA_DEFAULT + Common.URL_POST_MESSAGE)
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
            Thread.sleep(500);

            RequestBody bodyGet = RequestBody.create(Common.JSON, JsonToStringGetBase());
            mCompositedisposable.add(mService.getSyncMessage(bodyGet)
                    .timeout(1, TimeUnit.SECONDS)
                    .repeat(3)
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
                            Log.d(TAG, "mService " + throwable.getMessage());
                        }
                    })
            );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private String JsonToStringPostBase() {
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObjectPost = new JSONObject();
        try {
            jsonObject.put("Id", "7f660cd0-6e60-4a92-903c-da5944594b3f");
            jsonObject.put("MessageType", "Dv.DatabasesRequest");
            jsonObject.put("RequestId", "7f660cd0-6e60-4a92-903c-da5944594b3f");
            jsonObject.put("SettingsHash", 0);
            jsonObject.put("Message", jsonObjectPost.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String JsonToStringGetBase() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("RequestId", "7f660cd0-6e60-4a92-903c-da5944594b3f");
            jsonObject.put("SessionToken", "{}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private void deserializeFromJSON(List<SyncMessageResult> smr) {
        TreeSet<String> treeSet = new TreeSet<>();

        ObjectMapper mapper = new ObjectMapper();

        for (SyncMessageResult syncMessageResult : smr) {
            String message = syncMessageResult.getMessage();
            JsonNode rootNode = null;
            try {
                rootNode = mapper.readValue(message, JsonNode.class);
                ArrayNode dockersArrayNode = (ArrayNode) rootNode.get("Databases");

                for (int i = 0; i <= dockersArrayNode.size() - 1; i++) {
                    treeSet.add(dockersArrayNode.get(i).path("DisplayName").asText());
                }
                Iterator<String> itr = treeSet.iterator();
                mItems = new ArrayList<>();

                while (itr.hasNext()) {
                    mItems.add(itr.next().toString());
                }

                //spNameBase.setEnabled(true);
                mAdapter.clear();
                mAdapter.addAll(mItems);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
