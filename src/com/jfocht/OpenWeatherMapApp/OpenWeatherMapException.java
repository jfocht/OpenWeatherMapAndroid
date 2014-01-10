package com.jfocht.OpenWeatherMapApp;

public class OpenWeatherMapException extends Exception
{

    public OpenWeatherMapException(String message) {
        super(message);
    }

    public OpenWeatherMapException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
