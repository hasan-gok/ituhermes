package com.itu.software.ituhermes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.itu.software.ituhermes.Wrapper.User;
import com.itu.software.ituhermes.connection.FormValidator;
import com.itu.software.ituhermes.connection.HTTPClient;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText eName;
    private EditText eLastName;
    private EditText eEmail;
    private EditText ePassword;
    private EditText ePasswordVal;
    private Button bSignup;
    protected ProgressBar progressBar;
    private View vSignupForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        eName = findViewById(R.id.signup_name);
        eLastName = findViewById(R.id.signup_lname);
        eEmail = findViewById(R.id.signup_email);
        ePassword = findViewById(R.id.signup_pass);
        ePasswordVal = findViewById(R.id.signup_pass_val);
        bSignup = findViewById(R.id.signup_button);
        progressBar = findViewById(R.id.signup_progress);
        vSignupForm = findViewById(R.id.signup_form);
        bSignup.setOnClickListener(this);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            Log.e("ERR", "Signup" + e.getMessage());
        }
    }

    private void trySignUp() {
        String name = eName.getText().toString();
        String lastName = eLastName.getText().toString();
        String email = eEmail.getText().toString();
        String password = ePassword.getText().toString();
        String passwordVal = ePasswordVal.getText().toString();
        boolean abort = false;
        if (TextUtils.isEmpty(name)) {
            eName.setError(getString(R.string.error_field_required));
            eName.requestFocus();
            abort = true;
        } else if (!FormValidator.validateName(name)) {
            eName.setError(getString(R.string.error_invalid_chars));
            eName.requestFocus();
            abort = true;
        }
        if (TextUtils.isEmpty(lastName)) {
            eLastName.setError(getString(R.string.error_field_required));
            eLastName.requestFocus();
            abort = true;
        } else if (!FormValidator.validateName(lastName)) {
            eLastName.setError(getString(R.string.error_invalid_chars));
            eLastName.requestFocus();
            abort = true;
        }
        if (TextUtils.isEmpty(email)) {
            eEmail.setError(getString(R.string.error_field_required));
            eEmail.requestFocus();
            abort = true;
        } else if (!FormValidator.validateEmail(email)) {
            eEmail.setError(getString(R.string.error_invalid_email));
            eEmail.requestFocus();
            abort = true;
        }
        if (TextUtils.isEmpty(password)) {
            ePassword.setError(getString(R.string.error_field_required));
            ePassword.requestFocus();
            abort = true;
        } else if (!FormValidator.validatePassword(password)) {
            ePassword.setError(getString(R.string.error_invalid_password));
            ePassword.requestFocus();
            abort = true;
        } else if (!password.equals(passwordVal)) {
            ePassword.setError(getString(R.string.error_password_mismatch));
            ePasswordVal.setError(getString(R.string.error_password_mismatch));
            ePassword.requestFocus();
            abort = true;
        }
        if (!abort) {
            showProgressBar(true);
            SignupTask signupTask = new SignupTask(this, name, lastName, email, password);
            signupTask.execute();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signup_button:
                trySignUp();
                break;
        }
    }

    private void showProgressBar(final boolean show) {
        if (show) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(vSignupForm.getWindowToken(), 0);
        }
        vSignupForm.setVisibility(show ? View.GONE : View.VISIBLE);
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        vSignupForm.animate().setDuration(shortAnimTime)
                .alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                vSignupForm.setVisibility(show ? View.GONE : View.VISIBLE);
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

    private static class SignupTask extends AsyncTask<Void, Void, Integer> {
        private String name;
        private String lastName;
        private String password;
        private String email;
        private WeakReference<SignupActivity> activityReference;

        private SignupTask(SignupActivity context, String name, String lastName, String email, String password) {
            this.name = name;
            this.lastName = lastName;
            this.email = email;
            this.password = password;
            this.activityReference = new WeakReference<>(context);
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            String path = "/signup";
            JSONObject request = new JSONObject();
            Integer responseCode = -1;
            try {
                request.put("name", name);
                request.put("lastName", lastName);
                request.put("email", email);
                request.put("password", password);
                JSONObject response = HTTPClient.post(path, request);
                responseCode = response.getInt("code");
            } catch (Exception e) {
                Log.e("Signup", e.getMessage());
            }
            return responseCode;
        }

        @Override
        protected void onPostExecute(Integer result) {
            SignupActivity activity = activityReference.get();
            activity.showProgressBar(false);
            int green = activity.getResources().getColor(R.color.green);
            int red = activity.getResources().getColor(R.color.red);
            switch (result) {
                case -1:
                    Snackbar.make(activity.vSignupForm, R.string.unidentified_error, Snackbar.LENGTH_SHORT).setActionTextColor(red).show();
                    break;
                case 0:
                    Snackbar.make(activity.vSignupForm, R.string.signup_success, Snackbar.LENGTH_SHORT).setActionTextColor(green).show();
                    break;
                case 1:
                    Snackbar.make(activity.vSignupForm, R.string.error_database, Snackbar.LENGTH_SHORT).setActionTextColor(red).show();
                    break;
                case 2:
                    Snackbar.make(activity.vSignupForm, R.string.error_user_already_defined, Snackbar.LENGTH_SHORT).setActionTextColor(red).show();
                    break;
                case 3:
                    Snackbar.make(activity.vSignupForm, R.string.error_database, Snackbar.LENGTH_SHORT).setActionTextColor(red).show();
                    break;
            }
        }

        @Override
        protected void onCancelled() {
            SignupActivity activity = activityReference.get();
            activity.showProgressBar(false);
            int red = activity.getResources().getColor(R.color.red);
            Snackbar.make(activity.vSignupForm, R.string.unidentified_error, Snackbar.LENGTH_SHORT).setActionTextColor(red).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
