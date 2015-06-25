package weatherstation.netatmo.com.netatmo_api_android.sample;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import weatherstation.netatmo.com.netatmo_api_android.R;
import weatherstation.netatmo.com.netatmo_api_android.api.NetatmoUtils;
import weatherstation.netatmo.com.netatmo_api_android.api.model.Measures;
import weatherstation.netatmo.com.netatmo_api_android.api.model.Module;
import weatherstation.netatmo.com.netatmo_api_android.api.model.Params;
import weatherstation.netatmo.com.netatmo_api_android.api.model.Station;


public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener {

    public static final String TAG = "MainActivity";
    CustomAdapter mAdapter;
    List<Module> mListItems = new ArrayList<>();

    List<Station> mDevices;
    int mCompletedRequest;

    SampleHttpClient sampleHttpClient;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        sampleHttpClient = new SampleHttpClient(this);
        if(sampleHttpClient.getAccessToken() != null){
            //if the user is already logged
            initActionBar();
        }else{
            //else, stats the LoginActivity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent,0);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * "Disconnects" the user by clearing stored tokens. Then, starts the LoginActivity.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sign_out) {
            sampleHttpClient.clearTokens();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent,0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_CANCELED){
            finish();
        }else if(resultCode == RESULT_OK){
            initActionBar();
        }

    }

    /**
     * Calls getLastMeasures() for all modules associated with the selected station.
     */
    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                setSupportProgressBarIndeterminateVisibility(true);
            }
        });

        Station station = mDevices.get(itemPosition);
        final List<Module> modules = station.getModules();
        mCompletedRequest = modules.size();

        if(!mListItems.isEmpty()){
            mListItems.clear();
            mAdapter.notifyDataSetChanged();
        }

        final String[] types = new String[]{
                Params.TYPE_NOISE,
                Params.TYPE_CO2,
                Params.TYPE_PRESSURE,
                Params.TYPE_HUMIDITY,
                Params.TYPE_TEMPERATURE,
                Params.TYPE_RAIN,
                Params.TYPE_RAIN_SUM_1,
                Params.TYPE_RAIN_SUM_24,
                Params.TYPE_WIND_ANGLE,
                Params.TYPE_WIND_STRENGTH,
                Params.TYPE_GUST_ANGLE,
                Params.TYPE_GUST_STRENGTH
        };

        sampleHttpClient.getDevicesList(
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject res = null;
                        try {
                            res = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(res!=null){
                            HashMap<String, Measures> measuresHashMap = NetatmoUtils.parseMeasures(res,types);
                            for(Module module : modules){
                                if(measuresHashMap.containsKey(module.getId())){
                                    module.setMeasures(measuresHashMap.get(module.getId()));
                                    mListItems.add(module);
                                }
                            }
                            mAdapter.notifyDataSetChanged();

                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                setSupportProgressBarIndeterminateVisibility(false);
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG,error.toString());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                setSupportProgressBarIndeterminateVisibility(false);
                            }
                        });
                    }
                });


        return true;
    }

    /**
     * Initializing the action bar with the stations names using the parsed response returned by
     * NetatmoHttpClient.getDevicesList().
     * {@see #weatherstation.netatmo.com.netatmo_api_android.api.getDevicesList(Response.Listener<String>, Response.ErrorListener}
     */
    private void initActionBar(){
        mAdapter = new CustomAdapter(this, mListItems);
        ListView listView = (ListView)findViewById(R.id.list);
        listView.setAdapter(mAdapter);


        setSupportProgressBarIndeterminateVisibility(true);

        final MainActivity activity = this;


        sampleHttpClient.getDevicesList(
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject res = null;
                        try {
                            res = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (res != null) {
                            mDevices = NetatmoUtils.parseDevicesList(res);

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    List<String> stationName = new ArrayList<>();
                                    for (Station station : mDevices) {
                                        stationName.add(station.getName());
                                    }

                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                            getApplicationContext(),
                                            android.R.layout.simple_spinner_dropdown_item,
                                            stationName);

                                    ActionBar actionBar = getSupportActionBar();
                                    actionBar.setDisplayShowTitleEnabled(false);
                                    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
                                    actionBar.setListNavigationCallbacks(adapter, activity);

                                }
                            });

                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                setSupportProgressBarIndeterminateVisibility(false);
                            }
                        });



                    }



                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, error.toString());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                setSupportProgressBarIndeterminateVisibility(false);
                            }
                        });
                    }
                });
    }



}
