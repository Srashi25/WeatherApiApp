package com.codepros.weatherapp.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.codepros.weatherapp.MainActivity;
import com.codepros.weatherapp.models.WeatherReport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class WeatherDataService {

    public static final String CITY_ID_QUERY = "https://www.metaweather.com/api/location/search/?query=";
    public static final String WEATHER_BY_ID_QUERY = "https://www.metaweather.com/api/location/";
    Context context;
    String cityId;
    public WeatherDataService(Context context){
        this.context = context;
    }

    public interface VolleyResponseListener{
        void onError(String message);
        void onResponse(String cityId);
    }

    public void getCityId(String cityName, VolleyResponseListener volleyResponseListener){
        String url = CITY_ID_QUERY + cityName;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        cityId = "";
                        try {
                            JSONObject cityInfo = response.getJSONObject(0);
                            cityId = cityInfo.getString("woeid");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                       // Toast.makeText(context, cityId, Toast.LENGTH_SHORT).show();
                       volleyResponseListener.onResponse(cityId);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error! " + error, Toast.LENGTH_SHORT).show();
                volleyResponseListener.onError("Something Wrong");
            }
        });
        DataserviceSignleton.getInstance(context).addToRequestQueue(request);
    }
    public interface VolleyWeatherByIDResponse{
        void onError(String message);
        void onResponse(List<WeatherReport> weatherReportList);
    }
    public void getCityWeatherById(String cityId, VolleyWeatherByIDResponse volleyWeatherByIDResponse){
        List<WeatherReport> weatherReportList = new ArrayList<>();
        String url = WEATHER_BY_ID_QUERY + cityId;

        // get the Json object
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            JSONArray weatherList = response.getJSONArray("consolidated_weather");

                            for(int i =0; i< weatherList.length();i++){
                                WeatherReport wr = new WeatherReport();
                                JSONObject firstDay = (JSONObject)weatherList.get(i);
                                wr.setId(firstDay.getInt("id"));
                                wr.setWeather_state_name(firstDay.getString("weather_state_name"));
                                wr.setWeather_state_abbr(firstDay.getString("weather_state_abbr"));
                                wr.setWind_direction_compass(firstDay.getString("wind_direction_compass"));
                                wr.setCreated(firstDay.getString("created"));
                                wr.setApplicable_date(firstDay.getString("applicable_date"));
                                wr.setMin_temp(firstDay.getLong("min_temp"));
                                wr.setMax_temp(firstDay.getLong("max_temp"));
                                wr.setThe_temp(firstDay.getLong("the_temp"));
                                wr.setWind_speed(firstDay.getLong("wind_speed"));
                                wr.setAir_pressure(firstDay.getInt("air_pressure"));
                                wr.setHumidity(firstDay.getInt("humidity"));
                                wr.setVisibility(firstDay.getLong("visibility"));
                                wr.setPredictability(firstDay.getInt("predictability"));
                                weatherReportList.add(wr);
                            }
                            volleyWeatherByIDResponse.onResponse(weatherReportList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } 
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Please provide correct City name. ",Toast.LENGTH_SHORT).show();
        }
        });

        DataserviceSignleton.getInstance(context).addToRequestQueue(request);
    }

    public interface  GetCityForecastCallBack{
        void onError(String msg);
        void onResponse(List<WeatherReport> weatherList);
    }
    public void getWeatherByName(String name, GetCityForecastCallBack getCityForecastCallBack){

        //get cityId
        getCityId(name, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(context, "Error at fetching City Id: "+message,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String cityId) {
                getCityWeatherById(cityId, new VolleyWeatherByIDResponse() {
                    @Override
                    public void onError(String message) {
                        Toast.makeText(context, "Error at fetching Weather! " + message,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(List<WeatherReport> weatherReportList) {
                        getCityForecastCallBack.onResponse(weatherReportList);

                    }
                });

            }
        });

    }
}
