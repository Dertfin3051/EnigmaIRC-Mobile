package ru.dfhub.enigmaircmobile.eirc;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import java.util.concurrent.CompletableFuture;


import ru.dfhub.enigmaircmobile.MessagingActivity;
import ru.dfhub.enigmaircmobile.R;

public class Gui {

    public static enum MessageType {
        SELF_USER_MESSAGE, USER_MESSAGE, SYSTEM_GOOD, SYSTEM_INFO, SYSTEM_ERROR, USER_SESSION
    }
    /**
     * Show new message
     * @param formattedMessage Formatted message
     */

    public static void showNewMessage(String formattedMessage, MessageType type) {
        TextView message = new TextView(MessagingActivity.CONTEXT);
        message.setText(formattedMessage);
        message.setPadding(0, 13, 0, 0);
        message.setTextSize(14);
        message.setTypeface(ResourcesCompat.getFont(MessagingActivity.CONTEXT, R.font.kanit_regular));
        switch (type) {
            case SYSTEM_GOOD:
                message.setTextColor(ContextCompat.getColor(MessagingActivity.CONTEXT, R.color.system_good_message));
                break;
            case SYSTEM_INFO:
                message.setTextColor(ContextCompat.getColor(MessagingActivity.CONTEXT, R.color.system_info_message));
                break;
            case SYSTEM_ERROR:
                message.setTextColor(ContextCompat.getColor(MessagingActivity.CONTEXT, R.color.system_error_message));
                break;
            case SELF_USER_MESSAGE:
                message.setTextColor(ContextCompat.getColor(MessagingActivity.CONTEXT, R.color.self_user_message));
                break;
            case USER_MESSAGE:
                message.setTextColor(ContextCompat.getColor(MessagingActivity.CONTEXT, R.color.other_user_message));
                break;
            case USER_SESSION:
                message.setTextColor(ContextCompat.getColor(MessagingActivity.CONTEXT, R.color.user_session_message));
                break;
        }
        LinearLayout messageReadBox = MessagingActivity.CONTEXT.findViewById(R.id.message_read_box);
        messageReadBox.addView(message);
        scrollDown();
    }

    /**
     * Show welcome message.
     * Used instead of SYSTEM_GOOD due to increased font size
     */

    public static void showWelcomeMessage() {
        TextView message = new TextView(MessagingActivity.CONTEXT);
        message.setText("Welcome to EnigmaIRC!");
        message.setTextColor(ContextCompat.getColor(MessagingActivity.CONTEXT, R.color.system_good_message));
        message.setPadding(0, 7, 0, 0);
        message.setTextSize(15);
        message.setTypeface(ResourcesCompat.getFont(MessagingActivity.CONTEXT, R.font.kanit_regular));
        LinearLayout messageReadBox = MessagingActivity.CONTEXT.findViewById(R.id.message_read_box);
        messageReadBox.addView(message);
    }

    /**
     * Block input and exit the program after 2 minutes
     * Used for critical errors, implying the inability to further work with the program
     */
    public static void breakInput() {
        EditText input = MessagingActivity.CONTEXT.findViewById(R.id.message_input_box);
        input.setFocusable(false);
        input.setOnClickListener(view -> Toast.makeText(MessagingActivity.CONTEXT, "Not now available! Restart app and connect again", Toast.LENGTH_SHORT).show());
        
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(1000 * 120);
            } catch (InterruptedException e) {}
            Toast.makeText(MessagingActivity.CONTEXT, "EnigmaIRC closed!", Toast.LENGTH_LONG).show();
            System.exit(0);
        });
    }

    /**
     * Scroll down messageBox. Only for vertical scroll
     */
    public static void scrollDown() {
        ScrollView scroll = MessagingActivity.CONTEXT.findViewById(R.id.message_read_scroll);
        scroll.fullScroll(View.FOCUS_DOWN);
        scroll.scrollBy(0, 50);
    }
}