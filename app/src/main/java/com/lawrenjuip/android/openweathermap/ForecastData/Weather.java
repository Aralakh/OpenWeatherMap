package com.lawrenjuip.android.openweathermap.ForecastData;

public class Weather {
    int id;
    String main;
    String description;
    String icon;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    //resources have to start with a letter, so all icon names are simply reversed
    public String getResourceName(){
        String reverse = icon;
        return new StringBuffer(reverse).reverse().toString();
    }
}
