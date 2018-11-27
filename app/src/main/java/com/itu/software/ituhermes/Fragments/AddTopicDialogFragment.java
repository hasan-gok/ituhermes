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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.itu.software.ituhermes.Code;
import com.itu.software.ituhermes.IUICallback;
import com.itu.software.ituhermes.R;
import com.itu.software.ituhermes.Tasks.AddTopicTask;
import com.itu.software.ituhermes.Tasks.GetTags;
import com.itu.software.ituhermes.Wrapper.Topic;

import java.util.ArrayList;

public class AddTopicDialogFragment extends DialogFragment implements View.OnClickListener, IUICallback {

    IUICallback callback;
    TextInputEditText titleText;
    Spinner tagSpinner;
    Button addbutton;
    Button cancelButton;
    ProgressDialog progressDialog;
    View parentView;

    public void setCallback(IUICallback callback) {
        {
            this.callback = callback;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_topic_button: {
                progressDialog.show();
                String title = titleText.getText().toString();
                String tag = tagSpinner.getSelectedItem().toString();
                AddTopicTask task = new AddTopicTask(this, title, tag);
                task.execute();
            }
            case R.id.cancel_add_topic: {
                progressDialog.dismiss();
                this.dismiss();
            }
        }
    }

    @Override
    public void callbackUI(Code code, Object data) {
        switch (code) {
            case DATA_SUCCESS: {
                try {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getDialog().getOwnerActivity(), R.layout.spinner_item_layout, (ArrayList<String>) data);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    tagSpinner.setAdapter(adapter);
                } catch (Exception e) {
                    this.getDialog().dismiss();
                }
                break;
            }
            case ADD_TOPIC: {
                int topicId = (int) data;
                String title = titleText.getText().toString();
                String tag = tagSpinner.getSelectedItem().toString();
                Topic topic = new Topic(title, tag, topicId, 0, 1, true);
                try {
                    this.dismiss();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                callback.callbackUI(Code.ADD_TOPIC, topic);
                break;
            }
        }
        progressDialog.dismiss();
    }

    @Override
    public void callbackUI(Code code) {
        progressDialog.dismiss();
        switch (code) {
            case FAIL: {
                progressDialog.dismiss();
                try {
                    titleText.setError(this.getDialog().getOwnerActivity().getString(R.string.unidentified_error));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        try {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage(getResources().getString(R.string.wait_prompt));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();
            LayoutInflater inflater = getActivity().getLayoutInflater();
            parentView = inflater.inflate(R.layout.add_topic_layout, null);
            titleText = parentView.findViewById(R.id.topic_title);
            tagSpinner = parentView.findViewById(R.id.tag_spinner);
            addbutton = parentView.findViewById(R.id.add_topic_button);
            cancelButton = parentView.findViewById(R.id.cancel_add_topic);
            addbutton.setOnClickListener(this);
            cancelButton.setOnClickListener(this);
            builder.setView(parentView);
            GetTags task = new GetTags(this, false);
            task.execute();
        } catch (NullPointerException e) {
            Log.d("", "onCreateDialog: " + e.getMessage());
        }
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        return dialog;
    }
}
