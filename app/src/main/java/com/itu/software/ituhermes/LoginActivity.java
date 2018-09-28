package com.itu.software.ituhermes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.itu.software.ituhermes.connection.FormValidator;
import com.itu.software.ituhermes.connection.HTTPClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.regex.*;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int signup_request_code = 2;
    Button bLogin;
    Button bSignUp;
    EditText eEmail;
    EditText ePassword;
    View vLogin;
    ProgressBar progressBar;
    String email;
    String password;
    private UserLoginTask userLoginTask = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bLogin = findViewById(R.id.login_button);
        bSignUp = findViewById(R.id.sign_up_button);
        eEmail = findViewById(R.id.login_email);
        ePassword = findViewById(R.id.login_password);
        progressBar = findViewById(R.id.login_progress);
        vLogin = findViewById(R.id.login_form);
        bLogin.setOnClickListener(this);
        bSignUp.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.login_button){
            tryLogin();
        }
        else{
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
        }
    }
    private void tryLogin(){
        email = eEmail.getEditableText().toString();
        password = ePassword.getEditableText().toString();
        boolean abort = false;
        if (TextUtils.isEmpty(email)){
            eEmail.setError(getString(R.string.error_field_required));
            eEmail.requestFocus();
            abort = true;
        } else if (!FormValidator.validateEmail(email)) {
            eEmail.setError(getString(R.string.error_invalid_email));
            eEmail.requestFocus();
            abort = true;
        }
        if (TextUtils.isEmpty(password)){
            ePassword.setError(getString(R.string.error_field_required));
            ePassword.requestFocus();
            abort = true;
        } else if (!FormValidator.validateEmail(password)) {
            ePassword.setError(getString(R.string.error_invalid_password));
            ePassword.requestFocus();
            abort = true;
        }
        if (!abort){
            showProgressBar(true);
            userLoginTask = new UserLoginTask(this, email, password);
            userLoginTask.execute();
        }
    }
    private void showProgressBar(final boolean show){
        if (show){
            InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(vLogin.getWindowToken(), 0);
        } else if (!eEmail.hasFocus() || !ePassword.hasFocus()) {
            eEmail.requestFocus();
        }
        vLogin.setVisibility(show ? View.GONE : View.VISIBLE);
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        vLogin.animate().setDuration(shortAnimTime)
                .alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                vLogin.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        progressBar.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private static class UserLoginTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<LoginActivity> activityReference;
        private String email;
        private String password;
        private int responseCode = -1;

        private UserLoginTask(LoginActivity context, String email, String password) {
            this.activityReference = new WeakReference<>(context);
            this.email = email;
            this.password = password;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            try{
                String path = "/login";
                JSONObject body = new JSONObject();
                body.put("email", email);
                body.put("password", password);
                JSONObject response = HTTPClient.post(path, body);
                if (response != null) {
                    responseCode = Integer.parseInt(response.getString("code"));
                }
            } catch (JSONException e) {
                Log.e("Login", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            LoginActivity activity = activityReference.get();
            activity.showProgressBar(false);
            switch (responseCode){
                case -1:
                    Snackbar.make(activity.vLogin, R.string.unidentified_error, Snackbar.LENGTH_SHORT).show();
                    break;
                case 0:
                    Intent resultIntent = new Intent();
                    activity.setResult(Activity.RESULT_OK, resultIntent);
                    activity.finish();
                    break;
                case 1:
                    activity.ePassword.setError(activity.getString(R.string.error_incorrect_password));
                    break;
                case 2:
                    activity.eEmail.setError(activity.getString(R.string.error_no_user));
                    break;
                case 3:
                    Snackbar.make(activity.vLogin, R.string.error_database, Snackbar.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        protected void onCancelled(Void aVoid) {
            activityReference.get().showProgressBar(false);
        }
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, resultIntent);
        finish();
    }
}
