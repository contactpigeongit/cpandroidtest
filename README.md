# ContactPigeon Android SDK Library

Simple library for testing API functions

### Download

Add it in your root build.gradle at the end of repositories:

```
allprojects {
    repositories {
	...
	maven {
            url "https://jitpack.io"
            credentials { username authToken }
        }
    }
}
```

Download via Gradle:

```gradle
implementation 'com.github.maximtrapps:android:v0.1.0'
```
or Maven:
```xml
<dependency>
  <groupId>com.github.maximtrapps</groupId>
  <artifactId>android</artifactId>
  <version>v0.1.0</version>
</dependency>
```
### Build

Change the following [library](FBSDKConsumer) code. The necessary class for adding new API functions is in the file - CPConnectionService.java.
```java
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
```
After adding the code, upload its new version to the repository and create a new release. Next, go to the site https://jitpack.io and publish the created release for all users.

Take a look at the [sample project](sample) for more information.

### License 

```
Copyright 2020 contactpigeon.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```


[sample]: <https://github.com/maximtrapps/android/tree/master/sample>
[FBSDKConsumer]: <https://github.com/maximtrapps/android/tree/master/FBSDKConsumer>
