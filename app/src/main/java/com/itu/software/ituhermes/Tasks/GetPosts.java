package com.itu.software.ituhermes.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.itu.software.ituhermes.Code;
import com.itu.software.ituhermes.IUICallback;
import com.itu.software.ituhermes.Wrapper.Post;
import com.itu.software.ituhermes.connection.HTTPClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GetPosts<T extends IUICallback> extends AsyncTask<Void, Void, Integer> {
    private static final String TAG = "GetPosts";
    private T activityReference;
    private int topicId;
    private int pageNumber;
    private ArrayList<Post> posts;

    public GetPosts(T activityReference, int topicId, int pageNumber) {
        this.activityReference = activityReference;
        this.topicId = topicId;
        this.pageNumber = pageNumber;
        this.posts = new ArrayList<>();
    }

    @Override
    protected void onPostExecute(Integer returnCode) {
        if (returnCode < 0) {
            activityReference.callbackUI(Code.FAIL);
        } else {
            if (posts.size() > 0)
                activityReference.callbackUI(Code.DATA_SUCCESS, posts);
            else
                activityReference.callbackUI(Code.FAIL);
        }
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        int returnCode = -1;
        String path = String.format("/topic/%d/%d/posts", topicId, pageNumber);
        try {
            JSONObject response = HTTPClient.get(path);
            if (response != null) {
                JSONArray postArray = response.getJSONArray("posts");
                returnCode = response.getInt("code");
                for (int i = 0; i < postArray.length(); i++) {
                    JSONObject postObj = postArray.getJSONObject(i);
                    JSONObject sender = postObj.getJSONObject("sender");
                    String senderName = sender.getString("name");
                    String senderLastName = sender.getString("lastName");
                    String date = postObj.getString("date");
                    String message = postObj.getString("message");
                    Post newPost = new Post(senderName + ' ' + senderLastName, message, date);
                    posts.add(newPost);
                }
            }
        } catch (JSONException e) {
            Log.d(TAG, "doInBackground: " + e.getMessage());
        }
        return returnCode;
    }
}
