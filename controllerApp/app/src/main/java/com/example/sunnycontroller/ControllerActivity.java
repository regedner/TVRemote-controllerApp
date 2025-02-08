package com.example.sunnycontroller;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sunnycontroller.databinding.ActivityControllerBinding;

import java.util.HashMap;
import java.util.Map;

public class ControllerActivity extends AppCompatActivity {

    private ActivityControllerBinding binding;
    private long lastMessageTime = 0;
    WebSocketSingleton mWebSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityControllerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mWebSocket = WebSocketSingleton.getInstance();

        setQrButtonClickListener();
        setKeyboardButtonClickListener();
        setAnimatedButtonClickListeners();
        setAnimatedImageButtonClickListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebSocket.closeConnection();
    }

    private void clearIpAddress() {
        SharedPreferences preferences = getSharedPreferences("my_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("ipAddress");
        editor.apply();
    }

    private boolean canSendMessage() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastMessageTime < 350)
            return false;

        lastMessageTime = currentTime;
        return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setAnimatedButtonClickListeners() {
        Map<String, Button> animatedButtons = new HashMap<>();
        animatedButtons.put("1", binding.buttonOne);
        animatedButtons.put("2", binding.buttonTwo);
        animatedButtons.put("3", binding.buttonThree);
        animatedButtons.put("4", binding.buttonFour);
        animatedButtons.put("5", binding.buttonFive);
        animatedButtons.put("6", binding.buttonSix);
        animatedButtons.put("7", binding.buttonSeven);
        animatedButtons.put("8", binding.buttonEight);
        animatedButtons.put("9", binding.buttonNine);
        animatedButtons.put("0", binding.buttonZero);
        animatedButtons.put("ENTER", binding.btnOk);
        animatedButtons.put("VOLUMEUP", binding.btnVolumeUp);
        animatedButtons.put("VOLUMEDOWN", binding.btnVolumeDown);
        animatedButtons.put("CHANNELUP", binding.btnChUp);
        animatedButtons.put("CHANNELDOWN", binding.btnChDown);

        for (Map.Entry<String, Button> entry : animatedButtons.entrySet()) {
            entry.getValue().setOnTouchListener((view, motionEvent) -> {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        Button v = (Button) view;
                        v.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        Button v = (Button) view;
                        v.getBackground().clearColorFilter();
                        v.invalidate();

                        if (canSendMessage()) mWebSocket.sendMessage(entry.getKey());
                        break;
                    }
                }

                return true;
            });
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void  setAnimatedImageButtonClickListeners() {
        Map<String, ImageButton> animatedImageButtons = new HashMap<>();
        animatedImageButtons.put("MENU", binding.buttonMenu);
        animatedImageButtons.put("POWER", binding.btnPower);
        animatedImageButtons.put("UP", binding.btnUp);
        animatedImageButtons.put("DOWN", binding.btnDown);
        animatedImageButtons.put("LEFT", binding.btnLeft);
        animatedImageButtons.put("RIGHT", binding.btnRight);
        animatedImageButtons.put("HOME", binding.btnHome);
        animatedImageButtons.put("BACK", binding.btnBack);
        animatedImageButtons.put("BROWSER", binding.btnGoogle);
        animatedImageButtons.put("INPUT", binding.btnSource);
        animatedImageButtons.put("NETFLIX", binding.buttonNetflix);
        animatedImageButtons.put("PRIME_VIDEO", binding.btnPrime);
        animatedImageButtons.put("YOUTUBE", binding.btnYoutube);
        animatedImageButtons.put("MUTE", binding.btnMute);

        for (Map.Entry<String, ImageButton> entry : animatedImageButtons.entrySet()) {
            entry.getValue().setOnTouchListener((view, motionEvent) -> {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageButton v = (ImageButton) view;
                        v.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        ImageButton v = (ImageButton) view;
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        if (canSendMessage()) mWebSocket.sendMessage(entry.getKey());
                        break;
                    }
                }

                return true;
            });
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setQrButtonClickListener() {
        binding.buttonKeyboard.setOnTouchListener(((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    ImageButton v = (ImageButton) view;
                    v.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                    v.invalidate();
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    ImageButton v = (ImageButton) view;
                    v.getBackground().clearColorFilter();
                    v.invalidate();

                    mWebSocket.closeConnection();
                    clearIpAddress();
                    Intent intent = new Intent(ControllerActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                }
            }

            return true;
        }));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setKeyboardButtonClickListener() {
        binding.buttonKeyboard.setOnTouchListener(((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    ImageButton v = (ImageButton) view;
                    v.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                    v.invalidate();
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    ImageButton v = (ImageButton) view;
                    v.getBackground().clearColorFilter();
                    v.invalidate();

                    Dialog keyBoardDialog = new Dialog(ControllerActivity.this);
                    keyBoardDialog.setContentView(R.layout.keyboard_dialog_layout);
                    keyBoardDialog.setCancelable(false);

                    keyBoardDialog.findViewById(R.id.btnSend).setOnClickListener(view1 -> {
                        String inputValue = String.valueOf(((EditText) keyBoardDialog.findViewById(R.id.editTextInput)).getText());
                        mWebSocket.sendMessage("TEXT: " + inputValue);
                        keyBoardDialog.dismiss();
                    });

                    keyBoardDialog.findViewById(R.id.btnCancel).setOnClickListener(view1 -> keyBoardDialog.dismiss());

                    keyBoardDialog.show();
                    break;
                }
            }

            return true;
        }));
    }
}
