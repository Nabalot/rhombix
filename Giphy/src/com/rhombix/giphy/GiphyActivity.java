package com.rhombix.giphy;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Locale;

import org.json.JSONObject;
import android.annotation.TargetApi;
import android.os.Build;

public class GiphyActivity extends ActionBarActivity {

	EditText searchText;
	Button goButton;
	private final String URL = 
			"http://api.giphy.com/v1/gifs/search?q=%1&api_key=dc6zaTOxFJmzC&limit=%2&offset=%3";
	private String limit = "1";
	private String offset = "0";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_giphy);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
		searchText = (EditText) findViewById(R.id.searchText);
		goButton = (Button)findViewById(R.id.goButton);
		goButton.setOnClickListener(
		        new View.OnClickListener()
		        {
		            public void onClick(View view)
		            {
		            	String api_url = String.format(URL,searchText.getText().toString(),limit, offset);
		            	try {
							URLConnection connection = new java.net.URL(api_url).openConnection();
			            	connection.setRequestProperty("Accept-Charset", "UTF-8");
			            	String gifURL = toJSON(connection);
			            	
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		            	
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
								    JSONObject data = jsonObject.getJSONObject("data");
								    JSONObject images = data.getJSONObject("images");
								    Iterator<String> keys = images.keys();
								    String key = keys.next();
								    JSONObject image = images.getJSONObject(key);
								    return image.getString("url");
						        } catch (final Exception e) {
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
		if (id == R.id.action_settings) {
			return true;
		}
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
			container.addView();
			return rootView;
		}
	}
}
