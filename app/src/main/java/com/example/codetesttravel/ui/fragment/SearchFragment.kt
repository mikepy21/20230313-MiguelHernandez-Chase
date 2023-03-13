package com.example.codetesttravel.ui.fragment


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.codetesttravel.data.api.WeatherService
import com.example.codetesttravel.data.model.Weather
import com.example.codetesttravel.data.repository.WeatherRepository
import com.example.codetesttravel.databinding.FragmentSearchBinding
import com.example.codetesttravel.ui.MainActivity
import com.example.codetesttravel.util.Constants
import com.example.codetesttravel.util.Constants.BASE_ICON_URL
import com.example.codetesttravel.util.Constants.ICON_COLON_URL
import com.example.codetesttravel.viewModel.WeatherModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class SearchFragment : Fragment(), LocationListener {
    val TAG = SearchFragment::class.java.simpleName

    companion object {
        fun newInstance() = SearchFragment()
    }

    private lateinit var viewModel: WeatherModel

    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val interceptor = HttpLoggingInterceptor()
                interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BODY }
                val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
                val retrofit = Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()

                val service = retrofit.create<WeatherService>(WeatherService::class.java)

                val repository = WeatherRepository(service)

                return WeatherModel(repository) as T
            }

        }).get(WeatherModel::class.java)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.getCityWeather(query)
                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = false

        })

        binding.currentLocation.setOnClickListener {
            var location = getLocation(activity as MainActivity)
            viewModel.getWeather(location?.latitude.toString(), location?.longitude.toString())
        }

        viewModel.results.observe(viewLifecycleOwner, Observer<Weather> {
            it?.apply {

                binding.weather.text = name
                binding.temperature.text = main.temp.toString()
                binding.sky.text = weather[0].main
                Glide.with(binding.iconWeather.context)
                    .load(Uri.parse(BASE_ICON_URL + weather[0].icon + ICON_COLON_URL))
                    .into(binding.iconWeather)

            }
            Log.d(TAG, it.toString())
        })
        viewModel.resultCity.observe(viewLifecycleOwner) {
            viewModel.getWeather(it[0].lat.toString(), it[0].lon.toString())
        }
    }


    private fun getLocation(context: Context): Location? {
        val locationManager: LocationManager

        // flag for GPS status
        val isGPSEnabled: Boolean

        // flag for network status
        val isNetworkEnabled: Boolean
        var location: Location? = null // location
        try {
            locationManager = context
                .getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager

            // getting GPS status
            isGPSEnabled = true &&
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

            // getting network status
            isNetworkEnabled = true &&
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                if (isNetworkEnabled) {
                    // First get location from Network Provider
                    if (ActivityCompat.checkSelfPermission(
                            activity as AppCompatActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            activity as AppCompatActivity,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {

                    }
                    locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        60000,
                        10f,
                        this
                    )
                    location =
                        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                }
                if (isGPSEnabled) {
                    // if GPS Enabled get lat/long using GPS Services
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            60000,
                            10f,
                            this
                        )
                        location =
                            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "getLocation Error: " + e.printStackTrace())
        }
        return location
    }

    override fun onLocationChanged(p0: Location) {
        Log.e(
            "TAG", "onLocationChanged: " + p0.latitude.toString() + p0
                .longitude.toString()
        )
    }
}