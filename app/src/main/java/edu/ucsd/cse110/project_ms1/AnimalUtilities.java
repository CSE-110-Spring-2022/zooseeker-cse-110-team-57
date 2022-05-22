package edu.ucsd.cse110.project_ms1;

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
}