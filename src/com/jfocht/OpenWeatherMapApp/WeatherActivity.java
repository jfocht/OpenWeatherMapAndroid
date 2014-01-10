package com.jfocht.OpenWeatherMapApp;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Scanner;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;


public class WeatherActivity extends Activity
{

    private static final String WS_ENDPOINT = 
        "http://api.openweathermap.org/data/2.5/weather?q=%s&units=imperial";

    private static final String DEGREES_F = "\u00b0F";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        registerCallbacks();
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
        getButton().setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateWeather();
            }
        });
    }

    public void displayWeather(String weather)
    {
        getWeather().setText(weather);
    }

    private void updateWeather()
    {
        new UpdateWeatherTask().execute(getLocation().getText().toString());
    }

    private class UpdateWeatherTask extends AsyncTask<String, Void, String> {

        private Exception failure;

        public String doInBackground(String... cities) {
            HttpURLConnection urlConnection = null;
            String message = "";
            if (cities.length != 1) {
                return "Please enter one city";
            }
            String city = cities[0];
            try {
                final URL url = new URL(String.format(WS_ENDPOINT, city));
                urlConnection = (HttpURLConnection) url.openConnection();
                message = handleWeatherResponse(urlConnection);
            } catch (Exception e) {
                this.failure = e;
                message = readStackTrace(e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return message;
        }

        private String handleWeatherResponse(HttpURLConnection conn)
                throws Exception {
            int status = conn.getResponseCode();
            if (status >= 400) {
                return String.format("Server returned an error code: %d", status);
            }
            InputStream in = conn.getInputStream();
            InputStream bis = new BufferedInputStream(in);
            return messageFromJSONStream(bis);
        }

        private String messageFromJSONStream(InputStream is) {
            Scanner scanner = new Scanner(is, "UTF-8");
            String jsonString = scanner.useDelimiter("\\A").next();
            JSONObject object;
            try {
                object = new JSONObject(jsonString);
            } catch (org.json.JSONException e) {
                return "Unknown response from server";
            }
            JSONObject mainResponse = object.optJSONObject("main");
            if (mainResponse != null) {
                StringBuilder sb = new StringBuilder();
                sb.append(object.optString("name"));
                sb.append("\n");
                sb.append("Right Now: ");
                sb.append(mainResponse.optString("temp", "???"));
                sb.append(DEGREES_F);
                sb.append("\nHigh: ");
                sb.append(mainResponse.optString("temp_max", "???"));
                sb.append(DEGREES_F);
                sb.append("\nLow: ");
                sb.append(mainResponse.optString("temp_min", "???"));
                sb.append(DEGREES_F);
                return sb.toString();
            }
            return object.optString("message", "Unknown response from server");
        }

        private String readStackTrace(Throwable e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            return sw.toString();
        }

        public void onPostExecute(String weather) {
            displayWeather(weather);
        }
    }
}
