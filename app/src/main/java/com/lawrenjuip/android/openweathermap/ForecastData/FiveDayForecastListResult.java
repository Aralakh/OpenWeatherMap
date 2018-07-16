package com.lawrenjuip.android.openweathermap.ForecastData;

import java.util.List;

public class FiveDayForecastListResult {
    List<ForecastItem> list;

    public List<ForecastItem> getForecastItems() {
        return list;
    }

//    public void setList(List<ForecastItem> list) {
//        this.list = list;
//    }

}
