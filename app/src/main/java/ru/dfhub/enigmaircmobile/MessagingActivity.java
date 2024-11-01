package ru.dfhub.enigmaircmobile;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.CompletableFuture;

import ru.dfhub.enigmaircmobile.eirc.Config;
import ru.dfhub.enigmaircmobile.eirc.DataParser;
import ru.dfhub.enigmaircmobile.eirc.Gui;
import ru.dfhub.enigmaircmobile.eirc.ServerConnection;
import ru.dfhub.enigmaircmobile.eirc.util.Encryption;
import ru.dfhub.enigmaircmobile.eirc.util.Notification;

public class MessagingActivity extends AppCompatActivity {

    private ScrollView messageReadScroll;
    private LinearLayout messageReadBox;
    private EditText messageInput;
    private ImageButton messageSendButton;

    private static Handler handler;
    private static boolean isMinimized;
    private static ServerConnection serverConnection;

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
        handler = new Handler(Looper.getMainLooper());
        Notification.registerNotificationChannel();

        init();
        Gui.showWelcomeMessage();

        messageSendButton.setOnClickListener(this::onMessageSendButton);
    }

    public static ServerConnection getServerConnection() {
        return serverConnection;
    }

    public static void handleServerShutdown() {
        Vibrator vibrator = (Vibrator) MessagingActivity.CONTEXT.getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createOneShot(1000, 1));

        handler.post(() -> {
            Gui.showNewMessage("The server has shut down!", Gui.MessageType.SYSTEM_ERROR);
            Gui.breakInput();
        });
    }

    private void getActivityElements() {
        messageReadScroll = findViewById(R.id.message_read_scroll);
        messageInput = findViewById(R.id.message_input_box);
        messageReadBox = findViewById(R.id.message_read_box);
        messageSendButton = findViewById(R.id.message_send_button);
    }

    public static Handler getHandler() {
        return handler;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isMinimized = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isMinimized = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataParser.handleOutputSession(false);
        finish();
    }

    private void init() {
        /*
        Trying to load encryption key
        This occurs separately from encryption to distinguish between missing a key and an invalid key.
        If there is no key, an attempt will be made to generate a new one.
         */
        try {
            Encryption.initKey();
        } catch (Encryption.EncryptionException e)
        {
            Encryption.showNullKeyErrorAndGenerateNewOne();
        } catch (IllegalArgumentException e) {
            Encryption.showIncorrectKeyError();
        }

        /*
        Trying to initialize encryption.
        At this stage you can find out that the key is invalid
         */
        try {
            Encryption.initEncryption();
        } catch (Exception e)
        {
            Encryption.showIncorrectKeyError();
        }

        /*
        Trying to connect to the server. Need to run async
         */
        CompletableFuture.runAsync(() -> {
            try {
                serverConnection = new ServerConnection(Config.getConfig().optString("server-address"), Config.getConfig().optInt("server-port", 6667));
                DataParser.handleOutputSession(true);
            } catch (Exception e) {
                Gui.showNewMessage("Can't connect to the server!", Gui.MessageType.SYSTEM_ERROR);
                Gui.breakInput();
            }
        });
    }

    public static boolean isMinimized() {
        return isMinimized;
    }

    private void onMessageSendButton(View view) {
        String text = messageInput.getText().toString();
        if (text.isEmpty()) return;
        if (text.equalsIgnoreCase("!!clear")) {
            messageReadBox.removeAllViews();
            messageInput.setText("");
            return;
        }

        DataParser.handleOutputMessage(text);
        messageInput.setText("");
    }
}
