package com.itu.software.ituhermes.Tasks;


import android.os.AsyncTask;

import com.itu.software.ituhermes.Code;
import com.itu.software.ituhermes.IUICallback;
import com.itu.software.ituhermes.connection.HTTPClient;

import org.json.JSONObject;

public class SignupTask extends AsyncTask<Void, Void, Integer> {
    private static final String TAG = "SignupTask";
    private String name;
    private String lastName;
    private String password;
    private String email;
    private IUICallback callback;

    public SignupTask(IUICallback callback, String name, String lastName, String email, String password) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.callback = callback;
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        int returnCode = -1;
        try {
            String path = "signup";
            JSONObject request = new JSONObject();
            request.put("name", name);
            request.put("lastName", lastName);
            request.put("email", email);
            request.put("password", password);
            JSONObject response = HTTPClient.post(path, request);
            returnCode = response.getInt("code");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnCode;
    }

    @Override
    protected void onPostExecute(Integer returnCode) {
        switch (returnCode) {
            case 0:
                callback.callbackUI(Code.SUCCESS);
                break;
            case 2:
                callback.callbackUI(Code.USER_EXISTS);
                break;
            default:
                callback.callbackUI(Code.FAIL);
                break;
        }
    }
}
