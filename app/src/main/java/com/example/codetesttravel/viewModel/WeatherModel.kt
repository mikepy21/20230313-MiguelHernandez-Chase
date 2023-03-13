package com.example.codetesttravel.viewModel



import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.codetesttravel.data.model.City
import com.example.codetesttravel.data.model.CityItem
import com.example.codetesttravel.data.model.Weather
import com.example.codetesttravel.data.repository.WeatherRepository
import com.example.codetesttravel.util.Constants

import io.reactivex.disposables.Disposable

class WeatherModel(private val bookRepository: WeatherRepository): ViewModel() {

    val TAG = WeatherModel::class.java.simpleName

    private val _results: MutableLiveData<Weather> = MutableLiveData()
    private val _resultCity: MutableLiveData<City> = MutableLiveData()
    val results: LiveData<Weather>
        get() = _results

    val resultCity: LiveData<City>
        get() = _resultCity

    var searchName = ""
    var disposable: Disposable? = null

    override fun onCleared() {
        disposable?.dispose()
        super.onCleared()
    }

    fun getWeather(lat:String?, lon:String? ) {
        if (lat == null || searchName == lat || lon == null ) return

        searchName = lat

        disposable?.dispose()

        disposable = bookRepository.lookWeather(lat,lon, Constants.API_KEY)?.subscribe({ data ->
            _results.value = data
            Log.d(TAG, data.toString())
        }, { error ->
            Log.e(TAG, Log.getStackTraceString(error))
        }
        )
    }

    fun getCityWeather(city:String?) {
        if (city == null || searchName == city ) return

        searchName = city

        disposable?.dispose()

        disposable = bookRepository.lookCityWeather(city, Constants.API_KEY)?.subscribe({ data ->
            _resultCity.value = data
            Log.d(TAG, data.toString())
        }, { error ->
            Log.e(TAG, Log.getStackTraceString(error))
        }
        )
    }
}

