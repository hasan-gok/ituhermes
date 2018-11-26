package com.itu.software.ituhermes.Tasks;

import android.os.AsyncTask;

import com.itu.software.ituhermes.Wrapper.User;
import com.itu.software.ituhermes.connection.HTTPClient;

import org.json.JSONObject;

public class SendFirebaseToken extends AsyncTask<Void, Void, Void> {
    private String token;

    public SendFirebaseToken(String token) {
        this.token = token;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            JSONObject body = new JSONObject();
            body.put("fbToken", token);
            String path = String.format("user/fbToken?token=%s", User.getCurrentUser().getToken());
            HTTPClient.put(path, body);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
