package com.itu.software.ituhermes.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itu.software.ituhermes.Code;
import com.itu.software.ituhermes.IUICallback;
import com.itu.software.ituhermes.LoadTopicCallback;
import com.itu.software.ituhermes.R;
import com.itu.software.ituhermes.Tasks.SearchTopics;
import com.itu.software.ituhermes.Wrapper.Topic;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements IUICallback<ArrayList<Topic>> {
    LoadTopicCallback loadTopicCallback;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = view.findViewById(R.id.search_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        String query = getArguments().getString("SearchQuery");
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getActivity().getResources().getString(R.string.wait_prompt));
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        SearchTopics task = new SearchTopics(SearchFragment.this, query);
        task.execute();
        return view;
    }
    public void setLoadTopicCallback(LoadTopicCallback callback) {
        this.loadTopicCallback = callback;
    }

    @Override
    public void callbackUI(Code code, ArrayList<Topic> data) {
        progressDialog.dismiss();
        switch (code){
            case DATA_SUCCESS:{
                TopicListAdapter adapter = new TopicListAdapter(data);
                recyclerView.setAdapter(adapter);
                break;
            }
        }
    }

    @Override
    public void callbackUI(Code code) {
        progressDialog.dismiss();
        switch (code){
            case FAIL:{
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(false);
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
    }

    private class TopicListItem extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView postCountText;

        TopicListItem(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.topic_title_text);
            postCountText = itemView.findViewById(R.id.topic_post_count_text);
        }
    }

    private class TopicListAdapter extends RecyclerView.Adapter<TopicListItem> {
        ArrayList<Topic> topics;

        TopicListAdapter(ArrayList<Topic> topics) {
            this.topics = topics;
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
                final Topic topic = topics.get(i);
                topicListItem.titleText.setText(topic.getTitle());
                int postCount = topic.getPostCount();
                String countString = postCount > 999 ? "999+" : String.format("%d", postCount);
                topicListItem.postCountText.setText(countString);
                topicListItem.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadTopicCallback.onLoadRequest(topic);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return topics.size();
        }
    }
}
