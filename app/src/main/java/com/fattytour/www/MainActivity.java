package com.fattytour.www;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private final String host_url = "https://test-qwe8365879.c9users.io";
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private String mUserdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        // set cookie for webview
        android.webkit.CookieManager webCookieManager =
                android.webkit.CookieManager.getInstance();
        webCookieManager.setAcceptCookie(true);
        String[] cookies = intent.getStringArrayExtra("cookies");
        for (int i = 0; i < cookies.length; i++) {
            webCookieManager.setCookie(host_url, cookies[i]);
        }

        setContentView(com.fattytour.www.R.layout.activity_main);

        // show user's display name
        mUserdata = intent.getStringExtra("userdata");
        String display_name = "";
        try {
            JSONObject obj = new JSONObject(mUserdata);
            display_name = obj.getString("display_name");
        } catch ( JSONException e) {
            display_name = "飞度友行";
        }

        this.setTitle(display_name);


        Toolbar toolbar = (Toolbar) findViewById(com.fattytour.www.R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(com.fattytour.www.R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(com.fattytour.www.R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "show notifications here", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.fattytour.www.R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == com.fattytour.www.R.id.action_settings) {


            // show login activity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);

            // destroy main activity
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class UserinfoholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_USERDATA = "user_data";

        public UserinfoholderFragment() {
        }

        /**
         * Returns a new instance of this fragment
         */
        public static UserinfoholderFragment newInstance(String userdata) {
            UserinfoholderFragment fragment = new UserinfoholderFragment();
            Bundle args = new Bundle();
            args.putString(ARG_USERDATA,userdata);
//            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(com.fattytour.www.R.layout.fragment_userdata, container, false);
            TextView textView = (TextView) rootView.findViewById(com.fattytour.www.R.id.section_label);
            textView.setText( getArguments().getString(ARG_USERDATA) );
            return rootView;
        }
    }


    public static class WebviewholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_URL = "webview_url";

        public WebviewholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static WebviewholderFragment newInstance(String url) {
            WebviewholderFragment fragment = new WebviewholderFragment();
            Bundle args = new Bundle();
            args.putString(ARG_URL, url);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(com.fattytour.www.R.layout.fragment_main, container, false);


            WebView webview = (WebView) rootView.findViewById(com.fattytour.www.R.id.web_view);
            WebSettings webSettings = webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webview.setWebViewClient(new WebViewClient());
            String url = getArguments().getString(ARG_URL);
            webview.loadUrl( url );
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch (position) {
                case 0:
                    return UserinfoholderFragment.newInstance( mUserdata );
                case 1: {
                    try {
                        String encodedLocation = URLEncoder.encode("接送机订单","utf-8");
                        return WebviewholderFragment.newInstance(host_url + "/" +encodedLocation +"/");
                    } catch(UnsupportedEncodingException e) {
                        return null;
                    }
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "我";
                case 1:
                    return "浏览网站";
            }
            return null;
        }
    }
}
