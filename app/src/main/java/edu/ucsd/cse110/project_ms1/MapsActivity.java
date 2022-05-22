package edu.ucsd.cse110.project_ms1;
//package com.example.googlemapactivitytemplate;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;

import edu.ucsd.cse110.project_ms1.databinding.ActivityDirectionBinding;
//import androidx.databinding.DataBindingUtil;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap map;
    private ActivityDirectionBinding binding;
    private Location lastVisitedLocation;
    OnLocationChangeListener onLocationChangeListener;

    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), perms -> {
                perms.forEach((perm, isGranted) -> {
                    Log.i("LAB7", String.format("Permission %s granted: %s", perm, isGranted));
                });
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDirectionBinding.inflate(getLayoutInflater());
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
        {
            //Enable zoom controls
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
            map.moveCamera(CameraUpdateFactory.zoomTo(11.5f));
        }



        //permissions Setup
        {
            var requiredPermissions = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION

            };

            var hasNoLocationPerms = Arrays.stream(requiredPermissions)
                    .map(perm -> ContextCompat.checkSelfPermission(this, perm))
                    .allMatch(status -> status == PackageManager.PERMISSION_DENIED);

            if (hasNoLocationPerms) {
                requestPermissionLauncher.launch(requiredPermissions);
                //the activity will be restarted when permission change
                //this entire method will be re-run, but we won't get stuck here
                return;
            }
        }


        //listen for location updates
        {
            var provider = LocationManager.GPS_PROVIDER;
            var locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            var locationListener = new LocationListener(){
                @Override
                public void onLocationChanged(@NonNull Location location){
                    Log.d("LAB7", String.format("Location changed: %s", location));

                    LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                    var marker = new MarkerOptions().
                            position(current).title("Navigation Step");
                    map.addMarker(marker);
                    //update current location latitude & longtitude
                    LatLngs.currentLocationLatLng = current;
                    onLocationChangeListener.OnLocationChange(current);
                }
            };
            locationManager.requestLocationUpdates(provider, 0, 0f, locationListener);
        }

    }
/*
    @Override
    public void OnLocationChange(LatLng current) {
        onLocationChangeListener.OnLocationChange(LatLngs.currentLocationLatLng);
    }

 */
}
