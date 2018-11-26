package com.itu.software.ituhermes.Fragments;

import android.app.ProgressDialog;
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
    ProgressDialog progressDialog;
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
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.wait_prompt));
        updateItems();
        return view;
    }

    public void updateItems() {
        progressDialog.show();
        GetPosts task = new GetPosts(this, topicId, pageNumber);
        task.execute();
    }

    @Override
    public void callbackUI(Code code, ArrayList<Post> data) {
        switch (code) {
            case DATA_SUCCESS: {
                if (data.size() > 0) {
                    adapter.setPosts(data);
                }
                break;
            }
        }
        progressDialog.dismiss();
    }

    @Override
    public void callbackUI(Code code) {
        switch (code) {
            case FAIL: {
                failed = true;
                callback.callbackUI(Code.FAIL);
            }
        }
        progressDialog.dismiss();
    }

    public boolean isFailed() {
        return failed;
    }

    class PostListItem extends RecyclerView.ViewHolder {
        TextView senderText;
        TextView messageText;
        TextView dateText;

        PostListItem(@NonNull View itemView) {
            super(itemView);
            senderText = itemView.findViewById(R.id.post_sender);
            messageText = itemView.findViewById(R.id.post_message);
            dateText = itemView.findViewById(R.id.post_date);
        }
    }

    protected class PostListAdapter extends RecyclerView.Adapter<PostListItem> {
        ArrayList<Post> posts;

        PostListAdapter() {
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
