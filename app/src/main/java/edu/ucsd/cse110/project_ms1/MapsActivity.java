package edu.ucsd.cse110.project_ms1;
//package com.example.googlemapactivitytemplate;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

//import com.example.googlemapactivitytemplate.databinding.ActivityMapsBinding;
//import androidx.databinding.DataBindingUtil;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private final PermissionChecker permissionChecker = new PermissionChecker(this);
    private GoogleMap map;
    private ActivityMapsBinding binding;

    private Location lastVisitedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        var mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //Map Setup
        {//Enable zoom controls
            var uiSettings = map.getUiSettings();
            uiSettings.setZoomControlsEnabled(true);

            //Add a marker between UCSD and the zoo and move the camera
            var ucsdPosition = LatLngs.UCSD_LATLNG;
            var zooPosition = LatLngs.ZOO_LATLNG;

            //compute the midpoint between UCSD and the zoo
            var cameraPosition = LatLngs.midpoint(ucsdPosition, zooPosition);

            //Place a pin on UCSD
            map.addMarker(new MarkerOptions().position(ucsdPosition).title("UCSD"));

            //Move the camera and zoom to the right level
            map.moveCamera(CameraUpdateFactory.newLatLng(cameraPosition));
            map.moveCamera(CameraUpdateFactory.zoomTo(11.5f));}



        //permissions Setup
        {
            if (permissionChecker.ensurePermissions()) return;
        }


        //listen for location updates
        {
            var provider = LocationManager.GPS_PROVIDER;
            var locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            var locationListener = new LocationListener(){
                @Override
                public void onLocationChanged(@NonNull Location location){
                    Log.d("LAB7", String.format("Location changed: %s", location));

                    var marker = new MarkerOptions().
                            position(new LatLng(
                                    location.getLatitude(), location.getLongitude()
                            )).title("Navigation Step");
                    map.addMarker(marker);
                }
            };
            locationManager.requestLocationUpdates(provider, 0, 0f, locationListener);
        }

    }

    private boolean ensurePermissions() {

        return permissionChecker.ensurePermissions();
    }

}
