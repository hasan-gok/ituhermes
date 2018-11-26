package com.itu.software.ituhermes.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itu.software.ituhermes.Code;
import com.itu.software.ituhermes.IUICallback;
import com.itu.software.ituhermes.LoadTopicCallback;
import com.itu.software.ituhermes.R;
import com.itu.software.ituhermes.Tasks.GetTopics;
import com.itu.software.ituhermes.Wrapper.Topic;

import java.util.ArrayList;

public class TopicFragment extends Fragment implements IUICallback, SwipeRefreshLayout.OnRefreshListener {
    View view;
    RecyclerView recyclerView;
    TopicListAdapter adapter;
    SwipeRefreshLayout refreshLayout;
    LoadTopicCallback loadTopicCallback;
    FloatingActionButton fab;
    boolean refreshRequested = true;

    public TopicFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_topic, container, false);
        recyclerView = view.findViewById(R.id.topic_recycler);
        fab = view.findViewById(R.id.create_post_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTopicDialogFragment fragment = new AddTopicDialogFragment();
                fragment.setCallback(TopicFragment.this);
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                try {
                    fragment.show(activity.getSupportFragmentManager(), "postDialog");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        adapter = new TopicListAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new DownScrollListener());
        refreshLayout = view.findViewById(R.id.fragment_swipe_layout);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        refreshLayout.setRefreshing(true);
        GetTopics task = new GetTopics(this, 0);
        task.execute();
        return view;
    }

    @Override
    public void onRefresh() {
        refreshRequested = true;
        refreshLayout.setRefreshing(true);
        GetTopics task = new GetTopics(this, 0);
        task.execute();
    }

    @Override
    public void callbackUI(Code code, Object data) {
        switch (code) {
            case DATA_SUCCESS: {
                ArrayList<Topic> topics = (ArrayList<Topic>) data;
                if (refreshLayout.isRefreshing()) {
                    if (topics.size() > 0) {
                        if (refreshRequested) {
                            adapter.refreshTopics(topics);
                            recyclerView.smoothScrollToPosition(0);
                        } else {
                            adapter.addTopics(topics);
                            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                        }
                    }
                    refreshLayout.setRefreshing(false);
                }
                break;
            }
            case ADD_TOPIC: {
                Topic newTopic = (Topic) data;
                loadTopicCallback.onLoadRequest(newTopic);
                break;
            }
        }
    }

    @Override
    public void callbackUI(Code code) {
        switch (code) {
            case FAIL: {
                if (refreshLayout.isRefreshing()) {
                    refreshLayout.setRefreshing(false);
                }
                Snackbar.make(view, R.string.unidentified_error, Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    protected class TopicListItem extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView titleText;
        TextView postCountText;
        Topic topic;

        TopicListItem(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.topic_title_text);
            postCountText = itemView.findViewById(R.id.topic_post_count_text);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            loadTopicCallback.onLoadRequest(topic);
        }

        public void setTopic(Topic topic) {
            this.topic = topic;
        }
    }

    protected class TopicListAdapter extends RecyclerView.Adapter<TopicListItem> {
        ArrayList<Topic> topics;

        TopicListAdapter() {
            topics = new ArrayList<>();
        }

        @NonNull
        @Override
        public TopicListItem onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.topic_recycler_item, viewGroup, false);
            return new TopicListItem(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TopicListItem topicListItem, int i) {
            try {
                topicListItem.titleText.setText(topics.get(i).getTitle());
                int postCount = topics.get(i).getPostCount();
                String countString = postCount > 999 ? "999+" : String.format("%d", postCount);
                topicListItem.postCountText.setText(countString);
                topicListItem.setTopic(topics.get(i));
            } catch (Exception e) {
                Log.d("", "onBindViewHolder: " + e.getMessage());
            }
        }

        @Override
        public int getItemCount() {
            return topics.size();
        }

        void addTopics(ArrayList<Topic> topics) {
            this.topics.addAll(topics);
            this.notifyDataSetChanged();
        }

        void refreshTopics(ArrayList<Topic> topics) {
            this.topics = topics;
            this.notifyDataSetChanged();
        }
    }
    protected class DownScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE: {
                    if (!recyclerView.canScrollVertically(1)) {
                        TopicFragment.this.refreshRequested = false;
                        TopicFragment.this.refreshLayout.setRefreshing(true);
                        try {
                            GetTopics task = new GetTopics(TopicFragment.this, recyclerView.getAdapter().getItemCount());
                            task.execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    break;
                }
            }
        }
    }

    public void setLoadTopicCallback(LoadTopicCallback activity) {
        this.loadTopicCallback = activity;
    }
}
