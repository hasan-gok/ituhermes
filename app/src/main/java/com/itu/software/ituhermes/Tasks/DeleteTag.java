package com.itu.software.ituhermes.Tasks;

import android.os.AsyncTask;

import com.itu.software.ituhermes.Code;
import com.itu.software.ituhermes.IUICallback;
import com.itu.software.ituhermes.Wrapper.User;
import com.itu.software.ituhermes.connection.HTTPClient;

public class DeleteTag<T extends IUICallback> extends AsyncTask<Void, Void, Integer> {
    private String tag;
    private T activityReference;

    public DeleteTag(String tag, T activity) {
        this.tag = tag;
        activityReference = activity;
    }

    @Override
    protected void onPostExecute(Integer returnCode) {
        super.onPostExecute(returnCode);
        if (returnCode == 0) {
            activityReference.callbackUI(Code.DEL_TAG);
        } else {
            activityReference.callbackUI(Code.DATA_FAIL);
        }
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        User user = User.getCurrentUser();
        String path = String.format("/user/%s/tag/%s", user.getEmail(), tag);
        return HTTPClient.delete(path);
    }
}
