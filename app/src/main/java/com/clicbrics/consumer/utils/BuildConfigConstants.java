package com.clicbrics.consumer.utils;


import android.content.Context;
import android.util.Base64;

import com.clicbrics.consumer.BuildConfig;
import com.clicbrics.consumer.view.fragment.MapFragment;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Collections;
public class BuildConfigConstants {


    public static String BASE_URL;
    public static  String BACKEND_APP_NAME;
    public static String GCM_SENDER_ID;
    public static String SERVICE_ACCOUNT_ID;
//    public static String EMAIL_SCOPE ;
    public static String SERVICE_ACCOUNT_SECRET ;
    public static String SERVER_API_KEY = null;
    public static  String EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email";
    public static String BUCKET_NAME = "housing-master-server.appspot.com";
    public static String SWAGGER_URL;

    public static String storage_path,configName,WEB_URL,SERVER_URL;


    //public static String storage_path = "https://storage.googleapis.com/housing-master-server.appspot.com";
    //public static String configName = "housingMasterServer-Config-App.png"; // for dev server

//    public static String storage_path = "https://storage.googleapis.com/housingtestserver.appspot.com/";
//    public static final String configName = "housingTestServer-Config-App.png"; // for test  server

//    public static String storage_path = "https://storage.googleapis.com/redbrics-prod-backend.appspot.com/";
//    public static String configName = "HousingProdConfig.png"; // for Production server


//    public static final String WEB_URL = "https://redrics-web-1.appspot.com/news-and-articles/";         // test
    //public static final String WEB_URL = "https://housingwebappdev.appspot.com/news-and-articles/";    // dev
  //  public static String WEB_URL = "https://www.clicbrics.com/news-and-articles/";               // production

//    public static final String SERVER_URL = "https://housingtestserver.appspot.com/";               // test
//    public static String SERVER_URL = "https://redbrics-prod-backend.appspot.com/";   // production

//    private static String server = "test";
    private static String server = "production";

    static {
        if(BuildConfig.SERVER.equalsIgnoreCase("test"))
       /* if(server.equalsIgnoreCase("test"))*/{
            //SWAGGER_URL = "https://backend.clickbricks.in/TestServer/api/";
            SWAGGER_URL = "https://backend.clickbricks.in/TestServer/";
            storage_path = "https://storage.googleapis.com/housingtestserver.appspot.com/"; //test server
            configName = "housingTestServer-Config-App.png";                                // for test  server
            WEB_URL = "https://redrics-web-1.appspot.com/news-and-articles/";               // test
            SERVER_URL = "https://housingtestserver.appspot.com/";                          // test
        }else if(BuildConfig.SERVER.equalsIgnoreCase("production"))
            /*if(server.equalsIgnoreCase("production"))*/{
            //SWAGGER_URL = "https://backend.clickbricks.in/api/";
            SWAGGER_URL = "https://backend.clickbricks.in/";
            storage_path = "https://storage.googleapis.com/redbrics-prod-backend.appspot.com/"; //production
            configName = "HousingProdConfig.png";                                               // for Production server
            WEB_URL = "https://www.clicbrics.com/news-and-articles/";                           // production
            SERVER_URL = "https://redbrics-prod-backend.appspot.com/";                          // production
        }
    }




