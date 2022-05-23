package edu.ucsd.cse110.project_ms1;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

public class LatLngs {
    public static final LatLng UCSD_LATLNG = new LatLng(32.8801, -117.2340);
    public static final LatLng ZOO_LATLNG = new LatLng(32.7353, -117.1490);

    public static LatLng currentLocationLatLng;


    @NonNull
    public static LatLng midpoint(LatLng l1, LatLng l2) {
        return new LatLng(
                (l1.latitude + l2.latitude) / 2,
                (l1.longitude + l2.longitude) / 2
        );
    }
}

