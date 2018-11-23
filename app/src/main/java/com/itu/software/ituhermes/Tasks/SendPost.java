package com.itu.software.ituhermes.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.itu.software.ituhermes.Code;
import com.itu.software.ituhermes.IUICallback;
import com.itu.software.ituhermes.Wrapper.User;
import com.itu.software.ituhermes.connection.HTTPClient;

import org.json.JSONException;
import org.json.JSONObject;

public class SendPost<T extends IUICallback> extends AsyncTask<Void, Void, Integer> {
    T callback;
    int topicId;
    String postText;

    public SendPost(T callback, int topicId, String postText) {
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
        String path = String.format("/topic/%d/posts/", topicId);
        try {
            JSONObject body = new JSONObject();
            body.put("message", postText);
            body.put("email", User.getCurrentUser().getEmail());
            returnCode = HTTPClient.put(path, body);
        } catch (JSONException e) {
            Log.e("", "doInBackground: " + e.getMessage());
        }
        return returnCode;
    }
}
