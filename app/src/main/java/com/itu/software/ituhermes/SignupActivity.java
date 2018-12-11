package com.itu.software.ituhermes;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.itu.software.ituhermes.Tasks.SignupTask;
import com.itu.software.ituhermes.connection.FormValidator;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener, IUICallback {
    private EditText eName;
    private EditText eLastName;
    private EditText eEmail;
    private EditText ePassword;
    private EditText ePasswordVal;
    private Button bSignup;
    private ProgressDialog progressDialog;
    private View vSignupForm;
    private Toolbar toolbar;
    private SignupTask task;
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
        vSignupForm = findViewById(R.id.signup_form);
        toolbar = findViewById(R.id.signup_toolbar);
        setSupportActionBar(toolbar);
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.wait_prompt));
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                progressDialog.dismiss();
                try {
                    task.cancel(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        setSupportActionBar(toolbar);
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
            progressDialog.show();
            task = new SignupTask(this, name, lastName, email, password);
            task.execute();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void callbackUI(Code code, Object data) {
    }

    @Override
    public void callbackUI(Code code) {
        progressDialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        switch (code) {
            case SUCCESS:
                progressDialog.show();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                intent.putExtra("email", eEmail.getText().toString());
                intent.putExtra("pass", ePassword.getText().toString());
                finish();
                break;
            case USER_EXISTS:
                builder.setMessage(R.string.error_user_already_defined);
                builder.create().show();
                break;
            case FAIL:
                builder.setMessage(R.string.unidentified_error);
                builder.create().show();
                break;
        }
    }
}
