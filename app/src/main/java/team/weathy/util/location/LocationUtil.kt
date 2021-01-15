package team.weathy.util.location

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import team.weathy.model.entity.OverviewWeather
import team.weathy.util.SPUtil
import team.weathy.util.debugE
import javax.inject.Inject

@SuppressLint("MissingPermission")
class LocationUtil @Inject constructor(app: Application, private val spUtil: SPUtil) : DefaultLifecycleObserver {
    private val locationManager = app.getSystemService(LocationManager::class.java)

    private val _lastLocation = MutableStateFlow<Location?>(null)
    val lastLocation: StateFlow<Location?> = _lastLocation

    private val _isOtherPlaceSelected: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isOtherPlaceSelected: StateFlow<Boolean> = _isOtherPlaceSelected

    val selectedWeatherLocation: MutableStateFlow<OverviewWeather?> = MutableStateFlow(null)

    private var isRegistered = false

    override fun onCreate(owner: LifecycleOwner) {
        registerLocationListener()

        _isOtherPlaceSelected.value = spUtil.isOtherPlaceSelected
    }

    override fun onDestroy(owner: LifecycleOwner) {
        unregisterLocationListener()
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            debugE("lastLocationUpdated $location")
            _lastLocation.value = location
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String) {
        }

        override fun onProviderDisabled(provider: String) {
        }
    }

    fun registerLocationListener() {
        if (isRegistered) return


        try {
            val enabledProviders = locationManager.allProviders.filter {
                locationManager.isProviderEnabled(it)
            }

            enabledProviders.forEach {
                locationManager.requestLocationUpdates(it, 60000, 1f, locationListener)
                locationManager.requestLocationUpdates(it, 60000, 1f, locationListener)

                _lastLocation.value = locationManager.getLastKnownLocation(it)
                _lastLocation.value = locationManager.getLastKnownLocation(it)
            }

            isRegistered = true
            debugE("registerLocationListener with provider: $enabledProviders")
        } catch (e: Throwable) {
            debugE(e)
        }
    }

    private fun unregisterLocationListener() {


        locationManager.removeUpdates(locationListener)
        isRegistered = false
        debugE("unregisterLocationListener")
    }

    fun selectPlace(weather: OverviewWeather) {
        spUtil.lastSelectedLocationCode = weather.region.code
        selectedWeatherLocation.value = weather
        spUtil.isOtherPlaceSelected = false
        _isOtherPlaceSelected.value = false
    }

    fun selectOtherPlace(weather: OverviewWeather) {
        spUtil.lastSelectedLocationCode = weather.region.code
        selectedWeatherLocation.value = weather
        spUtil.isOtherPlaceSelected = true
        _isOtherPlaceSelected.value = true
    }
}