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
import edu.ucsd.cse110.project_ms1.databinding.ActivityMapsBinding;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import edu.ucsd.cse110.project_ms1.databinding.ActivityMapsBinding;
import edu.ucsd.cse110.project_ms1.location.Coord;
import edu.ucsd.cse110.project_ms1.location.Coords;
import edu.ucsd.cse110.project_ms1.location.LocationModel;
import edu.ucsd.cse110.project_ms1.location.LocationPermissionChecker;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{
    private static final String TAG = "FOOBAR";
    public static final String EXTRA_USE_LOCATION_SERVICE = "use_location_updated";
    private boolean useLocationService;
    private GoogleMap map;
    private ActivityMapsBinding binding;
    private LocationModel model;

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
        //If the EXTRA_USE_LOCATION_SERVICE extra is set to false, then only mocked location
        // updates will be shown, and location permissions will not be requested. This is
        // appropriate for testing purposes.
        useLocationService = getIntent().getBooleanExtra(EXTRA_USE_LOCATION_SERVICE, false);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);
/*
        var mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

 */
        // Set up the model.
        model = new ViewModelProvider(this).get(LocationModel.class);

        // If GPS is enabled, then update the model from the Location service.
        if (useLocationService) {
            var permissionChecker = new LocationPermissionChecker(this);
            permissionChecker.ensurePermissions();

            var locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            var provider = LocationManager.GPS_PROVIDER;
            model.addLocationProviderSource(locationManager, provider);
        }
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
        //set up map
        initializeMap(map);

//        //permissions Setup
//        {
//            var requiredPermissions = new String[]{
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//
//            };
//
//            var hasNoLocationPerms = Arrays.stream(requiredPermissions)
//                    .map(perm -> ContextCompat.checkSelfPermission(this, perm))
//                    .allMatch(status -> status == PackageManager.PERMISSION_DENIED);
//
//            if (hasNoLocationPerms) {
//                requestPermissionLauncher.launch(requiredPermissions);
//                //the activity will be restarted when permission change
//                //this entire method will be re-run, but we won't get stuck here
//                return;
//            }
//        }

//        //listen for location updates
//        {
//            var provider = LocationManager.GPS_PROVIDER;
//            var locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//            var locationListener = new LocationListener(){
//                @Override
//                public void onLocationChanged(@NonNull Location location){
//                    Log.d("LAB7", String.format("Location changed: %s", location));
//
//                    LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
//                    var marker = new MarkerOptions().
//                            position(current).title("Navigation Step");
//                    map.addMarker(marker);
//                    //update current location latitude & longtitude
//
//                    LatLngs.currentLocationLatLng = current;
//                    onLocationChangeListener.OnLocationChange(current);
//                }
//            };
//            locationManager.requestLocationUpdates(provider, 0, 0f, locationListener);
//        }

        {
            // Observe the model and place a blue pin whenever the location is updated.
            model.getLastKnownCoords().observe(this, (coord) -> {
                Log.i(TAG, String.format("Observing location model update to %s", coord));
                var marker = new MarkerOptions()
                        .position(coord.toLatLng())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .title("Last Known Location");
                map.addMarker(marker);
            });

            // Test the above by mocking movement...
            var route = Coords
                    .interpolate(Coords.UCSD, Coords.ZOO, 12)
                    .collect(Collectors.toList());

            if (!useLocationService) {
                model.mockRoute(this, route, 500, TimeUnit.MILLISECONDS);
            }
        }
    }
    private void initializeMap(GoogleMap map) {
        // Enable zoom controls.
        var uiSettings = map.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        var start = Coords.UCSD;
        var end = Coords.ZOO;

        // Compute the midpoint between UCSD and the zoo.
        var cameraPosition = Coords
                .midpoint(start, end)
                .toLatLng();

        // Place a marker at the start.
        map.addMarker(new MarkerOptions()
                .position(start.toLatLng())
                .title("Start")
        );

        // Place a marker at the end.
        map.addMarker(new MarkerOptions()
                .position(end.toLatLng())
                .title("End")
        );

        // Move the camera and zoom to the right level.
        map.moveCamera(CameraUpdateFactory.newLatLng(cameraPosition));
        map.moveCamera(CameraUpdateFactory.zoomTo(11.5f));
    }


}
