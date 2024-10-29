package ru.dfhub.enigmaircmobile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.CompletableFuture;

import ru.dfhub.enigmaircmobile.eirc.Config;
import ru.dfhub.enigmaircmobile.eirc.DataParser;
import ru.dfhub.enigmaircmobile.eirc.Gui;
import ru.dfhub.enigmaircmobile.eirc.ServerConnection;
import ru.dfhub.enigmaircmobile.eirc.util.Encryption;

public class MessagingActivity extends AppCompatActivity {

    private ScrollView messageReadScroll;
    private LinearLayout messageReadBox;
    private EditText messageInput;
    private ImageButton messageSendButton;

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

        Gui.showWelcomeMessage();
    }

    /**
     * A method that fires before the activity starts
     * and after returning to the application (after it goes into the background)
     */
    @Override
    protected void onResume() {
        super.onResume();

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
            } catch (Exception e) {
                Gui.showNewMessage(e.toString(), Gui.MessageType.SYSTEM_ERROR);
                Gui.breakInput();
            }
        });

        DataParser.handleOutputSession(true);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> DataParser.handleOutputSession(false)));
    }

    public static ServerConnection getServerConnection() {
        return serverConnection;
    }

    public static void handleServerShutdown() {
        Gui.showNewMessage("The server has shut down!", Gui.MessageType.SYSTEM_ERROR);

        Vibrator vibrator = (Vibrator) MessagingActivity.CONTEXT.getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createOneShot(1000, 1));

        Gui.breakInput();
    }

    private void getActivityElements() {
        messageReadScroll = findViewById(R.id.message_read_scroll);
        messageInput = findViewById(R.id.message_input_box);
        messageReadBox = findViewById(R.id.message_read_box);
        messageSendButton = findViewById(R.id.message_send_button);
    }

}
