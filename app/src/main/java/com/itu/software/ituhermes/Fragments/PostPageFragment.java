package com.itu.software.ituhermes.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.itu.software.ituhermes.Code;
import com.itu.software.ituhermes.IUICallback;
import com.itu.software.ituhermes.PostPagerActivity;
import com.itu.software.ituhermes.R;
import com.itu.software.ituhermes.Tasks.GetPosts;
import com.itu.software.ituhermes.Wrapper.Post;
import com.itu.software.ituhermes.Wrapper.Topic;
import com.itu.software.ituhermes.Wrapper.User;

import java.util.ArrayList;


public class PostPageFragment extends Fragment implements IUICallback<ArrayList<Post>> {
    private static final String TAG = "PostPageFragment";
    public static final String POST_KEY = "com.itu.software.ituhermes.postPage.postKey";
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
                break;
            }
            case POST_SUCCESS:
                updateItems();
                break;
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
        ImageButton editPostButton;
        PostListItem(@NonNull View itemView) {
            super(itemView);
            senderText = itemView.findViewById(R.id.post_sender);
            messageText = itemView.findViewById(R.id.post_message);
            dateText = itemView.findViewById(R.id.post_date);
            editPostButton = itemView.findViewById(R.id.edit_post_button);
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
        public void onBindViewHolder(@NonNull final PostListItem postListItem, int i) {
            try {
                final Post post = posts.get(i);
                postListItem.senderText.setText(post.getSender());
                postListItem.messageText.setText(post.getMessage());
                postListItem.dateText.setText(post.getDate());
                if (post.getSenderId().equals(User.getCurrentUser().getUserId())){
                    postListItem.editPostButton.setVisibility(View.VISIBLE);
                    postListItem.editPostButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PostEditFragment fragment = new PostEditFragment();
                            Bundle args = new Bundle();
                            args.putSerializable(POST_KEY, post);
                            fragment.setArguments(args);
                            fragment.setCallback(PostPageFragment.this);
                            fragment.show(getActivity().getSupportFragmentManager(), "editPostDialog");
                        }
                    });
                }
                else{
                    postListItem.editPostButton.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                e.printStackTrace();
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
