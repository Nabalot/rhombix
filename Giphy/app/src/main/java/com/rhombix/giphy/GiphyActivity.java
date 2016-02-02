package com.rhombix.giphy;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;

public class GiphyActivity extends Activity {

    AutoCompleteTextView autoComplete;
    // adapter for auto-complete
    ArrayAdapter<String> myAdapter;

    // for database operations
    DatabaseHandler databaseHandler;
    // just to add some initial value
    String[] item = new String[] {"Please search..."};

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
        try {

            // instantiate database handler
            databaseHandler = new DatabaseHandler(GiphyActivity.this);

            // put sample data to database
            insertSampleData();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        autoComplete = (AutoCompleteTextView) findViewById(R.id.searchText);
            // add the listener so it will tries to suggest while the user types
            autoComplete.addTextChangedListener(new AutoCompleteTextChangedListener(this));

            // set our adapter
            myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, item);
            autoComplete.setAdapter(myAdapter);
		    goButton.setOnClickListener(
		        new View.OnClickListener()
		        {
		            public void onClick(View view)
                    {
                        final String searchText = autoComplete.getText().toString();
		            	System.out.println("SearchText: " + searchText);
		            	Thread thread = new Thread(new Runnable(){
		            	    @Override
		            	    public void run() {
		            	        try {
		            	        	String api_url = String.format(URL, URLEncoder.encode(searchText, "UTF-8"), limit, offset);
		    		            	System.out.println(api_url);
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

    public void insertSampleData(){
        try {
            final InputStream file = getAssets().open("lookahed.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                databaseHandler.create(new LookAhead(line));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    // this function is used in CustomAutoCompleteTextChangedListener.java
    public String[] getItemsFromDb(String searchTerm){

        // add items on the array dynamically
        List<LookAhead> lookAheadList = databaseHandler.read(searchTerm);
        int rowCount = lookAheadList.size();

        String[] item = new String[rowCount];
        int x = 0;

        for (LookAhead record : lookAheadList) {
            item[x] = record.name;
            x++;
        }

        return item;
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
