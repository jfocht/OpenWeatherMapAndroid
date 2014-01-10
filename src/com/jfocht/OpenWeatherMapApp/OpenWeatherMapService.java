package com.jfocht.OpenWeatherMapApp;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Scanner;

import android.os.AsyncTask;

import org.json.JSONObject;


public class OpenWeatherMapService
{

    private static final String WS_ENDPOINT = 
        "http://api.openweathermap.org/data/2.5/weather?q=%s&units=imperial";

    public static interface WeatherCallback {
        public void done(Weather weather, Exception e);
    }

    public void fetchWeatherInBackground(String city, WeatherCallback callback) {
        FetchWeatherTask task = new FetchWeatherTask(WS_ENDPOINT, callback);
        task.execute(city);
    }

}


class FetchWeatherTask extends AsyncTask<String, Void, Weather> {

    private final OpenWeatherMapService.WeatherCallback callback;

    private final String endpoint;

    private static final String DEGREES_F = "\u00b0F";

    private Exception failure;

    public FetchWeatherTask(String endpoint, OpenWeatherMapService.WeatherCallback callback) {
        super();
        this.callback = callback;
        this.endpoint = endpoint;
    }

    public Weather doInBackground(String... cities) {
        HttpURLConnection urlConnection = null;
        String message = "";
        if (cities.length != 1) {
            this.failure = new OpenWeatherMapException("Multiple cities received");
            return null;
        }
        String city = cities[0];
        try {
            final URL url = new URL(String.format(endpoint, city));
            urlConnection = (HttpURLConnection) url.openConnection();
            return handleWeatherResponse(urlConnection);
        } catch (Exception e) {
            this.failure = e;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    private Weather handleWeatherResponse(HttpURLConnection conn)
            throws Exception {
        int status = conn.getResponseCode();
        if (status >= 400) {
            String msg = String.format("Server returned an error code: %d", status);
            throw new OpenWeatherMapException(msg);
        }
        InputStream in = conn.getInputStream();
        InputStream bis = new BufferedInputStream(in);
        return weatherFromJSONStream(bis);
    }

    private Weather weatherFromJSONStream(InputStream is)
            throws OpenWeatherMapException {
        Scanner scanner = new Scanner(is, "UTF-8");
        String jsonString = scanner.useDelimiter("\\A").next();
        JSONObject object;
        try {
            object = new JSONObject(jsonString);
        } catch (org.json.JSONException e) {
            throw new OpenWeatherMapException("Unknown response from server", e);
        }
        JSONObject mainResponse = object.optJSONObject("main");
        if (mainResponse != null) {
            return new Weather(
                    object.optString("name"),
                    mainResponse.optString("temp", "???") + DEGREES_F,
                    mainResponse.optString("temp_max", "???") + DEGREES_F,
                    mainResponse.optString("temp_min", "???") + DEGREES_F);
        }
        String errorMessage = object.optString(
                "message", "Unknown response from server");
        throw new OpenWeatherMapException(errorMessage);
    }

    private String readStackTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public void onPostExecute(Weather weather) {
        callback.done(weather, this.failure);
    }
}
