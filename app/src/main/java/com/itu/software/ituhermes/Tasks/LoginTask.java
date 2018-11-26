package com.itu.software.ituhermes.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.itu.software.ituhermes.Code;
import com.itu.software.ituhermes.IUICallback;
import com.itu.software.ituhermes.Wrapper.User;
import com.itu.software.ituhermes.connection.HTTPClient;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginTask extends AsyncTask<Void, Void, Integer> {
    private IUICallback callback;
    private String email;
    private String password;
    private String token;

    public LoginTask(IUICallback callback, String email, String password) {
        this.callback = callback;
        this.email = email;
        this.password = password;
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        int responseCode = -1;
        try {
            String path = "login";
            JSONObject body = new JSONObject();
            body.put("email", email);
            body.put("password", password);
            JSONObject response = HTTPClient.post(path, body);
            if (response != null) {
                responseCode = Integer.parseInt(response.getString("code"));
                token = response.getString("token");
            }
        } catch (JSONException e) {
            Log.e("Login", e.getMessage());
        }
        return responseCode;
    }

    @Override
    protected void onPostExecute(Integer returnCode) {
        switch (returnCode) {
            case 0:
                User.getCurrentUser().setEmail(email);
                callback.callbackUI(Code.SUCCESS, token);
                break;
            case 1:
                callback.callbackUI(Code.WRONG_PASS);
                break;
            case 2:
                callback.callbackUI(Code.NO_USER);
                break;
            default:
                callback.callbackUI(Code.FAIL);
                break;
        }
    }
}
