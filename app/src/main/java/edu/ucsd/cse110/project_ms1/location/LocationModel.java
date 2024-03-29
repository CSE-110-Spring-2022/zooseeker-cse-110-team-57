package edu.ucsd.cse110.project_ms1.location;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import edu.ucsd.cse110.project_ms1.AnimalItem;
import edu.ucsd.cse110.project_ms1.LatLngs;
import edu.ucsd.cse110.project_ms1.OnLocationChangeListener;

public class LocationModel extends AndroidViewModel {
    OnLocationChangeListener onLocationChangeListener;
    LifecycleOwner mOwner;
    private final String TAG = "FOOBAR";

    //A MediatorLiveData that merges events from both of the other two LiveData.
    private final MediatorLiveData<Coord> lastKnownCoords;
    //A MutableLiveData that is updated whenever a location update comes in from the Location Service.
    private LiveData<Coord> locationProviderSource = null;
    //A MutableLiveData that is updated whenever mockLocation is called.
    private MutableLiveData<Coord> mockSource = null;

    public LocationModel(@NonNull Application application, Context context, OnLocationChangeListener onLocationChangeListener) {
        super(application);
        this.onLocationChangeListener = onLocationChangeListener;
        this.mOwner = (LifecycleOwner) context;

        // Create and add the mock source.
        lastKnownCoords = new MediatorLiveData<>();
        mockSource = new MutableLiveData<>();

        //set the initialized location
        Coord gateCoord = AnimalItem.getExtranceGateCoord();
        mockSource.setValue(gateCoord);
        mockSource.postValue(gateCoord);

        lastKnownCoords.addSource(mockSource, new Observer<Coord>() {
            @Override
            public void onChanged(Coord coord) {
                lastKnownCoords.setValue(coord);
            }
        });
    }
    //getter of lastKnownCoords
    public LiveData<Coord> getLastKnownCoords() {
        return lastKnownCoords;
    }
    //getter of value of lastKnowCoords
    public Coord getCurrentCoord(){
        return lastKnownCoords.getValue();
    }



    /**
     * @param locationManager the location manager to request updates from.
     * @param provider        the provider to use for location updates (usually GPS).
     * @apiNote This method should only be called after location permissions have been obtained.
     * @implNote If a location provider source already exists, it is removed.
     */
    @SuppressLint("MissingPermission")
    public void addLocationProviderSource(LocationManager locationManager, String provider) {
        // If a location provider source is already added, remove it.
        if (locationProviderSource != null) {
            removeLocationProviderSource();
        }
        // Create a new GPS source.
        var providerSource = new MutableLiveData<Coord>();
        var locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Coord coord = Coord.fromLocation(location);
                Log.i(TAG, String.format("Model received GPS location update: %s", coord));
                providerSource.postValue(coord);
                onLocationChangeListener.OnLocationChange(coord);
            }
        };
        // Register for updates.
        locationManager.requestLocationUpdates(provider, 0, 0f, locationListener);
        locationProviderSource = providerSource;
        lastKnownCoords.addSource(locationProviderSource, lastKnownCoords::setValue);
    }

    void removeLocationProviderSource() {
        if (locationProviderSource == null) return;
        lastKnownCoords.removeSource(locationProviderSource);
    }
    //mock a single point
    @VisibleForTesting
    public void mockLocation(Coord updateCoord) {
        //if updated Coord != current Coord
        if (!updateCoord.equals(getCurrentCoord())) {
            mockSource.setValue(updateCoord);
            mockSource.postValue(updateCoord);
            onLocationChangeListener.OnLocationChange(updateCoord);
        }
        /*
        getLastKnownCoords().observe(mOwner, (coord) -> {
            Log.i(TAG, String.format("Observing location model update to %s", coord));
        });
         */
    }

    //mock a list of points
    @VisibleForTesting
    public Future<?> mockRoute(Context context, List<Coord> route, long delay, TimeUnit unit) {
        return Executors.newSingleThreadExecutor().submit(() -> {
            int i = 1;
            int n = route.size();
            for (var coord : route) {
                // Mock the location...
                Log.i(TAG, String.format("Model mocking route (%d / %d): %s", i++, n, coord));
                mockLocation(coord);

                // Sleep for a while...
                try {
                    Thread.sleep(unit.toMillis(delay));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
