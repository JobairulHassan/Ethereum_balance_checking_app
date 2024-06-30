package com.example.assesmentproject;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.EditText;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;

import java.math.BigInteger;

public class ManualInput extends AppCompatActivity implements fetchData{

    private EditText addressEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manual_input);

        addressEditText = findViewById(R.id.addressEditText);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onButtonClick(View view) {
        fetchBalanceAndNonce();
    }

    public void fetchBalanceAndNonce() {
        String address = addressEditText.getText().toString().trim();
        if (!address.isEmpty()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        BigInteger balance = EthereumUtils.getBalance(address);
                        BigInteger nonce = EthereumUtils.getNonce(address);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (balance != null && nonce != null) {
                                    showBalanceDialog(balance.toString(), nonce.toString());
                                } else {
                                    // Handle error
                                    showErrorDialog();
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
    public void showBalanceDialog(String balance, String nonce) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        CustomDialogFragment customDialogFragment = CustomDialogFragment.newInstance(balance, nonce);
        customDialogFragment.show(fragmentManager, "balance_nonce_dialog");
    }

    public void showErrorDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Failed to fetch balance or nonce. Please check the address and try again.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addressEditText.setText("");
                    }
                })
                .show();
    }


}