package com.itu.software.ituhermes.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.itu.software.ituhermes.Code;
import com.itu.software.ituhermes.IUICallback;
import com.itu.software.ituhermes.R;
import com.itu.software.ituhermes.Tasks.SubscribeTask;
import com.itu.software.ituhermes.Wrapper.Topic;

import org.json.JSONObject;

import java.util.ArrayList;

public class TopicNotificationFragment extends Fragment implements IUICallback {
    RecyclerView recyclerView;
    View thumbnail;
    View view;
    ProgressDialog progressDialog;
    ArrayList<Topic> newTopics;
    TopicNotificationAdapter adapter;
    int clickedIndex;
    SwitchCompat clickedButton;
    boolean previousSwitchState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.topic_notification_fragment, container, false);
        recyclerView = view.findViewById(R.id.new_topic_recycler);
        thumbnail = view.findViewById(R.id.topic_thumbnail);
        newTopics = new ArrayList<>();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getActivity().getString(R.string.wait_prompt));
        SharedPreferences preferences = getActivity().getSharedPreferences("TopicNotification", 0);
        for (String key : preferences.getAll().keySet()) {
            String data = preferences.getString(key, "");
            try {
                JSONObject dataObject = new JSONObject(data);
                String tag = dataObject.getString("tag");
                String title = dataObject.getString("title");
                int topicId = dataObject.getInt("topicId");
                Topic topic = new Topic(title, tag, topicId, 0, 0, false);
                newTopics.add(topic);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (newTopics.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            thumbnail.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            thumbnail.setVisibility(View.GONE);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            adapter = new TopicNotificationAdapter(newTopics);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    @Override
    public void callbackUI(Code code, Object data) {

    }

    @Override
    public void callbackUI(Code code) {
        switch (code) {
            case SUBSCRIBE_SUCCESS:
                switchSubscription(clickedButton, clickedIndex);
                break;
            case SUBSCRIBE_FAIL:
                clickedButton.setChecked(previousSwitchState);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getActivity().getString(R.string.unidentified_error));
                builder.setCancelable(false);
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

    private void switchSubscription(final SwitchCompat aSwitch, final int i) {
        if (!adapter.topics.get(i).isSubscribing()) {
            aSwitch.setText(R.string.subscribing);
            aSwitch.setChecked(true);
            adapter.topics.get(i).setSubscribing(true);
        } else {
            aSwitch.setText(R.string.notsubscribing);
            adapter.topics.get(i).setSubscribing(false);
            aSwitch.setChecked(false);
        }
        aSwitch.setClickable(true);
    }

    private class TopicHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView tagText;
        SwitchCompat subscribeButton;

        TopicHolder(@NonNull View itemView) {
            super(itemView);
            this.tagText = itemView.findViewById(R.id.topic_notify_tag);
            this.titleText = itemView.findViewById(R.id.topic_notify_title);
            this.subscribeButton = itemView.findViewById(R.id.new_sub_switch);
        }
    }

    private class TopicNotificationAdapter extends RecyclerView.Adapter<TopicHolder> {
        ArrayList<Topic> topics;

        TopicNotificationAdapter(ArrayList<Topic> topics) {
            this.topics = topics;
        }

        @NonNull
        @Override
        public TopicHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.topic_notification_item, viewGroup, false);
            return new TopicHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final TopicHolder topicHolder, int i) {
            final Topic topic = topics.get(i);
            topicHolder.titleText.setText(topic.getTitle());
            topicHolder.tagText.setText(topic.getTag());
            if (topics.get(i).isSubscribing()) {
                topicHolder.subscribeButton.setChecked(true);
                topicHolder.subscribeButton.setText(R.string.subscribing);
            } else {
                topicHolder.subscribeButton.setChecked(false);
                topicHolder.subscribeButton.setText(R.string.notsubscribing);
            }
            topicHolder.subscribeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    TopicNotificationFragment.this.previousSwitchState = topicHolder.subscribeButton.isChecked();
                    TopicNotificationFragment.this.clickedIndex = topicHolder.getAdapterPosition();
                    TopicNotificationFragment.this.clickedButton = topicHolder.subscribeButton;
                    Topic topic = topics.get(topicHolder.getAdapterPosition());
                    buttonView.setClickable(false);
                    SubscribeTask task = new SubscribeTask(TopicNotificationFragment.this, topic);
                    task.execute();
                }
            });
        }

        @Override
        public int getItemCount() {
            return topics.size();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences preferences = getActivity().getSharedPreferences("TopicNotification", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}
