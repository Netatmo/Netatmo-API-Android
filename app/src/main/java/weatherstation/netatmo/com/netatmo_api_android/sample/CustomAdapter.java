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
package weatherstation.netatmo.com.netatmo_api_android.sample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import weatherstation.netatmo.com.netatmo_api_android.R;
import weatherstation.netatmo.com.netatmo_api_android.api.model.Measures;
import weatherstation.netatmo.com.netatmo_api_android.api.model.Module;

public class CustomAdapter extends BaseAdapter {
    Context mContext;
    List<Module> mModules;

    TextView mModuleNameView;
    TextView mDateView;
    TextView mTypeView;
    TextView mTemperatureView;
    TextView mCO2View;
    TextView mHumidityView;
    TextView mPressureView;
    TextView mNoiseView;
    TextView mRainView;
    TextView mSumRain24View;
    TextView mSumRain1View;
    TextView mWindAngle;
    TextView mWindStrength;
    TextView mGustStrength;
    TextView mGustAngle;

    public CustomAdapter(Context context, List<Module> modules) {
        mContext = context;
        mModules = modules;
    }

    @Override
    public int getCount() {
        return mModules.size();
    }

    @Override
    public Object getItem(int position) {
        return mModules.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.listview_row, null);

            mModuleNameView = (TextView) convertView.findViewById(R.id.module_name);
            mDateView = (TextView) convertView.findViewById(R.id.date);
            mTypeView = (TextView) convertView.findViewById(R.id.type);
            mTemperatureView = (TextView) convertView.findViewById(R.id.temperature);
            mCO2View = (TextView) convertView.findViewById(R.id.co2);
            mHumidityView = (TextView) convertView.findViewById(R.id.humidity);
            mPressureView = (TextView) convertView.findViewById(R.id.pressure);
            mNoiseView = (TextView) convertView.findViewById(R.id.noise);
            mRainView = (TextView) convertView.findViewById(R.id.rain);
            mSumRain1View = (TextView) convertView.findViewById(R.id.sum_rain_1);
            mSumRain24View = (TextView) convertView.findViewById(R.id.sum_rain_24);
            mWindAngle = (TextView) convertView.findViewById(R.id.windAngle);
            mWindStrength = (TextView) convertView.findViewById(R.id.windStrength);
            mGustAngle = (TextView) convertView.findViewById(R.id.GustAngle);
            mGustStrength = (TextView) convertView.findViewById(R.id.gustStrength);

        }

        Module module = mModules.get(position);
        Measures measures = module.getMeasures();

        mModuleNameView.setText(module.getName());

        long beginTime = measures.getBeginTime();
        if (beginTime != 0) {
            Timestamp timestamp = new Timestamp(beginTime);
            String date = new SimpleDateFormat("MM/dd KK:mm a").format(timestamp);
            mDateView.setText("@ " + date);
        } else {
            mDateView.setText("@ " + Measures.STRING_NO_DATA);
        }


        switch (module.getType()) {
            case Module.TYPE_INDOOR:
                mCO2View.setVisibility(View.VISIBLE);
                mPressureView.setVisibility(View.VISIBLE);
                mNoiseView.setVisibility(View.VISIBLE);
                mTemperatureView.setVisibility(View.VISIBLE);
                mHumidityView.setVisibility(View.VISIBLE);

                mRainView.setVisibility(View.GONE);
                mSumRain1View.setVisibility(View.GONE);
                mSumRain24View.setVisibility(View.GONE);

                mWindStrength.setVisibility(View.GONE);
                mWindAngle.setVisibility(View.GONE);
                mGustStrength.setVisibility(View.GONE);
                mGustAngle.setVisibility(View.GONE);

                mTypeView.setText("INDOOR");
                mCO2View.setText("CO2 (ppm): " + measures.getCO2());
                mPressureView.setText("Pressure (mbar): " + measures.getPressure());
                mNoiseView.setText("Noise (db): " + measures.getNoise());
                mHumidityView.setText("Humidity (%): " + measures.getHumidity());
                mTemperatureView.setText("Temp. (°C): " + measures.getTemperature());
                break;
            case Module.TYPE_OUTDOOR:
                mCO2View.setVisibility(View.GONE);
                mPressureView.setVisibility(View.GONE);
                mNoiseView.setVisibility(View.GONE);

                mTemperatureView.setVisibility(View.VISIBLE);
                mHumidityView.setVisibility(View.VISIBLE);

                mRainView.setVisibility(View.GONE);
                mSumRain1View.setVisibility(View.GONE);
                mSumRain24View.setVisibility(View.GONE);

                mWindStrength.setVisibility(View.GONE);
                mWindAngle.setVisibility(View.GONE);
                mGustStrength.setVisibility(View.GONE);
                mGustAngle.setVisibility(View.GONE);

                mTypeView.setText("OUTDOOR");
                mHumidityView.setText("Humidity (%): " + measures.getHumidity());
                mTemperatureView.setText("Temp. (°C): " + measures.getTemperature());
                break;
            case Module.TYPE_RAIN_GAUGE:
                mCO2View.setVisibility(View.GONE);
                mPressureView.setVisibility(View.GONE);
                mNoiseView.setVisibility(View.GONE);
                mTemperatureView.setVisibility(View.GONE);
                mHumidityView.setVisibility(View.GONE);

                mWindStrength.setVisibility(View.GONE);
                mWindAngle.setVisibility(View.GONE);
                mGustStrength.setVisibility(View.GONE);
                mGustAngle.setVisibility(View.GONE);

                mTypeView.setText("RAIN GAUGE");
                mRainView.setText("Actual (mm): " + measures.getRain());
                mSumRain24View.setText("Last 24h (mm): " + measures.getSum_rain_24());
                mSumRain1View.setText("Last hour (mm): " + measures.getSum_rain_1());
                break;
            case Module.TYPE_WIND_GAUGE:
                mCO2View.setVisibility(View.GONE);
                mPressureView.setVisibility(View.GONE);
                mNoiseView.setVisibility(View.GONE);
                mTemperatureView.setVisibility(View.GONE);
                mHumidityView.setVisibility(View.GONE);

                mRainView.setVisibility(View.GONE);
                mSumRain1View.setVisibility(View.GONE);
                mSumRain24View.setVisibility(View.GONE);

                mTypeView.setText("WIND GAUGE");
                mWindAngle.setText("Wind angle (°): " + measures.getWindAngle());
                mWindStrength.setText("Wind strength (km/h): " + measures.getWindStrength());
                mGustAngle.setText("Gust angle (°): " + measures.getGustAngle());
                mGustStrength.setText("Gust strength (km/h): "+measures.getGustStrength());
                break;
        }



        return convertView;
    }
}