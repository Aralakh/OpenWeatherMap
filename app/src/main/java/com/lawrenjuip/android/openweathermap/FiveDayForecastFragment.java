package com.lawrenjuip.android.openweathermap;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.lawrenjuip.android.openweathermap.ForecastData.ForecastItem;

public class FiveDayForecastFragment extends Fragment {
    public static final String FIVE_DAY = "five_day";
    private LocationRetriever mLocationRetriever;
    private List<ForecastItem> mForecastItems = new ArrayList<>();
    private RecyclerView mForecastRecyclerView;
    private ForecastItemAdapter mForecastItemAdapter;
    private Location mLocation = null;
    private WeatherQuery mWeatherQuery = new WeatherQuery();
    public static FiveDayForecastFragment newInstance() { return new FiveDayForecastFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

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

        //hide the five day forecast menu item, enable the current forecast menu item
        MenuItem currentForecast = menu.findItem(R.id.current_forecast);
        currentForecast.setEnabled(true);
        currentForecast.setVisible(true);

        MenuItem fiveDayForecast = menu.findItem(R.id.five_day_forecast);
        fiveDayForecast.setEnabled(false);
        fiveDayForecast.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.current_forecast:
                Intent intent = CurrentForecastActivity.newIntent(getActivity());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_five_day_forecast, container, false);

        mForecastRecyclerView = view.findViewById(R.id.forecast_recycler_view);
        mForecastRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(mLocation != null){
            updateItems();
        }else{
            //calls the AsyncTask once a location has been retrieved
            mLocationRetriever.addListener(new LocationRetriever.LocationRetrieverListener() {
                @Override
                public void onLocationChanged(Location location) {
                    mLocation = location;
                    updateItems();
                }
            });
        }

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        mLocation = mLocationRetriever.getLocation();

        if(mLocation != null) {
            updateItems();
        } else {
            mLocationRetriever.addListener(new LocationRetriever.LocationRetrieverListener() {
                @Override
                public void onLocationChanged(Location location) {
                    mLocation = location;
                    updateItems();
                }
            });
        }
    }

    private void updateItems(){ new FetchFiveDayForecastTask().execute(); }

    private List<ForecastItem> queryWeather(Location location){
        List<ForecastItem> forecastItems = new ArrayList<>();
        if(location != null){
            forecastItems = mWeatherQuery.queryFiveDayForecast(FIVE_DAY, location.getLatitude(), location.getLongitude());
        }
        return forecastItems;
    }

    private class ForecastItemHolder extends RecyclerView.ViewHolder{
        private TextView forecastDate;
        private TextView forecastHighTemp;
        private TextView forecastLowTemp;
        private ImageButton forecastIcon;
        private ForecastItem forecastItem;

        public ForecastItemHolder(LayoutInflater inflater, ViewGroup parent, int viewType){
            super(inflater.inflate(R.layout.forecast_list_item_view, parent, false));

            forecastDate = itemView.findViewById(R.id.forecast_date);
            forecastHighTemp = itemView.findViewById(R.id.forecast_high);
            forecastLowTemp = itemView.findViewById(R.id.forecast_low);
            forecastIcon = itemView.findViewById(R.id.forecast_icon);
        }

        public void bind(ForecastItem item){
            forecastItem = item;
            int resourceId = getContext().getResources().getIdentifier(forecastItem.getWeather().get(0).getResourceName(), "drawable", getContext().getPackageName());
            forecastIcon.setImageResource(resourceId);
            forecastDate.setText(getString(R.string.forecast_date) + " " + forecastItem.getConvertedDate());
            forecastHighTemp.setText(getString(R.string.forecast_high) + " " + String.format("%.2f", (forecastItem.getTemperatures().getTempMaxFahrenheit())));
            forecastLowTemp.setText(getString(R.string.forecast_min) + " " + String.format("%.2f", (forecastItem.getTemperatures().getTempMinFahrenheit())));
        }
    }

    private class ForecastItemAdapter extends RecyclerView.Adapter<ForecastItemHolder>{
        private List<ForecastItem> forecastItems;

        public ForecastItemAdapter(List<ForecastItem> items){
            forecastItems = items;
        }

        @Override
        public ForecastItemHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ForecastItemHolder(layoutInflater, parent, viewType);
        }

        @Override
        public void onBindViewHolder(ForecastItemHolder holder, int position){
            ForecastItem item = forecastItems.get(position);
            holder.bind(item);
        }

        @Override
        public int getItemCount(){ return forecastItems.size(); }
    }

    private class FetchFiveDayForecastTask extends AsyncTask<Void, Void, List<ForecastItem>>{
        @Override
        protected List<ForecastItem> doInBackground(Void... params){ return queryWeather(mLocation); }

        @Override
        protected void onPostExecute(List<ForecastItem> items){
            mForecastItems = items;
            Collections.sort(mForecastItems);
            mForecastItemAdapter = new ForecastItemAdapter(mForecastItems);
            mForecastRecyclerView.setAdapter(mForecastItemAdapter);
        }
    }
}
