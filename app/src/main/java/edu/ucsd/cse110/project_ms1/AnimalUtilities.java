package edu.ucsd.cse110.project_ms1;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.ucsd.cse110.project_ms1.location.Coord;

public class AnimalUtilities {
    //load 3 Json files
    public static void loadZooInfo(Context context) {
        String vPath = "exhibit_info.json";
        String ePath = "trail_info.json";
        String gPath = "zoo_graph.json";
        try {
            AnimalItem.loadInfo(context, vPath, ePath, gPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //get the closest animalitem
    public static AnimalItem getClosestAnimalItem(List<AnimalItem> animal_items, String start, double min_distance, AnimalItem closest_animal) {
        String goal;
        //use for loop to find next closet exhibit
        for (AnimalItem item : animal_items) {
            goal = item.id;
            Graph<String, IdentifiedWeightedEdge> graph = AnimalItem.gInfo;
            GraphPath<String, IdentifiedWeightedEdge> path = AnimalItem.adapted_find_shortest_path(graph, start, goal);
            double curr_dis = AnimalItem.route_length(path);
            if (curr_dis < min_distance) {
                min_distance = curr_dis;
                closest_animal = item;
            }

        }
        return closest_animal;
    }


    //get the distance between a Coord and a AnimalItem
    public static double get_distance(LatLng ll, AnimalItem animal) {
        return animal.getDistanceToInFeet(new Coord(ll));
    }

    //Check if Off-route is needed
    public static boolean check_off_route(int visiting_order, List<route_node> route, LatLng curr_position) {
        //if just going to exit gate
        if (visiting_order + 1 == route.size()) {return false;}
        AnimalItem planned_next_animal = route.get(visiting_order).exhibit;
        double distance_to_the_next = DirectionHelper.getPathDistanceBetween(new Coord(curr_position), planned_next_animal);

        for (int i = visiting_order + 1; i < route.size() - 1; i++) {
            AnimalItem animal = route.get(i).exhibit;
            double dis = DirectionHelper.getPathDistanceBetween(new Coord(curr_position), animal);
            if (dis < distance_to_the_next) return true;
        }

        return false;
    }

    //reroute
    public static List<route_node> reroute(int visiting_order, List<route_node> route, LatLng curr_position, boolean going_forward) {
        List<AnimalItem> left_animal_items = new ArrayList<>();

        // find what is left to visit
        if (going_forward) {
            while (visiting_order < route.size() - 1) {

                route_node r = route.get(visiting_order );
                for (int i =0; i<r.names.size(); i++){
                    left_animal_items.add(new AnimalItem(r.ids.get(i),null,r.names.get(i),null));
                }
                route.remove(visiting_order );
            }
            route.remove(route.size()-1); //remove the gate, because it will be generated later
        } else {

            for (int k = 0; k < visiting_order-1; k++) {

                route_node r = route.get(0 );
                for (int i =0; i<r.names.size(); i++){
                    left_animal_items.add(new AnimalItem(r.ids.get(i),null,r.names.get(i),null));
                }
                route.remove(0);
            }
        }


        //find the route to the left exhibits
        String start = find_starting_point(left_animal_items, curr_position);
        List<route_node> rest_route;
        if (left_animal_items.size()==0){
            rest_route = new ArrayList<>();
            rest_route.add(new route_node(AnimalItem.gate,null,0.0000838,null));
        }
        else {
            rest_route = AnimalItem.plan_route(left_animal_items, start, false);
        }

        //debugging
        ArrayList<String> to_visit_names= new ArrayList<>();
        for (route_node r : rest_route) {
            to_visit_names.add(r.exhibit.name);
        }
        Log.d("rest animal", String.join(", ", to_visit_names));

        ArrayList<String> visited_animals= new ArrayList<>();
        for (route_node r : route) {
            visited_animals.add(r.exhibit.name);
        }
        Log.d("visited animal", String.join(", ", visited_animals));


        //concat (first half of) original and rest_route
        List<route_node> newRoute;
        if (going_forward) {
            newRoute = Stream.concat(route.stream(), rest_route.stream())
                    .collect(Collectors.toList());
        } else {
            rest_route.remove(0); //remove the generated gate
            newRoute = Stream.concat(rest_route.stream(), route.stream())
                    .collect(Collectors.toList());
        }



        List<String> newNames = new ArrayList<>();
        for (route_node r : newRoute) {
            newNames.add(r.exhibit.name);
        }
        return newRoute;
    }

    //given the left animals to visit, return where I should go first
    public static String find_starting_point(List<AnimalItem> animals, LatLng curr_position) {
        String retval = null;
        double min_dis = Double.MAX_VALUE;
        for (AnimalItem animal : animals) {
            if (min_dis > DirectionHelper.getPathDistanceBetween(new Coord(curr_position), animal)) {
                min_dis = DirectionHelper.getPathDistanceBetween(new Coord(curr_position), animal);
                retval = animal.id;
            }
        }
        return retval;
    }

    public static boolean matchByTag(List<String> stringList, String str) {
        for (String s : stringList) {
            if (s.contains(str)) return true;
        }
        return false;
    }


}
