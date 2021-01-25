package com.grammedia.fbconsumer;

import com.grammedia.fbsdkconsumer.FBSDKApplication;
import com.grammedia.fbsdkconsumer.data.CPConnectionService;

public class FBAcplication extends FBSDKApplication {   // extend application class our library
    @Override
    public void onCreate() {
        // One place initizializeion library
        CPConnectionService.init(getApplicationContext());
        super.onCreate();
    }
}
