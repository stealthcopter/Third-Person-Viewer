package com.stealthcopter.thirdpersonviewer.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.stealthcopter.thirdpersonviewer.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by matthew on 09/12/16.
 */

public class IpDialogFragment extends DialogFragment implements TextView.OnEditorActionListener {

    @BindView(R.id.ipEditText) EditText mEditText;

    OnIpEnteredListener onIpEnteredListener;
    private String ip;


    public interface OnIpEnteredListener{
        void onIpEntered(String ipAddress);
    }

    public IpDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    public void setOnIpEnteredListener(OnIpEnteredListener onIpEnteredListener) {
        this.onIpEnteredListener = onIpEnteredListener;
    }
    public void setIp(String ip) {
        this.ip = ip;
        if (mEditText != null){
            mEditText.setText(this.ip);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_ip, container);

        ButterKnife.bind(this, view);

        mEditText.setText(this.ip);

        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mEditText.setOnEditorActionListener(this);

        getDialog().setTitle(R.string.enter_ip);

        return view;
    }

    @OnClick(R.id.cancelButton) void cancelClicked(){
        this.dismiss();
    }

    @OnClick(R.id.okButton) void okClicked(){
        if (onIpEnteredListener != null) {
            onIpEnteredListener.onIpEntered(mEditText.getText().toString());
        }
        this.dismiss();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text to activityOnIpEnteredListener
            okClicked();
            return true;
        }
        return false;
    }

}