    public BuildConfigConstants(Context context){
            try {

                /*InputStream inputStream = context.getAssets().open(configName);
                Properties props = new Properties();
                props.load(CryptoUtils.decrypt(getAuthKey(),inputStream));
                BASE_URL = props.getProperty("BASE_URL",BASE_URL);
                BACKEND_APP_NAME = props.getProperty("BACKEND_APP_NAME",BACKEND_APP_NAME);
                GCM_SENDER_ID = props.getProperty("GCM_SENDER_ID",GCM_SENDER_ID);
                SERVICE_ACCOUNT_ID = props.getProperty("SERVICE_ACCOUNT_ID", SERVICE_ACCOUNT_ID);
//                EMAIL_SCOPE = props.getProperty("EMAIL_SCOPE",EMAIL_SCOPE);
                SERVICE_ACCOUNT_SECRET = props.getProperty("SERVICE_ACCOUNT_SECRET",SERVICE_ACCOUNT_SECRET);
                BUCKET_NAME = props.getProperty("BUCKET_NAME", BUCKET_NAME);
                SERVER_API_KEY = props.getProperty("SERVER_API_KEY", SERVER_API_KEY);
                if(configName.equalsIgnoreCase("housingTestServer-Config-App.png")){
                    storage_path = "https://storage.googleapis.com/housingtestserver.appspot.com/";
                }else if(configName.equalsIgnoreCase("housingMasterServer-Config-App.png")){
                    storage_path = "https://storage.cloud.google.com/redbrics-prod-backend.appspot.com/";
                }*/


                InputStream inputStream = context.getAssets().open(configName);
                String getTextFromFile = convertInputStreamToString(inputStream);
                byte[] by = Base64.decode(getTextFromFile, Base64.DEFAULT);
                String decryptString=CryptoUtils.decrypt(by,getAuthKey());
                JSONObject jsonObject=new JSONObject(decryptString);


//            Properties props = new Properties();2639
//            props.load(CryptoUtils.decrypt(getAuthKey(), inputStream));

                BASE_URL = jsonObject.getString("BASE_URL");
                BACKEND_APP_NAME = jsonObject.getString("BACKEND_APP_NAME");
                SERVICE_ACCOUNT_ID = jsonObject.getString("SERVICE_ACCOUNT_ID");
                SERVICE_ACCOUNT_SECRET = jsonObject.getString("SERVICE_ACCOUNT_SECRET");
                BUCKET_NAME = jsonObject.getString("BUCKET_NAME");

            }catch(Exception e){
                e.printStackTrace();
//                AnalyticsTrackers.trackException(e);
            }


//        BASE_URL = "https://housing-master-server.appspot.com/_ah/api/";
//        BACKEND_APP_NAME = "Housing Master Server";
//        GCM_SENDER_ID = "662863947899";
//        SERVICE_ACCOUNT_ID = "housing-master-server@appspot.gserviceaccount.com";
//        EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email";
//        BUCKET_NAME = "housing-master-server.appspot.com";
//        SERVICE_ACCOUNT_SECRET = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC6BLQf9ZEMYCoLjy+N2jx5tJlclxDclX81Yc5Q4excBbgnnckIUttvfZ7zGb+e09czHKMSdt5buG0q5ecQtcw8jp53OkTFqJfE9yDyuzkH+xiJSnAf3YWxubxdLRrGxlcR+oOn4r9qHqiWPUXzbAlX+rN+H+ysnHxzZ73SyrrvN5i2lDP7Zh7+XcphLp489tWq84zlORo+xvEgpzu7Aewd8VZgLPPjhHVHiF+SPCgk5inV9LAPE7/7ZRIefQL1Ps3aF4zbw7Rhv0VY8SbJwIro/z2pxsTRJwc47Oe5PVIQ5bCbAyzMTPsGiYhYAL/4MFynXIkrHlAZDUJyYowCB9qnAgMBAAECggEBAJC6hCKxfy8vysa/mvI1MARJPWFKr/XP4u93r3xCqxFV6df47aaqQMzX4kmmaO58R90elMoS7iJSta1AvqzDpaogFK8TCNr4t0yqMlpozJCnDooS/nXsds62ghXEuCabvrbgQHro8qDwRZ7PAZPJJOPszKZ+1DfgcPuhZZGR5iujtFFXIBnehDfz14PuuL9i6D7ypDoaU/LOBndPbw6tIFd7oyj8zSk+4b2PzIch7kYlse9M4/6YaOJSHvN3I9apQSTc0amg71pnJe+4iNX8IM+LTeTD3IYRJ0f7Wb7pYva9cjfDUz8tuGHG2Z907dmxhmjUBu7gJqT8QQAZBBqaaNECgYEA4T/DNJ8oAhGEyb1dWLXVKuZSV+X7cioSrtDGqisvsYioHGR9kMUZGmYEZln/uHelJhTOcT9rLVmg4EHImdi/JKyAsZw+rRWZStX9QaPbCKeCppbqDJZdOsBsFeF/VGhbSKWbhACSEZR9GBvS6+0UFKxu1tW5euOwJJbuAvvapC0CgYEA02ndtmLTmH1ZnNd6aXRleh6Ite6hQ83Nr91bps7y/4cjW6NXzBQ/VuwmxVJ4rSDg3CCBojtEkLC2cSde2mgxlOJe9u3umLotWNqo/C4OHxSm930hF/kClbsDSULgO17vzBK8zbRmTArb89p3KZx61qdSyt6kMfQC3dU4NPDv2qMCgYB0l6+wVk28PFsyna1q1Lwhd5TZ4LZ10qdXC8Q5ox8N00HJaI7CV/N1gab3X9CJKI04l/6ACiatE769Ne/jCpluH0iCJvEeYJa3cOqlhm5DHYn4NxexKkfZuG56KP3isB3IDcjg2/a/2M+jogtciZwiU1sQIxLeNGshlWpf6wOyhQKBgB62pZgmmVjw9QDdKRKC728jWncb2N/R2UzwWq1MKW9IAbIBc/LZCi6llihah9+XBDPWoR6hf5+JnKepsZUHSBS6IlO7U0tq2MWs1pn0UubfCDayPgmOinqYNa0CAzN84leCbV1xFN99QGEX3vHzosDmhBem6rG7ImN543WFf0MDAoGAOchb4fICoGRBzzyEORRTy51KLoEfGp1hpLIZvN7sOhowCjqo0m+oUsGTSP2fZX9oNgEIqnEwQum3/yRx5NZrhgBqAjmP1poaJq1XT+H56clYjua1SA5tApYvEd7LAk+GxAUfbIBtHtvoAkObOD3OXxmfiNBZ0K9adnYaP94MeQg=";

    }
    static String getToken(){
        return  "sergap";
    }
    static String getKey(){
        return  "732HJYG";
    }

