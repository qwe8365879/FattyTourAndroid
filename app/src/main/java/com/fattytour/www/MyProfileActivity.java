package com.fattytour.www;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fattytour.www.view.ProgressIndicator;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class MyProfileActivity extends AppCompatActivity {
    private String __TAG__ = "myProfileActivity";
    private ListView menuListView;
    private ProgressIndicator mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        menuListView = (ListView) findViewById(R.id.menu_list_view);
        mProgressView = (ProgressIndicator) findViewById(R.id.progressIndicator);

        final ArrayList<ListItem> mainMenu = new ArrayList<>();
        mainMenu.add(new ListSection());
        mainMenu.add(new ListNormalItem("mLogin", "我", "登录"));
        mainMenu.add(new ListSection());
        mainMenu.add(new ListNormalItem("myAirportTransOrders", "我的订单"));
        mainMenu.add(new ListNormalItem("pendingAirportTransOrders", "新的订单"));
        mainMenu.add(new ListNormalItem("bookedAirportTransOrders", "已抢订单"));

        final ListItemAdapter adapter = new ListItemAdapter(this, mainMenu);
        menuListView.setAdapter(adapter);
        menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListItem listItem = mainMenu.get(position);
                if(listItem.getType() == "item"){
                    Intent intent = new Intent();
                    switch (position){
                        case 1:
                            intent.setClass(MyProfileActivity.this, LoginActivity.class);
                            startActivity(intent);
                            break;
                        case 3:
                            intent.setClass(MyProfileActivity.this, AirportTransOrdersListActivity.class);
                            AirportTransOrdersListActivity.setOrderType("my_orders");
                            startActivity(intent);
                            break;
                        case 4:
                            intent.setClass(MyProfileActivity.this, AirportTransOrdersListActivity.class);
                            AirportTransOrdersListActivity.setOrderType("pending_orders");
                            startActivity(intent);
                            break;
                        case 5:
                            intent.setClass(MyProfileActivity.this, AirportTransOrdersListActivity.class);
                            AirportTransOrdersListActivity.setOrderType("booked_orders");
                            startActivity(intent);
                            break;
                    }
                }
            }
        });
        Observer loginSuccess = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                Log.d(__TAG__, "login success");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgress(false);
                        loginCompletion(true);
                    }
                });
                Global.communicationCore.getAirportTransOrders();
                //NotificationCenter.defaultCenter().removeObserver(Global.NotificationName.loginSuccess, this);
            }
        };
        NotificationCenter.defaultCenter().addObserver(Global.NotificationName.loginSuccess, loginSuccess);

        Observer loginFailed = new Observer() {
            @Override
            public void update(Observable o, final Object arg) {
                Log.d(__TAG__, "login failed");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgress(false);
                        loginCompletion(false);
                        Toast.makeText(MainApplication.getContext(), arg.toString(), Toast.LENGTH_LONG).show();
                    }
                });
                //NotificationCenter.defaultCenter().removeObserver(Global.NotificationName.loginFailed, this);
            }

        };
        NotificationCenter.defaultCenter().addObserver(Global.NotificationName.loginFailed, loginFailed);

        Observer connectionError = new Observer() {
            @Override
            public void update(Observable o, final Object arg) {
                Log.d(__TAG__, arg.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgress(false);
                        Toast.makeText(MainApplication.getContext(), arg.toString(), Toast.LENGTH_LONG).show();
                    }

                });

                //NotificationCenter.defaultCenter().removeObserver(Global.NotificationName.connectionError, this);
            }
        };
        NotificationCenter.defaultCenter().addObserver(Global.NotificationName.connectionError, connectionError);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainApplication.getContext());
        String username = preferences.getString(Global.LoaclStorageKey.username, "");
        String encryptedPassword = preferences.getString(Global.LoaclStorageKey.password, "");
        if(username.isEmpty() || encryptedPassword.isEmpty()){
            Log.i(__TAG__, "no defualt data");
            loginCompletion(false);
        }else{
            if(Global.communicationCore.getLogined()){
                loginCompletion(true);
            }else{
                mProgressView.setTitle("登录中");
                Global.communicationCore.login(username, encryptedPassword);
                showProgress(true);
            }
        }
    }

    private void loginCompletion(Boolean result){
        ListItemAdapter listAdapter = (ListItemAdapter) menuListView.getAdapter();
        ListNormalItem loginMenuItem = (ListNormalItem) listAdapter.getItem(1);
        if(result){
            loginMenuItem.setTitle(Global.communicationCore.getLoginedUser().fullname);
            loginMenuItem.setDetail("切换用户");

        }else{
            loginMenuItem.setTitle("未登录");
            loginMenuItem.setDetail("登录");
            Intent intent = new Intent();
            intent.setClass(this, LoginActivity.class);
            startActivity(intent);
        }
        listAdapter.notifyDataSetChanged();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        }else{
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}
