package gmail.deyrohit1212.weatherapp

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import gmail.deyrohit1212.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.util.Date
import java.util.Locale

//ec925c5bcd1bd8198f9e8363aec1196e
class MainActivity : AppCompatActivity()
{
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchData("Durgapur")
        SearchCity()
    }

    private fun SearchCity() {
        val search1 =binding.searchBar
        search1.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
               return true
            }

        })
    }

    private fun fetchData(CityName :String)
    {
        val url ="https://api.openweathermap.org/data/2.5/"
        val  retrofit =Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(url)
            .build().create(ApiInterface ::class.java)

        val response =retrofit.getWeatherData(CityName,"ec925c5bcd1bd8198f9e8363aec1196e","metric")
        response.enqueue(object :Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responsebody =response.body()
                if(responsebody!=null && response.isSuccessful) 
                {
                    //Temperature
                    val temp = responsebody.main.temp.toString()
                    binding.temptoday.text ="$temp °C"

                    val liketemp = responsebody.main.feels_like.toString()
                    binding.feelliketext.text ="Feel like : $liketemp °C"

                    //Weather Condition
                    val condition =responsebody.weather.firstOrNull()?.main?:"unknown"
                    binding.weather.text ="$condition"

                    //Max Temperature
                    val maxtemp =responsebody.main.temp_max
                    binding.max.text="Max Temp: $maxtemp °C"

                    //Min Temperature
                    val mintemp =responsebody.main.temp_max
                    binding.min.text="Min Temp: $mintemp °C"

                    //Humidity
                    val humid =responsebody.main.humidity
                    binding.humidtext1.text="$humid %"

                    //Wind Speed
                    val windspeed =responsebody.wind.speed
                    binding.windspeed1.text="$windspeed m/s"

                    //Sea Level
                    val sealevel=responsebody.main.pressure
                    binding.seatext1.text="$sealevel hPa"

                    //Sun Rise
                    val sunrise =(responsebody.sys.sunrise.toLong()*1000)
                    binding.sunrisetext1.text="${Time(sunrise)}"

                    //Sun Set
                    val sunset =(responsebody.sys.sunset.toLong()*1000)
                    binding.sunsetext1.text="${Time(sunset)}"

                    //City Name
                    binding.location.text ="$CityName"

                    //Condition
                    val cond1 =responsebody.weather.firstOrNull()?.description?:"unknown"
                    binding.raintext1.text="$cond1"
//                    val cond =responsebody.weather.firstOrNull()?.main?:"unknown"
//                    binding.raintext1.text ="$cond"

                    //Day
                    binding.weekday.text=dayName(System.currentTimeMillis())

                    //Date
                    binding.date.text=DateName()

                    //Image Change
                    ImageChange(condition)
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
            }

        })
    }

    private fun ImageChange(Cond:String) {
        when(Cond)
        {
            "Clear", "Sunny", "Clear Sky" ->{
                binding.root.setBackgroundResource(R.drawable.sunnyweather)
                binding.animation.setAnimation(R.raw.sunny)
            }
            "Haze", "Mist", "Foggy", "Clouds", "Partly Clouds", "Overcast", "Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.cloudweather)
                binding.animation.setAnimation(R.raw.cloud)
            }
            "Rain", "Light Rain", "Heavy Rain", "Moderate Rain", "Showers", "Drizzle" ->{
                binding.root.setBackgroundResource(R.drawable.rainweather)
                binding.animation.setAnimation(R.raw.rain)
            }
            "Thunderstorm"->{
                binding.root.setBackgroundResource(R.drawable.thunderweather)
                binding.animation.setAnimation(R.raw.thunder)
            }
            "Snow", "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snowweather)
                binding.animation.setAnimation(R.raw.snow)
            }
            else ->
            {
                binding.root.setBackgroundResource(R.drawable.sunnyweather)
                binding.animation.setAnimation(R.raw.sunny)
            }

        }
        binding.animation.playAnimation()

    }

    private fun DateName(): String {
        val date1 =SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return date1.format(Date())
    }

    private fun Time(timestamp: Long): String {
        val time1 =SimpleDateFormat("HH:mm", Locale.getDefault())
        return time1.format(Date(timestamp))
    }

    fun dayName(timestamp: Long):String
    {
        val day =SimpleDateFormat("EEEE", Locale.getDefault())
        return day.format(Date())
    }
}
