package com.jfocht.OpenWeatherMapApp;

public class Weather {

    private final String cityName;

    private final String currentTemperature;

    private final String highTemperature;

    private final String lowTemperature;

    public Weather(String cityName, String currentTemperature,
                   String highTemperature, String lowTemperature) {
        this.cityName = cityName;
        this.currentTemperature = currentTemperature;
        this.highTemperature = highTemperature;
        this.lowTemperature = lowTemperature;
    }

    public String getCityName() {
        return this.cityName;
    }

    public String getCurrentTemperature() {
        return this.currentTemperature;
    }

    public String getHighTemperature() {
        return this.highTemperature;
    }

    public String getLowTemperature() {
        return this.lowTemperature;
    }

}
