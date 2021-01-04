package com.grammedia.fbsdkconsumer.data;

import android.app.Notification;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.grammedia.fbsdkconsumer.PrefUtils;
import com.jaredrummler.android.device.DeviceName;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CPConnectionService {

    private Context context;
    public static final String TAG = "FBCONSUMER_LOG";

    public CPConnectionService(Context context) {
        this.context = context;
    }

    public void initWithOptions(String appscptoken, String appscpgroup, String appscpname, String doPostCartScreenshot, String isFIRAlreadyInc) {
        if (!isFIRAlreadyInc.isEmpty() && !isFIRAlreadyInc.equals("no")) {
            CPMainParameters.getInstance().isFIRAlreadyInc = "yes";
        }

        if (!doPostCartScreenshot.isEmpty()) {
            CPMainParameters.getInstance().postCartScreenshot = "yes";
            CPMainParameters.getInstance().isCartSelfContained = "yes";
        }

        printMsg(String.format("postCartScreenshot:%s \n isCartSelfContained:%s", CPMainParameters.getInstance().postCartScreenshot, CPMainParameters.getInstance().isCartSelfContained));

        CPMainParameters.getInstance().cptoken = appscptoken;
        PrefUtils.getEditor(context).putString("cptoken", appscptoken).commit();
        printMsg(String.format("cptoken:%s", CPMainParameters.getInstance().cptoken));

        CPMainParameters.getInstance().isPushActive = NotificationManagerCompat.from(context).areNotificationsEnabled();
        printMsg(String.format("isPushActive:%b",NotificationManagerCompat.from(context).areNotificationsEnabled()));

        if (!PrefUtils.getPrefs(context).getString("notsAllowed", "").isEmpty()) {
            CPMainParameters.getInstance().notsAllowed = PrefUtils.getPrefs(context).getString("notsAllowed","");
        }
        printMsg(String.format("notsAllowed:%s", CPMainParameters.getInstance().notsAllowed));

        DeviceName.init(context);
        //CPMainParameters.getInstance().systemVersion = "Android " + Build.VERSION.SDK_INT;
        CPMainParameters.getInstance().systemVersion = "Android " + Build.VERSION.RELEASE + " " + Build.VERSION.SDK_INT;
        printMsg(String.format("systemVersion:%s", CPMainParameters.getInstance().systemVersion));

        //CPMainParameters.getInstance().model = DeviceName.getDeviceInfo(context).model;
        CPMainParameters.getInstance().model = getDeviceName();

        CPMainParameters.getInstance().bundleID = context.getPackageName();
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        CPMainParameters.getInstance().identifierForVendor = androidId;

        printMsg(String.format("model:%s \n bundleID:%s \n identifierForVendor:%s",
                CPMainParameters.getInstance().model,
                CPMainParameters.getInstance().bundleID,
                CPMainParameters.getInstance().identifierForVendor));

        CPMainParameters.getInstance().ci = CPMainParameters.getInstance().identifierForVendor + "-" + appscpname;
        printMsg(String.format("ci:%s", CPMainParameters.getInstance().ci));
        printMsg(String.format("utmsr:%s", CPMainParameters.getInstance().utmsr));

        CPMainParameters.getInstance().langStr = Locale.getDefault().getLanguage();
        CPMainParameters.getInstance().pre = Locale.getDefault().getLanguage();
        printMsg(String.format("langStr:%s \n pre:%s", CPMainParameters.getInstance().langStr, CPMainParameters.getInstance().pre));

        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String version = pInfo.versionName;
            CPMainParameters.getInstance().bundleVersion = version;
        } catch (PackageManager.NameNotFoundException e) {
            CPMainParameters.getInstance().bundleVersion = "1.0.0";
            e.printStackTrace();
        }
        PrefUtils.getEditor(context).putString("cp_ver", CPMainParameters.getInstance().bundleVersion).commit();
        CPMainParameters.getInstance().group = appscpgroup;
        printMsg(String.format("group:%s", CPMainParameters.getInstance().group));

        if (!PrefUtils.getPrefs(context).getString("fcmtokenposted", "").isEmpty()) {
            String isFcmTokenPosted = PrefUtils.getPrefs(context).getString("fcmtokenposted", "");
            CPMainParameters.getInstance().curSessionFCMTokenPosted = isFcmTokenPosted;
        }
        printMsg(String.format("curSessionFCMTokenPosted:%s", CPMainParameters.getInstance().curSessionFCMTokenPosted));

        if (!PrefUtils.getPrefs(context).getString("oldcuem", "").isEmpty()) {
            CPMainParameters.getInstance().oldcuem = PrefUtils.getPrefs(context).getString("oldcuem", "");
            if (CPMainParameters.getInstance().cuem.isEmpty()) {
                CPMainParameters.getInstance().cuem = CPMainParameters.getInstance().oldcuem;
            }
        }
        printMsg(String.format("cuem:%s", CPMainParameters.getInstance().cuem));

        CPMainParameters.getInstance().plistFileName = "google-services.json";
        PrefUtils.getEditor(context).putString("cpSubmitDataEndpoint", CPMainParameters.getInstance().cpSubmitDataEndpoint).commit();
        printMsg(PrefUtils.getPrefs(context).getString("cp_ver", ""));
    }

    public void toggleDebugMode() {
        if (CPMainParameters.getInstance().debugMode.equals("on")) {
            printMsg("Deactivating debugMode");
            CPMainParameters.getInstance().debugMode = "off";
        } else {
            CPMainParameters.getInstance().debugMode = "on";
            printMsg("debugMode Activated");
        }
        printMsg(String.format("debugMode:%s", CPMainParameters.getInstance().debugMode));
    }

    public static void printMsg(String message) {
        if (CPMainParameters.getInstance().debugMode.equals("on")) {
            Log.d(TAG, message);
        }
    }

    public void resetcurSessionFCMTokenPosted(String newValue) {
        printMsg(String.format("curSessionFCMTokenPosted:%s", CPMainParameters.getInstance().curSessionFCMTokenPosted));
        CPMainParameters.getInstance().curSessionFCMTokenPosted = newValue;
        printMsg(String.format("curSessionFCMTokenPosted:%s", CPMainParameters.getInstance().curSessionFCMTokenPosted));
    }

    public void resetcurSessionCi(String newValue) {
        printMsg(String.format("ci:%s", CPMainParameters.getInstance().ci));
        CPMainParameters.getInstance().ci = newValue;
        printMsg(String.format("ci:%s", CPMainParameters.getInstance().ci));
    }

    public void setContactMail(String eMail, String utmp) {
        String putmp = utmp;
        if (putmp.isEmpty()) {
            putmp = "/" + CPMainParameters.getInstance().bundleID + "/android/registerMail/";
        }
        printMsg(String.format("putmp:%s", putmp));

        if (!eMail.isEmpty()) {
            CPMainParameters.getInstance().cuem = eMail;
            printMsg(String.format("cuem:%s", CPMainParameters.getInstance().cuem));

            if (!CPMainParameters.getInstance().cuem.equals(CPMainParameters.getInstance().oldcuem)) {
                printMsg(String.format("oldcuem:%s", CPMainParameters.getInstance().oldcuem));
                CPMainParameters.getInstance().oldcuem = eMail;
                printMsg(String.format("oldcuem:%s", CPMainParameters.getInstance().oldcuem));

                PrefUtils.getEditor(context).putString("oldcuem", eMail).commit();
                String parameters = String.format("action=cp_registerEmail&cptoken=%s&cuem=%s&ci=%s&device_type=android&device_id=%s&device_osver=%s&bundle_id=%s&app_version=%s&utmsr=%s&reg_page=%s&cp_ver=%s&cp_verClient=%s&language_code=%s",
                        CPMainParameters.getInstance().cptoken,
                        CPMainParameters.getInstance().cuem,
                        CPMainParameters.getInstance().ci,
                        CPMainParameters.getInstance().model,
                        CPMainParameters.getInstance().systemVersion,
                        CPMainParameters.getInstance().bundleID,
                        CPMainParameters.getInstance().bundleVersion,
                        CPMainParameters.getInstance().utmsr,
                        putmp,
                        CPMainParameters.getInstance().cp_ver,
                        CPMainParameters.getInstance().cp_verClient,
                        CPMainParameters.getInstance().langStr
                        );
                printMsg(String.format("setContactMail parameters:%s", parameters));
                new DoPostRequest().execute(CPMainParameters.getInstance().cpSubmitDataEndpoint, parameters);
            }
        }
    }

    public void pageView(String utmdt, String utmp) {
        String putmp = utmp;
        if (putmp.isEmpty()) {
            putmp = "/" + CPMainParameters.getInstance().bundleID + "/android/";
        }
        printMsg(String.format("putmp:%s", putmp));

        String parameters = String.format("action=page&cptoken=%s&utmipc=&utmipn=&cf1=&cf2=&cf3=&utmtid=&utmtto=&utmp=%s&utmdt=%s&cuem=%s&device_type=android&device_id=%s&device_osver=%s&bundle_id=%s&cp_ver=%s&cp_verClient=%s&utmsr=%s&language_code=%s&ci=%s",
                CPMainParameters.getInstance().cptoken,
                putmp,
                utmdt,
                CPMainParameters.getInstance().cuem,
                CPMainParameters.getInstance().model,
                CPMainParameters.getInstance().systemVersion,
                CPMainParameters.getInstance().bundleID,
                CPMainParameters.getInstance().cp_ver,
                CPMainParameters.getInstance().cp_verClient,
                CPMainParameters.getInstance().utmsr,
                CPMainParameters.getInstance().langStr,
                CPMainParameters.getInstance().ci
        );
        printMsg(String.format("pageView parameters:%s", parameters));
        new DoPostRequest().execute(CPMainParameters.getInstance().cpSubmitDataEndpoint, parameters);
    }

    public void productView(String pName, String pSku, String utmp) {
        String putmp = utmp;
        if (putmp.isEmpty()) {
            putmp = "/" + CPMainParameters.getInstance().bundleID + "/android/" + pName;
        }
        printMsg(String.format("putmp:%s", putmp));

        String parameters = String.format("action=page&cptoken=%s&utmipc=%s&utmipn=%s&cf1=&cf2=&cf3=&utmtid=&utmtto=&utmp=%s&utmdt=%s&cuem=%s&device_type=android&device_id=%s&device_osver=%s&bundle_id=%s&cp_ver=%s&cp_verClient=%s&utmsr=%s&language_code=%s&ci=%s",
                CPMainParameters.getInstance().cptoken,
                pSku,
                pName,
                putmp,
                pName,
                CPMainParameters.getInstance().cuem,
                CPMainParameters.getInstance().model,
                CPMainParameters.getInstance().systemVersion,
                CPMainParameters.getInstance().bundleID,
                CPMainParameters.getInstance().cp_ver,
                CPMainParameters.getInstance().cp_verClient,
                CPMainParameters.getInstance().utmsr,
                CPMainParameters.getInstance().langStr,
                CPMainParameters.getInstance().ci
        );
        printMsg(String.format("productView parameters:%s", parameters));
        new DoPostRequest().execute(CPMainParameters.getInstance().cpSubmitDataEndpoint, parameters);
    }

    public void add2cart(String pName, String pSku, int pQty, double pUnitPrice, String pImgURL, String utmp) {
        String putmp = utmp;
        if (putmp.isEmpty()) {
            putmp = "/" + CPMainParameters.getInstance().bundleID + "/android/" + pName;
        }
        printMsg(String.format("putmp:%s", putmp));

        String parameters = String.format("action=event&cptoken=%s&utmipc=%s&utmipn=%s&cf1=add2cart&cf2=%s&cf3=%s&utmtid=&utmtto=&utmp=%s&utmdt=%s&cuem=%s&device_type=android&device_id=%s&device_osver=%s&bundle_id=%s&cp_ver=%s&cp_verClient=%s&utmsr=%s&language_code=%s&ci=%s",
                CPMainParameters.getInstance().cptoken,
                pSku,
                pName,
                pQty,
                pUnitPrice,
                putmp,
                pName,
                CPMainParameters.getInstance().cuem,
                CPMainParameters.getInstance().model,
                CPMainParameters.getInstance().systemVersion,
                CPMainParameters.getInstance().bundleID,
                CPMainParameters.getInstance().cp_ver,
                CPMainParameters.getInstance().cp_verClient,
                CPMainParameters.getInstance().utmsr,
                CPMainParameters.getInstance().langStr,
                CPMainParameters.getInstance().ci
        );
        printMsg(String.format("add2cart parameters:%s", parameters));
        new DoPostRequest().execute(CPMainParameters.getInstance().cpSubmitDataEndpoint, parameters);

        CPMainParameters.CPCartItem curCartItem = new CPMainParameters.CPCartItem(pSku, pName, pQty, pUnitPrice, putmp, pImgURL);
        if (!CPMainParameters.getInstance().postCartScreenshot.equals("no")) {
            alterCart(curCartItem, "add");
        }
    }

    public void removefromcart(String pName, String pSku, int pQty, double pUnitPrice, String pImgURL, String utmp) {
        String putmp = utmp;
        if (putmp.isEmpty()) {
            putmp = "/" + CPMainParameters.getInstance().bundleID + "/android/" + pName;
        }
        printMsg(String.format("putmp:%s", putmp));

        String parameters = String.format("action=event&cptoken=%s&utmipc=%s&utmipn=%s&cf1=removefromcart&cf2=%s&cf3=%s&utmtid=&utmtto=&utmp=%s&utmdt=%s&cuem=%s&device_type=android&device_id=%s&device_osver=%s&bundle_id=%s&cp_ver=%s&cp_verClient=%s&utmsr=%s&language_code=%s&ci=%s",
                CPMainParameters.getInstance().cptoken,
                pSku,
                pName,
                pQty,
                pUnitPrice,
                putmp,
                pName,
                CPMainParameters.getInstance().cuem,
                CPMainParameters.getInstance().model,
                CPMainParameters.getInstance().systemVersion,
                CPMainParameters.getInstance().bundleID,
                CPMainParameters.getInstance().cp_ver,
                CPMainParameters.getInstance().cp_verClient,
                CPMainParameters.getInstance().utmsr,
                CPMainParameters.getInstance().langStr,
                CPMainParameters.getInstance().ci
        );
        printMsg(String.format("removefromcart parameters:%s", parameters));
        new DoPostRequest().execute(CPMainParameters.getInstance().cpSubmitDataEndpoint, parameters);

        CPMainParameters.CPCartItem curCartItem = new CPMainParameters.CPCartItem(pSku, pName, pQty, pUnitPrice, putmp, pImgURL);
        alterCart(curCartItem, "remove");
    }

    public void setOrderData(String oId, double oValue) {
        CPMainParameters.getInstance().curOrderData.utmtid = oId;
        printMsg(String.format("utmtid:%s", CPMainParameters.getInstance().curOrderData.utmtid));

        CPMainParameters.getInstance().curOrderData.utmtto = Math.ceil(oValue*100) / 100;
        printMsg(String.format("utmtto:%s", CPMainParameters.getInstance().curOrderData.utmtto));

        CPMainParameters.getInstance().curOrderData.items.clear();
    }

    public void addOrderItem(String sku, String name, int quant, double price) {
        CPMainParameters.CPOrderItem newOrderItem = new CPMainParameters.CPOrderItem();
        newOrderItem.sku = sku;
        newOrderItem.name = name;
        newOrderItem.qty = quant;
        newOrderItem.unitPrice = Math.ceil(price*100)/100;

        printMsg(String.format("orderItems Count:%s", CPMainParameters.getInstance().curOrderData.items.size()));
        CPMainParameters.getInstance().curOrderData.items.add(newOrderItem);
        printMsg(String.format("orderItems Count:%s", CPMainParameters.getInstance().curOrderData.items.size()));
    }

    public void postOrder(String utmp) {
        String putmp = utmp;
        if (putmp.isEmpty()) {
            putmp = "/" + CPMainParameters.getInstance().bundleID + "/android/ordercomplete/" + CPMainParameters.getInstance().curOrderData.utmtid;
        }
        printMsg(String.format("putmp:%s", putmp));
        String json = new Gson().toJson(CPMainParameters.getInstance().curOrderData.items);
        String parameters = String.format("action=event&cptoken=%s&utmipc=&utmipn=&cf1=order&cf2=&cf3=&cart=%s&utmtid=%s&utmtto=%s&utmp=%s&utmdt=%s&cuem=%s&device_type=android&device_id=%s&device_osver=%s&bundle_id=%s&cp_ver=%s&cp_verClient=%s&utmsr=%s&language_code=%s&ci=%s",
                CPMainParameters.getInstance().cptoken,
                json != null ? json : "",
                CPMainParameters.getInstance().curOrderData.utmtid,
                CPMainParameters.getInstance().curOrderData.utmtto,
                putmp,
                CPMainParameters.getInstance().curOrderData.utmtid,
                CPMainParameters.getInstance().cuem,
                CPMainParameters.getInstance().model,
                CPMainParameters.getInstance().systemVersion,
                CPMainParameters.getInstance().bundleID,
                CPMainParameters.getInstance().cp_ver,
                CPMainParameters.getInstance().cp_verClient,
                CPMainParameters.getInstance().utmsr,
                CPMainParameters.getInstance().langStr,
                CPMainParameters.getInstance().ci
        );
        printMsg(String.format("postOrder parameters:%s", parameters));
        new DoPostRequest().execute(CPMainParameters.getInstance().cpSubmitDataEndpoint, parameters);
        emptyCart();
    }

    public void alterCart(CPMainParameters.CPCartItem curCartItem, String cartAction) {
        List<CPMainParameters.CPCartItem> curCartScreenshot = CPMainParameters.getInstance().curCartItems;
        int cartItemIndex = -1;
        int cartItemsCounter = -1;
        for (CPMainParameters.CPCartItem cartItem : curCartScreenshot) {
            cartItemsCounter++;
            if (cartItem.sku.equals(curCartItem.sku)) {
                cartItemIndex = cartItemsCounter;
            }
        }
        printMsg(String.format("alertCart cartItemIndex:%s", cartItemIndex));

        if (cartItemIndex > -1) {
            if (cartAction.equals("add")) {
                printMsg(String.format("alertCart old Qty:%s)", curCartScreenshot.get(cartItemIndex).qty));
                curCartScreenshot.get(cartItemIndex).qty = curCartScreenshot.get(cartItemIndex).qty + curCartItem.qty;
                printMsg(String.format("alertCart old Qty:%s)", curCartScreenshot.get(cartItemIndex).qty));
            } else if (cartAction.equals("remove")) {
                if (curCartItem.qty >= curCartScreenshot.get(cartItemIndex).qty) {
                    printMsg(String.format("curCartScreenshot Count:%s", curCartScreenshot.size()));
                    curCartScreenshot.remove(cartItemIndex);
                    printMsg(String.format("curCartScreenshot Count:%s", curCartScreenshot.size()));
                } else {
                    printMsg(String.format("alertCart old Qty:%s)", curCartScreenshot.get(cartItemIndex).qty));
                    curCartScreenshot.get(cartItemIndex).qty = curCartScreenshot.get(cartItemIndex).qty - curCartItem.qty;
                    printMsg(String.format("alertCart old Qty:%s)", curCartScreenshot.get(cartItemIndex).qty));
                }
            }
        } else {
            if (cartAction.equals("add")) {
                curCartScreenshot.add(curCartItem);
            }
        }
        CPMainParameters.getInstance().curCartItems = curCartScreenshot;
        String json = new Gson().toJson(CPMainParameters.getInstance().curCartItems);
        printMsg(String.format("curCartScreenshot json:%s)", json != null ? json : ""));

        postCartToCP(json, CPMainParameters.getInstance().isCartSelfContained);
    }

    public void postCart() {
        String json = new Gson().toJson(CPMainParameters.getInstance().curCartItems);
        printMsg(String.format("postCart json:%s)", json != null ? json : ""));

        postCartToCP(json, CPMainParameters.getInstance().isCartSelfContained);
    }

    public void emptyCart() {
        CPMainParameters.getInstance().curCartItems.clear();
        String json = new Gson().toJson(CPMainParameters.getInstance().curCartItems);
        printMsg(String.format("emptyCart json:%s)", json != null ? json : ""));

        postCartToCP(json, CPMainParameters.getInstance().isCartSelfContained);
    }

    public void postCartToCP(String cartJson, String isContained) {
        printMsg(String.format("postCartToCP cartJson:%s \n isContained:%s", cartJson, isContained));

        String parameters = String.format("action=cartscreenshot&isselfcontained=%s&cartItems=%s&cptoken=%s&cuem=%s&device_type=android&device_id=%s&device_osver=%s&bundle_id=%s&cp_ver=%s&cp_verClient=%s&utmsr=%s&language_code=%s&ci=%s",
                isContained,
                cartJson,
                CPMainParameters.getInstance().cptoken,
                CPMainParameters.getInstance().cuem,
                CPMainParameters.getInstance().model,
                CPMainParameters.getInstance().systemVersion,
                CPMainParameters.getInstance().bundleID,
                CPMainParameters.getInstance().cp_ver,
                CPMainParameters.getInstance().cp_verClient,
                CPMainParameters.getInstance().utmsr,
                CPMainParameters.getInstance().langStr,
                CPMainParameters.getInstance().ci
        );
        printMsg(String.format("postCartToCP parameters:%s", parameters));
        printMsg(String.format("postCartToCP postCartScreenshot:%s", CPMainParameters.getInstance().postCartScreenshot));
        if (!CPMainParameters.getInstance().postCartScreenshot.equals("no")) {
            new DoPostRequest().execute(CPMainParameters.getInstance().cpSubmitDataEndpoint, parameters);
        }
    }

    public String getGcmMessageIDKey() {
        return "gcm.message_id";
    }

    public void askforregistration() {
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            CPMainParameters.getInstance().notsAllowed = "yes";
            PrefUtils.getEditor(context).putString("notsAllowed", CPMainParameters.getInstance().notsAllowed).commit();
            if (PrefUtils.getPrefs(context).getString("fcmToken", "").isEmpty()) {
                getRegistrationToken();
            } else {
                CPMainParameters.getInstance().fcmToken = PrefUtils.getPrefs(context).getString("fcmToken", "");
            }
        } else {
            CPMainParameters.getInstance().notsAllowed = "no";
            PrefUtils.getEditor(context).putString("notsAllowed", CPMainParameters.getInstance().notsAllowed).commit();
            postDeniedFCMToCP();
        }
        printMsg(String.format("askforregistration notsAllowed:%s", CPMainParameters.getInstance().notsAllowed));
    }


    public void getRegistrationToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            printMsg(String.format("getRegistrationToken error:Error!!!!! fetching remote instance Id: %s", task.getException()));
                            return;
                        }
                        // Get new Instance ID token
                        String fcmToken = task.getResult().getToken();
                        printMsg(String.format("getRegistrationToken fcmToken:%s", fcmToken));
                        postFCMTokenToCP(fcmToken);
                        CPMainParameters.getInstance().isPushActive = true;
                        CPMainParameters.getInstance().fcmToken = fcmToken;
                        PrefUtils.getEditor(context).putString("fcmToken", fcmToken).commit();
                    }
                });
    }

    public void getRefreshedRegistrationToken(Notification notification) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            printMsg(String.format("getRefreshedRegistrationToken error:Error!!!!! fetching remote instance Id: %s", task.getException()));
                            return;
                        }
                        // Get new Instance ID token
                        String fcmToken = task.getResult().getToken();
                        printMsg(String.format("getRefreshedRegistrationToken fcmToken:%s", fcmToken));
                        postFCMTokenToCP(fcmToken);
                    }
                });
    }

    public void didRecieveNotificationExtensionRequest(Map<String, String> userInfo, boolean appForeground) {
        String pstate = "";
        if (appForeground){
            pstate = "active";
        } else {
            pstate = "background";
        }
        printMsg(String.format("didRecieveNotificationExtensionRequest pstate:%s", pstate));

        String gcmMessageIDKey = getGcmMessageIDKey();
        if (userInfo.get(gcmMessageIDKey) != null) {
            printMsg(String.format("didRecieveNotificationExtensionRequest Message ID2:%s", userInfo.get(gcmMessageIDKey)));
        }

        printMsg(String.format("didRecieveNotificationExtensionRequest full Message:%s", userInfo));

        String showalert = CPMainParameters.getInstance().handleInAppNots;
        printMsg(String.format("didRecieveNotificationExtensionRequest showalert:%s", showalert));

        String cp_cpn = "";
        if (userInfo.get("gcm.notification.cp_cpn") != null) {
            cp_cpn = userInfo.get("gcm.notification.cp_cpn");
        } else {
            if (userInfo.get("cp_cpn") != null) {
                cp_cpn = userInfo.get("cp_cpn");
            }
        }
        printMsg(String.format("didRecieveNotificationExtensionRequest cp_cpn:%s", cp_cpn));

        String cp_uinc = "";
        if (userInfo.get("gcm.notification.cp_uinc") != null) {
            cp_uinc = userInfo.get("gcm.notification.cp_uinc");
        } else {
            if (userInfo.get("cp_uinc") != null) {
                cp_uinc = userInfo.get("cp_uinc");
            }
        }
        printMsg(String.format("didRecieveNotificationExtensionRequest cp_uinc:%s", cp_uinc));

        String cp_d = "";
        if (userInfo.get("gcm.notification.cp_device") != null) {
            cp_uinc = userInfo.get("gcm.notification.cp_device");
        } else {
            if (userInfo.get("cp_device") != null) {
                cp_uinc = userInfo.get("cp_device");
            }
        }
        printMsg(String.format("didRecieveNotificationExtensionRequest cp_d:%s", cp_d));

        postReceivedNotification(cp_cpn, cp_uinc, cp_d, pstate);
    }

    public void postReceivedNotification(String cp_cpn, String cp_uinc, String cp_d, String pstate) {
        String cp_ver = CPMainParameters.getInstance().cp_ver;
        String cp_token = CPMainParameters.getInstance().cptoken;

        String parameters = String.format("action=cp_NotReceived&cptoken=%s&d_type=click&p_state=%s&cpn=%s&uinc=%s&cp_d=%s&swver=%s",
                cp_token,
                pstate,
                cp_cpn,
                cp_uinc,
                cp_d,
                cp_ver
        );
        printMsg(String.format("postReceivedNotification parameters:%s", parameters));
        new DoPostRequest().execute(CPMainParameters.getInstance().cpSubmitDataEndpoint, parameters);
    }

    public void postFCMTokenToCP(String fcmToken) {
        printMsg(String.format("postFCMTokenToCP fcmToken:%s", fcmToken));
        if (!fcmToken.isEmpty()) {
            String oldfcmtoken = "";
            if (!PrefUtils.getPrefs(context).getString("oldfcmtoken", "").isEmpty()) {
                oldfcmtoken = PrefUtils.getPrefs(context).getString("oldfcmtoken", "");
            }
            printMsg(String.format("postFCMTokenToCP oldfcmtoken:%s", oldfcmtoken));

            String isFcmUpdate = "no";
            if (!oldfcmtoken.isEmpty() && !fcmToken.equals(oldfcmtoken)) {
                isFcmUpdate = "yes";
                CPMainParameters.getInstance().curSessionFCMTokenPosted = "no";
            }
            printMsg(String.format("postFCMTokenToCP isFcmUpdate:%s", isFcmUpdate));

            if (isFcmUpdate.equals("no")) {
                oldfcmtoken = "";
            }
            printMsg(String.format("postFCMTokenToCP oldfcmtoken:%s", oldfcmtoken));

            String parameters = String.format("cptoken=%s&fcmToken=%s&oldfcmToken=%s&isupdate=%s&cuem=%s&ci=%s&device_type=android&device_id=%s&device_osver=%s&app_version=%s&utmsr=%s&reg_page=%s_%s&cp_ver=%s&cp_verClient=%s&language_code=%s&group=%s",
                    CPMainParameters.getInstance().cptoken,
                    fcmToken,
                    oldfcmtoken,
                    isFcmUpdate,
                    CPMainParameters.getInstance().cuem,
                    CPMainParameters.getInstance().ci,
                    CPMainParameters.getInstance().model,
                    CPMainParameters.getInstance().systemVersion,
                    CPMainParameters.getInstance().bundleVersion,
                    CPMainParameters.getInstance().utmsr,
                    CPMainParameters.getInstance().model,
                    CPMainParameters.getInstance().systemVersion,
                    CPMainParameters.getInstance().cp_ver,
                    CPMainParameters.getInstance().cp_verClient,
                    CPMainParameters.getInstance().langStr,
                    CPMainParameters.getInstance().group
            );
            printMsg(String.format("postFCMTokenToCP parameters:%s", parameters));

            printMsg(String.format("postFCMTokenToCP isPushActive:%s", CPMainParameters.getInstance().isPushActive));
            if (CPMainParameters.getInstance().isPushActive) {
                printMsg(String.format("postFCMTokenToCP notsAllowed:%s", CPMainParameters.getInstance().notsAllowed));

                if (!CPMainParameters.getInstance().notsAllowed.equals("no")) {
                    printMsg(String.format("postFCMTokenToCP curSessionFCMTokenPosted:%s, fcmToken:%s, oldfcmtoken:%s", CPMainParameters.getInstance().curSessionFCMTokenPosted, fcmToken, oldfcmtoken));

                    if (CPMainParameters.getInstance().curSessionFCMTokenPosted.equals("no") || (!fcmToken.equals(oldfcmtoken) && !oldfcmtoken.isEmpty())) {
                        new DoPostRequest().execute(CPMainParameters.getInstance().cpSubmitDataEndpoint, parameters);
                        PrefUtils.getEditor(context).putString("oldfcmtoken", fcmToken).commit();
                        PrefUtils.getEditor(context).putString("fcmtokenposted", "yes").commit();
                        PrefUtils.getEditor(context).putString("postedDeniedFCM", "no").commit();
                        CPMainParameters.getInstance().curSessionFCMTokenPosted = "yes";
                        printMsg(String.format("postFCMTokenToCP curSessionFCMTokenPosted:%s", CPMainParameters.getInstance().curSessionFCMTokenPosted));
                    }
                }
            }
        }
    }

    public void postDeniedFCMToCP() {
        String putmp = "/"+CPMainParameters.getInstance().bundleID+"/android/";
        printMsg(String.format("postDeniedFCMToCP putmp:%s", putmp));

        String parameters = String.format("cptoken=%s&fcmToken=&oldfcmToken=&isupdate=denied&cuem=%s&ci=%s&device_type=android&device_id=%s&device_osver=%s&app_version=%s&utmsr=%s&reg_page=%s&cp_ver=%s&cp_verClient=%s&language_code=%s&group=%s",
                CPMainParameters.getInstance().cptoken,
                CPMainParameters.getInstance().cuem,
                CPMainParameters.getInstance().ci,
                CPMainParameters.getInstance().model,
                CPMainParameters.getInstance().systemVersion,
                CPMainParameters.getInstance().bundleVersion,
                CPMainParameters.getInstance().utmsr,
                putmp,
                CPMainParameters.getInstance().cp_ver,
                CPMainParameters.getInstance().cp_verClient,
                CPMainParameters.getInstance().langStr,
                CPMainParameters.getInstance().group
        );
        printMsg(String.format("postDeniedFCMToCP parameters:%s", parameters));

        if (!PrefUtils.getPrefs(context).getString("postedDeniedFCM", "").isEmpty()) {
            PrefUtils.getEditor(context).putString("postedDeniedFCM", "yes").commit();
            PrefUtils.getEditor(context).putString("fcmtokenposted", "no").commit();
            new DoPostRequest().execute(CPMainParameters.getInstance().cpSubmitDataEndpoint, parameters);
        }
    }




    private static String getScreenResolution(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        return width + "x" + height;
    }

    protected static class DoPostRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                connection.setDoOutput(true);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                writer.write(params[1]);
                writer.close();
                connection.connect();

                InputStream is = null;

                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    is = connection.getInputStream();// is is inputstream
                } else {
                    is = connection.getErrorStream();
                }
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            is, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    String response = sb.toString();
                    printMsg(response);
                } catch (Exception e) {
                    Log.e("Buffer Error", "Error converting result " + e.toString());
                }

            } catch (Exception e) {
                Log.d(TAG, e.toString() + "");
            }

            return null;
        }
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

}
