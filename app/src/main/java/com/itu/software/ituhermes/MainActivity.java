package com.itu.software.ituhermes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.itu.software.ituhermes.Fragments.TopicFragment;
import com.itu.software.ituhermes.Wrapper.Topic;
import com.itu.software.ituhermes.Wrapper.User;

public class MainActivity extends AppCompatActivity implements LoadTopicCallback {
    private static final int LOGIN_REQUEST_CODE = 1;
    TextView text;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        if (User.getCurrentUser().getEmail().isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST_CODE);
        } else {
            initTopicFragment();
        }
    }

    private void initTopicFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = manager.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = new TopicFragment();
            transaction.replace(R.id.fragment_container, fragment);
        } else {
            fragment = new TopicFragment();
            transaction.add(R.id.fragment_container, fragment);
        }
        transaction.addToBackStack(null);
        transaction.commit();
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
        if (requestCode == LOGIN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Snackbar.make(toolbar, getString(R.string.login_success), Snackbar.LENGTH_SHORT).show();
                initTopicFragment();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof TopicFragment) {
            TopicFragment topicFragment = (TopicFragment) fragment;
            topicFragment.setLoadTopicCallback(this);

        }
    }

    @Override
    public void onLoadRequest(Topic topic) {
        Intent intent = new Intent(this, PostPagerActivity.class);
        intent.putExtra(PostPagerActivity.TOPIC_KEY, topic);
        startActivity(intent);
    }
}
