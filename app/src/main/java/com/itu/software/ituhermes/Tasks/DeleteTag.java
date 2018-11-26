package com.itu.software.ituhermes.Tasks;

import android.os.AsyncTask;

import com.itu.software.ituhermes.Code;
import com.itu.software.ituhermes.IUICallback;
import com.itu.software.ituhermes.Wrapper.User;
import com.itu.software.ituhermes.connection.HTTPClient;

public class DeleteTag extends AsyncTask<Void, Void, Integer> {
    private String tag;
    private IUICallback callback;

    public DeleteTag(IUICallback callback, String tag) {
        this.tag = tag;
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(Integer returnCode) {
        super.onPostExecute(returnCode);
        if (returnCode == 0) {
            callback.callbackUI(Code.DEL_TAG);
        } else {
            callback.callbackUI(Code.DATA_FAIL);
        }
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        int returnCode = -1;
        try {
            String path = String.format("user/tag/%s?token=%s", tag, User.getCurrentUser().getToken());
            returnCode = HTTPClient.delete(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnCode;
    }
}
