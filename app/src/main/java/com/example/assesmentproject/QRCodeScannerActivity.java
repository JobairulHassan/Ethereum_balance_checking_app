package com.example.assesmentproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.math.BigInteger;

public class QRCodeScannerActivity extends AppCompatActivity implements fetchData{

    private static final int PERMISSION_REQUEST_CAMERA = 1;
    private String scannedAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initQRCodeScanner();
    }

    private void initQRCodeScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan a QR code");
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setOrientationLocked(true);  // Lock orientation to portrait
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initQRCodeScanner();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_LONG).show();
            } else {
                scannedAddress = result.getContents();
                fetchBalanceAndNonce();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void fetchBalanceAndNonce() {
        if (!scannedAddress.isEmpty()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        BigInteger balance = EthereumUtils.getBalance(scannedAddress);
                        BigInteger nonce = EthereumUtils.getNonce(scannedAddress);

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
        customDialogFragment.setOnDialogDismissListener(new CustomDialogFragment.OnDialogDismissListener() {
            @Override
            public void onDialogDismiss() {
                navigateToMainActivity();
            }
        });
        customDialogFragment.show(fragmentManager, "balance_nonce_dialog");
    }

    public void showErrorDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Failed to fetch balance or nonce. Please check the address and try again.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        navigateToMainActivity();
                    }
                })
                .show();
    }
    private void navigateToMainActivity(){
        Intent intent = new Intent(QRCodeScannerActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}