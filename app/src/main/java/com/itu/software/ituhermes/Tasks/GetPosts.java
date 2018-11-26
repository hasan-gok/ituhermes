package com.itu.software.ituhermes.Tasks;

import android.os.AsyncTask;

import com.itu.software.ituhermes.Code;
import com.itu.software.ituhermes.IUICallback;
import com.itu.software.ituhermes.Wrapper.Post;
import com.itu.software.ituhermes.Wrapper.User;
import com.itu.software.ituhermes.connection.HTTPClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class GetPosts extends AsyncTask<Void, Void, Integer> {
    private static final String TAG = "GetPosts";
    private IUICallback callback;
    private int topicId;
    private int pageNumber;
    private ArrayList<Post> posts;

    public GetPosts(IUICallback callback, int topicId, int pageNumber) {
        this.callback = callback;
        this.topicId = topicId;
        this.pageNumber = pageNumber;
        this.posts = new ArrayList<>();
    }

    @Override
    protected void onPostExecute(Integer returnCode) {
        if (returnCode < 0) {
            callback.callbackUI(Code.FAIL);
        } else {
            if (posts.size() > 0)
                callback.callbackUI(Code.DATA_SUCCESS, posts);
            else
                callback.callbackUI(Code.FAIL);
        }
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        int returnCode = -1;
        try {
            String path = String.format("topic/%d/%d/posts?token=%s", topicId, pageNumber, User.getCurrentUser().getToken());
            JSONObject response = HTTPClient.get(path);
                JSONArray postArray = response.getJSONArray("posts");
                returnCode = response.getInt("code");
                for (int i = 0; i < postArray.length(); i++) {
                    JSONObject postObj = postArray.getJSONObject(i);
                    JSONObject sender = postObj.getJSONObject("sender");
                    String senderName = sender.getString("name");
                    String senderLastName = sender.getString("lastName");
                    String date = postObj.getString("date");
                    String message = postObj.getString("message");
                    Post newPost = new Post(senderName + ' ' + senderLastName, message, date, topicId);
                    posts.add(newPost);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnCode;
    }
}
