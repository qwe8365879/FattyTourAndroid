package com.fattytour.www;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fattytour.www.module.AirportTransOrder;
import com.fattytour.www.view.ProgressIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class AirportTransOrderDetailActivity extends AppCompatActivity {
    private static final String __TAG__ = "AirOrderActivity";
    private static final String[] __DATAKEY__ = {"name", "phone", "email", "wechat", "qq", "address", "transport", "airport", "depart_datetime", "flight_no", "on_person", "on_children", "large_baggage", "small_baggage", "distance", "customer_note"};


    private ListView orderDetailListView;
    private ArrayList<ListItem> detailList;
    private Button actionButton;
    private ProgressIndicator mProgressView;
    private ListItemAdapter listItemAdapter;

    public static String actionType = "booked_order";
    private static AirportTransOrder order;

    public static AirportTransOrder getOrder() {
        return order;
    }

    public static void setOrder(AirportTransOrder order) {
        AirportTransOrderDetailActivity.order = order;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airport_trans_order_detail);

        orderDetailListView = (ListView) findViewById(R.id.detailList);
        actionButton = (Button) findViewById(R.id.orderControlBtn);
        mProgressView = (ProgressIndicator) findViewById(R.id.progressIndicator);

        switch (actionType){
            case "unassign_order":
                actionButton.setText(R.string.unassign_order);
                actionButton.setBackgroundResource(R.color.colorPrimary);
                actionButton.setEnabled(true);
                actionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        actOnOrder("unassign");
                    }
                });
                break;
            case "assign_order":
                actionButton.setText(R.string.assign_order);
                actionButton.setBackgroundResource(R.color.colorSecondary);
                actionButton.setEnabled(true);
                actionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        actOnOrder("assign");
                    }
                });
                break;
            default:
                actionButton.setText(R.string.booked_order);
                actionButton.setBackgroundResource(R.color.colorGray);
                actionButton.setEnabled(false);
                break;
        }

        detailList = new ArrayList<>();
        listItemAdapter = new ListItemAdapter(this, detailList);
        orderDetailListView.setAdapter(listItemAdapter);
        loadDataToList();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void loadDataToList(){
        detailList.clear();
        if(order != null){
            HashMap<String, String> orderData = order.data;
            for(String k : __DATAKEY__){
                detailList.add(new ListNormalItem(order.id, getString(getResources().getIdentifier("detail_"+k, "string", getPackageName())), orderData.get(k)));
            }
        }
        listItemAdapter.notifyDataSetChanged();
    }

    private void actOnOrder(String action){
        Global.communicationCore.actionsForAirportTransService(action, order.id, Global.communicationCore.getLoginedUser().id);
        mProgressView.setTitle(getString(getResources().getIdentifier("progress_order", "string", getPackageName())));
        showProgress(true);

        Observer airportTransOrderControlSuccess = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                Global.communicationCore.getAirportTransOrders();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgress(false);
                        finish();
                    }
                });
                NotificationCenter.defaultCenter().removeObserver(Global.NotificationName.airportTransOrderControlSuccess, this);
            }
        };
        NotificationCenter.defaultCenter().addObserver(Global.NotificationName.airportTransOrderControlSuccess, airportTransOrderControlSuccess);

        Observer airportTransOrderControlFailed = new Observer() {
            @Override
            public void update(Observable o, final Object arg) {
                Global.communicationCore.getAirportTransOrders();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgress(false);
                        Toast.makeText(MainApplication.getContext(), arg.toString(), Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
                NotificationCenter.defaultCenter().removeObserver(Global.NotificationName.airportTransOrderControlFailed, this);
            }
        };
        NotificationCenter.defaultCenter().addObserver(Global.NotificationName.airportTransOrderControlFailed, airportTransOrderControlFailed);

        Observer connectionError = new Observer() {
            @Override
            public void update(Observable o, final Object arg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgress(false);
                        Toast.makeText(MainApplication.getContext(), arg.toString(), Toast.LENGTH_LONG).show();
                    }

                });

                NotificationCenter.defaultCenter().removeObserver(Global.NotificationName.connectionError, this);
            }
        };
        NotificationCenter.defaultCenter().addObserver(Global.NotificationName.connectionError, connectionError);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show){
        actionButton.setEnabled(!show);
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
