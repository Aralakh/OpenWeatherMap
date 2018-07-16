package com.lawrenjuip.android.openweathermap;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;


public class CurrentForecastActivity extends SingleFragmentActivity{
    public static Intent newIntent(Context context){
        return new Intent(context, CurrentForecastActivity.class);
    }

    @Override
    protected Fragment createFragment(){ return CurrentForecastFragment.newInstance(); }
}
