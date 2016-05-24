Weather Station Sample App - Android
========

Sample application that implements the Netatmo API available here: [https://dev.netatmo.com/dev/resources#/technical/introduction][8]


This app uses the Android Volley library (which uses HttpUrlConnection or AndroidHttpClient). You can use any other library as long as it support [__SNI__][7].

It contains two packages:

 * [__api__][1]: Classes to get you started with our API including a http client and some parsing utilies. This implementation provides tools to get the last measurements for a given device.
 * [__sample__][2]: A quick example of what you can do with the __api__ package.



Explanation & Usage
--------

1. Register your Netatmo application: https://dev.netatmo.com/dev/createanapp.
2. Extend the [__NetatmoHttpClient__][3] class, e.g. [__SampleHttpClient__][4].
3. Using the previously created class, authenticate your user, e.g. [__LoginActivity__][5].
4. When authentication tokens are stored, call getDevicesList() and getLastMeasures() with proper parameters, e.g. [__MainActivity__][6].

Each request is made using the __HTTP library volley__.


Quick Start with the apps
--------
1. Download the sources. We assume that you are using android studio
2. Import the project : File -> New -> Project from version control -> Github
3. Set your CLIENT\_ID and  CLIENT\_SECRET and APP_SCOPE in /values/keys.xml
4. Run the project


Credits
--------
 * Android Volley library: https://developer.android.com/training/volley/index.html


License
--------

    Copyright 2013 Netatmo

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[1]: app/src/main/java/weatherstation/netatmo/com/netatmo_api_android/api/
[2]: app/src/main/java/weatherstation/netatmo/com/netatmo_api_android/sample/
[3]: app/src/main/java/weatherstation/netatmo/com/netatmo_api_android/api/NetatmoHttpClient.java
[4]: app/src/main/java/weatherstation/netatmo/com/netatmo_api_android/sample/SampleHttpClient.java
[5]: app/src/main/java/weatherstation/netatmo/com/netatmo_api_android/sample/LoginActivity.java
[6]: app/src/main/java/weatherstation/netatmo/com/netatmo_api_android/sample/MainActivity.java
[7]: https://en.wikipedia.org/wiki/Server_Name_Indication
[8]: https://dev.netatmo.com/doc/
