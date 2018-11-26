package com.itu.software.ituhermes.Tasks;

import android.os.AsyncTask;

import com.itu.software.ituhermes.Code;
import com.itu.software.ituhermes.IUICallback;
import com.itu.software.ituhermes.Wrapper.User;
import com.itu.software.ituhermes.connection.HTTPClient;

import org.json.JSONObject;

public class AddTopicTask extends AsyncTask<Void, Void, Integer> {
    private IUICallback callback;
    private String title;
    private String tag;
    private int topicId;

    public AddTopicTask(IUICallback callback, String title, String tag) {
        this.callback = callback;
        this.title = title;
        this.tag = tag;
    }

    @Override
    protected void onPostExecute(Integer returnCode) {
        super.onPostExecute(returnCode);
        if (returnCode >= 0) {
            callback.callbackUI(Code.ADD_TOPIC, topicId);
        } else {
            callback.callbackUI(Code.FAIL);
        }
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        int returnCode;
        try {
            String path = String.format("topic?token=%s", User.getCurrentUser().getToken());
            JSONObject body = new JSONObject();
            body.put("title", title);
            body.put("tag", tag);
            returnCode = HTTPClient.put(path, body);
            JSONObject extra = body.getJSONObject("extra");
            topicId = extra.getInt("topicId");
        } catch (Exception e) {
            e.printStackTrace();
            returnCode = -1;
        }
        return returnCode;
    }
}
