package com.itu.software.ituhermes.Tasks;

import android.os.AsyncTask;

import com.itu.software.ituhermes.Code;
import com.itu.software.ituhermes.IUICallback;
import com.itu.software.ituhermes.Wrapper.User;
import com.itu.software.ituhermes.connection.HTTPClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class GetTags extends AsyncTask<Void, Void, ArrayList<String>> {
    private IUICallback callback;
    private boolean userRelated;

    public GetTags(IUICallback callback, boolean userOnly) {
        this.callback = callback;
        this.userRelated = userOnly;
    }

    @Override
    protected void onPostExecute(ArrayList<String> strings) {
        super.onPostExecute(strings);
        if (strings != null) {
            callback.callbackUI(Code.DATA_SUCCESS, strings);
        } else {
            callback.callbackUI(Code.DATA_FAIL, null);
        }
    }

    @Override
    protected ArrayList<String> doInBackground(Void... voids) {
        try {
            String path = String.format("tags?token=%s", User.getCurrentUser().getToken());
            if (!userRelated) {
                path = path.concat("&all=true");
            }
            JSONObject response = HTTPClient.get(path);
            JSONArray tagArray = response.getJSONArray("tags");
            ArrayList<String> tags = new ArrayList<>(tagArray.length());
            for (int i = 0; i < tagArray.length(); i++) {
                tags.add(tagArray.getString(i));
            }
            return tags;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
