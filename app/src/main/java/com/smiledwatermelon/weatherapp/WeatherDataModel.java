package com.smiledwatermelon.weatherapp;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by salahuddin on 2/23/18.
 */

public class WeatherDataModel {


    private String mTempreture, mCity, mIconName;
    private int mCondition;

    public  static WeatherDataModel fromJson (JSONObject jsonObject){

        try {
        WeatherDataModel weatherData=new WeatherDataModel();


            weatherData.mCity=jsonObject.getString("name");
            weatherData.mCondition=jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
            weatherData.mIconName=updateWeatherIcon(weatherData.mCondition);


            double tempTMP =jsonObject.getJSONObject("main").getDouble("temp")-273.15;
            int roundedTMP= (int) Math.rint(tempTMP);

            weatherData.mTempreture=Integer.toString(roundedTMP);


            return weatherData;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }


    }

    // Get the weather image name from OpenWeatherMap's condition (marked by a number code)
    private static String updateWeatherIcon(int condition) {

        if (condition >= 0 && condition < 300) {
            return "tstorm1";
        } else if (condition >= 300 && condition < 500) {
            return "light_rain";
        } else if (condition >= 500 && condition < 600) {
            return "shower3";
        } else if (condition >= 600 && condition <= 700) {
            return "snow4";
        } else if (condition >= 701 && condition <= 771) {
            return "fog";
        } else if (condition >= 772 && condition < 800) {
            return "tstorm3";
        } else if (condition == 800) {
            return "sunny";
        } else if (condition >= 801 && condition <= 804) {
            return "cloudy2";
        } else if (condition >= 900 && condition <= 902) {
            return "tstorm3";
        } else if (condition == 903) {
            return "snow5";
        } else if (condition == 904) {
            return "sunny";
        } else if (condition >= 905 && condition <= 1000) {
            return "tstorm3";
        }

        return "dunno";
    }

    public String getTempreture() {
        return mTempreture + " °";
    }

    public String getCity() {
        return mCity;
    }

    public String getIconName() {
        return mIconName;
    }
}
