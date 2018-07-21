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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
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
    private static String mSessionCookie;

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
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
    public void handleGetRequest(String url, @Nullable final Callable<Integer> callback) {
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            callback.call();
                        }
                        catch (java.lang.Exception e) {
                            Log.e(TAG, "GET callback function error", e);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Volley error", error);
                    }
                })
        {
            //Send sessionID cookie with every GET request
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Cookie", mSessionCookie);
                return headers;
            }
        };
        addToRequestQueue(jsonObjectRequest);
    }

    private String parseMapBody(Map<String, String> body) {
        String parsedString = "";
        for (Map.Entry<String,String> entry : body.entrySet())
            parsedString += entry.getKey() + "=" + entry.getValue() + "&";
        return parsedString.substring(0, parsedString.length()-1);
    }
    public void handlePostRequest(String url, Map<String, String> body, @Nullable final Callable<Integer> callback) {
        final String requestBody = parseMapBody(body);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    callback.call();
                }
                catch (java.lang.Exception e) {
                    Log.e(TAG, "POST callback function error", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        }) {
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
            // set the sessionID upon response
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    Map<String, String> responseHeaders = response.headers;
                    mSessionCookie = responseHeaders.get("Set-Cookie");
                    String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }
            }
        };
        addToRequestQueue(stringRequest);
    }
}
