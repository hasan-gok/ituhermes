package com.itu.software.ituhermes.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.itu.software.ituhermes.Code;
import com.itu.software.ituhermes.IUICallback;
import com.itu.software.ituhermes.Wrapper.User;
import com.itu.software.ituhermes.connection.HTTPClient;

import org.json.JSONObject;

import static android.support.constraint.Constraints.TAG;

public class EditPost extends AsyncTask<Void, Void, Integer> {
    private IUICallback callback;
    private String postId;
    private String postText;

    public EditPost(IUICallback callback, String postId, String postText) {
        this.callback = callback;
        this.postId = postId;
        this.postText = postText;
    }

    @Override
    protected void onPostExecute(Integer returnCode) {
        super.onPostExecute(returnCode);
        Log.d(TAG, "onPostExecute: " + returnCode);
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
            String path = String.format("post/%s?token=%s", postId, User.getCurrentUser().getToken());
            JSONObject body = new JSONObject();
            body.put("message", postText);
            returnCode = HTTPClient.put(path, body);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "doInBackground: " + returnCode);
        return returnCode;
    }
}
