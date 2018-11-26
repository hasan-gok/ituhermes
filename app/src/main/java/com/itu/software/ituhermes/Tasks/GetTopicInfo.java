package com.itu.software.ituhermes.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.itu.software.ituhermes.Code;
import com.itu.software.ituhermes.IUICallback;
import com.itu.software.ituhermes.Wrapper.Topic;
import com.itu.software.ituhermes.Wrapper.User;
import com.itu.software.ituhermes.connection.HTTPClient;

import org.json.JSONObject;

import static android.support.constraint.Constraints.TAG;

public class GetTopicInfo extends AsyncTask<Void, Void, Integer> {
    private IUICallback callback;
    private int topicId;
    private Topic topic;

    public GetTopicInfo(IUICallback callback, int topicId) {
        this.callback = callback;
        this.topicId = topicId;
    }

    @Override
    protected void onPostExecute(Integer returnCode) {
        super.onPostExecute(returnCode);
        if (returnCode >= 0) {
            callback.callbackUI(Code.DATA_SUCCESS, topic);
        } else {
            callback.callbackUI(Code.FAIL);
        }
    }


    @Override
    protected Integer doInBackground(Void... voids) {
        int returnCode = -1;
        try {
            String path = String.format("topic/info/%d?token=%s", topicId, User.getCurrentUser().getToken());
            Log.d(TAG, "doInBackground: " + path);
            JSONObject response = HTTPClient.get(path);
            Log.d(TAG, "doInBackground: " + response.toString(2));
            String title = response.getString("title");
            String tag = response.getString("tag");
            int size = response.getInt("size");
            boolean isSubscribing = response.getBoolean("isSubscribing");
            topic = new Topic(title, tag, topicId, size, 0, isSubscribing);
            returnCode = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnCode;
    }
}
