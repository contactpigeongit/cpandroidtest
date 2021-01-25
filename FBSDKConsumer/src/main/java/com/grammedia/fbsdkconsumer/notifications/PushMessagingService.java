package com.grammedia.fbsdkconsumer.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.grammedia.fbsdkconsumer.FBSDKApplication;
import com.grammedia.fbsdkconsumer.PrefUtils;
import com.grammedia.fbsdkconsumer.R;
import com.grammedia.fbsdkconsumer.data.CPConnectionService;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.grammedia.fbsdkconsumer.PrefUtils.kUDFCMToken;

public class PushMessagingService extends FirebaseMessagingService {

    private static final String TAG = "PushMessagingService";
    Bitmap bitmapBigImage;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        System.out.println("From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            System.out.println("1111111111111111111111111");
            System.out.println("Message data payload: " + remoteMessage.getData());
            String imageUri = remoteMessage.getData().get("image");
            if (imageUri != null && !imageUri.isEmpty())
                bitmapBigImage = getBitmapfromUrl(imageUri);
            sendNotification(remoteMessage);
            return;
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            String imageUri = remoteMessage.getNotification().getImageUrl().toString();
            if (imageUri != null && !imageUri.isEmpty())
                bitmapBigImage = getBitmapfromUrl(imageUri);
            sendNotification(remoteMessage);
        }

    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String newToken) {
        System.out.println("Refreshed token: " + newToken);
        PrefUtils.getEditor(getApplicationContext()).putString(kUDFCMToken,newToken).commit();
        CPConnectionService.getInstance().getRegistrationToken();
    }
    // [END on_new_token]

    private void sendNotification(RemoteMessage userInfo) {
        CPConnectionService.getInstance().didRecieveNotificationExtensionRequest(userInfo.getData(), FBSDKApplication.IS_APP_IN_FOREGROUND);
        String mPackage = getApplicationContext().getPackageName();
        Intent intent =  Intent.makeMainActivity(new ComponentName(mPackage,mPackage + ".MainActivity"));
        intent.putExtra("Test","Test data");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(userInfo.getData().get("title"))
                        .setContentText(userInfo.getData().get("body"))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
        if (bitmapBigImage != null) {
            notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmapBigImage));
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }
}

