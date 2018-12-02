package com.itu.software.ituhermes.connection;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.support.constraint.Constraints.TAG;

public class HTTPClient {
    //private static final String baseUrl = "https://ituhermes-server.herokuapp.com/";
    private static final String baseUrl = "http://10.0.2.2:5000/";

    private static int write(HttpURLConnection connection, JSONObject body) {
        if (body != null) {
            connection.setDoOutput(true);
            connection.setChunkedStreamingMode(0);
            try {
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                out.write(body.toString());
                Log.d("TAG", "write: " + body.toString());
                out.flush();
                out.close();
                return 0;
            } catch (IOException e) {
                return -1;
            }
        }
        return -1;
    }

    private static String read(HttpURLConnection connection) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder builder = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                builder.append(inputLine);
            }
            in.close();
            return builder.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public static JSONObject post(String path, JSONObject body) {
        JSONObject response = null;
        try {
            URL url = new URL(baseUrl + path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("content-type", "application/json; charset=utf-8");
            write(connection, body);
            if (connection.getResponseCode() == 200) {
                String str = read(connection);
                if (str.equals("")) {
                    response = new JSONObject("{code:0}");
                } else {
                    response = new JSONObject(str);
                }
            } else {
                response = new JSONObject("{code:-1}");
            }
            connection.disconnect();
        } catch (Exception e) {
            Log.e("HTTPClient", e.getMessage());
        }
        return response;
    }

    public static JSONObject get(String path) {
        JSONObject response = null;
        try {
            URL url = new URL(baseUrl + path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("content-type", "application/json; charset=utf-8");
            if (connection.getResponseCode() == 200) {
                String str = read(connection);
                response = new JSONObject(str);
                response.put("code", 0);
            } else {
                response = new JSONObject();
                response.put("code", -1);
            }
            connection.disconnect();
        } catch (Exception e) {
            Log.e("HTTPClient", e.getMessage());
        }
        return response;
    }

    public static int delete(String path) {
        int code = -1;
        try {
            URL url = new URL(baseUrl + path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            if (connection.getResponseCode() == 200) {
                code = 0;
            }
            connection.disconnect();
        } catch (Exception e) {
            Log.e("HTTPClient", e.getMessage());
        }
        return code;
    }

    public static int delete(String path, JSONObject body) {
        int code = -1;
        try {
            URL url = new URL(baseUrl + path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("content-type", "application/json; charset=utf-8");
            write(connection, body);
            if (connection.getResponseCode() == 200) {
                code = 0;
            }
            connection.disconnect();
        } catch (Exception e) {
            Log.e("HTTPClient", e.getMessage());
        }
        return code;
    }

    public static int put(String path, JSONObject body) {
        int code = -1;
        try {
            URL url = new URL(baseUrl + path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("content-type", "application/json; charset=utf-8");
            write(connection, body);
            if (connection.getResponseCode() == 200) {
                code = 0;
                String str = read(connection);
                body.put("extra", new JSONObject(str));
            }
            connection.disconnect();
        } catch (Exception e) {
            Log.e("HTTPClient", e.getMessage());
        }
        return code;
    }

    public static int put(String path) {
        int code = -1;
        try {
            URL url = new URL(baseUrl + path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("content-type", "application/json; charset=utf-8");
            if (connection.getResponseCode() == 200) {
                code = 0;
            }
            connection.disconnect();
        } catch (Exception e) {
            Log.e("HTTPClient", e.getMessage());
        }
        return code;
    }
}
