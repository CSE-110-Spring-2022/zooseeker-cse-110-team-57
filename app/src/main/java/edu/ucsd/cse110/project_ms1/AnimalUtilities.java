package edu.ucsd.cse110.project_ms1;

import com.google.android.gms.maps.model.LatLng;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.List;

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
}