package com.rhombix.giphy;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.widget.LinearLayout;

public class GiphyActivity extends Activity {

	EditText searchText;
	Button goButton;
	private final String URL = 
			"http://api.giphy.com/v1/gifs/search?q=%s&api_key=dc6zaTOxFJmzC&limit=%s&offset=%s";
	private String limit = "1";
	private String offset = "0";
	public static Activity activity;
	public static String gifUrl = "";
    public WebView webview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_giphy);
		goButton = (Button)findViewById(R.id.goButton);
		activity = this;
        webview = (WebView)findViewById(R.id.webView);
		goButton.setOnClickListener(
		        new View.OnClickListener()
		        {
		            public void onClick(View view)
		            {
		            	searchText = (EditText) findViewById(R.id.searchText);
		            	System.out.println("SearchText: " + searchText);
		            	Thread thread = new Thread(new Runnable(){
		            	    @Override
		            	    public void run() {
		            	        try {
		            	        	String api_url = URLEncoder.encode(String.format(URL,searchText.getText().toString(),limit, offset), "UTF-8");
		    		            	URLConnection connection = new java.net.URL(api_url).openConnection();
					            	connection.setRequestProperty("Accept-Charset", "UTF-8");
					            	gifUrl = toJSON(connection);
                                    GiphyActivity.activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            webview.loadUrl(gifUrl);
                                        }
                                    });

								} catch (MalformedURLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
		            	    }
		            	});
		            	thread.start();
		            }

					private String toJSON(URLConnection connection) throws IOException {
						try {
							InputStream response = connection.getInputStream();
							 try {
								 BufferedReader streamReader = new BufferedReader(new InputStreamReader(response, "UTF-8"));
								 StringBuilder responseStrBuilder = new StringBuilder();

								    String inputStr;
								    while ((inputStr = streamReader.readLine()) != null)
								        responseStrBuilder.append(inputStr);

								    JSONObject jsonObject = new JSONObject(responseStrBuilder.toString());
								    JSONArray data = (JSONArray)jsonObject.get("data");
                                    Object firstData = data.get(0);
								    JSONObject images = ((JSONObject) firstData).getJSONObject("images");
								    Iterator<String> keys = images.keys();
								    String key = keys.next();
								    JSONObject image = images.getJSONObject(key);
								    return image.getString("url");
						        } catch (final Exception e) {
                                 e.printStackTrace();
						        }
						}
						finally {
						}
						return null;
					}
		        }
		        );
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.giphy, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_giphy, container, false);
			container.addView(new GifView(GiphyActivity.activity, GiphyActivity.gifUrl));
			return rootView;
		}
	}
}
