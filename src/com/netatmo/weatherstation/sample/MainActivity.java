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

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;

import com.netatmo.weatherstation.api.NetatmoResponseHandler;
import com.netatmo.weatherstation.api.model.Measures;
import com.netatmo.weatherstation.api.model.Module;
import com.netatmo.weatherstation.api.model.Params;
import com.netatmo.weatherstation.api.model.Station;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity implements ActionBar.OnNavigationListener {
    final int REQUEST_CODE = 0;

    CustomAdapter mAdapter;
    List<Module> mListItems = new ArrayList<Module>();

    List<Station> mDevices;
    int mCompletedRequest;

    SampleHttpClient mHttpClient;

    
    public static String TAG = "MainActivity: ";
    Handler handler = new Handler();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        final String M = "onCreate: ";
        Log.i(TAG, M);
        
        // HttpClient used for all requests in this activity.
        mHttpClient = new SampleHttpClient(this);

        if (mHttpClient.getAccessToken() != null) {
            // If the user is already logged in.
            initActionBar();
        } else {
            // Else, starts LoginActivity.
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                initActionBar();
            } else if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    /**
     * Initializing the action bar with the stations' names using the parsed response returned by
     * NetatmoHttpClient.getDevicesList(NetatmoResponseHandler).
     */
    private void initActionBar() {
        mAdapter = new CustomAdapter(this, mListItems);
        setListAdapter(mAdapter);

        final MainActivity activity = this;

        // NetatmoResponseHandler returns a parsed response (by overriding onGetDevicesListResponse).
        // You can also use JsonHttpResponseHandler and process the response as you wish.
        mHttpClient.getDevicesList(new NetatmoResponseHandler(mHttpClient,
                NetatmoResponseHandler.REQUEST_GET_DEVICES_LIST, null) {
            @Override
            public void onStart() {
                super.onStart();
                setProgressBarIndeterminateVisibility(Boolean.TRUE);
            }

            @Override
            public void onGetDevicesListResponse(final List<Station> devices) {
                mDevices = devices;

                handler.post(new Runnable() {
					
 					@Override	public void run() {
 						
 
                List<String> stationsNames = new ArrayList<String>();
                for (Station station : devices) {
                    stationsNames.add(station.getName());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,
                        android.R.layout.simple_spinner_dropdown_item, stationsNames);

                ActionBar actionBar = getActionBar();
                actionBar.setDisplayShowTitleEnabled(false);
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
                actionBar.setListNavigationCallbacks(adapter, activity);
                
				}});
            }

            @Override
            public void onFinish() {
            	
            	super.onFinish();
            	
                handler.post(new Runnable() {
					
 					@Override	public void run() {
                
 						setProgressBarIndeterminateVisibility(Boolean.FALSE);
 				}});
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * "Disconnects" the user by clearing stored tokens. Then, starts the LoginActivity.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                mHttpClient.clearTokens();

                Intent intent = new Intent(this, LoginActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Calls getLastMeasures() for all modules associated with the selected station.
     */
    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        setProgressBarIndeterminateVisibility(Boolean.TRUE);

        Station station = mDevices.get(itemPosition);
        List<Module> modules = station.getModules();
        mCompletedRequest = modules.size();

        if (!mListItems.isEmpty()) {
            mListItems.clear();
            mAdapter.notifyDataSetChanged();
        }

        String[] types = new String[]{
                Params.TYPE_NOISE,
                Params.TYPE_CO2,
                Params.TYPE_PRESSURE,
                Params.TYPE_HUMIDITY,
                Params.TYPE_TEMPERATURE
        };

        for (final Module module : modules) {
            // NetatmoResponseHandler returns a parsed response (by overriding onGetMeasuresResponse).
            // You can also use JsonHttpResponseHandler and process the response as you wish.
            mHttpClient.getLastMeasures(station.getId(), module.getId(), Params.SCALE_MAX, types,
                    new NetatmoResponseHandler(mHttpClient, NetatmoResponseHandler.REQUEST_GET_LAST_MEASURES, types) {
                @Override
                public void onGetMeasuresResponse( final Measures measures) {
                	
                    handler.post(new Runnable() {
    					
     					@Override	public void run() {
     						
                    module.setMeasures(measures);
                    mListItems.add(module);
                    mAdapter.notifyDataSetChanged();
                    
     				}});
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    
                    handler.post(new Runnable() {
    					
     					@Override	public void run() {
                    mCompletedRequest--;
                    if (mCompletedRequest == 0) {
                        setProgressBarIndeterminateVisibility(Boolean.FALSE);
                    }
                    
     				}});
                }
            });
        }

        return true;
    }
}
