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

package com.netatmo.weatherstation.sample;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.netatmo.weatherstation.api.NetatmoHttpClient;
import com.netatmo.weatherstation.api.NetatmoUtils;

/**
 * This is just an example of how you can extend NetatmoHttpClient.
 * Tokens are stored in the shared preferences of the app, but you can store them as you wish
 * as long as they are properly returned by the getters.
 * If you want to add your own '/getmeasure' requests, this is also the place to do it.
 */
public class SampleHttpClient extends NetatmoHttpClient {
    final String CLIENT_ID = "YOUR_CLIENT_ID";
    final String CLIENT_SECRET = "YOUR_CLIENT_SECRET";

    SharedPreferences mSharedPrefs;

    public SampleHttpClient(Context context) {
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    protected String getClientId() {
        return CLIENT_ID;
    }

    @Override
    protected String getClientSecret() {
        return CLIENT_SECRET;
    }

    @Override
    public void storeTokens(String refreshToken, String accessToken, long expiresAt) {
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.putString(NetatmoUtils.KEY_REFRESH_TOKEN, refreshToken);
        editor.putString(NetatmoUtils.KEY_ACCESS_TOKEN, accessToken);
        editor.putLong(NetatmoUtils.KEY_EXPIRES_AT, expiresAt);
        editor.apply();
    }

    @Override
    public void clearTokens() {
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.clear();
        editor.apply();
    }

    @Override
    public String getAccessToken() {
        return mSharedPrefs.getString(NetatmoUtils.KEY_ACCESS_TOKEN, null);
    }

    @Override
    public String getRefreshToken() {
        return mSharedPrefs.getString(NetatmoUtils.KEY_REFRESH_TOKEN, null);
    }

    @Override
    public long getExpiresAt() {
        return mSharedPrefs.getLong(NetatmoUtils.KEY_EXPIRES_AT, 0);
    }
}
