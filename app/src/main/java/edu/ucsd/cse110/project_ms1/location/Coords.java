package edu.ucsd.cse110.project_ms1.location;

import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import edu.ucsd.cse110.project_ms1.AnimalItem;

public class Coords {
    public static final Coord UCSD = Coord.of(32.8801, -117.2340);
    public static final Coord ZOO = Coord.of(32.7353, -117.1490);
    public static Coord currentCoord;
    public static final Double DEG_LAT_IN_FT = 363843.57;
    public static final Double DEG_LNG_IN_FT = 307515.50;
    public static final Double BASE = 100.00;

    /**
     * @param p1 first coordinate
     * @param p2 second coordinate
     * @return midpoint between p1 and p2
     */
    public static Coord midpoint(Coord p1, Coord p2) {
        return Coord.of((p1.lat + p2.lat) / 2, (p1.lng + p2.lng) / 2);
    }

    /**
     * @param p1 start coordinate
     * @param p2 end coordinate
     * @param n number of points between to interpolate.
     * @return a list of evenly spaced points between p1 and p2 (including p1 and p2).
     */
    public static Stream<Coord> interpolate(Coord p1, Coord p2, int n) {
        // Map from i={0, 1, ... n} to t={0.0, 0.1, ..., 1.0} with n divisions.
        ///     t(i; n) = i / n
        // Linear interpolate between l1 and l2 using t:
        //      p(t) = p1 + (p2 - p1) * t

        return IntStream.range(0, n)
            .mapToDouble(i -> (double) i / (double) n)
            .mapToObj(t -> Coord.of(
                p1.lat + (p2.lat - p1.lat) * t,
                p1.lng + (p2.lng - p1.lng) * t
            ));
    }

    public static List<Coord> getTenPointsInLine(String start, String goal){
        AnimalItem start_landmark = AnimalItem.search_by_tag(start).get(0);
        AnimalItem goal_landmark = AnimalItem.search_by_tag(goal).get(0);
        Coord start_coord = Coord.fromLatLng(start_landmark.position);
        Coord goal_coord = Coord.fromLatLng(goal_landmark.position);
        List<Coord> route = Coords
                .interpolate(start_coord, goal_coord, 10)
                .collect(Collectors.toList());
        return route;
    }

}
