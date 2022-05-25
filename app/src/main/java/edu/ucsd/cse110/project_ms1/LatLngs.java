package edu.ucsd.cse110.project_ms1;

import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import edu.ucsd.cse110.project_ms1.location.Coord;

public class LatLngs {
//    public static final LatLng UCSD_LATLNG = new LatLng(32.8801, -117.2340);
//    public static final LatLng ZOO_LATLNG = new LatLng(32.7353, -117.1490);
    public static final Coord UCSD = Coord.of(32.8801, -117.2340);
    public static final Coord ZOO = Coord.of(32.7353, -117.1490);

    public static final Double DEG_LAT_IN_FT = 363843.57;
    public static final Double DEG_LNG_IN_FT = 307515.50;
    public static final Double BASE = 100.00;

    public static LatLng currentLocationLatLng;

    public static LatLng fromLocation(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }


    public static Location toGPSLocation(LatLng latLng) {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        location.setAccuracy(100f);
        location.setTime(System.currentTimeMillis());
        location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        return location;
    }

    public static LatLng midpoint(LatLng l1, LatLng l2) {
        return new LatLng(
                (l1.latitude + l2.latitude) / 2,
                (l1.longitude + l2.longitude) / 2
        );
    }

    public static List<LatLng> pointsBetween(LatLng l1, LatLng l2, int n) {
        var dLat = Math.abs(l1.latitude - l2.latitude);
        var dLng = Math.abs(l1.longitude - l2.longitude);

        return IntStream.range(0, n)
                // Map from i={0, 1, ... n} to t={0.0, 0.1, ..., 1.0} with n divisions.
                .mapToDouble(i -> (double) i / (double) n)
                // Linear interpolate between l1 and l2 using t.
                .mapToObj(t -> {
                    // p(t) = p0 + (p1 - p0) * t
                    return new LatLng(
                            l1.latitude + (l2.latitude - l1.latitude) * t,
                            l1.longitude + (l2.longitude - l1.longitude) * t
                    );
                })
                .collect(Collectors.toList());
    }
    public Pair<Double, Double> CoordsToFeet(LatLng latLng){
        return Pair.create(latLng.latitude * DEG_LAT_IN_FT, latLng.longitude * DEG_LNG_IN_FT);
    }
}

