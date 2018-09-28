package com.itu.software.ituhermes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.*;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String baseUrl = "http://10.0.2.2:8001/login";
    Button loginButton;
    Button signUpButton;
    EditText emailText;
    EditText passwordText;
    View loginForm;
    ProgressBar progressBar;

    String email;
    String password;
    private UserLoginTask userLoginTask = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = findViewById(R.id.login_button);
        signUpButton = findViewById(R.id.sign_up_button);
        emailText = findViewById(R.id.login_email);
        passwordText = findViewById(R.id.login_password);
        progressBar = findViewById(R.id.login_progress);
        loginForm = findViewById(R.id.login_form);
        loginButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);

    }
    private boolean validateEmail(String email){
        Pattern pattern = Pattern.compile(".+@.+");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    private boolean validatePassword(String password) {
        Pattern pattern = Pattern.compile("\\w{8,20}");
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.login_button){
            tryLogin();
        }
        else{
            //TODO add user signup form
        }
    }
    private void tryLogin(){
        email = emailText.getEditableText().toString();
        password = passwordText.getEditableText().toString();
        boolean abort = false;
        if (TextUtils.isEmpty(email)){
            emailText.setError(getString(R.string.error_field_required));
            emailText.requestFocus();
            abort = true;
        }
        else if (!validateEmail(email)){
            emailText.setError(getString(R.string.error_invalid_email));
            emailText.requestFocus();
            abort = true;
        }
        if (TextUtils.isEmpty(password)){
            passwordText.setError(getString(R.string.error_field_required));
            passwordText.requestFocus();
            abort = true;
        }
        else if (!validatePassword(password)){
            passwordText.setError(getString(R.string.error_invalid_password));
            passwordText.requestFocus();
            abort = true;
        }
        if (!abort){
            showProgressBar(true);
            userLoginTask = new UserLoginTask(email, password);
            userLoginTask.execute();
        }
    }
    private void showProgressBar(final boolean show){
        if (show){
            InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(loginForm.getWindowToken(), 0);
        }
        else if (!emailText.hasFocus() || !passwordText.hasFocus()){
            emailText.requestFocus();
        }
        loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        loginForm.animate().setDuration(shortAnimTime)
                .alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
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
    private class UserLoginTask extends AsyncTask<Void, Void, Void>{
        private String email;
        private String password;
        private int responseCode = -1;
        public UserLoginTask(String email, String password){
            this.email = email;
            this.password = password;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            try{
                URL url = new URL(baseUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("content-type", "application/json");
                JSONObject request = new JSONObject();
                request.put("username", email);
                request.put("password", password);
                connection.setDoOutput(true);
                connection.setChunkedStreamingMode(0);
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes(request.toString());
                out.flush();
                out.close();
                if (connection.getResponseCode() == 200){
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder builder = new StringBuilder();
                    while ((inputLine = in.readLine()) != null){
                        builder.append(inputLine);
                    }
                    JSONObject jsonObject = new JSONObject(builder.toString());
                    responseCode = Integer.parseInt(jsonObject.getString("code"));
                }
                connection.disconnect();
                Log.d("Conn",String.valueOf(connection.getResponseCode()));
            }
            catch (Exception e){
                Snackbar.make(loginForm, R.string.unidentified_error, Snackbar.LENGTH_SHORT);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            showProgressBar(false);
            switch (responseCode){
                case -1: Snackbar.make(loginForm, R.string.unidentified_error, Snackbar.LENGTH_SHORT);
                case 0: finish();
                case 1: passwordText.setError(getString(R.string.error_incorrect_password));
                case 2: emailText.setError(getString(R.string.error_no_user));
                case 3: Snackbar.make(loginForm, R.string.error_database, Snackbar.LENGTH_SHORT);
            }
        }
    }

}
