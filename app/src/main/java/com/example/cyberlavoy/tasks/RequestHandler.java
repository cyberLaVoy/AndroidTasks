package com.example.cyberlavoy.tasks;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by CyberLaVoy on 7/21/2018.
 */

public class RequestHandler {
    private static final String TAG = "RequestHandler";
    private static RequestHandler mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;
    private static String mSessionCookie = "";

    private RequestHandler(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }
    public static synchronized RequestHandler getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RequestHandler(context);
        }
        return mInstance;
    }
    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    private Map<String, String> attachSessionCookie() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Cookie", mSessionCookie);
        return headers;
    }
    private void setSessionCookie(NetworkResponse response) {
        Map<String, String> responseHeaders = response.headers;
        mSessionCookie = responseHeaders.get("Set-Cookie");
    }
    private String parseMapBody(Map<String, String> body) {
        String parsedString = "";
        for (Map.Entry<String,String> entry : body.entrySet())
            parsedString += entry.getKey() + "=" + entry.getValue() + "&";
        return parsedString.substring(0, parsedString.length()-1);
    }
    public void handleGETRequest(String url, @Nullable final ArrayList<String> onResponseArray, @Nullable final Callable<Integer> onResponseCallBack) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (onResponseArray != null) {
                            onResponseArray.add(response.toString());
                        }
                        onResponseArray.add(response.toString());
                        if (onResponseCallBack != null) {
                            try {
                                onResponseCallBack.call();
                            } catch (java.lang.Exception e) {
                                Log.e(TAG, "GET callback function error", e);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Volley error", error);
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return attachSessionCookie();
            }
        };
        addToRequestQueue(jsonObjectRequest);
    }

    public void handlePOSTRequest(String url, Map<String, String> body, @Nullable final Callable<Integer> onResponseCallBack) {
        final String requestBody = parseMapBody(body);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (onResponseCallBack != null) {
                    try {
                        onResponseCallBack.call();
                    } catch (java.lang.Exception e) {
                        Log.e(TAG, "POST callback function error", e);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        })
        {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=utf-8";
            }
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    if (mSessionCookie == "") {
                        setSessionCookie(response);
                    }
                    String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return attachSessionCookie();
            }
        };
        addToRequestQueue(stringRequest);
    }
}
