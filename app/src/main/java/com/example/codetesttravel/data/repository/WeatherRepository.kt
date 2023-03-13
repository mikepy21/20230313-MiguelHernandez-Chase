package com.example.codetesttravel.data.repository

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.example.codetesttravel.data.api.WeatherService
import com.example.codetesttravel.data.model.City
import com.example.codetesttravel.data.model.Weather
import com.example.codetesttravel.data.model.WeatherResult


class WeatherRepository(private val booksApiService: WeatherService) {
    fun lookWeather(lat:String, lon:String, key:String): Observable<Weather>? =
        booksApiService.listWeather(lat,lon, key)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun lookCityWeather(cityName:String, key:String): Observable<City>? =
        booksApiService.getCityWeather(cityName, key)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}