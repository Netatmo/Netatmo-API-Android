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

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.netatmo.weatherstation.api.NetatmoResponseHandler;

import org.json.JSONObject;

public class LoginActivity extends Activity {
    private Boolean mInProgress = false;

    private String mEmail;
    private String mPassword;

    private EditText mEmailView;
    private EditText mPasswordView;
    private Button mSignInButtonView;

    public static String TAG = "LoginActivity: ";
    
    Handler handler = new Handler();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        setResult(RESULT_CANCELED);

        mEmailView = (EditText) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mSignInButtonView = (Button) findViewById(R.id.sign_in_button);
        mSignInButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent browserIntent = new Intent();
        browserIntent.setAction(Intent.ACTION_VIEW);

        switch (item.getItemId()) {
            case R.id.action_forgot_password:
                browserIntent.setData(Uri.parse("https://auth.netatmo.com/access/lostpassword"));
                startActivity(browserIntent);
                return true;
            case R.id.action_create_account:
                browserIntent.setData(Uri.parse("https://auth.netatmo.com/access/signup"));
                startActivity(browserIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void attemptLogin() {
        if (mInProgress) {
            return;
        }

        mEmailView.setError(null);
        mPasswordView.setError(null);

        mEmail = mEmailView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mEmail)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!mEmail.contains("@")) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        
        
        if (cancel) {
            focusView.requestFocus();
        } else {
            netatmoLogin();
        }
    }

    private void netatmoLogin() {
    	final String M = "netatmoLogin: ";
    	Log.i(TAG,M);
    	
        final SampleHttpClient httpClient = new SampleHttpClient(this);

        // NetatmoResponseHandler parses and handles the response.
        // You can also use JsonHttpResponseHandler and process the response as you wish.
        httpClient.login(mEmail, mPassword, new NetatmoResponseHandler(httpClient,
                NetatmoResponseHandler.REQUEST_LOGIN, null) {
            @Override
            public void onStart() {
            	
            	Log.i(TAG,M + " onStart:");
            	
            	
                super.onStart();
                
                
                
                handler.post(new Runnable() {
					
					@Override	public void run() {

		                setProgressBarIndeterminateVisibility(Boolean.TRUE);
		                mSignInButtonView.setVisibility(View.GONE);
		                mInProgress = true;						
				}});

            }

            @Override
            public void onLoginResponse() {
            	Log.i(TAG,M + " onLoginResponse:");
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {
            	Log.i(TAG,M + " onFailure:");
            	
                super.onFailure(e, errorResponse);
                
                Log.i(TAG,M + " onFailure:");
                handler.post(new Runnable() {
					
					@Override	public void run() {
		                
		                mEmailView.setError(getString(R.string.error_bad_credentials));
		                mEmailView.requestFocus();				
				}});
                
                

            }

            @Override
            public void onFinish() {
            	Log.i(TAG,M + " onFinish:");
            	
                super.onFinish();
                
                
                handler.post(new Runnable() {
					
					@Override	public void run() {
					
		                setProgressBarIndeterminateVisibility(Boolean.FALSE);
		                mSignInButtonView.setVisibility(View.VISIBLE);
		                mInProgress = false;
					}});

            }
        });
    }
}
