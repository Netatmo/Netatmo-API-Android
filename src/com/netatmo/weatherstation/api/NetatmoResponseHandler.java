/*
 * Copyright 2013 Netatmo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netatmo.weatherstation.api;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.netatmo.weatherstation.api.model.Measures;
import com.netatmo.weatherstation.api.model.Station;

import org.json.JSONObject;

import java.util.List;

/**
 * Netatmo's implemantation of {@link com.loopj.android.http.JsonHttpResponseHandler}.
 * You can use this handler that parses and returns the different responses from the Netatmo API calls.
 * See the sample package for examples.
 */
public class NetatmoResponseHandler extends JsonHttpResponseHandler {
    public static final int REQUEST_LOGIN = 0;
    public static final int REQUEST_GET_DEVICES_LIST = 1;
    public static final int REQUEST_GET_LAST_MEASURES = 2;

    static final String TAG = "NetatmoResponseHandler";

    NetatmoHttpClient mHttpClient;
    int mRequestType;
    String[] mMeasuresTypes;

    /**
     * @param requestType Used by {@link #onSuccess(org.json.JSONObject)} to return the right response.
     * You must use the constant corresponding to your request:
     * {@link #REQUEST_LOGIN}, {@link #REQUEST_GET_DEVICES_LIST}, {@link #REQUEST_GET_LAST_MEASURES}.
     * @param httpClient Your instance of {@link NetatmoHttpClient}.
     * @param measuresTypes Required for {@link #REQUEST_GET_LAST_MEASURES} only.
     */
    public NetatmoResponseHandler(NetatmoHttpClient httpClient, int requestType, String[] measuresTypes) {
        super();
        mHttpClient = httpClient;
        mRequestType = requestType;
        mMeasuresTypes = measuresTypes;
    }

    public void onLoginResponse() {}
    public void onGetDevicesListResponse(List<Station> devices) {}
    public void onGetMeasuresResponse(Measures measures) {}

    @Override
    public void onSuccess(JSONObject response) {
    	
    	final String M = "onSuccess: ";
        //Log.i(TAG, M);
    	
        super.onSuccess(response);

        switch (mRequestType) {
            case REQUEST_LOGIN:
                mHttpClient.processOAuthResponse(response);
                onLoginResponse();
                break;
            case REQUEST_GET_DEVICES_LIST:
                List<Station> list = NetatmoUtils.parseDevicesList(response);
                onGetDevicesListResponse(list);
                break;
            case REQUEST_GET_LAST_MEASURES:
                Measures measures = NetatmoUtils.parseMeasures(response, mMeasuresTypes);
                onGetMeasuresResponse(measures);
                break;
            default:
                break;
        }
    }

    @Override
    public void onFailure(Throwable e, JSONObject errorResponse) {
    	final String M = "onFailure: ";
        Log.i(TAG, M + errorResponse);
    	
    	
        super.onFailure(e, errorResponse);
    }

    @Override
    public void onFailure(Throwable error, String content) {
    	
    	final String M = "onFailure: ";
    	Log.i(TAG, M + content);
    	
        super.onFailure(error, content);

    }
}
