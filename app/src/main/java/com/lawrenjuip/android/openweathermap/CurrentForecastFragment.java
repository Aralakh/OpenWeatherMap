package com.lawrenjuip.android.openweathermap;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lawrenjuip.android.openweathermap.ForecastData.ForecastItem;

public class CurrentForecastFragment extends Fragment {
    public static final String CURRENT = "current";
    private ForecastItem mCurrentForecastItem = new ForecastItem();
    private LocationRetriever mLocationRetriever;
    private Location mLocation = null;
    private WeatherQuery mWeatherQuery = new WeatherQuery();
    private TextView mCurrentTempTextView;

    public static CurrentForecastFragment newInstance() { return new CurrentForecastFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //must retain instance due to AsyncTask
        setRetainInstance(true);
        setHasOptionsMenu(true);

        //check if user has allowed location permissions & that GPS is enabled
        mLocationRetriever = new LocationRetriever(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_forecast_selector, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);

        //hide the current forecast menu item, enable five day forecast menu item
        MenuItem currentForecast = menu.findItem(R.id.current_forecast);
        currentForecast.setEnabled(false);
        currentForecast.setVisible(false);

        MenuItem fiveDayForecast = menu.findItem(R.id.five_day_forecast);
        fiveDayForecast.setEnabled(true);
        fiveDayForecast.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.five_day_forecast:
                Intent intent = FiveDayForecastActivity.newIntent(getActivity());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        mLocation = mLocationRetriever.getLocation();

        if(mLocation != null) {
            updateItem();
        } else {
            //calls the AsyncTask once a location has been retrieved
            mLocationRetriever.addListener(new LocationRetriever.LocationRetrieverListener() {
                @Override
                public void onLocationChanged(Location location) {
                    mLocation = location;
                    updateItem();
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_current_forecast, container, false);

        mCurrentTempTextView = view.findViewById(R.id.current_temperature);
        if(mLocation != null){
            updateItem();
        }else{
            mLocationRetriever.addListener(new LocationRetriever.LocationRetrieverListener() {
                @Override
                public void onLocationChanged(Location location) {
                    mLocation = location;
                    updateItem();
                }
            });
        }
        return view;
    }

    private void updateItem(){
        new FetchForecastTask().execute();
    }

    private ForecastItem queryWeather(Location location) {
        ForecastItem item = new ForecastItem();
        if (location != null) {
            item = mWeatherQuery.queryCurrentForecast(CURRENT, location.getLatitude(), location.getLongitude());
        }
        return item;
    }

    private class FetchForecastTask extends AsyncTask<Void, Void, ForecastItem> {
        @Override
        protected ForecastItem doInBackground(Void... params){
            return queryWeather(mLocation);
        }

        @Override
        protected void onPostExecute(ForecastItem item){
            mCurrentForecastItem = item;
            mCurrentTempTextView.setText(String.format("%.2f",(mCurrentForecastItem.getTemperatures().getTempFahrenheit())));
        }
    }
}
