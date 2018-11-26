package com.itu.software.ituhermes;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.itu.software.ituhermes.Fragments.PostNotificationFragment;
import com.itu.software.ituhermes.Fragments.TopicNotificationFragment;
import com.itu.software.ituhermes.Wrapper.User;
import com.itu.software.ituhermes.connection.JWTUtility;

public class NotificationActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        if (User.getCurrentUser().getToken().isEmpty()) {
            String token = JWTUtility.getToken(this);
            User.getCurrentUser().setToken(token);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.notification_fragment, new PostNotificationFragment())
                .commit();
//        notificationRecycler = findViewById(R.id.notification_recycler);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        switch (menuItem.getItemId()) {
            case R.id.menu_post:
                ft.replace(R.id.notification_fragment, new PostNotificationFragment());
                break;
            case R.id.menu_recommendation:
                ft.replace(R.id.notification_fragment, new TopicNotificationFragment());
                break;
        }
        ft.commit();
        return true;
    }
}
