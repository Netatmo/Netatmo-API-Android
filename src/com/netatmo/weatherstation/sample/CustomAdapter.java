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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.netatmo.weatherstation.api.model.Measures;
import com.netatmo.weatherstation.api.model.Module;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

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

        mTemperatureView.setText("Temp. (°C): " + measures.getTemperature());

        if (module.getType().equals(Module.TYPE_INDOOR)) {
            if (mCO2View.getVisibility() == View.GONE) {
                mCO2View.setVisibility(View.VISIBLE);
                mPressureView.setVisibility(View.VISIBLE);
                mNoiseView.setVisibility(View.VISIBLE);
            }

            mTypeView.setText("INDOOR");
            mCO2View.setText("CO2 (ppm): " + measures.getCO2());
            mPressureView.setText("Pressure (mbar): " + measures.getPressure());
            mNoiseView.setText("Noise (db): " + measures.getNoise());
        } else {
            if (mCO2View.getVisibility() == View.VISIBLE) {
                mCO2View.setVisibility(View.GONE);
                mPressureView.setVisibility(View.GONE);
                mNoiseView.setVisibility(View.GONE);
            }

            mTypeView.setText("OUTDOOR");
        }

        mHumidityView.setText("Humidity (%): " + measures.getHumidity());

        return convertView;
    }
}
