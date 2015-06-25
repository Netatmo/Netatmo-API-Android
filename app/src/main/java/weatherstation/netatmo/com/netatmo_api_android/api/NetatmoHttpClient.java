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
package weatherstation.netatmo.com.netatmo_api_android.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


abstract public class NetatmoHttpClient {

    //volley library part
    RequestQueue queue;


    // API URLs that will be used for requests, see: https://dev.netatmo.com/doc.
    protected final String URL_BASE = "https://api.netatmo.net";
    protected final String URL_REQUEST_TOKEN = URL_BASE + "/oauth2/token";
    protected final String URL_GET_DEVICES_LIST = URL_BASE + "/api/devicelist";

    public NetatmoHttpClient(Context context){
        queue = Volley.newRequestQueue(context);
    }


    /**
     * Post request using volley
     * @param url
     * @param params
     * @param successListener
     * @param errorListener
     */
    protected void post(String url, final HashMap<String,String> params, Response.Listener<String> successListener, Response.ErrorListener errorListener){

        StringRequest request = new StringRequest(Request.Method.POST, url,successListener,errorListener) {
            @Override
            protected Map<String,String> getParams(){
                return params;
            }
        };


        queue.add(request);
    }

    /**
     * Get request using volley.
     * Since the access token is needed for each GET request to the Netatmo API,
     * we need to check if it has not expired.
     * @param url
     * @param params
     * @param successListener
     * @param errorListener
     */
    protected void get(final String url, final HashMap<String,String> params, final Response.Listener<String> successListener, final Response.ErrorListener errorListener){

        if(System.currentTimeMillis() >= getExpiresAt()){
            refreshToken(getRefreshToken(), new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jsonObject;
                            try {
                                jsonObject= new JSONObject(response);
                                processOAuthResponse(jsonObject);
                                params.put("access_token", getAccessToken());
                                post(url,params,successListener,errorListener);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("errorRefreshToken",error.toString());
                        }
                    });
        }else{
            params.put("access_token", getAccessToken());
            post(url,params,successListener,errorListener);
        }

    }

    /**
     * This is the first request you have to do before being able to use the API.
     * It allows you to retrieve an access token in one step,
     * using your application's credentials and the user's credentials.
     * @param email
     * @param password
     * @param successListener
     * @param errorListener
     */
    public void login(String email, String password, Response.Listener<String> successListener, Response.ErrorListener errorListener){
        HashMap<String,String> params = new HashMap<>();
        params.put("grant_type", "password");
        params.put("client_id", getClientId());
        params.put("client_secret", getClientSecret());
        params.put("username", email);
        params.put("password", password);
        params.put("scope",getAppScope());

        post(URL_REQUEST_TOKEN, params, successListener, errorListener);

    }

    /**
     *
     * Once an access token has been obtained, it can be used immediately to access the REST API.
     * After a certain amount of time, the access token expires
     * and the application needs to use the refresh token to renew the access token.
     * Both the refresh token and the expiration time are obtained during the authentication phase.
     * @param refreshToken
     * @param successListener
     * @param errorListener
     */
    public void refreshToken(String refreshToken, Response.Listener<String> successListener, Response.ErrorListener errorListener){
        HashMap<String, String> params = new HashMap<>();
        params.put("grant_type", "refresh_token");
        params.put("refresh_token", refreshToken);
        params.put("client_id", getClientId());
        params.put("client_secret", getClientSecret());

        post(URL_REQUEST_TOKEN, params, successListener, errorListener);
    }

    /**
     * Returns the list of devices owned by the user, and their modules.
     * A device is identified by its _id (which is its mac address) and each device may have one,
     * several or no modules, also identified by an _id.
     * See <a href="https://dev.netatmo.com/doc/methods/devicelist">dev.netatmo.com/doc/methods/devicelist</a> for more information.
     * @param successListener
     * @param errorListener
     */
    public void getDevicesList(Response.Listener<String> successListener, Response.ErrorListener errorListener){
        get(URL_GET_DEVICES_LIST, new HashMap<String, String>(), successListener, errorListener);
    }

    /**
     * Making sure to call {@link #storeTokens(String, String, long)} with proper values.
     * @param response
     */
    public void processOAuthResponse(JSONObject response){
        HashMap<String,String> parsedResponse = NetatmoUtils.parseOAuthResponse(response);
        storeTokens(parsedResponse.get(NetatmoUtils.KEY_REFRESH_TOKEN),
                parsedResponse.get(NetatmoUtils.KEY_ACCESS_TOKEN),
                Long.valueOf(parsedResponse.get(NetatmoUtils.KEY_EXPIRES_AT)));
    }


    /**
     * You can get your client id by creating a Netatmo app first:
     * <a href="https://dev.netatmo.com/dev/createapp">dev.netatmo.com/dev/createapp</a>
     * @return CLIENT_ID
     */
    protected abstract String getClientId();

    /**
     * You can get your client secret by creating a Netatmo app first:
     * <a href="https://dev.netatmo.com/dev/createapp">dev.netatmo.com/dev/createapp</a>
     * @return CLIENT_SECRET
     */
    protected abstract String getClientSecret();

    /**
     * The application can require different scopes depending on the action it will need to execute.
     *<a href="https://dev.netatmo.com/doc/authentication/scopes">dev.netatmo.com/doc/authentication/scopes</a>
     * @return APP_SCOPE
     */
    protected abstract String getAppScope();

    /**
     * You have to call this method to store the different tokens
     * @param refreshToken
     * @param accessToken
     * @param expiresAt
     */
    protected abstract void storeTokens(String refreshToken, String accessToken, long expiresAt);

    /**
     * Called when the user sign out
     */
    protected abstract void clearTokens();

    /**
     * Return the refresh token stored by {@link #storeTokens(String, String, long)}.
     * @return refreshToken
     */
    protected abstract String getRefreshToken();

    /**
     * Return the access token stored by {@link #storeTokens(String, String, long)}.
     * @return accessToken
     */
    protected abstract String getAccessToken();

    /**
     * Return the expiration date, of the refreshToken, stored by {@link #storeTokens(String, String, long)}.
     * @return expiresAt
     */
    protected abstract long getExpiresAt();

}
