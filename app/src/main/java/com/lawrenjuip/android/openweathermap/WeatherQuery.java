package com.lawrenjuip.android.openweathermap;

import android.net.Uri;
import android.util.Log;
import com.google.gson.Gson;
import com.lawrenjuip.android.openweathermap.ForecastData.ForecastItem;
import com.lawrenjuip.android.openweathermap.ForecastData.FiveDayForecastListResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherQuery {
    private static final String TAG = "WeatherQuery";
    private static final String API_KEY = "f121615ddfc1f4f5d0d54c7729d5430d";
    private static final Uri FORECAST = Uri.parse("http://api.openweathermap.org/data/2.5/forecast");
    private static final Uri CURRENT = Uri.parse("http://api.openweathermap.org/data/2.5/weather");

    private byte[] getUrlBytes(String urlSpec) throws IOException{
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while((bytesRead = in.read(buffer)) > 0){
                out.write(buffer, 0, bytesRead);
            }

            out.close();
            return out.toByteArray();
        }finally{
            connection.disconnect();
        }
    }

    public List<ForecastItem> queryFiveDayForecast(String query, Double latitude, Double longitude){
        String url = buildUrl(query, latitude, longitude);
        return downloadFiveDayForecastItems(url);
    }

    public ForecastItem queryCurrentForecast(String query, Double latitude, Double longitude){
        String url = buildUrl(query, latitude, longitude);
        return downloadCurrentForecastItem(url);
    }

    private String getUrlString(String urlSpec)throws IOException{
        return new String(getUrlBytes(urlSpec));
    }

    //build the query depending on if you want a 5-day or 1-day forecast
    private String buildUrl(String method, double latitude, double longitude){
        Uri.Builder builder;

        if(method.equals(FiveDayForecastFragment.FIVE_DAY)) {
            builder = FORECAST.buildUpon();
        }else{
            builder = CURRENT.buildUpon();
        }

        builder.appendQueryParameter("lon", Double.toString(longitude))
                .appendQueryParameter("lat", Double.toString(latitude))
                .appendQueryParameter("APPID", API_KEY);
        return builder.build().toString();
    }

    private List<ForecastItem> downloadFiveDayForecastItems(String url){
        List<ForecastItem> items = new ArrayList<>();
        try{
            String jsonString = getUrlString(url);

            Log.i(TAG, "Received JSON: " + jsonString);

            Gson gson = new Gson();
            FiveDayForecastListResult result = gson.fromJson(jsonString, FiveDayForecastListResult.class);
            items = result.getForecastItems();

            items = getDailyForecasts(items);
        }catch(IOException ioe){
            Log.e(TAG, "Failed to receive forecast", ioe);
        }catch(Exception e){
            Log.e(TAG, "Failed to parse JSON: " + e.getMessage());
        }

        return items;
    }

    // loop over each day's 3hour temperatures to get the high & low temp of the day
    private ForecastItem getFiveDayForecastHighAndLowTemp(List<ForecastItem> items){
        ForecastItem dailyForecastItem = items.get(0);
        double minTemp = items.get(0).getTemperatures().getTempMinKelvin();
        double maxTemp = items.get(0).getTemperatures().getTempMaxKelvin();

        for(int i = 0; i < items.size(); i++){
            if(items.get(i).getTemperatures().getTempMaxKelvin() > maxTemp){
                maxTemp = items.get(i).getTemperatures().getTempMaxKelvin();
            }
            if(items.get(i).getTemperatures().getTempMinKelvin() < minTemp){
                minTemp = items.get(i).getTemperatures().getTempMinKelvin();
            }
        }
        return dailyForecastItem;
    }

    //each day has 3hr intervals of data which is 8 results per day. separate each day's data to get the high & low temps for each day
    private List<ForecastItem> getDailyForecasts(List<ForecastItem> items){
        Map<String, List<ForecastItem>> forecastsByDate = new HashMap<>();
        for (ForecastItem item : items){
            String key = item.getConvertedDate();

            List<ForecastItem> forecastsForDay = forecastsByDate.get(key);
            if (forecastsForDay == null) {
                forecastsForDay = new ArrayList<>();
                forecastsForDay.add(item);
                forecastsByDate.put(key, forecastsForDay);
            } else {
                forecastsForDay.add(item);
            }
        }

        List<ForecastItem> minMaxForecastTempByDay = new ArrayList<>();

        for(Map.Entry<String, List<ForecastItem>> temperatureSamplesForDay : forecastsByDate.entrySet()){
            minMaxForecastTempByDay.add(getFiveDayForecastHighAndLowTemp(temperatureSamplesForDay.getValue()));
        }

        return minMaxForecastTempByDay;
    }

    private ForecastItem downloadCurrentForecastItem(String url){
        ForecastItem item = new ForecastItem();

        try {
            String jsonString = getUrlString(url);

            Log.i(TAG, "Received JSON: " + jsonString);
            Gson gson = new Gson();
            item = gson.fromJson(jsonString, ForecastItem.class);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to receive forecast", ioe);
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse JSON: " + e.getMessage());
        }
        return item;
    }

}
