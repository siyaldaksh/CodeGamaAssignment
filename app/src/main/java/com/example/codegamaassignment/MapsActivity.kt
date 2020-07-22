package com.example.codegamaassignment

import android.Manifest
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_maps.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var currentLocation: ImageButton
    private lateinit var restaurantNearby: ImageButton
    private lateinit var schoolNearby: ImageButton
    private lateinit var hospitalNearby: ImageButton
    private lateinit var enteredLocation: EditText
    private lateinit var searchLocation: ImageView
    private lateinit var permissionLayout : ConstraintLayout
    private lateinit var allow : Button
    private lateinit var deny : Button
    private lateinit var apiService :Api

    companion object{
        private const val PERMISSION_REQUEST_CODE : Int = 0
    }

    //Location Classes
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationCallback: LocationCallback? = null

    var latitude : Double = 0.0
    var longitude : Double = 0.0
    lateinit var mapFragment : SupportMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_maps)

        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        permissionLayout = findViewById(R.id.permission)
        allow = findViewById(R.id.allowPermission)
        deny = findViewById(R.id.denyPermission)
        currentLocation = findViewById(R.id.myLocation)
        restaurantNearby = findViewById(R.id.hotel)
        schoolNearby = findViewById(R.id.school)
        hospitalNearby= findViewById(R.id.hospital)
        enteredLocation = findViewById(R.id.editText)
        searchLocation = findViewById(R.id.search)

       isGooglePlayServicesAvailable(this)

        // Initialize the FusedLocationClient.
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(getLocationRequest())

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                try {
                    exception.startResolutionForResult(this,
                        99)
                } catch (sendEx: IntentSender.SendIntentException) {
                }
            }
        }

        //getting location permission
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED  -> {
                permissionLayout.visibility = View.GONE
            }

            else -> {
                permissionLayout.visibility = View.VISIBLE
            }
        }

        allow.setOnClickListener {
            requestPermissionFun()
        }

        deny.setOnClickListener {
            Toast.makeText(this,"Please allow to continue", Toast.LENGTH_SHORT).show()
        }
        //--------------------------------------------------------------------------------------------//

        // Initialize the location callbacks.
        mLocationCallback = object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult?) {

                    val location = locationResult?.lastLocation
                    latitude = location!!.latitude
                    longitude = location.longitude
                    mapFragment.getMapAsync(this@MapsActivity)

                Log.e("map","onLocationResult")

            }
        }
        //-------------------------------------------------------------------------------------------//

        currentLocation.setOnClickListener{
            currentLocation.background = resources.getDrawable(R.drawable.greycircle,null)
            mFusedLocationClient!!.requestLocationUpdates(
                getLocationRequest(),
                mLocationCallback,
                null
            )
        }


        searchLocation.setOnClickListener {
            currentLocation.background = resources.getDrawable(R.drawable.circle,null)
            hospitalNearby.background = resources.getDrawable(R.drawable.circle,null)
            restaurantNearby.background = resources.getDrawable(R.drawable.circle,null)
            schoolNearby.background = resources.getDrawable(R.drawable.circle,null)
            if(editText.text.toString().trim().length>1) {
                getLocationFromAddress(editText.text.toString().trim())
            }
        }

        hospitalNearby.setOnClickListener {
            mMap.clear()
            val latLng = LatLng(latitude, longitude)
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng)
            mMap.addMarker(markerOptions)

            getNearbyPlaces("hospital")

            hospitalNearby.background = resources.getDrawable(R.drawable.greycircle,null)
            restaurantNearby.background = resources.getDrawable(R.drawable.circle,null)
            schoolNearby.background = resources.getDrawable(R.drawable.circle,null)
        }

        restaurantNearby.setOnClickListener {
            mMap.clear()
            val latLng = LatLng(latitude, longitude)
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng)
            mMap.addMarker(markerOptions)
            getNearbyPlaces("restaurant")

            hospitalNearby.background = resources.getDrawable(R.drawable.circle,null)
            restaurantNearby.background = resources.getDrawable(R.drawable.greycircle,null)
            schoolNearby.background = resources.getDrawable(R.drawable.circle,null)
        }

        schoolNearby.setOnClickListener {
            mMap.clear()
            val latLng = LatLng(latitude, longitude)
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng)
            mMap.addMarker(markerOptions)
            getNearbyPlaces("school")

            hospitalNearby.background = resources.getDrawable(R.drawable.circle,null)
            restaurantNearby.background = resources.getDrawable(R.drawable.circle,null)
            schoolNearby.background = resources.getDrawable(R.drawable.greycircle,null)
        }



    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.clear()
        mMap.uiSettings.isMapToolbarEnabled = false
        val location = LatLng(latitude, longitude)
        mMap.addMarker(MarkerOptions().position(location).title(""))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,15f))

    }

    private fun getLocationRequest(): LocationRequest {
        return LocationRequest.create()?.apply {
            numUpdates = 1
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }!!
    }

    private fun requestPermissionFun() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {

                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    mFusedLocationClient!!.requestLocationUpdates(
                        getLocationRequest(),
                        mLocationCallback,
                        null
                    )
                    permissionLayout.visibility = View.GONE
                }
                return
            }
        }
    }

    private fun getLocationFromAddress(placeName: String){
        val coder = Geocoder(this)
        val address: List<Address>?
        try {
            address = coder.getFromLocationName(placeName, 5)
            if (address.size>0) {
                val location = address[0]
                latitude = location.latitude
                longitude = location.longitude
                mapFragment.getMapAsync(this)
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun isGooglePlayServicesAvailable(context:Context) {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        googleApiAvailability.isGooglePlayServicesAvailable(context)

        if(googleApiAvailability.isUserResolvableError(0)) {
            googleApiAvailability.getErrorDialog(this, 0, 2404).show()
        }

    }

    private fun getNearbyPlaces(type:String){
        apiService = ApiUtils.getAPIService()

        val call = apiService.getNearbyPlaces(type, "$latitude,$longitude",5000)



        call.enqueue(object : Callback<PlacesDetail> {
            override fun onFailure(call: Call<PlacesDetail>, t: Throwable) {
                Toast.makeText(this@MapsActivity,"fail",Toast.LENGTH_SHORT).show()
            }
            val markerOptions = MarkerOptions()

            override fun onResponse(call: Call<PlacesDetail>, response: Response<PlacesDetail>) {
                try {
                    for (i:Int in response.body()!!.getResults().indices) {
                        val lat = response.body()!!.getResults().get(i).getGeometry()!!.getLocation()!!.getLat()
                        val lng = response.body()!!.getResults().get(i).getGeometry()!!.getLocation()!!.getLng()
                        val placeName = response.body()!!.getResults().get(i).getName()

                        val latLng = LatLng(lat!!, lng!!)
                        markerOptions.position(latLng)
                        markerOptions.title(placeName)
                        mMap.addMarker(markerOptions)

                    }
                } catch (e: Exception) {
                    Log.d("onResponse", "There is an error")
                    e.printStackTrace()
                }
            }
        })
        }
}
