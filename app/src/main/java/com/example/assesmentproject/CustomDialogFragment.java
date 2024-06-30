package com.example.assesmentproject;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class CustomDialogFragment extends DialogFragment {
    private static final String ARG_BALANCE = "balance";
    private static final String ARG_NONCE = "nonce";

    public interface OnDialogDismissListener {
        void onDialogDismiss();
    }

    private OnDialogDismissListener dismissListener;

    public static CustomDialogFragment newInstance(String balance, String nonce) {
        CustomDialogFragment fragment = new CustomDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BALANCE, balance);
        args.putString(ARG_NONCE, nonce);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnDialogDismissListener(OnDialogDismissListener listener) {
        this.dismissListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_dialog_box, container, false);

        TextView balanceTextView = view.findViewById(R.id.balanceTextView);
        TextView nonceTextView = view.findViewById(R.id.nonceTextView);
        Button closeButton = view.findViewById(R.id.closeButton);

        assert getArguments() != null;
        String balance = getArguments().getString(ARG_BALANCE);
        String nonce = getArguments().getString(ARG_NONCE);
        balanceTextView.setText("Balance: " + balance);
        nonceTextView.setText("Nonce: " + nonce);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dismissListener != null) {
            dismissListener.onDialogDismiss();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
