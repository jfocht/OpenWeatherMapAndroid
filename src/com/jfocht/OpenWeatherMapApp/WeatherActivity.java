package com.jfocht.OpenWeatherMapApp;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class WeatherActivity extends Activity
{

    private UpdateWeatherTask updateWeatherTask = null;

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

    private class WeatherCompleteListener implements UpdateWeatherTask.OnCompleteListener
    {
        public void onComplete(String weather) {
            displayWeather(weather);
        }
    }

    public void displayWeather(String weather)
    {
        getWeather().setText(weather);
    }

    private void updateWeather()
    {
        if (updateWeatherTask != null) {
            updateWeatherTask.cancel(true);
        }
        updateWeatherTask = new UpdateWeatherTask(new WeatherCompleteListener());
        updateWeatherTask.execute(getLocation().getText().toString());
    }
}
