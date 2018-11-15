package com.itu.software.ituhermes.Tasks;

import android.os.AsyncTask;

import com.itu.software.ituhermes.Code;
import com.itu.software.ituhermes.IUICallback;
import com.itu.software.ituhermes.Wrapper.User;
import com.itu.software.ituhermes.connection.HTTPClient;

public class AddTag<T extends IUICallback> extends AsyncTask<Void, Void, Integer> {
    private T activityReference;
    private String tag;

    public AddTag(String tag, T activity) {
        this.tag = tag;
        this.activityReference = activity;
    }

    @Override
    protected void onPostExecute(Integer returnCode) {
        super.onPostExecute(returnCode);
        if (returnCode == 0) {
            activityReference.callbackUI(Code.ADD_TAG);
        } else {
            activityReference.callbackUI(Code.DATA_FAIL);
        }
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        String path = String.format("/user/%s/tag/%s", User.getCurrentUser().getEmail(), tag);
        return HTTPClient.put(path);
    }
}
