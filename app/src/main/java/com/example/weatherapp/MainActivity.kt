package com.example.weatherapp

import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val API_KEY = ""
    private val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    private val DEFAULT_CITY = "Kolkata"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initial data load
        fetchWeatherData(DEFAULT_CITY)

        // Handle SearchView
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun fetchWeatherData(city: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Apiinterface::class.java)

        val call = retrofit.getWeatherData(city, API_KEY, "metric")
        call.enqueue(object : Callback<WeatherApp> {
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        val temperature = data.main.temp.toString()
                        val cityName = data.name
                        val weatherType = data.weather.firstOrNull()?.main ?: "N/A"
                        val maxTemp = data.main.temp_max
                        val minTemp = data.main.temp_min

                        // Date & Time formatting
                        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
                        val sunrise = sdf.format(Date(data.sys.sunrise * 1000L))
                        val sunset = sdf.format(Date(data.sys.sunset * 1000L))


                        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                        val dateFormat = SimpleDateFormat("d MMMM, yyyy", Locale.getDefault())

                        binding.temp.text = "$temperature°C"
                        binding.location.text = cityName
                        binding.weatype.text = weatherType
                        binding.maxtemp.text = "MAX: $maxTemp°C"
                        binding.mintemp.text = "MIN: $minTemp°C"
                        binding.humidityValue.text = "${data.main.humidity}%"
                        binding.pressureValue.text = "${data.main.pressure} hPa"
                        binding.windValue.text = "${data.wind.speed} m/s"
                        binding.conditionValue.text = weatherType
                        binding.sunriseValue.text = sunrise
                        binding.sunsetValue.text = sunset
                        binding.today.text = "Today"
                        binding.dayname.text = dayFormat.format(Date())
                        binding.date.text = dateFormat.format(Date())
                    }
                } else {
                    Toast.makeText(this@MainActivity, "City not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}