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

public class GetTopics extends AsyncTask<Void, Void, Integer> {
    private static final String TAG = "GetTopics";
    private static final int maxTopicAtOnce = 10;
    private IUICallback callback;
    private ArrayList<Topic> topics;
    private int rangeStart;

    public GetTopics(IUICallback callback, int rangeStart) {
        this.callback = callback;
        this.rangeStart = rangeStart;
        topics = new ArrayList<>();
    }

    @Override
    protected void onPostExecute(Integer returnCode) {
        super.onPostExecute(returnCode);
        if (returnCode < 0) {
            callback.callbackUI(Code.FAIL);
        } else {
            callback.callbackUI(Code.DATA_SUCCESS, topics);
        }
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        int returnCode = -1;
        try {
            String path = String.format("topic?rangeStart=%d&increment=%d&token=%s", rangeStart, maxTopicAtOnce, User.getCurrentUser().getToken());
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
