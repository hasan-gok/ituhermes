package com.itu.software.ituhermes.connection;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormValidator {
    public static boolean validateEmail(String email) {
        return validator(".+@.+", email);
    }

    public static boolean validatePassword(String password) {
        return validator("[a-zA-Z\\d@$!%*#?&]{8,20}", password);
    }

    public static boolean validateName(String name) {
        return validator("[a-zA-Zğüşiöç]+", name);
    }

    private static boolean validator(String regex, String toMatch) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(toMatch);
        Log.d("", "validator: " + matcher.toString());
        return matcher.matches();
    }
}
