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
import team.weathy.util.debugE
import java.util.*

@SuppressLint("MissingPermission")
class LocationUtil(app: Application) : DefaultLifecycleObserver {
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


    override fun onStart(owner: LifecycleOwner) {
        registerLocationListener()
    }

    override fun onStop(owner: LifecycleOwner) {
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
}