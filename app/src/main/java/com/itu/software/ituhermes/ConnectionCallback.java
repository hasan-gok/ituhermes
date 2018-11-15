package com.itu.software.ituhermes;

import java.util.ArrayList;

public interface ConnectionCallback {
    <T> void callbackConnection(int code, T data);

    <T extends ArrayList<?>> void callbackConnection(int code, T data);
}
