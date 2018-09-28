package com.itu.software.ituhermes.connection;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPClient {
    private static final String baseUrl = "https://ituhermes-server.herokuapp.com";

    public static JSONObject post(String path, JSONObject body) {
        JSONObject response = null;
        try {
            URL url = new URL(baseUrl + path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("content-type", "application/json; charset=utf-8");
            connection.setDoOutput(true);
            connection.setChunkedStreamingMode(0);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
            out.write(body.toString());
            out.flush();
            out.close();
            if (connection.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder builder = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    builder.append(inputLine);
                }
                in.close();
                response = new JSONObject(builder.toString());
            }
            connection.disconnect();
        } catch (Exception e) {
            Log.e("HTTPClient", e.getMessage());
        }
        return response;
    }
}
