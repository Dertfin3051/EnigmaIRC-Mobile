package ru.dfhub.enigmaircmobile.eirc.util;

/*
import ru.dfhub.eirc.Main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.InputStream;

 */

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import ru.dfhub.enigmaircmobile.MessagingActivity;
import ru.dfhub.enigmaircmobile.R;

/**
 * Notification sound
 */
public class Notification {

    private static final String CHANNEL_ID = "enigmairc";
    private static final NotificationManager manager = (NotificationManager) MessagingActivity.CONTEXT.getSystemService(Context.NOTIFICATION_SERVICE);
    private static int NOTIFICATION_ID = 0;

    public static void registerNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "EnigmaIRC", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("EnigmaIRC Notifications");
        manager.createNotificationChannel(channel);
    }

    public static void sendNotification(String title, String content) {
        NOTIFICATION_ID += 1;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MessagingActivity.CONTEXT, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.notification_logo)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        builder.setChannelId(CHANNEL_ID);
        registerNotificationChannel();
        manager.notify(NOTIFICATION_ID, builder.build());
    }
}
