package com.grammedia.fbconsumer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.grammedia.fbsdkconsumer.FBSDKActivity;
import com.grammedia.fbsdkconsumer.data.CPConnectionService;
import com.grammedia.fbsdkconsumer.data.CPMainParameters;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends FBSDKActivity {
    private final String CPToken = "f5f49293fb634246b546ffc80d09cdc0";
    private final String CPGroupName = "Cp test";
    private final String CPName = "moirizw";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setTitle("Check your Console Now 2");
        CPConnectionService.getInstance().initWithOptions(CPToken, CPGroupName, CPName, "", "");
        CPConnectionService.getInstance().askforregistration();
    }

    @OnClick(R.id.btn_do_post_token)
    public void postTokenBtnPressed(View view) {
        CPConnectionService.getInstance().resetcurSessionFCMTokenPosted("no");
        CPConnectionService.getInstance().postFCMTokenToCP(CPMainParameters.getInstance().fcmToken);
    }

    @OnClick(R.id.btn_page_view)
    public void pageViewBtnPressed(View view) {
        String utmdt = "my simple Page View";
        String utmp = "/myandroidapp/category/subcategory/mypage/";
        CPConnectionService.getInstance().pageView(utmdt, utmp);
    }

    @OnClick(R.id.btn_product_view)
    public void productViewBtnPressed(View view) {
        String pName = "το προϊον μου";
        String pSku = "12345-abcd";
        String utmp = "/myandroidapp/myprodpage/";
        CPConnectionService.getInstance().productView(pName, pSku, utmp);
    }

    @OnClick(R.id.btn_add_2_cart)
    public void addCartBtnPressed(View view) {
        String pName = "το προϊον μου";
        String pSku = "12345-abcd";
        int pQty = 6;
        double pUnitPrice = 12.36;
        String utmp = "/myandroidapp/myprodpage/";
        String pimg = "";
        CPConnectionService.getInstance().add2cart(pName, pSku, pQty, pUnitPrice, pimg, utmp);
    }

    @OnClick(R.id.btn_remove_from_cart)
    public void removeCartBtnPressed(View view) {
        String pName = "το προϊον μου";
        String pSku = "12345-abcd";
        int pQty = 2;
        double pUnitPrice = 12.36;
        String utmp = "/myandroidapp/myprodpage/";
        String pimg = "";
        CPConnectionService.getInstance().removefromcart(pName, pSku, pQty, pUnitPrice, pimg, utmp);
    }

    @OnClick(R.id.btn_place_order)
    public void placeOrderBtnPressed(View view) {
        String utmp = "";
        CPConnectionService.getInstance().setOrderData("oid-0008", 36.36);
        CPConnectionService.getInstance().addOrderItem("81", "Four Punch Man T-Shirt", 2, 149);
        CPConnectionService.getInstance().addOrderItem("49", "Sassy, loose fit dual tone dress", 5, 190);
        CPConnectionService.getInstance().postOrder(utmp);
    }

    @OnClick(R.id.btn_post_cart)
    public void postCartBtnPressed(View view) {
        CPConnectionService.getInstance().postCart();
    }

    @OnClick(R.id.btn_post_contact_email)
    public void postContactEmailBtnPressed(View view) {
        String cp_curEmail = "test@test.gr";
        String utmp = "/myandroidapp/category/subcategory/mypage/";
        CPConnectionService.getInstance().setContactMail(cp_curEmail,utmp);
    }

}


