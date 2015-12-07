/*
 * __________________
 * Qaobee
 * __________________
 *
 * Copyright (c) 2015.  Qaobee
 * All Rights Reserved.
 *
 * NOTICE: All information contained here is, and remains
 * the property of Qaobee and its suppliers,
 * if any. The intellectual and technical concepts contained
 * here are proprietary to Qaobee and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Qaobee.
 */

package giwi.org.cesitwitter.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Map;

/**
 * The type Http helper.
 */
public class HTTPHelper {
    private static String LANG = Locale.getDefault().toString();
    private static final String TAG = HTTPHelper.class.getName();

    /**
     * Is internet available boolean.
     *
     * @param context the context
     * @return the boolean
     */
    public static boolean isInternetAvailable(Context context) {
        try {
            ConnectivityManager cm
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        } catch (Exception e) {
            Log.e("HelloWorld", "Error on checking internet:", e);

        }
        //default allowed to access internet
        return true;
    }

    /**
     * Send rest response.
     *
     * @param method  the method
     * @param url     the url
     * @param headers the headers
     * @return the rest response
     */
    public RestResponse send(Methods method, String url, Map<String, String> headers) {
        return send(method, url, "", headers);
    }

    /**
     * Send rest response.
     *
     * @param method            the method
     * @param url               the url
     * @param body              the body
     * @param additionalHeaders the additional headers
     * @return the rest response
     */
    public RestResponse send(Methods method, String url, JSONObject body, Map<String, String> additionalHeaders) {
        return send(method, url, body.toString(), additionalHeaders);
    }

    /**
     * Send rest response.
     *
     * @param method            the method
     * @param url               the url
     * @param body              the body
     * @param additionalHeaders the additional headers
     * @return the rest response
     */
    public RestResponse send(Methods method, String url, JSONArray body, Map<String, String> additionalHeaders) {
        return send(method, url, body.toString(), additionalHeaders);
    }

    /**
     * Send rest response.
     *
     * @param method            the method
     * @param url               the url
     * @param body              the body
     * @param additionalHeaders the additional headers
     * @return the rest response
     */
    public RestResponse send(Methods method, String url, String body, Map<String, String> additionalHeaders) {
        try {
            Log.i(TAG, method.name() + " : " + url);

            URL targetUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) targetUrl.openConnection();
            conn.setRequestMethod(method.name());
            if (StringUtils.isNotBlank(body)) {
                Log.i(TAG, body);
                conn.setFixedLengthStreamingMode(body.getBytes().length);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
            }
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept-Language", LANG);
            if (additionalHeaders != null) {
                for (String key : additionalHeaders.keySet()) {
                    Log.i(TAG, "http headers : " + key + " : " + additionalHeaders.get(key));
                    conn.setRequestProperty(key, additionalHeaders.get(key));
                }
            }
            conn.connect();
            if (StringUtils.isNotBlank(body)) {
                DataOutputStream printout = new DataOutputStream(conn.getOutputStream());
                printout.write(body.getBytes());
                printout.flush();
                printout.close();
            }
            return handleResponse(conn, url, body, additionalHeaders);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    /**
     * Handle exception rest response.
     *
     * @param e the e
     * @return the rest response
     */
    public RestResponse handleException(Exception e) {
        RestResponse response = new RestResponse();
        Log.e(TAG, e.getMessage(), e);
        response.setError(true);
        response.setErrorCode(e.getClass().getSimpleName());
        JSONObject json = new JSONObject();
        try {
            json.put("message", e.getMessage());
        } catch (JSONException e1) {
            Log.e(TAG, e1.getMessage(), e1);
        }
        response.setBody(json.toString());
        return response;
    }

    /**
     * Handle response rest response.
     *
     * @param conn              the conn
     * @param url               the url
     * @param body              the body
     * @param additionalHeaders the additional headers
     * @return the rest response
     * @throws IOException the io exception
     */
    private RestResponse handleResponse(HttpURLConnection conn, String url, String body, Map<String, String> additionalHeaders) throws IOException {
        RestResponse res = new RestResponse();
        if (conn.getResponseCode() < 400) {
            String respStr = readStream(conn.getInputStream());
            Log.i(TAG, respStr);
            if (respStr.startsWith("{") || respStr.startsWith("[")) {
                res.setBody(respStr);
                res.setError((conn.getResponseCode() != HttpURLConnection.HTTP_OK));
                res.setErrorCode(String.valueOf(conn.getResponseCode()));
            } else {
                res.setBody(respStr);
                res.setError(true);
                res.setErrorCode("400");
            }

            return res;
        } else {
            String respStr = readStream(conn.getErrorStream());
            Log.e(TAG, respStr);
            if (respStr.startsWith("{") || respStr.startsWith("[")) {
                res.setBody(respStr);
                res.setError(true);
                res.setErrorCode(String.valueOf(conn.getResponseCode()));
            } else {
                res.setBody(respStr);
                res.setError(true);
                res.setErrorCode("400");
            }
            return res;
        }
    }

    /**
     * Read stream string.
     *
     * @param in the in
     * @return the string
     */
    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}
