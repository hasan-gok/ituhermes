package com.itu.software.ituhermes.connection;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class JWTUtility {
    private static String filename = "token.tok";

    public static void deleteToken(Context context) {
        File directory = context.getFilesDir();
        File file = new File(directory, filename);
        file.delete();
    }

    public static String getToken(Context context) {
        try {
            FileInputStream stream = context.openFileInput(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            return reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean saveToken(Context context, String token) {
        try {
            FileOutputStream stream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            stream.write(token.getBytes(Charset.forName("UTF-8")));
            stream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
