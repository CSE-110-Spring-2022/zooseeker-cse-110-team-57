package edu.ucsd.cse110.project_ms1.location;

import android.location.Location;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Objects;


public class Coord {
    public final Double lat;
    public final Double lng;
    public static final Double DEG_LAT_IN_FT = 363843.57;
    public static final Double DEG_LNG_IN_FT = 307515.50;
    public static final Double BASE = 100.00;

    public Coord(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Coord(LatLng ll){
        this.lat = ll.latitude;
        this.lng = ll.longitude;
    }

    public static Coord of(Double lat, Double lng) {
        return new Coord(lat, lng);
    }

    public static Coord fromLatLng(LatLng latLng) {
        return Coord.of(latLng.latitude, latLng.longitude);
    }

    public LatLng toLatLng() {
        return new LatLng(lat, lng);
    }

    public static Coord fromLocation(Location location) {
        return Coord.of(location.getLatitude(), location.getLongitude());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coord coord = (Coord) o;
        return Double.compare(coord.lat, lat) == 0 && Double.compare(coord.lng, lng) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(lat, lng);
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("Coord{lat=%s, lng=%s}", lat, lng);
    }

    public Pair<Double, Double> CoordToFeet(){
        return Pair.create(this.lat * DEG_LAT_IN_FT, this.lng * DEG_LNG_IN_FT);
    }

}
