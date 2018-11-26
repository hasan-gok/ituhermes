package com.itu.software.ituhermes.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.itu.software.ituhermes.Code;
import com.itu.software.ituhermes.IUICallback;
import com.itu.software.ituhermes.Wrapper.Topic;
import com.itu.software.ituhermes.Wrapper.User;
import com.itu.software.ituhermes.connection.HTTPClient;

public class SubscribeTask extends AsyncTask<Void, Void, Integer> {
    private static final String TAG = "SubscribeTask";
    private IUICallback callback;
    private Topic topic;

    public SubscribeTask(IUICallback callback, Topic topic) {
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
        try {
            String path = String.format("topic/%d/subscribe?token=%s", topic.getTopicId(), User.getCurrentUser().getToken());
            if (!topic.isSubscribing()) {
                returnCode = HTTPClient.put(path);
            } else {
                returnCode = HTTPClient.delete(path);
            }
        } catch (Exception e) {
            Log.d(TAG, "doInBackground: " + e.getMessage());
        }
        return returnCode;
    }
}