    private static String getAuthKey(){
        return MapFragment.getKey()+
                UtilityMethods.getKey()+
                getKey()+
                getToken();
    }

    private static volatile PrivateKey privateKey = null;
    private static volatile GoogleCredential credential = null;

    public static GoogleCredential authorize() {
        // load client secrets
        if(true) {
            return null;
        }
        if (credential == null) {
            try {
                HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                JsonFactory jsonFactory = new AndroidJsonFactory();

                credential = new GoogleCredential.Builder().setTransport(httpTransport)
                        .setJsonFactory(jsonFactory)
                        .setServiceAccountId(BuildConfigConstants.SERVICE_ACCOUNT_ID)
                        .setServiceAccountScopes(Collections.singleton(BuildConfigConstants.EMAIL_SCOPE))
                        .setServiceAccountPrivateKey(BuildConfigConstants.getPrivateKey())
                        .build();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return credential;
    }

    public static PrivateKey getPrivateKey() throws Exception {
        if (privateKey == null) {
            String keyStr = BuildConfigConstants.SERVICE_ACCOUNT_SECRET;
            privateKey = loadPrivateKey(keyStr);
        }
        return privateKey;
    }


    private static PrivateKey loadPrivateKey(String key64) throws GeneralSecurityException {
        byte[] clear = Base64.decode(key64, Base64.DEFAULT);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        PrivateKey priv = fact.generatePrivate(keySpec);
        Arrays.fill(clear, (byte) 0);
        return priv;
    }

//	public static String savePrivateKey(PrivateKey priv) throws GeneralSecurityException {
//		KeyFactory fact = KeyFactory.getInstance("RSA");
//		PKCS8EncodedKeySpec spec = fact.getKeySpec(priv,
//				PKCS8EncodedKeySpec.class);
//		byte[] packed = spec.getEncoded();
//		String key64 = Base64.encodeToString(packed, Base64.DEFAULT);
//		Log.d("key", "" + key64);
//
//		Arrays.fill(packed, (byte) 0);
//		return key64;
//	}
//
//    private static PrivateKey getPrivateKey() throws Exception {
//        KeyStore keystore = KeyStore.getInstance("PKCS12");
//        keystore.load(new FileInputStream("/sdcard/key.p12"), "notasecret".toCharArray());
//        PrivateKey key = (PrivateKey)keystore.getKey(keystore.aliases().nextElement(), "notasecret".toCharArray());
//
//        if (privateKey == null) {
//            String keyStr = BuildConfigConstants.SERVICE_ACCOUNT_SECRET;
//            privateKey = loadPrivateKey(savePrivateKey(key));
//        }
//        return privateKey;
//    }




    private String convertInputStreamToString(final InputStream inputStream) {
        if (inputStream != null) {
            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();

            String line;
            try {

                br = new BufferedReader(new InputStreamReader(inputStream));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return sb.toString();
        } else {
            return "";
        }

    }


}
