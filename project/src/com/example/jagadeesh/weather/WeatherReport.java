package com.example.jagadeesh.weather;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.TextView;
import android.graphics.Typeface;


public class WeatherReport extends Activity {

    public String[] message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_report);

        //set fonts
        TextView txt = (TextView) findViewById(R.id.city_result);
        TextView txt2 = (TextView) findViewById(R.id.icon_result);
        TextView txt3 = (TextView) findViewById(R.id.condition_result);
        TextView txt4 = (TextView) findViewById(R.id.today);
        TextView txt5 = (TextView) findViewById(R.id.second);
        TextView txt6 = (TextView) findViewById(R.id.third);
        TextView txt7 = (TextView) findViewById(R.id.fourth);
        TextView txt8 = (TextView) findViewById(R.id.fifth);
        TextView txt9 = (TextView) findViewById(R.id.sunriseIcon);
        TextView txt10 = (TextView) findViewById(R.id.sunriseTime);
        TextView txt11 = (TextView) findViewById(R.id.sunsetIcon);
        TextView txt12 = (TextView) findViewById(R.id.sunsetTime);
        Typeface font = Typeface.createFromAsset(getAssets(), "AliquamREG.ttf");
        Typeface weatherFont = Typeface.createFromAsset(getAssets(), "artill_clean_icons.otf");
        txt.setTypeface(font);
        txt2.setTypeface(weatherFont);
        txt3.setTypeface(font);
        txt4.setTypeface(font);
        txt5.setTypeface(font);
        txt6.setTypeface(font);
        txt7.setTypeface(font);
        txt8.setTypeface(font);
        txt9.setTypeface(weatherFont);
        txt10.setTypeface(font);
        txt11.setTypeface(weatherFont);
        txt12.setTypeface(font);

        //get message from intent
        Intent intent = getIntent();
        message = intent.getStringArrayExtra(MainActivity.EXTRA_MESSAGE);
        setResult(message);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_weather_report, menu);
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

    //set result on screen
    public void setResult(String[] result)
    {
        TextView city = (TextView)findViewById(R.id.city_result);
        city.setText(result[0]);
        TextView icon = (TextView)findViewById(R.id.icon_result);
        icon.setText(result[1]);
        TextView condition = (TextView)findViewById(R.id.condition_result);
        condition.setText(result[2]);
        TextView fcToday = (TextView)findViewById(R.id.today);
        fcToday.setText(result[3]);
        TextView fcSecond = (TextView)findViewById(R.id.second);
        fcSecond.setText(result[4]);
        TextView fcThird = (TextView)findViewById(R.id.third);
        fcThird.setText(result[5]);
        TextView fcForth = (TextView)findViewById(R.id.fourth);
        fcForth.setText(result[6]);
        TextView fcFifth = (TextView)findViewById(R.id.fifth);
        fcFifth.setText(result[7]);
        TextView sunrise = (TextView)findViewById(R.id.sunriseTime);
        sunrise.setText(result[8]);
        TextView sunset = (TextView)findViewById(R.id.sunsetTime);
        sunset.setText(result[9]);
    }
}
