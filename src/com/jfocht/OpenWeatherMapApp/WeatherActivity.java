package com.jfocht.OpenWeatherMapApp;

import java.io.StringWriter;
import java.io.PrintWriter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class WeatherActivity extends Activity
{

    private OpenWeatherMapService weatherService = new OpenWeatherMapService();

    private ProgressDialog progress;

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

      savedInstanceState.putString(
              "Current", getCurrentTempTextView().getText().toString());
      savedInstanceState.putString(
              "High", getHighTempTextView().getText().toString());
      savedInstanceState.putString(
              "Low", getLowTempTextView().getText().toString());
    }

    public void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState == null) return;

        if (savedInstanceState.containsKey("Current")) {
            getCurrentTempTextView().setText(
                    savedInstanceState.getString("Current"));
            getHighTempTextView().setText(
                    savedInstanceState.getString("High"));
            getLowTempTextView().setText(
                    savedInstanceState.getString("Low"));
        }
    }

    private EditText getLocation() {
        return (EditText) findViewById(R.id.location);
    }

    private Button getButton() {
        return (Button) findViewById(R.id.done);
    }

    private TextView getCurrentTempTextView() {
        return (TextView) findViewById(R.id.textViewTempNow);
    }

    private TextView getHighTempTextView() {
        return (TextView) findViewById(R.id.textViewTempHigh);
    }

    private TextView getLowTempTextView() {
        return (TextView) findViewById(R.id.textViewTempLow);
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
        getCurrentTempTextView().setText(weather.getCurrentTemperature());
        getHighTempTextView().setText(weather.getHighTemperature());
        getLowTempTextView().setText(weather.getLowTemperature());
        clearProgressMaybe();
    }

    private void displayError(Exception e) {
        String msg;
        clearProgressMaybe();
        if (e == null) {
            msg = "Unknown error";
        } else if (e instanceof OpenWeatherMapException) {
            msg = e.getMessage();
        } else {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            msg = sw.toString();
        }
        // TODO present an error message
    }

    private void clearProgressMaybe() {
        if (progress != null) {
            progress.dismiss();
            progress = null;
        }
    }

    private void updateWeather()
    {
        String city = getLocation().getText().toString();
        progress = new ProgressDialog(this);
        progress.setTitle(getString(R.string.loading));
        progress.setMessage(String.format(getString(R.string.loading_weather_fmt), city));
        progress.show();
        weatherService.fetchWeatherInBackground(city, new WeatherCallback());
    }
}
