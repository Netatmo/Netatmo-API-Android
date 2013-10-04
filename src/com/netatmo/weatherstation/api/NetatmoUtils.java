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

import com.netatmo.weatherstation.api.model.Measures;
import com.netatmo.weatherstation.api.model.Module;
import com.netatmo.weatherstation.api.model.Params;
import com.netatmo.weatherstation.api.model.Station;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NetatmoUtils {
    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String KEY_REFRESH_TOKEN = "refresh_token";
    public static final String KEY_EXPIRES_AT = "expires_at";

    public static HashMap<String, String> parseOAuthResponse(JSONObject response) {
        HashMap<String, String> parsedResponse = new HashMap<String, String>();

        try {
            String refreshToken = response.getString("refresh_token");
            parsedResponse.put("refresh_token", refreshToken);

            String accessToken = response.getString("access_token");
            parsedResponse.put("access_token", accessToken);

            String expiresIn = response.getString("expires_in");
            Long expiresAt = System.currentTimeMillis() + Long.valueOf(expiresIn)*1000;
            parsedResponse.put("expires_at", expiresAt.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return parsedResponse;
    }

    public static List<Station> parseDevicesList(JSONObject response) {
        List<Station> devices = new ArrayList<Station>();

        try {
            JSONArray JSONstations = response.getJSONObject("body").getJSONArray("devices");

            for (int i = 0; i < JSONstations.length(); i++) {
                JSONObject station = JSONstations.getJSONObject(i);
                String name = station.getString("station_name");
                String moduleName = station.getString("module_name");
                String id = station.getString("_id");

                Station newStation = new Station(name, id);
                Module newModule = new Module(moduleName, id, Module.TYPE_INDOOR);

                newStation.addModule(newModule);
                devices.add(newStation);
            }

            JSONArray JSONmodules = response.getJSONObject("body").getJSONArray("modules");

            for (int i = 0; i < JSONmodules.length(); i++) {
                JSONObject module = JSONmodules.getJSONObject(i);
                String mainDevice = module.getString("main_device");
                String name = module.getString("module_name");
                String id = module.getString("_id");
                String type = module.getString("type");

                Module newModule = new Module(name, id, type);
                for (Station station : devices) {
                    if (mainDevice.equals(station.getId())) {
                        station.addModule(newModule);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return devices;
    }

    public static Measures parseMeasures(JSONObject response, String[] types) {
        Measures measures = new Measures();

        try {
            JSONObject body = response.getJSONArray("body").getJSONObject(0);

            String beginTime = body.getString("beg_time");
            measures.setBeginTime((beginTime == null) ? 0 : Long.parseLong(beginTime)*1000);

            JSONArray values = body.getJSONArray("value").getJSONArray(0);

            for (int i=0; i < types.length; i++) {
                if (types[i].equals(Params.TYPE_TEMPERATURE)) {
                    measures.setTemperature(getStringFromObject(values.get(i)));
                } else if (types[i].equals(Params.TYPE_CO2)) {
                    measures.setCO2(getStringFromObject(values.get(i)));
                } else if (types[i].equals(Params.TYPE_HUMIDITY)) {
                    measures.setHumidity(getStringFromObject(values.get(i)));
                } else if (types[i].equals(Params.TYPE_PRESSURE)) {
                    measures.setPressure(getStringFromObject(values.get(i)));
                } else if (types[i].equals(Params.TYPE_NOISE)) {
                    measures.setNoise(getStringFromObject(values.get(i)));
                } else if (types[i].equals(Params.TYPE_MIN_TEMP)) {
                    measures.setMinTemp(getStringFromObject(values.get(i)));
                } else if (types[i].equals(Params.TYPE_MAX_TEMP)) {
                    measures.setMaxTemp(getStringFromObject(values.get(i)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return measures;
    }

    private static String getStringFromObject(Object object) {
        String string = object.toString();

        if (string == null || string.equals("null")) {
            return Measures.STRING_NO_DATA;
        }

        return string;
    }
}
