package com.itu.software.ituhermes.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.itu.software.ituhermes.Code;
import com.itu.software.ituhermes.IUICallback;
import com.itu.software.ituhermes.Wrapper.Topic;
import com.itu.software.ituhermes.Wrapper.User;
import com.itu.software.ituhermes.connection.HTTPClient;

import org.json.JSONException;
import org.json.JSONObject;

public class SubscribeTask<T extends IUICallback> extends AsyncTask<Void, Void, Integer> {
    private static final String TAG = "SubscribeTask";
    T callback;
    Topic topic;

    public SubscribeTask(T callback, Topic topic) {
        this.callback = callback;
        this.topic = topic;
    }

    @Override
    protected void onPostExecute(Integer returnCode) {
        super.onPostExecute(returnCode);
        if (returnCode >= 0) {
            callback.callbackUI(Code.SUBSCRIBE_SUCCESS);
        } else {
            callback.callbackUI(Code.SUBSCRIBE_FAIL);
        }
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        int returnCode = -1;
        String path;
        path = String.format("/topic/%d/subscribe", topic.getTopicId());
        try {
            JSONObject body = new JSONObject();
            body.put("email", User.getCurrentUser().getEmail());
            if (!topic.isSubscribing()) {
                returnCode = HTTPClient.put(path, body);
            } else {
                returnCode = HTTPClient.delete(path, body);
            }
        } catch (JSONException e) {
            Log.d(TAG, "doInBackground: " + e.getMessage());
        }
        return returnCode;
    }
}
