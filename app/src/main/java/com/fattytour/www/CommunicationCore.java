package com.fattytour.www;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.fattytour.www.module.AirportTransOrder;
import com.fattytour.www.module.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Junjie on 4/01/2017.
 */

class CommunicationCore {
    private String encryptKey = "jjcustomize";
    private Boolean isLogined = false;
    private User loginedUser;
    private HashMap<String, ArrayList<AirportTransOrder>> mAirportTransOrders;
    private CookieManager mCookieManager;

    public CommunicationCore() {
        this.mCookieManager = new CookieManager();
        this.mAirportTransOrders = new HashMap<>();
    }

    private class ConnectionUrl{
        public static final String host = "https://test-qwe8365879.c9users.io/";
        //public final String host= "https://www.fattytour.com/";
        public static final String api = "wp-admin/admin-ajax.php";
    }

    Boolean getLogined() {
        return isLogined;
    }

    User getLoginedUser(){
        return this.loginedUser;
    }

    public HashMap<String, ArrayList<AirportTransOrder>> getMAirportTransOrders() {
        return mAirportTransOrders;
    }

    interface CallbackHandler{
        void run(JSONObject object);
    }

    void login(final String username, final String passwordEncrypted){
        Log.d("login", "start Login");
        String postString = String.format("action=jj_login&username=%s&password=%s", username, passwordEncrypted);


        ApiConnection apiConnection = new ApiConnection(postString, new CallbackHandler() {
            @Override
            public void run(JSONObject object) {
                try {
                    Boolean hasError = object.getBoolean("hasError");
                    if(hasError){
                        Log.e("login", object.getString("errorMsg"));
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainApplication.getContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.remove(Global.LoaclStorageKey.username);
                        editor.remove(Global.LoaclStorageKey.password);
                        editor.apply();
                        isLogined = false;
                        loginedUser = null;
                        NotificationCenter.defaultCenter().postNotification(Global.NotificationName.loginFailed, object.getString("errorMsg"));
                    }else{
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainApplication.getContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(Global.LoaclStorageKey.username, username);
                        editor.putString(Global.LoaclStorageKey.password, passwordEncrypted);
                        editor.apply();
                        User loginUser = new User();
                        JSONObject userData = object.getJSONObject("userData");
                        loginUser.id = userData.getString("ID");
                        loginUser.fullname = userData.getString("display_name");
                        loginUser.email = userData.getString("user_email");
                        isLogined = true;
                        loginedUser = loginUser;
                        NotificationCenter.defaultCenter().postNotification(Global.NotificationName.loginSuccess, object);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        apiConnection.execute((Void) null);
    }

    void getAirportTransOrders(final String orderType){
        Log.d("get order", "start get order");
        String postString = String.format("action=jj_get_airport_trans_orders&order_type=%s", orderType);
        ApiConnection apiConnection = new ApiConnection(postString, new CallbackHandler() {
            @Override
            public void run(JSONObject object) {
                try{
                    Boolean hasError = object.getBoolean("hasError");
                    if(!hasError){
                        HashMap<String, ArrayList<AirportTransOrder>> allOrders = new HashMap<>();

                        JSONObject rawAllOrders = object.getJSONObject("orders");
                        for(Iterator<String> orderTypes = rawAllOrders.keys(); orderTypes.hasNext();){
                            String orderType = orderTypes.next();
                            JSONArray rawOrders = rawAllOrders.getJSONArray(orderType);
                            ArrayList<AirportTransOrder> orders = new ArrayList<>();
                            for(int i = 0; i < rawOrders.length(); i++){
                                JSONObject rawOrder = rawOrders.getJSONObject(i);
                                AirportTransOrder order = new AirportTransOrder();
                                order.id = Integer.toString(rawOrder.getInt("id"));
                                order.title = rawOrder.getString("title");
                                order.data = new HashMap<>();
                                JSONObject rawDatas = rawOrder.getJSONObject("order_data");
                                for(Iterator<String> dataKeys = rawDatas.keys(); dataKeys.hasNext();){
                                    String key = dataKeys.next();
                                    order.data.put(key, rawDatas.getString(key));
                                }
                                orders.add(order);
                            }
                            allOrders.put(orderType, orders);
                        }
                        mAirportTransOrders = allOrders;
                        NotificationCenter.defaultCenter().postNotification(Global.NotificationName.getAirportTransOrder, allOrders);
                    }else{
                        Log.e("has error", object.getString("errorMsg"));
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
        apiConnection.execute((Void) null);
    }

    void actionsForAirportTransService(String actionType, String orderId, String memberId){
        orderId = EncryptUtils.Encrypt(orderId, "jj-customizer");
        String postString = String.format("action=jj_%s_air_trans_order&order_id=%s&member_id=%s", actionType, orderId, memberId);
        ApiConnection apiConnection = new ApiConnection(postString, new CallbackHandler() {
            @Override
            public void run(JSONObject object) {
                try{
                    Boolean hasError = object.getBoolean("hasError");
                    if(!hasError){
                        NotificationCenter.defaultCenter().postNotification(Global.NotificationName.airportTransOrderControlSuccess, null);
                    }else{
                        Log.e("has error", object.getString("errorMsg"));
                        NotificationCenter.defaultCenter().postNotification(Global.NotificationName.airportTransOrderControlFailed, object.getString("errorMsg"));
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
        apiConnection.execute((Void) null);
    }

    void getAirportTransOrders(){
        getAirportTransOrders("all");
    }

    private class ApiConnection extends AsyncTask<Void, Void, Boolean>{
        private String postString;
        private CallbackHandler successHandler;

        public ApiConnection(String postString, CallbackHandler successHandler){
            this.postString = postString;
            this.successHandler = successHandler;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            Boolean result = false;
            CookieStore rawCookieStore = mCookieManager.getCookieStore();

            try{
                URL url = new URL(ConnectionUrl.host + ConnectionUrl.api);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                if(rawCookieStore.getCookies().size() > 0){
                    urlConnection.setRequestProperty("Cookie", TextUtils.join(";", rawCookieStore.getCookies()));
                }
                urlConnection.setDoOutput(true);
                String postData = URLEncoder.encode(this.postString, "UTF-8");
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream());
                outputStreamWriter.write(this.postString);
                outputStreamWriter.flush();

                int responseCode = urlConnection.getResponseCode();
                if(responseCode == 200){
                    reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null){
                        sb.append(line);
                    }
                    String response = sb.toString();
                    try{
                        JSONObject obj = new JSONObject(response);
                        Log.d("obj", obj.toString());
                        successHandler.run(obj);
                        Map<String, List<String>> headerFields = urlConnection.getHeaderFields();
                        List<String> cookiesHeader = headerFields.get("Set-Cookie");
                        if(cookiesHeader != null){
                            for(String cookie : cookiesHeader){
                                rawCookieStore.add(null, HttpCookie.parse(cookie).get(0));
                            }
                        }
                        Log.w("cookie", rawCookieStore.getCookies().toString());
                    }catch (JSONException e){
                        Log.e("parse Json", e.toString());
                        NotificationCenter.defaultCenter().postNotification(Global.NotificationName.connectionError, e.toString());
                    }
                }else{
                    Log.e("http response error", urlConnection.getResponseCode() + urlConnection.getResponseMessage());
                    NotificationCenter.defaultCenter().postNotification(Global.NotificationName.connectionError, urlConnection.getResponseMessage());
                }
            }catch (IOException e){
                Log.e("http Connection", e.toString());
                NotificationCenter.defaultCenter().postNotification(Global.NotificationName.connectionError, e.toString());
            }finally {
                if(urlConnection != null){
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("closing stream", e.toString());
                    }
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if(success){

            }
        }
    }
}
