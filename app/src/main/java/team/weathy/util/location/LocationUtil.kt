package team.weathy.util.location

import android.annotation.SuppressLint
import android.app.Application
import android.location.Geocoder
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
import java.util.*
import javax.inject.Inject

@SuppressLint("MissingPermission")
class LocationUtil @Inject constructor(app: Application, private val spUtil: SPUtil) : DefaultLifecycleObserver {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(app)
    private val geoCoder = Geocoder(app, Locale.KOREA)

    private val locationRequest = LocationRequest.create().apply {
        interval = 60000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val _lastLocation = MutableStateFlow<Location?>(null)
    val lastLocation: StateFlow<Location?> = _lastLocation
    private val _isLocationAvailable = MutableStateFlow(false)
    val isLocationAvailable: StateFlow<Boolean> = _isLocationAvailable

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
            _isLocationAvailable.value = result.isLocationAvailable
        }
    }

    private fun registerLocationListener() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun unregisterLocationListener() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun reverseGeocode() {
        lastLocation.value?.let { location ->
            debugE(geoCoder.getFromLocation(location.latitude, location.longitude, 1).first())
        }
    }

    fun selectPlace(weather: OverviewWeather) {
        spUtil.lastSelectedLocationCode = weather.region.code
        selectedWeatherLocation.value = weather
    }

    fun selectOtherPlace(weather: OverviewWeather) {
        selectPlace(weather)

        unregisterLocationListener()
        _isLocationAvailable.value = false
        spUtil.isOtherPlaceSelected = true
        _isOtherPlaceSelected.value = true
    }
}