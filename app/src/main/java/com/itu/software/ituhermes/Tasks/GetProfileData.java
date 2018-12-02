package com.itu.software.ituhermes.Tasks;

import android.os.AsyncTask;

import com.itu.software.ituhermes.Code;
import com.itu.software.ituhermes.IUICallback;
import com.itu.software.ituhermes.Wrapper.User;
import com.itu.software.ituhermes.connection.HTTPClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetProfileData extends AsyncTask<Void, Void, Integer> {
    private IUICallback callback;

    public GetProfileData(IUICallback callback) {
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(Integer returnCode) {
        super.onPostExecute(returnCode);
        if (returnCode == 0) {
            callback.callbackUI(Code.SUCCESS);
        } else {
            callback.callbackUI(Code.FAIL);
        }

    }

    @Override
    protected Integer doInBackground(Void... voids) {
        User user = User.getCurrentUser();
        int returnCode = -1;
        try {
            String path = String.format("user?token=%s", user.getToken());
            JSONObject response = HTTPClient.get(path);
            JSONArray tagArray = response.getJSONArray("tags");
            for (int i = 0; i < tagArray.length(); i++) {
                user.addTopicTag(tagArray.getString(i));
            }
            String name = response.getString("name");
            String lastName = response.getString("lastName");
            String email = response.getString("email");
            String userId = response.getString("_id");
            user.setName(name);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setUserId(userId);
            returnCode = response.getInt("code");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnCode;
    }
}
