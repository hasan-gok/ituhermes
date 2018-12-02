package com.itu.software.ituhermes.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.itu.software.ituhermes.Tasks.EditPost;
import com.itu.software.ituhermes.Tasks.SendPost;
import com.itu.software.ituhermes.Wrapper.Post;
import com.itu.software.ituhermes.Wrapper.Topic;

import static android.support.constraint.Constraints.TAG;

public class PostEditFragment extends DialogFragment implements View.OnClickListener, IUICallback {

    TextInputEditText inputEditText;
    Button sendButton;
    Button cancelButton;
    IUICallback callback;
    ProgressDialog progressDialog;
    View parentView;
    Post post;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        try {
            progressDialog = new ProgressDialog(getContext());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            parentView = inflater.inflate(R.layout.edit_post_layout, null);
            post = (Post) getArguments().getSerializable(PostPageFragment.POST_KEY);
            inputEditText = parentView.findViewById(R.id.edit_post_text);
            inputEditText.setText(post.getMessage());
            sendButton = parentView.findViewById(R.id.edit_post_button);
            cancelButton = parentView.findViewById(R.id.cancel_edit_button);
            sendButton.setOnClickListener(this);
            cancelButton.setOnClickListener(this);
            builder.setView(parentView);
            builder.setCancelable(false);
        } catch (NullPointerException e) {
            Log.d("", "onCreateDialog: " + e.getMessage());
        }
        return builder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_post_button: {
                String text = inputEditText.getText().toString();
                EditPost task = new EditPost(this, post.getPostId(), text);
                progressDialog.setMessage("Sending post");
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                task.execute();
                break;
            }
            case R.id.cancel_edit_button: {
                getDialog().dismiss();
                break;
            }
        }
    }
    public void setCallback(IUICallback callback) {
        {
            this.callback = callback;
        }
    }
    @Override
    public void callbackUI(Code code, Object data) {

    }

    @Override
    public void callbackUI(Code code) {
        progressDialog.dismiss();
        switch (code) {
            case POST_SUCCESS: {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(false);
                builder.setMessage(R.string.post_success);
                builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        PostEditFragment.this.getDialog().dismiss();
                        callback.callbackUI(Code.POST_SUCCESS);
                    }
                });
                builder.create().show();
                break;
            }
            case POST_FAIL: {
                inputEditText.setError(getString(R.string.post_fail));
                break;
            }
        }
    }
}
