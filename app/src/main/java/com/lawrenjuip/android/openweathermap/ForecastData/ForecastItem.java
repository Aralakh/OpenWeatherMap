package com.lawrenjuip.android.openweathermap.ForecastData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ForecastItem implements Comparable<ForecastItem>{
    int id;
    List<Weather> weather;
    ForecastTemperatures main;
    Long dt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public ForecastTemperatures getTemperatures() {
        return main;
    }

    public String getConvertedDate(){
        Date date = new Date(dt * 1000);
        String convertedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);

        return convertedDate;
    }

    @Override
    public int compareTo(ForecastItem forecastItem){
        if(dt == null || forecastItem.dt == null){
            return 0;
        }
        return dt.compareTo(forecastItem.dt);
    }

}
