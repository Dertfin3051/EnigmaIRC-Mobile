package ru.dfhub.enigmaircmobile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ru.dfhub.enigmaircmobile.eirc.Gui;

public class MessagingActivity extends AppCompatActivity {

    private ScrollView messageReadScroll;
    private LinearLayout messageReadBox;
    private EditText messageInput;
    private ImageButton messageSendButton;

    @SuppressLint("StaticFieldLeak")
    public static MessagingActivity CONTEXT;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messaging_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivityElements();
        CONTEXT = this;

        Gui.showWelcomeMessage();
    }

    private void getActivityElements() {
        messageReadScroll = findViewById(R.id.message_read_scroll);
        messageInput = findViewById(R.id.message_input_box);
        messageReadBox = findViewById(R.id.message_read_box);
        messageSendButton = findViewById(R.id.message_send_button);
    }
}
