package com.itu.software.ituhermes;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.itu.software.ituhermes.Tasks.AddTag;
import com.itu.software.ituhermes.Tasks.DeleteTag;
import com.itu.software.ituhermes.Tasks.GetProfileData;
import com.itu.software.ituhermes.Tasks.GetTags;
import com.itu.software.ituhermes.Wrapper.User;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements IUICallback<ArrayList<String>> {
    View profileForm;
    Spinner tagNames;
    Button addButton;
    TextView emailText;
    TextView nameText;
    RecyclerView followedTags;
    ProgressDialog progressDialog;
    Toolbar toolbar;
    private String tagToAdd;
    private String tagToDelete;
    private GetProfileData task;
    ArrayAdapter<String> adapter;
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
                AddTag task = new AddTag(ProfileActivity.this, tagToAdd);
                task.execute();
            }
        });
        emailText = findViewById(R.id.email_text);
        nameText = findViewById(R.id.name_text);
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.wait_prompt));
        followedTags = findViewById(R.id.followed_tags);
        followedTags = findViewById(R.id.followed_tags);
        followedTags.setLayoutManager(new LinearLayoutManager(this));
        followedTags.setAdapter(new TopicTagAdapter());
        emailText.setText(User.getCurrentUser().getEmail());
        nameText.setText(String.format("%s %s", User.getCurrentUser().getName(), User.getCurrentUser().getLastName()));
        progressDialog.show();
        GetTags taskG = new GetTags(this, true);
        taskG.execute();
    }

    @Override
    public void callbackUI(Code code, ArrayList<String> data) {
        progressDialog.dismiss();
        switch (code) {
            case DATA_FAIL:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.unidentified_error);
                builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
            case DATA_SUCCESS:
                if (tagNames.getAdapter() == null) {
                    adapter = new ArrayAdapter<>(this, R.layout.spinner_item_layout, data);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    tagNames.setAdapter(adapter);
                } else {
                    adapter.clear();
                    adapter.addAll(data);
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public void callbackUI(Code code) {
        GetTags taskG = new GetTags(this, true);
        switch (code) {
            case FAIL:
                progressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.unidentified_error);
                builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
            case ADD_TAG:
                User.getCurrentUser().addTopicTag(tagToAdd);
                followedTags.getAdapter().notifyDataSetChanged();
                progressDialog.show();
                taskG.execute();
                break;
            case DEL_TAG:
                int index = User.getCurrentUser().deleteTopicTag(tagToDelete);
                followedTags.getAdapter().notifyItemRemoved(index);
                progressDialog.show();
                taskG.execute();
                break;
        }
    }
    protected class TopicTagViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private String topicTag;
        TextView topicTagText;
        ImageButton removeButton;

        TopicTagViewHolder(@NonNull View itemView) {
            super(itemView);
            this.topicTagText = itemView.findViewById(R.id.topic_tag_text);
            this.removeButton = itemView.findViewById(R.id.delete_button);
            removeButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            tagToDelete = topicTagText.getText().toString();
            DeleteTag task = new DeleteTag(ProfileActivity.this, tagToDelete);
            task.execute();
        }

        void setTopicTag(String topicTag) {
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
