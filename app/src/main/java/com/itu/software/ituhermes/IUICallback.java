package com.itu.software.ituhermes;

public interface IUICallback<T> {
    void callbackUI(Code code, T data);

    void callbackUI(Code code);
}