package team.weathy.util.location

import android.app.Application
import android.location.Location
import android.os.Looper
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import team.weathy.model.entity.OverviewWeather
import team.weathy.util.SPUtil
import team.weathy.util.debugE
import javax.inject.Inject

class LocationUtil @Inject constructor(app: Application, private val spUtil: SPUtil) : DefaultLifecycleObserver {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(app)

    private val locationRequest = LocationRequest.create().apply {
        interval = 60000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val _lastLocation = MutableStateFlow<Location?>(null)
    val lastLocation: StateFlow<Location?> = _lastLocation

    private val _isOtherPlaceSelected: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isOtherPlaceSelected: StateFlow<Boolean> = _isOtherPlaceSelected

    val selectedWeatherLocation: MutableStateFlow<OverviewWeather?> = MutableStateFlow(null)

    override fun onCreate(owner: LifecycleOwner) {
        registerLocationListener()

        _isOtherPlaceSelected.value = spUtil.isOtherPlaceSelected
    }

    override fun onDestroy(owner: LifecycleOwner) {
        unregisterLocationListener()
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            result ?: return
            _lastLocation.value = result.lastLocation
        }

        override fun onLocationAvailability(result: LocationAvailability?) {
            result ?: return
        }
    }

    private fun registerLocationListener() {
        debugE("registerLocationListener")
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun unregisterLocationListener() {
        debugE("unregisterLocationListener")
        fusedLocationClient.removeLocationUpdates(locationCallback)
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