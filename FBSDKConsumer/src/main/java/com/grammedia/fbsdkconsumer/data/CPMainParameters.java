package com.grammedia.fbsdkconsumer.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CPMainParameters {
    public static CPMainParameters instance;

    public static synchronized CPMainParameters getInstance() {
        if (instance == null) {
            instance = new CPMainParameters();
        }
        return instance;
    }

    public String cpSubmitDataEndpoint = "https://plato.contactpigeon.com/bi/atlantis/various/0587d93972144bd394f77eca8e2cecdd_cordova/";
    public String cptoken = "";
    public String fcmToken = "";
    public String cuem = "";
    public String oldcuem = "";
    public String systemVersion = "";
    public String model = "";
    public String bundleID = "";
    public String identifierForVendor = "";
    public String ci = "";
    public String utmsr = "";
    public String cp_ver = "1.0.0";
    public String cp_verClient = "1.0.0";
    public String langStr = "";
    public String pre = "";
    public String bundleVersion = "";
    public String group = "";
    public String curSessionFCMTokenPosted = "no";
    public String curSessionEmailPosted = "";
    public boolean isPushActive = false;
    public String plistFileName = "";
    public String notsAllowed = "no";
    public String isCartSelfContained = "no";
    public String postCartScreenshot = "yes";
    public String handleInAppNots = "yes";
    public String debugMode = "on";
    public String isFIRAlreadyInc = "no";
    @Expose
    @SerializedName("curOrderData")
    public CPOrderData curOrderData = new CPOrderData();
    public List<CPCartItem> curCartItems = new ArrayList<>();


    public static class CPOrderData {
        public String utmtid = "";
        public double utmtto = 0.0;
        @Expose
        @SerializedName("items")
        public List<CPOrderItem> items = new ArrayList<>();
    }

    public static class CPOrderItem {
        @Expose
        @SerializedName("sku")
        public String sku = "";
        @Expose
        @SerializedName("name")
        public String name = "";
        @Expose
        @SerializedName("qty")
        public int qty = 0;
        @Expose
        @SerializedName("unitPrice")
        public double unitPrice = 0.0;
    }

    public static class CPCartItem {
        public String sku = "";
        public String name = "";
        public int qty = 0;
        public double unitPrice = 0.0;
        public String link = "";
        public String image = "";

        public CPCartItem() {
        }

        public CPCartItem(String sku, String name, int qty, double unitPrice, String link, String image) {
            this.sku = sku;
            this.name = name;
            this.qty = qty;
            this.unitPrice = unitPrice;
            this.link = link;
            this.image = image;
        }
    }

}
