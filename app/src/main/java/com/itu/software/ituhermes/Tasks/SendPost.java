package com.itu.software.ituhermes.Tasks;

import android.os.AsyncTask;

import com.itu.software.ituhermes.Code;
import com.itu.software.ituhermes.IUICallback;
import com.itu.software.ituhermes.Wrapper.User;
import com.itu.software.ituhermes.connection.HTTPClient;

import org.json.JSONObject;

public class SendPost extends AsyncTask<Void, Void, Integer> {
    private IUICallback callback;
    private int topicId;
    private String postText;

    public SendPost(IUICallback callback, int topicId, String postText) {
        this.callback = callback;
        this.topicId = topicId;
        this.postText = postText;
    }

    @Override
    protected void onPostExecute(Integer returnCode) {
        super.onPostExecute(returnCode);
        if (returnCode >= 0) {
            callback.callbackUI(Code.POST_SUCCESS);
        } else {
            callback.callbackUI(Code.POST_FAIL);
        }
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        int returnCode = -1;
        try {
            String path = String.format("topic/%d/posts?token=%s", topicId, User.getCurrentUser().getToken());
            JSONObject body = new JSONObject();
            body.put("message", postText);
            returnCode = HTTPClient.put(path, body);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnCode;
    }
}
