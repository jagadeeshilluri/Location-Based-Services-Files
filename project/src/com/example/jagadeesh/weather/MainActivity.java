package com.example.jagadeesh.weather;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Looper;
import android.provider.Settings;
import android.app.Activity;
import android.os.Bundle;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.concurrent.ExecutionException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.graphics.Typeface;
import android.app.ProgressDialog;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.SimpleAdapter;




public class MainActivity extends Activity {

    public final static String EXTRA_MESSAGE = "com.example.jagadeesh.weather.MESSAGE";
    public static String jsonData;
    public static Boolean errorFlag = false;
    static String API_KEY = "AIzaSyBdyNq9gkvd-Swfy49yVc9PN4aMHWS81Cw";
    Animation animZoomin;
    AutoCompleteTextView inputPlaces;
    PlacesTask placesTask;
    ParserTask parserTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //change font
        TextView txt = (TextView) findViewById(R.id.city_field);
        TextView txt2 = (TextView) findViewById(R.id.buttonGo);
        TextView txt3 = (TextView) findViewById(R.id.buttonCurLoc);
        TextView txt4 = (TextView) findViewById(R.id.or);
        EditText editText = (EditText) findViewById(R.id.city_field);
        Typeface font = Typeface.createFromAsset(getAssets(), "AliquamREG.ttf");
        txt.setTypeface(font);
        txt2.setTypeface(font);
        txt3.setTypeface(font);
        txt4.setTypeface(font);

        //set background zoom in animation
        animZoomin = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        ImageView ll = (ImageView) findViewById(R.id.bcgImage);
        ll.startAnimation(animZoomin);

        //ENTER key <--> GO button
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    try {
                        sendMessage();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });

        //CURRENT LOCATION -> GPS
        Button btnShowLocation = (Button) findViewById(R.id.buttonCurLoc);
        btnShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // create class object
                GPSTracker gps = new GPSTracker(MainActivity.this);

                // check if GPS enabled
                if (gps.canGetLocation()) {

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    convertToZip convert = new convertToZip(longitude, latitude);
                    String zipCode = convert.getZip();
                    String QUERY_YAHOO_API_GPS = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22" + zipCode + "%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
                    launchRingDialog(QUERY_YAHOO_API_GPS);

                    //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                } else {
                    //ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }

            }
        });

        //AUTO complete places
        inputPlaces = (AutoCompleteTextView) findViewById(R.id.city_field);
        inputPlaces.setThreshold(1);

        inputPlaces.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                placesTask = new PlacesTask();
                placesTask.execute(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
    }
        //=============================================================================
        /** A method to download json data from url */
        private String downloadUrl(String strUrl) throws IOException
        {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try{
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while( ( line = br.readLine()) != null){
                    sb.append(line);
                }

                data = sb.toString();

                br.close();

            }catch(Exception e){
                Log.d("Exception while downloading url", e.toString());
            }finally{
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }

        // Fetches all places from GooglePlaces AutoComplete Web Service
        private class PlacesTask extends AsyncTask<String, Void, String>
        {

            @Override
            protected String doInBackground(String... place) {
                // For storing data from web service
                String data = "";

                // Obtain browser key from https://code.google.com/apis/console
                String key = "key="+API_KEY;


                String input="";

                try {
                    input = "input=" + URLEncoder.encode(place[0], "utf-8");
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }

                // place type to be searched
                String types = "types=geocode";

                // Sensor enabled
                String sensor = "sensor=false";

                // Building the parameters to the web service
                String parameters = input+"&"+types+"&"+sensor+"&"+key;

                // Output format
                String output = "json";

                // Building the url to the web service
                String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"+output+"?"+parameters;

                try{
                    // Fetching the data from we service
                    data = downloadUrl(url);
                }catch(Exception e){
                    Log.d("Background Task",e.toString());
                }
                return data;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                // Creating ParserTask
                parserTask = new ParserTask();

                // Starting Parsing the JSON string returned by Web Service
                parserTask.execute(result);
            }
        }

        // A class to parse the Google Places in JSON format
        private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>
        {

            JSONObject jObject;

            @Override
            protected List<HashMap<String, String>> doInBackground(String... jsonData) {

                List<HashMap<String, String>> places = null;

                PlaceJSONParser placeJsonParser = new PlaceJSONParser();

                try{
                    jObject = new JSONObject(jsonData[0]);

                    // Getting the parsed data as a List construct
                    places = placeJsonParser.parse(jObject);

                }catch(Exception e){
                    Log.d("Exception",e.toString());
                }
                return places;
            }

            @Override
            protected void onPostExecute(List<HashMap<String, String>> result) {

                String[] from = new String[] { "description"};
                int[] to = new int[] { android.R.id.text1 };

                // Creating a SimpleAdapter for the AutoCompleteTextView
                SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);

                // Setting the adapter
                inputPlaces.setAdapter(adapter);
            }
        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String beforeSendMessage(String inputCity)
    {
        String[] input = inputCity.split(",");
        if(input.length>1)
            return input[0]+input[1];
        else
            return inputCity;
    }

    //Called when "Go" button clicked
    public void sendMessage(View view) throws UnsupportedEncodingException
    {
        EditText city_name = (EditText) findViewById(R.id.city_field);
        String city = URLEncoder.encode(beforeSendMessage(city_name.getText().toString()),"UTF-8");

        String QUERY_YAHOO_API = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22" + city + "%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
        launchRingDialog(QUERY_YAHOO_API);
    }
    //called when ENTER pressed on keyboard
    public void sendMessage() throws UnsupportedEncodingException
    {
        EditText city_name = (EditText) findViewById(R.id.city_field);
        String city = URLEncoder.encode(beforeSendMessage(city_name.getText().toString()),"UTF-8");

        String QUERY_YAHOO_API = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22" + city + "%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
        launchRingDialog(QUERY_YAHOO_API);
        if(errorFlag==true)
            openAlert();

        System.out.println("errorflag send = "+errorFlag);
    }

    public void callAPI(String url)
    {
        String aString = null;
        Intent intent = new Intent(this, WeatherReport.class);

        try {
            aString = new AsynTaskJson().execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(errorFlag==false)
        {
            String[] message = null;
            FetchData fetchData = new FetchData(jsonData);
            try {
                message = fetchData.readData();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
        }
    }

    public void launchRingDialog(final String URL)
    {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(MainActivity.this, "Please wait ...", "Loading ...", true);
        ringProgressDialog.setCancelable(true);
        new Thread(new Runnable()
        {
            @Override
            public void run() {
                try {
                    callAPI(URL);
                    //Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ringProgressDialog.dismiss();
                if(errorFlag == true)
                {
                    errorFlag = false;
                    Looper.prepare();
                    openAlert();
                    Looper.loop();
                }
            }
        }).start();
    }

    public class AsynTaskJson extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(params[0]);
                HttpResponse response = client.execute(get);
                HttpEntity entity = response.getEntity();
                jsonData = EntityUtils.toString(entity);

                try {
                    JSONObject jsonObj = new JSONObject(jsonData);
                    JSONObject query = jsonObj.getJSONObject("query");
                    String count = query.getString("count");
                    if (count.equals("1"))
                        errorFlag = false;
                    else
                    {
                        errorFlag = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void openAlert()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

        alertDialogBuilder.setTitle("Notice");
        alertDialogBuilder.setMessage("No matching input. Please enter correct CITY or ZIPCODE.");

        // set neutral button: Exit the app message
        alertDialogBuilder.setNeutralButton("OK",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
