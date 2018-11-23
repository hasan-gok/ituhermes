package com.itu.software.ituhermes.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.itu.software.ituhermes.Code;
import com.itu.software.ituhermes.IUICallback;
import com.itu.software.ituhermes.Wrapper.Topic;
import com.itu.software.ituhermes.Wrapper.User;
import com.itu.software.ituhermes.connection.HTTPClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GetTopics<T extends IUICallback> extends AsyncTask<Void, Void, Integer> {
    private static final String TAG = "GetTopics";
    public static final int maxTopicAtOnce = 10;
    private T activityReference;
    private ArrayList<Topic> topics;
    private int rangeStart;

    public GetTopics(T activityReference, int rangeStart) {
        this.activityReference = activityReference;
        this.rangeStart = rangeStart;
        topics = new ArrayList<>();
    }

    @Override
    protected void onPostExecute(Integer returnCode) {
        super.onPostExecute(returnCode);
        if (returnCode < 0) {
            activityReference.callbackUI(Code.FAIL);
        } else {
            activityReference.callbackUI(Code.DATA_SUCCESS, topics);
        }
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        int returnCode = -1;
        try {
            String path = String.format("/topic?rangeStart=%d&increment=%d&email=%s", rangeStart, maxTopicAtOnce, User.getCurrentUser().getEmail());
            JSONObject response = HTTPClient.get(path);
            if (response != null) {
                returnCode = response.getInt("code");
                JSONArray topicArray = response.getJSONArray("topics");
                for (int i = 0; i < topicArray.length(); i++) {
                    JSONObject topicObj = topicArray.getJSONObject(i);
                    String title = topicObj.getString("title");
                    String tag = topicObj.getString("tag");
                    int topicId = topicObj.getInt("topicId");
                    int postSize = topicObj.getInt("postSize");
                    int pageCount = topicObj.getInt("pageCount");
                    boolean isFollowing = topicObj.getBoolean("isSubscribing");
                    Topic topic = new Topic(title, tag, topicId, postSize, pageCount, isFollowing);
                    topics.add(topic);
                }
            }
        } catch (JSONException e) {
            Log.d(TAG, "doInBackground: " + e.getMessage());
        }
        return returnCode;
    }
}
