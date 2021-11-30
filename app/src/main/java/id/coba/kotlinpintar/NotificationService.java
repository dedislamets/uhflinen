package id.coba.kotlinpintar;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.common.internal.service.Common;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static android.content.ContentValues.TAG;

public class NotificationService extends FirebaseMessagingService {

    public static final String CHANNEL_ID = "#123";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

        }
        if (remoteMessage.getNotification() != null) {
            handleMessage(remoteMessage);
//            sendNotification(remoteMessage.getNotification().getBody());
        }
    }

    private void sendNotification(String messageBody) {
        createNotificationChannel();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                0);
        String CHANNEL_ID = "Linenku";

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(messageBody)
                .setContentText(messageBody)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLights(200,200,200)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(defaultSoundUri)
                .setChannelId(CHANNEL_ID)
                .setContentIntent(pendingIntent);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(88, notificationBuilder.build());
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "all_notifications" ;
            NotificationChannel mChannel = new NotificationChannel(
                    channelId,
                    "Linenku",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            mChannel.setDescription( "This is default channel used for all other notifications");

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE) ;
            notificationManager.createNotificationChannel(mChannel);
        }
    }
    private void handleMessage(final RemoteMessage remoteMessage ) {
        String link = "";
        if (remoteMessage.getData().get("link") != null) {
            try {
                link = remoteMessage.getData().get("link");
            } catch (Exception e) {
                link = "";
                e.printStackTrace();
            }
        }

        Intent intent;
        PendingIntent pendingIntent;
        if (link.equals("")) { // Simply run your activity
            intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                    PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder notificationBuilder = null;
            try {
                notificationBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(URLDecoder.decode(getString(R.string.app_name), "UTF-8"))
                        .setContentText(URLDecoder.decode(remoteMessage.getNotification().getBody(), "UTF-8"))
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent);
                if (notificationBuilder != null) {
                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(22, notificationBuilder.build());
                } else {
                    Log.d(TAG, "error NotificationManager");
                }
            }catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else { // open a link
            String url = "";
            if (!link.equals("")) {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(link));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                        PendingIntent.FLAG_ONE_SHOT);

                NotificationCompat.Builder notificationBuilder = null;
                try {
                    notificationBuilder = new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(URLDecoder.decode(getString(R.string.app_name), "UTF-8"))
                            .setContentText(URLDecoder.decode(remoteMessage.getNotification().getBody(), "UTF-8"))
                            .setAutoCancel(true)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setContentIntent(pendingIntent);

                    if (notificationBuilder != null) {
                        NotificationManager notificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(22, notificationBuilder.build());
                    } else {
                        Log.d(TAG, "error NotificationManager");
                    }
                }catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

        }


        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent sendBC = new Intent();
                sendBC.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                        .setAction("broadcast_notify")
                        .putExtra("myBroadcast","Broadcast diterima!");
                sendBroadcast(sendBC);
//                Toast.makeText(getApplicationContext(), remoteMessage.getNotification().getBody(),
//                        Toast.LENGTH_LONG).show();
            }
        });

    }

}