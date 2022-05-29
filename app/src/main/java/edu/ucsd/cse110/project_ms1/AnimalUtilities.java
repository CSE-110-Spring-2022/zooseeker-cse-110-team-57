package edu.ucsd.cse110.project_ms1;

import android.content.Context;

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
    public static void loadZooInfo(Context context){
        String vPath = "exhibit_info.json";
        String ePath = "trail_info.json";
        String gPath = "zoo_graph.json";
        try {
            AnimalItem.loadInfo(context, vPath, ePath, gPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static AnimalItem getClosestAnimalItem(List<AnimalItem> animal_items, String start, double min_distance, AnimalItem closest_animal) {
        String goal;
        //use for loop to find next closet exhibit
        for (AnimalItem item : animal_items) {
            goal = item.id;
            Graph<String, IdentifiedWeightedEdge>  graph = AnimalItem.gInfo;
            GraphPath<String, IdentifiedWeightedEdge> path = AnimalItem.adapted_find_shortest_path(graph, start, goal);
            double curr_dis = AnimalItem.route_length(path);
            if (curr_dis < min_distance) {
                min_distance = curr_dis;
                closest_animal = item;
            }

        }
        return closest_animal;
    }

//    public static  double get_distance(LatLng l1, LatLng l2){
//        return 0;
//    }

    public static  double get_distance(LatLng ll, AnimalItem animal){
        return animal.getDistanceToInFeet(new Coord(ll));
    }

    public static boolean check_off_route (int visiting_order, List<route_node> route, LatLng curr_position){

        //if just going to exit gate
        if (visiting_order+1 == route.size()) return false;

        AnimalItem planned_next_animal = route.get(visiting_order+1).exhibit;
        double distance_to_the_next =get_distance(curr_position,planned_next_animal);
        for (int i=visiting_order+1; i<route.size()-1; i++){
            AnimalItem animal = route.get(i+1).exhibit;
            double dis = get_distance(curr_position,animal);
            if (dis<distance_to_the_next) return true;
        }
        return  false;
    }

    public static List<route_node> reroute (int visiting_order, List<route_node> route, LatLng curr_position){
        List<AnimalItem> left_animal_items = new ArrayList<>();

        while(visiting_order<route.size()-1){

            left_animal_items.add(route.get(visiting_order+1).exhibit);
            route.remove(visiting_order+1);
        }

        String start = find_starting_point(left_animal_items,curr_position);

        List<route_node> rest_route = AnimalItem.plan_route(left_animal_items, start);

        //concat (first half of) original and rest_route
        List<route_node> newRoute = Stream.concat(route.stream(), rest_route.stream())
                .collect(Collectors.toList());


        List<String> newNames = new ArrayList<>();
        for (route_node r : newRoute){
            newNames.add(r.exhibit.name);
        }
        return  newRoute;
    }

    public static String find_starting_point(List<AnimalItem> animals, LatLng curr_position){
        String retval = null;
        double min_dis = Double.MAX_VALUE;
        for (AnimalItem  animal : animals){
            if (min_dis > get_distance(curr_position, animal)){
                min_dis = get_distance(curr_position, animal);
                retval = animal.id;
            }
        }
        return  retval;
    }

    public static boolean matchByTag(List<String> stringList, String str){
        for (String s : stringList){
            if (s.contains(str)) return true;
        }
        return  false;
    }

}