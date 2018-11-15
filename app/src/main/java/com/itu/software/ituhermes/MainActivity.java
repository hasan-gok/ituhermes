package com.itu.software.ituhermes;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.itu.software.ituhermes.Wrapper.User;

public class MainActivity extends AppCompatActivity {
    private static final int login_request_code = 1;
    TextView text;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = findViewById(R.id.txt);
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        if (User.getCurrentUser().getEmail().isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, login_request_code);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.profile_button) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Snackbar.make(text, R.string.login_success, Snackbar.LENGTH_SHORT).show();
            } else {
                finish();
            }
        }
    }
}
