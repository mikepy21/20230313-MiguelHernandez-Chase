package com.example.codetesttravel.data.api

import com.example.codetesttravel.data.model.City
import com.example.codetesttravel.data.model.Weather
import com.example.codetesttravel.data.model.WeatherResult
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("geo/1.0/direct")
    fun getCityWeather(@Query("q") cityName: String,
    @Query("appid") apikey: String): Observable<City>

    @GET("data/2.5/weather")
    fun listWeather(@Query("lat") lat: String,
                  @Query("lon") lon: String,
                  @Query("appid") apikey: String): Observable<Weather>
}



