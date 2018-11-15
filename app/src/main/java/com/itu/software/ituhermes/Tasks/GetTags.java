package com.itu.software.ituhermes.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.itu.software.ituhermes.Code;
import com.itu.software.ituhermes.IUICallback;
import com.itu.software.ituhermes.Wrapper.User;
import com.itu.software.ituhermes.connection.HTTPClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GetTags<T extends IUICallback> extends AsyncTask<Void, Void, ArrayList<String>> {
    private T activityReference;

    public GetTags(T activity) {
        this.activityReference = activity;
    }

    @Override
    protected void onPostExecute(ArrayList<String> strings) {
        super.onPostExecute(strings);
        if (strings != null) {
            activityReference.callbackUI(Code.DATA_SUCCESS, strings);
        } else {
            activityReference.callbackUI(Code.DATA_FAIL, null);
        }
    }

    @Override
    protected ArrayList<String> doInBackground(Void... voids) {
        User user = User.getCurrentUser();
        String email = user.getEmail();
        if (!email.isEmpty()) {
            String path = "/tags";
            JSONObject response = HTTPClient.get(path);
            if (response != null) {
                try {
                    JSONArray tagArray = response.getJSONArray("tags");
                    ArrayList<String> tags = new ArrayList<>(tagArray.length());
                    for (int i = 0; i < tagArray.length(); i++) {
                        tags.add(tagArray.getString(i));
                    }
                    return tags;
                } catch (JSONException e) {
                    Log.d("GetTags", "doInBackground: " + e.getMessage());
                }
            }
        }
        return null;
    }
}
