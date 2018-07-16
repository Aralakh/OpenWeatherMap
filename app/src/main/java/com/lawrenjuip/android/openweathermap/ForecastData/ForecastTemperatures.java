package com.lawrenjuip.android.openweathermap.ForecastData;

public class ForecastTemperatures {
    private double temp;
    private double temp_min;
    private double temp_max;

    public double getTempMinKelvin() { return temp_min; }

    public double getTempMaxKelvin() {
        return temp_max;
    }

    private double convertKelvinToFahrenheit(double temperature){
        return (9.0 / 5.0 * (temperature - 273.0) + 32.0);
    }

    public double getTempFahrenheit(){ return convertKelvinToFahrenheit(temp); }

    public double getTempMinFahrenheit(){ return convertKelvinToFahrenheit(temp_min); }

    public double getTempMaxFahrenheit(){ return convertKelvinToFahrenheit(temp_max); }

}
