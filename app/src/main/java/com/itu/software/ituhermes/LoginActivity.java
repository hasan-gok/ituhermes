package com.itu.software.ituhermes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.itu.software.ituhermes.Tasks.LoginTask;
import com.itu.software.ituhermes.Wrapper.User;
import com.itu.software.ituhermes.connection.FormValidator;
import com.itu.software.ituhermes.connection.JWTUtility;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, IUICallback<String> {
    private static final int signup_request_code = 2;
    Button bLogin;
    Button bSignUp;
    EditText eEmail;
    EditText ePassword;
    String email;
    String password;
    View vLogin;
    ProgressDialog progressDialog;
    LoginTask task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bLogin = findViewById(R.id.login_button);
        bSignUp = findViewById(R.id.sign_up_button);
        eEmail = findViewById(R.id.login_email);
        ePassword = findViewById(R.id.login_password);
        vLogin = findViewById(R.id.login_form);
        bLogin.setOnClickListener(this);
        bSignUp.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.wait_prompt));
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                try {
                    task.cancel(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void callbackUI(Code code, String data) {
        progressDialog.dismiss();
        switch (code) {
            case SUCCESS:
                JWTUtility.saveToken(this, data);
                User.getCurrentUser().setToken(data);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.login_success);
                builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent resultIntent = new Intent();
                        LoginActivity.this.setResult(Activity.RESULT_OK, resultIntent);
                        Log.d("", "Close: ");

                        LoginActivity.this.finish();
                    }
                });
                builder.create().show();
                break;
        }
    }

    @Override
    public void callbackUI(Code code) {
        progressDialog.dismiss();
        switch (code) {
            case FAIL:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.unidentified_error);
                builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            case WRONG_PASS:
                ePassword.setError(getString(R.string.error_incorrect_password));
                break;
            case NO_USER:
                eEmail.setError(getString(R.string.error_no_user));
                break;
        }
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
        } else if (!FormValidator.validatePassword(password)) {
            ePassword.setError(getString(R.string.error_invalid_password));
            ePassword.requestFocus();
            abort = true;
        }
        if (!abort){
            progressDialog.show();
            LoginTask task = new LoginTask(this, email, password);
            task.execute();
        }
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, resultIntent);
        finish();
    }
}
