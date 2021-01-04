package com.grammedia.fbsdkconsumer;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtils {
    private static final String PREF_NAME = "fbconsumer";

    public static final String kUDFCMToken = "Firbase_Token";

    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor getEditor(Context context) {
        return getPrefs(context).edit();
    }
}
