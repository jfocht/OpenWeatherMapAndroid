package com.jfocht.OpenWeatherMapApp;

import java.io.StringWriter;
import java.io.PrintWriter;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class WeatherActivity extends Activity
{

    private static final String DEGREES_F = "\u00b0F";

    private OpenWeatherMapService weatherService = new OpenWeatherMapService();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        registerCallbacks();
        restoreState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
      super.onSaveInstanceState(savedInstanceState);

      savedInstanceState.putString("Weather", getWeather().getText().toString());
    }

    public void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState == null) return;

        if (savedInstanceState.containsKey("Weather")) {
            getWeather().setText(savedInstanceState.getString("Weather"));
        }
    }

    private EditText getLocation() {
        return (EditText) findViewById(R.id.location);
    }

    private TextView getWeather() {
        return (TextView) findViewById(R.id.weather);
    }

    private Button getButton() {
        return (Button) findViewById(R.id.done);
    }

    private void registerCallbacks()
    {
        getButton().setOnClickListener(new DoneListener());
        getLocation().setOnEditorActionListener(new EnterListener());
    }

    private class DoneListener implements View.OnClickListener
    {
        public void onClick(View v) {
            updateWeather();
        }
    }

    private class EnterListener implements TextView.OnEditorActionListener
    {
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            updateWeather();
            return true;
        }
    }

    private class WeatherCallback implements OpenWeatherMapService.WeatherCallback
    {
        public void done(Weather weather, Exception e) {
            if (weather != null) {
                displayWeather(weather);
            } else {
                displayError(e);
            }
        }
    }

    private void displayWeather(Weather weather)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(weather.getCityName());
        sb.append("\n");
        sb.append("Right Now: ");
        sb.append(weather.getCurrentTemperature());
        sb.append(DEGREES_F);
        sb.append("\nHigh: ");
        sb.append(weather.getHighTemperature());
        sb.append(DEGREES_F);
        sb.append("\nLow: ");
        sb.append(weather.getLowTemperature());
        sb.append(DEGREES_F);
        getWeather().setText(sb.toString());
    }

    private void displayError(Exception e) {
        String msg;
        if (e == null) {
            msg = "Unknown error";
        } else if (e instanceof OpenWeatherMapException) {
            msg = e.getMessage();
        } else {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            msg = sw.toString();
        }
        getWeather().setText(msg);
    }

    private void updateWeather()
    {
        String city = getLocation().getText().toString();
        weatherService.fetchWeatherInBackground(city, new WeatherCallback());
    }
}
