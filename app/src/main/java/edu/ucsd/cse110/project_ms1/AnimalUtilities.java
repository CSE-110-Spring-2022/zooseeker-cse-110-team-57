package edu.ucsd.cse110.project_ms1;

import com.google.android.gms.maps.model.LatLng;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnimalUtilities {
    static AnimalItem getClosestAnimalItem(List<AnimalItem> animal_items, String start, double min_distance, AnimalItem closest_animal) {
        String goal;
        //use for loop to find next closet exhibit
        for (AnimalItem item : animal_items) {
            goal = item.id;
            GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(AnimalItem.gInfo, start, goal);
            double curr_dis = AnimalItem.route_length(path);
            if (curr_dis < min_distance) {
                min_distance = curr_dis;
                closest_animal = item;

            }
        }
        return closest_animal;
    }

    //get the closest landmark of current location
    public static ZooData.VertexInfo getClosestLandmark(LatLng current){
        return null;
//        Double min;
//        ZooData.VertexInfo closest;
//        for (ZooData.VertexInfo landmark: "wait for stater code // all vertex with latlng"){
//            LatLng landmarkPosistion = new LatLng(landmark.lat, landmark.lng);
//            // Or maybe a vertex and a latlng and then find a path ...? most likely. wait for start code
//            Double distance = ;
//            if (distance < min){
//                min = distance;
//                closest = landmark;
//            }
//        }
//        return closest;
    }

    public static  double get_distance(LatLng l1, LatLng l2){
        return 0;
    }

    public static  double get_distance(LatLng ll, AnimalItem animal){
        return 0;
    }

    public static boolean off_route (int visiting_order, List<route_node> route, LatLng curr_position){
        // all visited, going to the gate
        if (visiting_order == route.size())
            return false;

        AnimalItem planned_next_animal = route.get(visiting_order+1).animal;
        double distance_to_the_next =get_distance(curr_position,planned_next_animal);
        for (int i=visiting_order+1; i<route.size(); i++){
            AnimalItem animal = route.get(i+1).animal;
            double dis = get_distance(curr_position,animal);
            if (dis<distance_to_the_next) return true;
        }
        return  false;
    }

    public static List<route_node> reroute (int visiting_order, List<route_node> route, LatLng curr_position){
        List<AnimalItem> left_animal_items = new ArrayList<>();

        while(visiting_order!=route.size()){

            left_animal_items.add(route.get(visiting_order+1).animal);
            route.remove(visiting_order+1);
        }
        List<route_node> rest_route = AnimalItem.plan_route(left_animal_items);

        //concat (first half of) original and rest_route
        List<route_node> newRoute = Stream.concat(route.stream(), rest_route.stream())
                .collect(Collectors.toList());
        return  newRoute;
    }

    public static boolean matchByTag(List<String> stringList, String str){
        for (String s : stringList){
            if (s.contains(str)) return true;
        }
        return  false;
    }
}