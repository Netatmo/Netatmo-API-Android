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

/**
 * When a request fails, the server will return
 * a JSONObject response with one of the number listed here.
 * NB: no error code is returned when doing a '/oauth/*' request.
 */
public class NetatmoErrorCodes {
    public final static int ACCESS_TOKEN_MISSING = 1;
    public final static int INVALID_ACCESS_TOKEN = 2;
    public final static int ACCESS_TOKEN_EXPIRED = 3;
    public final static int INCONSISTENCY_ERROR = 4;
    public final static int APPLICATION_DEACTIVATED = 5;
    public final static int INVALID_EMAIL = 6;
    public final static int NOTHING_TO_MODIFY = 7;
    public final static int EMAIL_ALREADY_EXISTS = 8;
    public final static int DEVICE_NOT_FOUND = 9;
    public final static int MISSING_ARGS = 10;
    public final static int INTERNAL_ERROR = 11;
    public final static int DEVICE_OR_SECRET_NO_MATCH = 12;
    public final static int OPERATION_FORBIDDEN = 13;
    public final static int APPLICATION_NAME_ALREADY_EXISTS = 14;
    public final static int NO_PLACES_IN_DEVICE = 15;
    public final static int MGT_KEY_MISSING = 16;
    public final static int BAD_MGT_KEY = 17;
    public final static int DEVICE_ID_ALREADY_EXISTS = 18;
    public final static int IP_NOT_FOUND = 19;
    public final static int TOO_MANY_USER_WITH_IP = 20;
    public final static int INVALID_ARG = 21;
    public final static int APPLICATION_NOT_FOUND = 22;
    public final static int USER_NOT_FOUND = 23;
    public final static int INVALID_TIMEZONE = 24;
    public final static int INVALID_DATE = 25;
    public final static int MAX_USAGE_REACHED = 26;
    public final static int MEASURE_ALREADY_EXISTS = 27;
    public final static int ALREADY_DEVICE_OWNER = 28;
    public final static int INVALID_IP = 29;
    public final static int INVALID_REFRESH_TOKEN = 30;
    public final static int NOT_FOUND = 31;
    public final static int BAD_PASSWORD = 32;
    public final static int FORCE_ASSOCIATE = 33;

    public final static int USER_NEED_LOGIN = 9997;
    public final static int EXCEPTION_NOCONNECT = 9998;
    public final static int UNKNOWN = 9999;
}