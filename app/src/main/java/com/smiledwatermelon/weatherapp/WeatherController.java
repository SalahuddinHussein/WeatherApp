package com.smiledwatermelon.weatherapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class WeatherController extends AppCompatActivity {

    final int REQ_CODE=112;
    // Base URL for the OpenWeatherMap API.
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";

    // App ID to use OpenWeather data
    final String APP_ID = "e72ca729af228beabd5d20e3b7749713";

    // set location provider

    final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;

    // Time between location updates (5000 milliseconds or 5 seconds)
    final long MIN_TIME = 5000;

    // Distance between location updates (1000m or 1km)
    final float MIN_DISTANCE = 1000;


    // Member Variables:
    boolean mUseLocation = true;
    TextView mCityLabel;
    ImageView mWeatherImage;
    TextView mTemperatureLabel;

    // Declaring a LocationManager and a LocationListener here:
    LocationManager mLocationManager;
    LocationListener mLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller_layout);
        mCityLabel=findViewById(R.id.locationTV);
        mWeatherImage=findViewById(R.id.weatherSymbolIV);
        mTemperatureLabel=findViewById(R.id.tempTV);

        ImageButton changeCity =findViewById(R.id.changeCityButton);

        changeCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent=new Intent(WeatherController.this,ChangeCityController.class);
                startActivity(myIntent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Weather", "on resume called");
        Intent myIntent=getIntent();
        String city=myIntent.getStringExtra("City");

        if(city!=(null)){
            getWeatherForNewCity(city);
            Log.d("Weather","new city comes now :)");
        }else {

            Log.d("Weather", "Getting current Location ");
            getWeatherForCurrentLocation();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

       if(mLocationManager!=null)mLocationManager.removeUpdates(mLocationListener);
    }

    private void getWeatherForNewCity(String city) {
        RequestParams params=new RequestParams();
        params.put("q",city);
        params.put("appid",APP_ID);
        getWeatherParameters(params);
    }

    private void getWeatherForCurrentLocation() {

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                String Longitude= String.valueOf(location.getLongitude());
                String Latitude=String.valueOf(location.getLatitude());


                Log.d("Weather", "Location changed. right?!! new LON="+Longitude+" New LAT="+Latitude);
                RequestParams params=new RequestParams();
                params.put("lat",Latitude);
                params.put("lon",Longitude);
                params.put("appid",APP_ID);
                getWeatherParameters(params);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("Weather", "Do U change the status?");

            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("Weather", "Great Job U enable Provider");

            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("Weather", "Why U disable Provider !!!");

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQ_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);
    }

    private void getWeatherParameters(RequestParams params) {

        AsyncHttpClient client=new AsyncHttpClient();
        client.get(WEATHER_URL,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){

                Log.d("Weather","Json is:"+ response.toString());
                WeatherDataModel weatherDataModel=WeatherDataModel.fromJson(response);
                updateUI(weatherDataModel);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("Weather","Fail: "+ throwable.toString());
                Log.d("Weather","Status Code: "+ statusCode);
                Toast.makeText(WeatherController.this,"Request Failure",Toast.LENGTH_SHORT).show();


            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==REQ_CODE){
            if(grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Log.d("Weather","Permission Granted");
                getWeatherForCurrentLocation();
            }else{
                Log.d("Weather","Permission Denied");
            }
        }

    }

    private  void  updateUI(WeatherDataModel weatherData){
        mTemperatureLabel.setText(weatherData.getTempreture());
        mCityLabel.setText(weatherData.getCity());

        int resourceID=getResources().getIdentifier(weatherData.getIconName(),
                "drawable",getPackageName());
        mWeatherImage.setImageResource(resourceID);

    }
}
