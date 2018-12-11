package com.itu.software.ituhermes;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.itu.software.ituhermes.Fragments.TopicFragment;
import com.itu.software.ituhermes.Tasks.GetProfileData;
import com.itu.software.ituhermes.Tasks.SendFirebaseToken;
import com.itu.software.ituhermes.Wrapper.Topic;
import com.itu.software.ituhermes.Wrapper.User;
import com.itu.software.ituhermes.connection.JWTUtility;

public class MainActivity extends AppCompatActivity implements LoadTopicCallback, IUICallback{
    private static final int LOGIN_REQUEST_CODE = 1;
    public static final String CHANNEL_ID = "itu_hermes_channel";
    TextView text;
    Toolbar toolbar;
    Button profileButton;
    ImageButton searchButton;
    View fragmentHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.main_toolbar);
        profileButton = findViewById(R.id.profile_toolbar_button);
        fragmentHolder = findViewById(R.id.fragment_container);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        profileButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.log_off_prompt);
                builder.setPositiveButton(R.string.log_off, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        JWTUtility.deleteToken(MainActivity.this);
                        User.getCurrentUser().clearData();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivityForResult(intent, LOGIN_REQUEST_CODE);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                return true;
            }
        });
        searchButton = findViewById(R.id.search_menu_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                Fragment fragment = new SearchFragment();
                ((SearchFragment) fragment).setLoadTopicCallback(MainActivity.this);
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        setSupportActionBar(toolbar);
        String token = JWTUtility.getToken(this);
        createNotificationChannel();
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                SendFirebaseToken task = new SendFirebaseToken(instanceIdResult.getToken());
                task.execute();
            }
        });

        if (token.isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST_CODE);
        } else {
            User.getCurrentUser().setToken(token);
            GetProfileData task = new GetProfileData(this);
            task.execute();
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

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment != null){
            fm.popBackStack();
        }
        if (fm.getBackStackEntryCount() == 1) {
            super.onBackPressed();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Hermes Channel";
            String description = "Notification channel of ITU Hermes";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    @Override
    public void callbackUI(Code code, Object data) {

    }

    @Override
    public void callbackUI(Code code) {
        switch (code){
            case FAIL:
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setMessage(R.string.unidentified_error);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        }
    }

}
