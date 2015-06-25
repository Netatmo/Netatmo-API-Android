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
package weatherstation.netatmo.com.netatmo_api_android.api.model;

public class Params {
    // These are all the scales available.
    public static final String SCALE_MAX = "max";
    public static final String SCALE_THIRTY_MINUTES = "30min";
    public static final String SCALE_THREE_HOURS = "3hours";
    public static final String SCALE_ONE_DAY = "1day";
    public static final String SCALE_ONE_WEEK = "1week";
    public static final String SCALE_ONE_MONTH = "1month";

    // These are some of the types available.
    // See the full list here: http://dev.netatmo.com/doc/restapi/getmeasure
    public static final String TYPE_TEMPERATURE = "Temperature";
    public static final String TYPE_CO2 = "CO2";
    public static final String TYPE_HUMIDITY = "Humidity";
    public static final String TYPE_PRESSURE = "Pressure";
    public static final String TYPE_NOISE = "Noise";
    public static final String TYPE_MIN_TEMP = "min_temp";
    public static final String TYPE_MAX_TEMP = "max_temp";
    public static final String TYPE_RAIN = "Rain";
    public static final String TYPE_RAIN_SUM_24 = "sum_rain_24";
    public static final String TYPE_RAIN_SUM_1 = "sum_rain_1";
    public static final String TYPE_WIND_ANGLE = "WindAngle";
    public static final String TYPE_WIND_STRENGTH = "WindStrength";
    public static final String TYPE_GUST_ANGLE = "GustAngle";
    public static final String TYPE_GUST_STRENGTH = "GustStrength";
}