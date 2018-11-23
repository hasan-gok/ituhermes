package com.itu.software.ituhermes.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itu.software.ituhermes.Code;
import com.itu.software.ituhermes.IUICallback;
import com.itu.software.ituhermes.PostPagerActivity;
import com.itu.software.ituhermes.R;
import com.itu.software.ituhermes.Tasks.GetPosts;
import com.itu.software.ituhermes.Wrapper.Post;
import com.itu.software.ituhermes.Wrapper.Topic;

import java.util.ArrayList;


public class PostPageFragment extends Fragment implements IUICallback<ArrayList<Post>> {
    private static final String TAG = "PostPageFragment";
    View view;
    RecyclerView recyclerView;
    PostListAdapter adapter;
    ProgressBar progressBar;
    public int topicId;
    int pageNumber;
    boolean failed = false;
    IUICallback callback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.post_pager_item, container, false);
        recyclerView = view.findViewById(R.id.post_recycler);
        adapter = new PostListAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(adapter);
        topicId = ((Topic) getArguments().getSerializable(PostPagerActivity.TOPIC_KEY)).getTopicId();
        pageNumber = getArguments().getInt(PostPagerActivity.PAGENUMBER_KEY);
        progressBar = view.findViewById(R.id.post_proggressbar);
        updateItems();
        return view;
    }

    private void showProgressBar(final boolean show) {
        view.setVisibility(show ? View.GONE : View.VISIBLE);
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        view.animate().setDuration(shortAnimTime)
                .alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(show ? View.GONE : View.VISIBLE);
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

    public void updateItems() {
        try {
            showProgressBar(true);
        } catch (IllegalStateException e) {
            Log.d(TAG, e.getMessage());
        }
        GetPosts<PostPageFragment> task = new GetPosts<>(this, topicId, pageNumber);
        task.execute();
    }

    @Override
    public void callbackUI(Code code, ArrayList<Post> data) {
        switch (code) {
            case DATA_SUCCESS: {
                try {
                    showProgressBar(false);
                } catch (IllegalStateException e) {
                    Log.d(TAG, e.getMessage());
                }
                if (data.size() > 0) {
                    adapter.setPosts(data);
                }
                break;
            }
        }
    }

    @Override
    public void callbackUI(Code code) {
        switch (code) {
            case FAIL: {
                failed = true;
                callback.callbackUI(Code.FAIL);
            }
        }
    }

    public boolean isFailed() {
        return failed;
    }

    protected class PostListItem extends RecyclerView.ViewHolder {
        TextView senderText;
        TextView messageText;
        TextView dateText;

        public PostListItem(@NonNull View itemView) {
            super(itemView);
            senderText = itemView.findViewById(R.id.post_sender);
            messageText = itemView.findViewById(R.id.post_message);
            dateText = itemView.findViewById(R.id.post_date);
        }
    }

    protected class PostListAdapter extends RecyclerView.Adapter<PostListItem> {
        ArrayList<Post> posts;

        public PostListAdapter() {
            posts = new ArrayList<>();
        }

        @NonNull
        @Override
        public PostListItem onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_item, viewGroup, false);
            return new PostListItem(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PostListItem postListItem, int i) {
            try {
                postListItem.senderText.setText(posts.get(i).getSender());
                postListItem.messageText.setText(posts.get(i).getMessage());
                postListItem.dateText.setText(posts.get(i).getDate());
            } catch (Exception e) {
                Log.d("", "onBindViewHolder: " + e.getMessage());
            }
        }

        @Override
        public int getItemCount() {
            return posts.size();
        }

        public void setPosts(ArrayList<Post> posts) {
            this.posts = posts;
            this.notifyDataSetChanged();
        }
    }

    public void setCallback(IUICallback callback) {
        this.callback = callback;
    }

}
