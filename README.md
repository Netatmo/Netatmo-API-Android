Weather Station Sample App - Android
========

Sample application that implements the Netatmo API available here: http://dev.netatmo.com/doc/

It contains two packages:
 * [__api__][1]: Classes to get you started with our API including a http client and some parsing utilies. This implementation provides tools to get the last measurements for a given device.
 * [__sample__][2]: A quick example of what you can do with the __api__ package.


Explanation & Usage
--------

1. Register your Netatmo application: http://dev.netatmo.com/dev/createapp.
2. Extend the [__NetatmoHttpClient__][3] class, e.g. [__SampleHttpClient__][4].
3. Using the previously created class, authenticate your user, e.g. [__LoginActivity__][5].
4. When authentication tokens are stored, call getDevicesList() and getLastMeasures() with proper parameters, e.g. [__MainActivity__][6].

Each request is made using the __Android Asynchronous Http Client__. This is a callback-based http client library. When a response is received, the library sends it to a handler. We provide our own handler ([__NetatmoResponseHandler__][7]) that returns a parsed response but you can also use the ones provided by the [library][8]. 


Quick Start with Ant
--------
1. Download the sources. We have suposition that 'android' and 'ant' binaries in your PATH.
2. Enter the source directory.
3. Create ant project with command: 
android update project -p . --target android-17
4. Set your CLIENT\_ID and  CLIENT\_SECRET in SampleHttpClient.java.
5. Compile and build project:
ant debug install
6. Application compiled and installed. You can run application.


Credits
--------
 * Android Asynchronous Http Client: http://loopj.com/android-async-http/


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

[1]: src/com/netatmo/weatherstation/api/
[2]: src/com/netatmo/weatherstation/sample/
[3]: src/com/netatmo/weatherstation/api/NetatmoHttpClient.java
[4]: src/com/netatmo/weatherstation/sample/SampleHttpClient.java
[5]: src/com/netatmo/weatherstation/sample/LoginActivity.java
[6]: src/com/netatmo/weatherstation/sample/MainActivity.java
[7]: src/com/netatmo/weatherstation/api/NetatmoResponseHandler.java
[8]: http://loopj.com/android-async-http/doc/com/loopj/android/http/AsyncHttpResponseHandler.html
