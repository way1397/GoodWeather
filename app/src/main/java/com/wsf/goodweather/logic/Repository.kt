package com.wsf.goodweather.logic

import androidx.lifecycle.liveData
import com.wsf.goodweather.logic.dao.PlaceDao
import com.wsf.goodweather.logic.model.Location
import com.wsf.goodweather.logic.model.Place
import com.wsf.goodweather.logic.model.Weather
import com.wsf.goodweather.logic.network.GoodWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

object Repository {

    fun savePlace(place: Place) = PlaceDao.savePlace(place)

    fun getSavePlace() = PlaceDao.getPlace()

    fun isPlaceSave() = PlaceDao.isPlaceSaved()

    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        val placeResponse = GoodWeatherNetwork.searchPlace(query)
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Result.success(places)
        } else {
            val list = arrayListOf<Place>()
            val myPlace = Place("北京市", Location("116.407387", "39.904179"), "北京市address")
            list.add(myPlace)
            Result.success(list)
//            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }
    }

    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO) {
        coroutineScope {
            val deferredRealTime = async {
                GoodWeatherNetwork.getRealTimeWeather(lng, lat)
            }
            val deferredDaily = async {
                GoodWeatherNetwork.getDailyWeather(lng, lat)
            }
            val realtimeResponse = deferredRealTime.await()
            val dailyResponse = deferredDaily.await()
            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                val weather =
                    Weather(realtimeResponse.result.realtime, dailyResponse.result.daily)
                Result.success(weather)
            } else {
                Result.failure(
                    java.lang.RuntimeException(
                        "realTime response status is${realtimeResponse.status}+" +
                                "daily response status is${dailyResponse.status}"
                    )
                )
            }
        }
    }

    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: java.lang.Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }
}
