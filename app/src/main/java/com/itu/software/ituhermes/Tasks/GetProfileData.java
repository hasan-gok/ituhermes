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

public class GetProfileData<T extends IUICallback> extends AsyncTask<Void, Void, Integer> {
    private T activityReference;

    public GetProfileData(T activity) {
        this.activityReference = activity;
    }

    @Override
    protected void onPostExecute(Integer returnCode) {
        super.onPostExecute(returnCode);
        if (returnCode == 0) {
            activityReference.callbackUI(Code.SUCCESS);
        } else {
            activityReference.callbackUI(Code.FAIL);
        }

    }

    @Override
    protected Integer doInBackground(Void... voids) {
        User user = User.getCurrentUser();
        String email = user.getEmail();
        int returnCode = -1;
        if (!email.isEmpty()) {
            String path = String.format("/user/%s", email);
            JSONObject response = HTTPClient.get(path);
            try {
                if (response != null) {
                    JSONArray tagArray = response.getJSONArray("tags");
                    for (int i = 0; i < tagArray.length(); i++) {
                        user.addTopicTag(tagArray.getString(i));
                    }
                    returnCode = response.getInt("code");
                }
            } catch (JSONException e) {
                Log.d("ProfileGet", "doInBackground: " + e.getMessage());
            }
        }
        return returnCode;
    }
}
