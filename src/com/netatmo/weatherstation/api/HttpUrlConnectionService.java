package com.netatmo.weatherstation.api;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import com.loopj.android.http.RequestParams;

import android.util.Log;

public abstract class HttpUrlConnectionService {

	public static final String TAG = "HttpUrlConnectionService: ";
	
	
	public abstract void onSuccess(final String response);
	public abstract void onFailure(final String response);
	
	String user_agent;
	
	public HttpUrlConnectionService(String url, HashMap<String, String>  url_params, String user_agent) {
		this.user_agent = user_agent;
		httpUrlConnectionRequest(url, url_params);
	}
	public void httpUrlConnectionRequest(final String url, final HashMap<String, String>  url_params) {
		final String M = "httpUrlConnectionRequest: " ; 
		
		new Thread(new Runnable() {
			
		@Override
		public void run() {
			
		try {

			
			
			URL URL_OBJECT = new URL(url);
			final HttpURLConnection connection =  (HttpURLConnection) URL_OBJECT.openConnection();
			
			if(connection instanceof HttpsURLConnection) {
				HttpsURLConnection connection_https = (HttpsURLConnection) connection;
				connection_https.setHostnameVerifier(new HostnameVerifier() {
				
				@Override
				public boolean verify(String hostname, SSLSession session) {
					Log.i(TAG,M + "SKIP VERIFY HOST");
					return true;
			}});
			}
			
			connection.setDefaultUseCaches(false);
			connection.setUseCaches(false);
			
			
			connection.setRequestProperty("User-Agent", user_agent );

			
			
			connection.setReadTimeout(10000);
			connection.setConnectTimeout(15000);
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);

			
			
//			/* params */
			if( !applyParams(connection, url_params) ) {
			}
				


			
			/* RESPONSE : output */
			try {
				

				
				final int http_code = connection.getResponseCode();
				Log.i(TAG, M + "http_code: " + http_code );
			

				
					try {
					if(http_code == 200 ) { /* good code */ 
						String response = readStream(connection.getInputStream());
						connection.disconnect();
						onSuccess( response );
						
				
					} else { /* error code*/
						String response = readStream(connection.getErrorStream()) ;
						connection.disconnect();
						onFailure( response );
					}
					} catch (Exception e) {
						e.printStackTrace();
						onFailure( null );
					}
		
				
				
			} catch (Exception e) {
				e.printStackTrace();
				onFailure( null );
			}
			
			
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			onFailure( null );
		}
		
		}}).start();
	}

	
	
	private static String readStream(InputStream in) {
		final String M = "readStream: ";
		Log.i(TAG, M + " bgn");
		
		String rv = null;
		
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = null;
		  try {
		    reader = new BufferedReader(new InputStreamReader(in));
		    String line = "";
		    
		    while ((line = reader.readLine()) != null) 
		    	sb.append(line);
		      rv = sb.toString();
		      Log.i(TAG, "out:" + rv);
		      
		    
		  } catch (Exception e) {
			  e.printStackTrace();
		  }
		  
		  try { reader.close(); } catch (Exception ee) { ee.printStackTrace();  }
		  
		  
		  Log.i(TAG, M + " end");
		  
		  return rv;
	}
	
	private static boolean applyParams(HttpURLConnection connection, HashMap<String, String> params_hash) {
		try {
		String params = createParamsLine(params_hash);
		
		OutputStream os = connection.getOutputStream();
		BufferedWriter writer = new BufferedWriter(
		        new OutputStreamWriter(os, "UTF-8"));
		writer.write(params);
		writer.close();
		os.close();
		
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			
			return false;
		}
	}
	
	private static String createParamsLine(HashMap<String, String> p) throws UnsupportedEncodingException
	{
	    StringBuilder result = new StringBuilder();
	    boolean first = true;

	    if ( p.size() > 0)
	    for (Entry<String,String> pair : p.entrySet() ) {
	    	
	        if (first) 
	            first = false;
	        else
	            result.append("&");

	        result.append(URLEncoder.encode(pair.getKey(), "UTF-8")).append("=").append(URLEncoder.encode(pair.getValue(), "UTF-8"));
	    }

	    return result.toString();
	}

	public static RequestParams createParams(HashMap<String, String> a){
		Log.i(TAG, "createParams: size:" + a.keySet().size() );
		
		RequestParams rp = new RequestParams();
		
		
		for (java.util.Map.Entry<String, String> pair : a.entrySet())
			rp.put(pair.getKey(), pair.getValue());
		
		return rp;
	}
	
}
