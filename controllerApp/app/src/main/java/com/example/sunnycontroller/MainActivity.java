package com.example.sunnycontroller;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

public class MainActivity extends AppCompatActivity implements WebSocketEventListener {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;
    private CompoundBarcodeView barcodeScannerView;
    BarcodeCallback barcodeCallback;
    private String ipAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        barcodeScannerView = findViewById(R.id.barcodescanner);
        SharedPreferences preferences = getSharedPreferences("my_prefs", MODE_PRIVATE);
            ipAddress = preferences.getString("ipAddress", "Empty ipAddress");
        Log.d("iptv:" ,ipAddress);
        if (ipAddress != null) {
            WebSocketSingleton.getInstance().setEventListener(this);
            if (!WebSocketSingleton.getInstance().connectToWebSocket(ipAddress)) {
                clearIpAddress();
                startQRScanning();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                startQRScanning();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void saveIpAddress(String ipAddress) {
        SharedPreferences preferences = getSharedPreferences("my_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ipAddress", ipAddress);
        editor.apply();
    }

    private void clearIpAddress() {
        SharedPreferences preferences = getSharedPreferences("my_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("ipAddress");
        editor.apply();
    }

    private void startQRScanning() {
        if (barcodeScannerView != null) {
            barcodeCallback = new BarcodeCallback() {
                @Override
                public void barcodeResult(BarcodeResult result) {
                    String qrCodeResult = result.getText();
                    Log.d("QRCodeResult", qrCodeResult);
                    ipAddress = qrCodeResult;
                    if (WebSocketSingleton.getInstance().connectToWebSocket(ipAddress))
                        barcodeCallback = null;
                }
            };
            barcodeScannerView.pause();
            barcodeScannerView.decodeContinuous(barcodeCallback);


        } else {
            Log.e("MainActivity", "barcodeScannerView is null");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startQRScanning();
            } else {
                Toast.makeText(this, "Kamera izni reddedildi.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeScannerView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeScannerView.pause();
    }

    private void navigateToControllerActivity() {
        Intent intent = new Intent(MainActivity.this, ControllerActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onOpen() {
        WebSocketSingleton.getInstance().sendMessage("Merhaba, WebSocket Server!");
        Log.d("websocket", "bağlantı açıldı");
        saveIpAddress(ipAddress);
        navigateToControllerActivity();
    }

    @Override
    public void onTextReceived(String message) {

    }

    @Override
    public void onBinaryReceived(byte[] data) {

    }

    @Override
    public void onPingReceived(byte[] data) {

    }

    @Override
    public void onPongReceived(byte[] data) {

    }

    @Override
    public void onException(Exception e) {
        Log.d("websocket", "çöktü hata var " + e.getMessage());
    }

    @Override
    public void onCloseReceived() {

    }
}
