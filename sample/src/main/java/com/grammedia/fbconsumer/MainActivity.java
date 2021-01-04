package com.grammedia.fbconsumer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.grammedia.fbsdkconsumer.data.CPConnectionService;
import com.grammedia.fbsdkconsumer.data.CPMainParameters;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    CPConnectionService gregorysConnector;
    String gregorysCPToken = "f5f49293fb634246b546ffc80d09cdc0";
    String gregorysCPGroupName = "Cp test";
    String gregorysCPName = "moirizw";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setTitle("Check your Console Now 2");
        registerChannelNotification();
        gregorysConnector = new CPConnectionService(this);
        gregorysConnector.initWithOptions(gregorysCPToken, gregorysCPGroupName, gregorysCPName, "", "");
        gregorysConnector.askforregistration();
        if (getIntent().getExtras() != null) {
            Map<String, String> userInfo = new HashMap<>();
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                userInfo.put(key, String.valueOf(value));
                Log.d("asd", "Key: " + key + " Value: " + value);
            }
            gregorysConnector.didRecieveNotificationExtensionRequest(userInfo, FBAcplication.IS_APP_IN_FOREGROUND);
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

    @OnClick(R.id.btn_do_post_token)
    public void postTokenBtnPressed(View view) {
        gregorysConnector.resetcurSessionFCMTokenPosted("no");
        gregorysConnector.postFCMTokenToCP(CPMainParameters.getInstance().fcmToken);
    }

    @OnClick(R.id.btn_page_view)
    public void pageViewBtnPressed(View view) {
        String utmdt = "my simple Page View";
        String utmp = "/myandroidapp/category/subcategory/mypage/";
        gregorysConnector.pageView(utmdt, utmp);
    }

    @OnClick(R.id.btn_product_view)
    public void productViewBtnPressed(View view) {
        String pName = "το προϊον μου";
        String pSku = "12345-abcd";
        String utmp = "/myandroidapp/myprodpage/";
        gregorysConnector.productView(pName, pSku, utmp);
    }

    @OnClick(R.id.btn_add_2_cart)
    public void addCartBtnPressed(View view) {
        String pName = "το προϊον μου";
        String pSku = "12345-abcd";
        int pQty = 6;
        double pUnitPrice = 12.36;
        String utmp = "/myandroidapp/myprodpage/";
        String pimg = "";
        gregorysConnector.add2cart(pName, pSku, pQty, pUnitPrice, pimg, utmp);
    }

    @OnClick(R.id.btn_remove_from_cart)
    public void removeCartBtnPressed(View view) {
        String pName = "το προϊον μου";
        String pSku = "12345-abcd";
        int pQty = 2;
        double pUnitPrice = 12.36;
        String utmp = "/myandroidapp/myprodpage/";
        String pimg = "";
        gregorysConnector.removefromcart(pName, pSku, pQty, pUnitPrice, pimg, utmp);
    }

    @OnClick(R.id.btn_place_order)
    public void placeOrderBtnPressed(View view) {
        String utmp = "";
        gregorysConnector.setOrderData("oid-0008", 36.36);
        gregorysConnector.addOrderItem("81", "Four Punch Man T-Shirt", 2, 149);
        gregorysConnector.addOrderItem("49", "Sassy, loose fit dual tone dress", 5, 190);
        gregorysConnector.postOrder(utmp);
    }

    @OnClick(R.id.btn_post_cart)
    public void postCartBtnPressed(View view) {
        gregorysConnector.postCart();
    }

    @OnClick(R.id.btn_post_contact_email)
    public void postContactEmailBtnPressed(View view) {
        String cp_curEmail = "test@test.gr";
        String utmp = "/myandroidapp/category/subcategory/mypage/";
        gregorysConnector.setContactMail(cp_curEmail,utmp);
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


