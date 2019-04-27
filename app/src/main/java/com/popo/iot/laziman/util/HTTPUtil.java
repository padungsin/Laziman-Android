package com.popo.iot.laziman.util;


import android.webkit.CookieManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

public class HTTPUtil {



    public static String get(String url)throws Exception{


        InputStream inputStream = null;

        try {
            if (url.startsWith("https")) {
                inputStream = httpsGet(url);
            } else {
                inputStream = httpGet(url);
            }

            if (inputStream != null) {
                return convertStreamToString(inputStream);
            }

        }finally {
            inputStream.close();
        }
        return null;
    }



    private static InputStream httpsGet(String url) throws Exception {
        try {
            URL request_url = new URL(url);
            HttpsURLConnection urlConnection = (HttpsURLConnection) request_url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(95 * 1000);
            urlConnection.setConnectTimeout(95 * 1000);
            // urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("X-Environment", "android");


            urlConnection.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    /** if it necessarry get url verfication */
                    //return HttpsURLConnection.getDefaultHostnameVerifier().verify("your_domain.com", session);
                    return true;
                }
            });
            urlConnection.setSSLSocketFactory((SSLSocketFactory) SSLSocketFactory.getDefault());
            urlConnection.connect();
            return urlConnection.getInputStream();

        }catch (Exception e){
            throw e;
        }


    }

    private static InputStream httpGet(String url)throws Exception {

        URL request_url = new URL(url);
        HttpURLConnection httpConn = (HttpURLConnection)request_url.openConnection();
        return httpConn.getInputStream();

    }

    private static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
