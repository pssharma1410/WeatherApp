package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    Button b;
    EditText city;
    TextView weather;
    public class JsonData extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder res = new StringBuilder();

            URL url = null;
            try {
                url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data != -1) {
                    res.append((char) data + "");
                    data = reader.read();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            return res.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonO = new JSONObject(s);
                String Weatherinfo = jsonO.getString("weather");
                JSONArray weatheri = new JSONArray(Weatherinfo);
                StringBuilder puraweather = new StringBuilder();

                for(int i = 0; i < weatheri.length(); i++){
                    JSONObject temp = weatheri.getJSONObject(i);
                    puraweather.append(temp.getString("main")+" : "+temp.getString("description")+"\n");
                }
                //To get temperature
                String tempinfo =jsonO.getString("main");
                JSONObject tempk = new JSONObject(tempinfo);
                String currtemp = tempk.getString("temp");
                String mintemp = tempk.getString("temp_min");
                String maxtemp = tempk.getString("temp_max");
                String humid = tempk.getString("humidity");
                puraweather.append("Current Temperature :"+convertkelvintodegrees(currtemp)+" C\nMin Temperature : "+convertkelvintodegrees(mintemp)+" C\nMax Temperature : "+convertkelvintodegrees(maxtemp)+" C\n Humidity : "+humid+" %");
                weather.setText(puraweather.toString());
            } catch (Exception exception) {
                exception.printStackTrace();
                Toast.makeText(getApplicationContext(), "Enter a valid city name", Toast.LENGTH_SHORT).show();
            }

        }
    }
    public String convertkelvintodegrees(String kelvin){
        float kel = Float.parseFloat(kelvin);
        float deg = kel - (float)273.15;
        return String.format("%.2f",deg);
    }
    public void getWeather(View view){
        try {
            JsonData data = new JsonData();
            String cityname = city.getText().toString();
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(city.getWindowToken(),0);

            data.execute("http://api.openweathermap.org/data/2.5/weather?q="+cityname+"&appid=51f9198c49d0edc59705eafb97b03140");
            //json data will be processed on completion of execute
        }catch(Exception e){
            Toast.makeText(getApplicationContext(), "Enter a valid city name", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b = (Button) findViewById(R.id.button);
        city = (EditText) findViewById(R.id.editTextTextPersonName);
        weather = (TextView) findViewById(R.id.textView2);
    }
}