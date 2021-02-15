package com.codepros.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepros.weatherapp.models.WeatherReport;
import com.codepros.weatherapp.utils.WeatherDataService;

import java.util.List;

public class WeatherListActivity extends AppCompatActivity {

    TextView tvCity;
    Button btnByName;
    EditText etDataInput;
    ListView lvWeatherReport;
    WeatherDataService weatherDataService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_list);

        //assign values to controls
        btnByName = findViewById(R.id.btnByName);
        etDataInput = findViewById(R.id.etValue);
        lvWeatherReport = findViewById(R.id.lvWeatherList);
        tvCity = findViewById(R.id.tvCity);
        weatherDataService = new WeatherDataService(WeatherListActivity.this);



        // click listeners for buttons
        btnByName.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(etDataInput.getText().toString())){
                    etDataInput.setError("City Name is required");
                    return;
                }
                //setting TextView Text
                tvCity.setText(etDataInput.getText().toString());
                // reset edit text
                etDataInput.setText("");
                weatherDataService.getWeatherByName(etDataInput.getText().toString(), new WeatherDataService.GetCityForecastCallBack() {
                    @Override
                    public void onError(String message) {
                        Toast.makeText(WeatherListActivity.this, message.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(List<WeatherReport> weatherReportList) {
                        ArrayAdapter arrayAdapter = new ArrayAdapter(WeatherListActivity.this, android.R.layout.simple_expandable_list_item_1,weatherReportList);
                        lvWeatherReport.setAdapter(arrayAdapter);
                    }
                });
            }
        });
    }
}