package com.example.manoj.hyveg_observation.Interfaces;

import org.json.JSONObject;

/**
 * Created by User on 12-01-2018.
 */

public interface IServiceListener {
    void onResponse(JSONObject response, String api_name);

    void onError(String error, String api_name);
}


