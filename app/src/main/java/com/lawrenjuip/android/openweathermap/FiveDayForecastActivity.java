package com.lawrenjuip.android.openweathermap;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;

public class FiveDayForecastActivity extends SingleFragmentActivity {
    public static Intent newIntent(Context context){
        return new Intent(context, FiveDayForecastActivity.class);
    }

    @Override
    protected Fragment createFragment(){ return FiveDayForecastFragment.newInstance(); }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch(requestCode){
            case LocationRetriever.ACCESS_FINE_LOCATION_REQUEST:
                if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                    DialogErrorBuilder builder = new DialogErrorBuilder();
                    builder.showDialogError(this);
                }
                break;
        }
    }
}
