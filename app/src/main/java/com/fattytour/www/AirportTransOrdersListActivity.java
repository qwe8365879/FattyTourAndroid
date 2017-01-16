package com.fattytour.www;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fattytour.www.module.AirportTransOrder;
import com.fattytour.www.view.ProgressIndicator;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class AirportTransOrdersListActivity extends AppCompatActivity {
    private static final String __TAG__ = "AirTransOrderActivity";

    private ListView orderListView;
    private ProgressIndicator mProgressView;

    private static String orderType = "my_orders";
    private static final String[] titleMap = {"my_orders", "pending_orders", "booked_orders"};


    private ArrayList<AirportTransOrder> orders;
    private ArrayList<ListItem> orderList;
    private ListItemAdapter listItemAdapter;

    public static String getOrderType() {
        return orderType;
    }

    public static void setOrderType(String orderType) {
        for(String temp : titleMap){
            if(temp == orderType){
                AirportTransOrdersListActivity.orderType = orderType;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airport_trans_orders_list);
        orderListView = (ListView) findViewById(R.id.ordersList);
        mProgressView = (ProgressIndicator) findViewById(R.id.loginProgressIndicator);
        this.setTitle(getResources().getString(getResources().getIdentifier(orderType, "string", getPackageName())));


        orders = Global.communicationCore.getMAirportTransOrders().get(orderType);
        orderList = new ArrayList<>();
        listItemAdapter = new ListItemAdapter(this, orderList);
        orderListView.setAdapter(listItemAdapter);

        orderListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListItem listItem = orderList.get(position);
                if(listItem.getType() == "item") {
                    Intent intent = new Intent();
                    AirportTransOrder order = orders.get(position);
                    AirportTransOrderDetailActivity.setOrder(order);
                    switch (orderType){
                        case "my_orders":
                            AirportTransOrderDetailActivity.actionType = "unassign_order";
                            break;
                        case "pending_orders":
                            AirportTransOrderDetailActivity.actionType = "assign_order";
                            break;
                        default:
                            AirportTransOrderDetailActivity.actionType = "booked_order";
                            break;
                    }

                    intent.setClass(AirportTransOrdersListActivity.this, AirportTransOrderDetailActivity.class);
                    startActivity(intent);
                }
            }
        });

        Observer getAirportTransOrders = new Observer() {
            @Override
            public void update(Observable o, final Object arg) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        orders = Global.communicationCore.getMAirportTransOrders().get(orderType);
                        loadDataToList();
                    }
                });
                NotificationCenter.defaultCenter().removeObserver(Global.NotificationName.getAirportTransOrder, this);
            }
        };
        NotificationCenter.defaultCenter().addObserver(Global.NotificationName.getAirportTransOrder,getAirportTransOrders);
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadDataToList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menuItemRefresh){
            Global.communicationCore.getAirportTransOrders();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadDataToList(){
        orderList.clear();
        if(orders != null && !orders.isEmpty()){
            for(AirportTransOrder order : orders){
                orderList.add(new ListNormalItem(order.id, order.title));
            }
        }
        listItemAdapter.notifyDataSetChanged();
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
