package com.fumei.pdfread;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fumei.pdfread.utils.httputil.ApiService;
import com.fumei.pdfread.utils.httputil.GetIpInfoResponse;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {
    private static final String ENDPOINT = "http://ip.taobao.com";
    private TextView mTvContent;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      //  setSupportActionBar(toolbar);
        mTvContent = (TextView) findViewById(R.id.tv_content);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab == null) {
            return;
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /*Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(ENDPOINT)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                ApiService apiService = retrofit.create(ApiService.class);

                mProgressBar.setVisibility(View.VISIBLE);

                Call<GetIpInfoResponse> call = apiService.getIpInfo("63.223.108.42");
                call.enqueue(new Callback<GetIpInfoResponse>() {
                    @Override
                    public void onResponse(Response<GetIpInfoResponse> response, Retrofit retrofit) {
                        mProgressBar.setVisibility(View.GONE);
                        GetIpInfoResponse getIpInfoResponse = response.body();
                        mTvContent.setText(getIpInfoResponse.data.country);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        mProgressBar.setVisibility(View.GONE);
                        mTvContent.setText(t.getMessage());
                    }
                });*/

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(ENDPOINT)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .build();
                ApiService apiService = retrofit.create(ApiService.class);
                apiService.getIpInfo("63.223.108.42")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<GetIpInfoResponse>() {
                            @Override
                            public void onCompleted() {
                                mProgressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Throwable e) {
                                mProgressBar.setVisibility(View.GONE);
                                mTvContent.setText(e.getMessage());
                            }

                            @Override
                            public void onNext(GetIpInfoResponse getIpInfoResponse) {
                                mTvContent.setText(getIpInfoResponse.data.country+"\n"+getIpInfoResponse.data.area_id
                                        +"\n"+getIpInfoResponse.data.country_id+"\n"+getIpInfoResponse.data.ip);
                            }
                        });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
