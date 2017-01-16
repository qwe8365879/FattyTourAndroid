package com.fattytour.www;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fattytour.www.view.ProgressIndicator;

import java.util.Observable;
import java.util.Observer;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity {
    private String __TAG__ = "LoginActivity";

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private ProgressIndicator mProgressView;
    private Button mloginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mloginBtn = (Button) findViewById(R.id.login_button);
        mloginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mProgressView = (ProgressIndicator) findViewById(R.id.progressIndicator);

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        Global.communicationCore.login( mUsernameView.getText().toString(), EncryptUtils.Encrypt(mPasswordView.getText().toString(), "jjcustomize"));
        mProgressView.setTitle("登录中");
        showProgress(true);
        final Observer loginSuccess = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                Log.d(__TAG__, "login success");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgress(false);
                        finish();
                    }
                });
                NotificationCenter.defaultCenter().removeObserver(Global.NotificationName.loginSuccess, this);
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
                        Toast.makeText(MainApplication.getContext(), arg.toString(), Toast.LENGTH_LONG).show();
                    }
                });
                NotificationCenter.defaultCenter().removeObserver(Global.NotificationName.loginFailed, this);
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

                NotificationCenter.defaultCenter().removeObserver(Global.NotificationName.connectionError, this);
            }
        };
        NotificationCenter.defaultCenter().addObserver(Global.NotificationName.connectionError, connectionError);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        mUsernameView.setEnabled(!show);
        mPasswordView.setEnabled(!show);
        mloginBtn.setEnabled(!show);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            Log.i(__TAG__, show ? "true" : "fales");

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

}

