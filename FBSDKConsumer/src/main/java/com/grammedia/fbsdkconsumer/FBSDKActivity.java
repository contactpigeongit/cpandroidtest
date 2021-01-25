package com.grammedia.fbsdkconsumer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.grammedia.fbsdkconsumer.data.CPConnectionService;

import java.util.HashMap;
import java.util.Map;

public abstract class FBSDKActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        registerChannelNotification();
        if (getIntent().getExtras() != null) {
            Map<String, String> userInfo = new HashMap<>();
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                userInfo.put(key, String.valueOf(value));
                Log.d("asd", "Key: " + key + " Value: " + value);
            }
            CPConnectionService.getInstance().didRecieveNotificationExtensionRequest(userInfo, FBSDKApplication.IS_APP_IN_FOREGROUND);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d("asd", "Key: " + key + " Value: " + value);
            }
        }
    }

    private void registerChannelNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getResources().getString(R.string.default_notification_channel_id);
            String channelName = getResources().getString(R.string.fbconsumer_channel);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_HIGH));
        }
    }
}
