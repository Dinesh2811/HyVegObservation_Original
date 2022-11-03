package com.example.manoj.hyveg_observation.services;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.manoj.hyveg_observation.Interfaces.IServiceListener;
import com.example.manoj.hyveg_observation.volley.MySingleton;

import org.json.JSONObject;


public class
JsonServiceHelper {
    private static String TAG="json";
        Context context;
        IServiceListener ServiceListener;
        public JsonServiceHelper(Context context) {
            this.context = context;
        }

    public void setServiceListener(IServiceListener ServiceListener) {
            this.ServiceListener =  ServiceListener;
        }

        public void makeGetEventServiceCall(String URL, JSONObject jsonObject, final String api_name ) {
            Log.d(TAG, "URL : " + URL + "Json "+jsonObject);
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST,
                    URL, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "Response : " + response);
                    ServiceListener.onResponse(response,api_name);
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            ServiceListener.onError(String.valueOf(error), api_name);
                        }
                    });
            int MY_SOCKET_TIMEOUT_MS = 10000*12;
            postRequest.setRetryPolicy(new DefaultRetryPolicy(  MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getInstance(context).addToRequestQueue(postRequest);
        }
}
