package com.itu.software.ituhermes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.itu.software.ituhermes.Tasks.AddTag;
import com.itu.software.ituhermes.Tasks.GetProfileData;
import com.itu.software.ituhermes.Tasks.GetTags;
import com.itu.software.ituhermes.Tasks.DeleteTag;
import com.itu.software.ituhermes.Wrapper.User;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements IUICallback<ArrayList<String>> {
    View profileForm;
    Spinner tagNames;
    Button addButton;
    TextView emailText;
    RecyclerView followedTags;
    ProgressBar progressBar;
    Toolbar toolbar;
    private String tagToAdd;
    private String tagToDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        profileForm = findViewById(R.id.profile_form);
        tagNames = findViewById(R.id.tag_names);
        addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagToAdd = tagNames.getSelectedItem().toString();
                AddTag<ProfileActivity> task = new AddTag<>(tagToAdd, ProfileActivity.this);
                task.execute();
            }
        });
        emailText = findViewById(R.id.email_text);
        emailText.setText(User.getCurrentUser().getEmail());
        progressBar = findViewById(R.id.profile_progress);
        followedTags = findViewById(R.id.followed_tags);
        followedTags = findViewById(R.id.followed_tags);
        followedTags.setHasFixedSize(true);
        followedTags.setLayoutManager(new LinearLayoutManager(this));
        followedTags.setAdapter(new TopicTagAdapter());
        GetProfileData<ProfileActivity> task = new GetProfileData<>(this);
        task.execute();
        showProgressBar(true);
    }

    @Override
    public void callbackUI(Code code, ArrayList<String> data) {
        showProgressBar(false);
        switch (code) {
            case DATA_FAIL:
                Snackbar.make(profileForm, R.string.unidentified_error, Snackbar.LENGTH_SHORT).show();
                break;
            case DATA_SUCCESS:
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                tagNames.setAdapter(adapter);
                break;
        }
    }

    @Override
    public void callbackUI(Code code) {
        switch (code) {
            case DATA_FAIL:
                Snackbar.make(profileForm, R.string.unidentified_error, Snackbar.LENGTH_SHORT).show();
                break;
            case DATA_SUCCESS:
                followedTags.getAdapter().notifyDataSetChanged();
                GetTags<ProfileActivity> task = new GetTags<>(this);
                task.execute();
                break;
            case ADD_TAG:
                User.getCurrentUser().addTopicTag(tagToAdd);
                followedTags.getAdapter().notifyDataSetChanged();
                break;
            case DEL_TAG:
                int index = User.getCurrentUser().deleteTopicTag(tagToDelete);
                followedTags.getAdapter().notifyItemRemoved(index);
                break;
        }
    }

    private void showProgressBar(final boolean show) {
        profileForm.setVisibility(show ? View.GONE : View.VISIBLE);
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        profileForm.animate().setDuration(shortAnimTime)
                .alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                profileForm.setVisibility(show ? View.GONE : View.VISIBLE);
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

    protected class TopicTagViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private String topicTag;
        TextView topicTagText;
        ImageButton removeButton;

        public TopicTagViewHolder(@NonNull View itemView) {
            super(itemView);
            this.topicTagText = itemView.findViewById(R.id.topic_tag_text);
            this.removeButton = itemView.findViewById(R.id.delete_button);
            removeButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            tagToDelete = topicTagText.getText().toString();
            DeleteTag<ProfileActivity> task = new DeleteTag<>(tagToDelete, (ProfileActivity) v.getContext());
            task.execute();
        }

        public void setTopicTag(String topicTag) {
            this.topicTag = topicTag;
            topicTagText.setText(topicTag);
        }
    }

    protected class TopicTagAdapter extends RecyclerView.Adapter<TopicTagViewHolder> {
        @NonNull
        @Override
        public TopicTagViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View topicFollowView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.followed_tag_layout, viewGroup, false);
            return new TopicTagViewHolder(topicFollowView);
        }

        @Override
        public void onBindViewHolder(@NonNull TopicTagViewHolder topicTagViewHolder, int i) {
            topicTagViewHolder.setTopicTag(User.getCurrentUser().getTopicTags().get(i));
        }

        @Override
        public int getItemCount() {
            return User.getCurrentUser().getTopicTags().size();
        }
    }

    public void onBackPressed() {
        finish();
    }
}
