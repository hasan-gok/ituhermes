package com.itu.software.ituhermes.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.itu.software.ituhermes.Code;
import com.itu.software.ituhermes.IUICallback;
import com.itu.software.ituhermes.PostPagerActivity;
import com.itu.software.ituhermes.R;
import com.itu.software.ituhermes.Tasks.SendPost;
import com.itu.software.ituhermes.Wrapper.Topic;

public class PostDialogFragment extends DialogFragment implements View.OnClickListener, IUICallback<Void> {
    TextInputEditText inputEditText;
    Button sendButton;
    Button cancelButton;
    IUICallback callback;
    ProgressDialog progressDialog;
    View parentView;

    public PostDialogFragment() {
        super();
    }

    public void setCallback(IUICallback callback) {
        {
            this.callback = callback;
        }
    }

    @Override
    public void callbackUI(Code code) {
        progressDialog.dismiss();
        switch (code) {
            case POST_SUCCESS: {
                getDialog().dismiss();
                callback.callbackUI(Code.POST_SUCCESS);
            }
            case POST_FAIL: {
                inputEditText.setError(getString(R.string.post_fail));
            }
        }
    }

    @Override
    public void callbackUI(Code code, Void data) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_post_button: {
                String text = inputEditText.getText().toString();
                Topic topic = (Topic) getArguments().getSerializable(PostPagerActivity.TOPIC_KEY);
                SendPost task = new SendPost(this, topic.getTopicId(), text);
                progressDialog.setMessage("Sending post");
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                task.execute();
                break;
            }
            case R.id.cancel_button: {
                getDialog().dismiss();
                break;
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        try {
            progressDialog = new ProgressDialog(getContext());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            parentView = inflater.inflate(R.layout.send_post_layout, null);
            inputEditText = parentView.findViewById(R.id.input_post_text);
            sendButton = parentView.findViewById(R.id.send_post_button);
            cancelButton = parentView.findViewById(R.id.cancel_button);
            sendButton.setOnClickListener(this);
            cancelButton.setOnClickListener(this);
            builder.setView(parentView);
            builder.setCancelable(false);
        } catch (NullPointerException e) {
            Log.d("", "onCreateDialog: " + e.getMessage());
        }
        return builder.create();
    }
}
