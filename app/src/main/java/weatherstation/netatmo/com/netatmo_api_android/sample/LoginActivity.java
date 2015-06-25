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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
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

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import weatherstation.netatmo.com.netatmo_api_android.R;

public class LoginActivity extends ActionBarActivity {

    private Boolean mInProgress = false;

    private String mEmail;
    private String mPassword;

    private EditText mEmailView;
    private EditText mPasswordView;
    private Button mSignInButtonView;

    Handler handler = new Handler();

    public static final String TAG = "LoginActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_login);
        setResult(RESULT_CANCELED);

        mEmailView = (EditText)findViewById(R.id.email);

        mPasswordView = (EditText)findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == 42 || actionId == EditorInfo.IME_NULL){
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mSignInButtonView = (Button)findViewById(R.id.sign_in_button);
        mSignInButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent browserIntent = new Intent();
        browserIntent.setAction(Intent.ACTION_VIEW);

        switch (item.getItemId()){
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

    public void attemptLogin(){
        if(mInProgress){
            return;
        }
        mEmailView.setError(null);
        mPasswordView.setError(null);

        mEmail = mEmailView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(mPassword)){
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }else if(!mEmail.contains("@")){
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if(cancel){
            focusView.requestFocus();
        }else{
            netatmoLogin();
        }


    }


    private void netatmoLogin(){

        final SampleHttpClient sampleHttpClient = new SampleHttpClient(this);
        sampleHttpClient.login(mEmail, mPassword,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject;
                        try {
                            jsonObject= new JSONObject(response);
                            sampleHttpClient.processOAuthResponse(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        setResult(RESULT_OK);
                        finish();
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
                }


        );

}


}
