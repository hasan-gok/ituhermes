package com.itu.software.ituhermes.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.itu.software.ituhermes.Code;
import com.itu.software.ituhermes.IUICallback;
import com.itu.software.ituhermes.PostPagerActivity;
import com.itu.software.ituhermes.R;
import com.itu.software.ituhermes.Tasks.GetTopicInfo;
import com.itu.software.ituhermes.Wrapper.Post;
import com.itu.software.ituhermes.Wrapper.Topic;

import org.json.JSONObject;

import java.util.ArrayList;

public class PostNotificationFragment extends Fragment implements IUICallback<Topic> {
    RecyclerView recyclerView;
    View thumbnail;
    View view;
    ProgressDialog progressDialog;
    ArrayList<Post> newPosts;

    @Override
    public void callbackUI(Code code, Topic data) {
        switch (code) {
            case DATA_SUCCESS:
                progressDialog.dismiss();
                Log.d("", "callbackUI: " + data);
                Intent intent = new Intent(getActivity(), PostPagerActivity.class);
                intent.putExtra(PostPagerActivity.TOPIC_KEY, data);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void callbackUI(Code code) {
        switch (code) {
            case FAIL:
                progressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.unidentified_error);
                builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.post_notification_fragment, container, false);
        newPosts = new ArrayList<>();
        recyclerView = view.findViewById(R.id.new_post_recycler);
        thumbnail = view.findViewById(R.id.post_thumbnail);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        try {
            progressDialog.setMessage(getActivity().getResources().getString(R.string.wait_prompt));
            SharedPreferences preferences = getActivity().getSharedPreferences("PostNotification", Context.MODE_PRIVATE);
            for (String key : preferences.getAll().keySet()) {
                String data = preferences.getString(key, "");
                JSONObject dataObject = new JSONObject(data);
                String sender = dataObject.getString("sender");
                String message = dataObject.getString("post");
                String date = dataObject.getString("date");
                int topicId = dataObject.getInt("topicId");
                Post post = new Post(sender, message, date, topicId, "", "");
                newPosts.add(post);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (newPosts.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            thumbnail.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            thumbnail.setVisibility(View.GONE);
            PostAdapter adapter = new PostAdapter(newPosts);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    private class PostHolder extends RecyclerView.ViewHolder {
        TextView senderText;
        TextView postText;
        Button goButton;

        PostHolder(@NonNull View itemView) {
            super(itemView);
            senderText = itemView.findViewById(R.id.sender_name);
            postText = itemView.findViewById(R.id.new_post_text);
            goButton = itemView.findViewById(R.id.goto_topic);
        }
    }

    private class PostAdapter extends RecyclerView.Adapter<PostHolder> {
        ArrayList<Post> posts;

        PostAdapter(ArrayList<Post> posts) {
            this.posts = posts;
        }

        @NonNull
        @Override
        public PostHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_notification_item, viewGroup, false);
            return new PostHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull PostHolder postHolder, int i) {
            final Post post = posts.get(i);
            postHolder.senderText.setText(post.getSender());
            postHolder.postText.setText(post.getMessage());
            postHolder.goButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GetTopicInfo task = new GetTopicInfo(PostNotificationFragment.this, post.getTopicId());
                    task.execute();
                }
            });
        }

        @Override
        public int getItemCount() {
            return posts.size();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            SharedPreferences preferences = getActivity().getSharedPreferences("PostNotification", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
