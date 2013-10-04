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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.HashMap;

abstract public class NetatmoHttpClient {
    // API URLs that will be used for requests, see: http://dev.netatmo.com/doc/restapi.
    protected final String URL_BASE = "https://api.netatmo.net";
    protected final String URL_REQUEST_TOKEN = URL_BASE + "/oauth2/token";
    protected final String URL_GET_DEVICES_LIST = URL_BASE + "/api/devicelist";
    protected final String URL_GET_MEASURES = URL_BASE + "/api/getmeasure";

    // You can find the AsyncHttpClient library documentation here: http://loopj.com/android-async-http.
    AsyncHttpClient mClient = new AsyncHttpClient();

    /**
     * POST request using AsyncHttpClient.
     */
    protected void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        mClient.post(url, params, responseHandler);
    }

    /**
     * GET request using AsyncHttpClient.
     * Since the access token is needed for each GET request to the Netatmo API,
     * we need to check if it has not expired.
     * See {@link #refreshToken(String, com.loopj.android.http.JsonHttpResponseHandler)}.
     */
    protected void get(final String url, final RequestParams params, final AsyncHttpResponseHandler responseHandler) {
        if (System.currentTimeMillis() >= getExpiresAt()) {
            refreshToken(getRefreshToken(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    super.onSuccess(response);
                    processOAuthResponse(response);
                    params.put("access_token", getAccessToken());
                    mClient.get(url, params, responseHandler);
                }
            });
        } else {
            params.put("access_token", getAccessToken());
            mClient.get(url, params, responseHandler);
        }
    }

    /**
     * This is the first request you have to do before being able to use the API.
     * It allows you to retrieve an access token in one step,
     * using your application's credentials and the user's credentials.
     */
    public void login(String email, String password, JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("grant_type", "password");
        params.put("client_id", getClientId());
        params.put("client_secret", getClientSecret());
        params.put("username", email);
        params.put("password", password);

        post(URL_REQUEST_TOKEN, params, responseHandler);
    }

    /**
     * Once an access token has been obtained, it can be used immediately to access the REST API.
     * After a certain amount of time, the access token expires
     * and the application needs to use the refresh token to renew the access token.
     * Both the refresh token and the expiration time are obtained during the authentication phase.
     */
    public void refreshToken(String refreshToken, JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("grant_type", "refresh_token");
        params.put("refresh_token", refreshToken);
        params.put("client_id", getClientId());
        params.put("client_secret", getClientSecret());

        post(URL_REQUEST_TOKEN, params, responseHandler);
    }

    /**
     * Returns the list of devices owned by the user, and their modules.
     * A device is identified by its _id (which is its mac address) and each device may have one,
     * several or no modules, also identified by an _id.
     * See <a href="http://dev.netatmo.com/doc/restapi/devicelist">http://dev.netatmo.com/doc/restapi/devicelist</a> for more.
     */
    public void getDevicesList(JsonHttpResponseHandler responseHandler) {
        get(URL_GET_DEVICES_LIST, new RequestParams(), responseHandler);
    }

    /**
     * Returns the last measurements for the given parameters.
     *
     * @param stationId A main device id from the {@link #getDevicesList(com.loopj.android.http.JsonHttpResponseHandler)} request.
     * @param moduleId Put here the stationId if you want measurements from the main device but not from a module.
     * @param scale You can specify a SCALE_* from {@link com.netatmo.weatherstation.api.model.Params}.
     * @param types If you want to use {@link com.netatmo.weatherstation.api.NetatmoUtils#parseMeasures(org.json.JSONObject, String[])}
     *              you have to use TYPES_* from {@link com.netatmo.weatherstation.api.model.Params}.
     * @param responseHandler
     *
     * See <a href="http://dev.netatmo.com/doc/restapi/getmeasure">http://dev.netatmo.com/doc/restapi/getmeasure</a> for more.
     */
    public void getLastMeasures(String stationId, String moduleId, String scale, String[] types, JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("device_id", stationId);

        if (!moduleId.equals(stationId)) {
            params.put("module_id", moduleId);
        }

        params.put("scale", scale);

        String stringTypes = "";
        for (int i=0; i < types.length; i++) {
            stringTypes += types[i];
            if (i+1 < types.length) {
                stringTypes += ",";
            }
        }

        params.put("type", stringTypes);
        params.put("date_end", "last");

        get(URL_GET_MEASURES, params, responseHandler);
    }


    /**
     * Making sure to call {@link #storeTokens(String, String, long)} with proper values.
     */
    protected void processOAuthResponse(JSONObject response) {
        HashMap<String,String> parsedResponse = NetatmoUtils.parseOAuthResponse(response);

        storeTokens(parsedResponse.get(NetatmoUtils.KEY_REFRESH_TOKEN),
                parsedResponse.get(NetatmoUtils.KEY_ACCESS_TOKEN),
                Long.valueOf(parsedResponse.get(NetatmoUtils.KEY_EXPIRES_AT)));
    }

    /**
     * You can get your client id by creating a Netatmo app first:
     * <a href="http://dev.netatmo.com/dev/createapp">http://dev.netatmo.com/dev/createapp</a>
     */
    protected abstract String getClientId();

    /**
     * You can get your client secret by creating a Netatmo app first:
     * <a href="http://dev.netatmo.com/dev/createapp">http://dev.netatmo.com/dev/createapp</a>
     */
    protected abstract String getClientSecret();

    /**
     * You have to call this method when receiving the response from
     * {@link #login(String, String, com.loopj.android.http.JsonHttpResponseHandler)}.
     */
    protected abstract void storeTokens(String refreshToken, String accessToken, long expiresAt);

    /**
     * You have to this method when signing out the user.
     */
    protected abstract void clearTokens();

    /**
     * Must return the refresh token stored by {@link #storeTokens(String, String, long)}.
     */
    protected abstract String getRefreshToken();

    /**
     * Must return the access token stored by {@link #storeTokens(String, String, long)}.
     */
    protected abstract String getAccessToken();

    /**
     * Must return expiration date stored by {@link #storeTokens(String, String, long)}.
     */
    protected abstract long getExpiresAt();
}
