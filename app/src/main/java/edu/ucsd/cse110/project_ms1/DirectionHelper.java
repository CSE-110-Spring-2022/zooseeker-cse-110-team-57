package edu.ucsd.cse110.project_ms1;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DirectionHelper {


    public static List<String> animalInRoute(List<AnimalItem> orderedAnimalList){
        List<String> route = new ArrayList<>();
        orderedAnimalList.forEach(animalItem ->route.add(animalItem.id));
        return route;
    }

    public static List<AnimalItem> loadAnimalItem(Context context, List<String> orderedAnimal){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Team57", Activity.MODE_PRIVATE);
        StringAndAnimalItem stringAndAnimalItem = new StringAndAnimalItem();
        List<AnimalItem> orderedAnimalItemList = new ArrayList<>();

        for (String animalName: orderedAnimal){
            //Log.d("animalName",animalName);
            //find the string containing all related information of a selected animal
            String animalItemInfoString = sharedPreferences.getString(animalName,
                    "No found such animal in sharedPreference");
            //get the animalItem
            //Log.d("StringtoAnimal",animalItemInfoString);
            AnimalItem animalItem = stringAndAnimalItem.StringToAnimalItem(animalItemInfoString);
            //add animalItem to the AnimalItem list
            orderedAnimalItemList.add(animalItem);
        }

        return orderedAnimalItemList;
    }


    public static HashMap<Integer,List<IdentifiedWeightedEdge>> findRoute(List<route_node> planned_route){
        //  (order,paths)
        HashMap<Integer,List<IdentifiedWeightedEdge>> route = new HashMap<>();

        for(int i = 0;i < planned_route.size() - 1 ;i++){
            String source = planned_route.get(i).animal.name;
            String sink = planned_route.get(i+1).animal.name;

            GraphPath<String, IdentifiedWeightedEdge> path = AnimalItem.adapted_find_shortest_path(AnimalItem.gInfo, source, sink);
            //list of street in this walk.
            List<IdentifiedWeightedEdge> streets = path.getEdgeList();

            route.put(i,streets);
        }

        return route;
    }

    public static List<IdentifiedWeightedEdge> findPathBetween(String source,String goal){

            GraphPath<String, IdentifiedWeightedEdge> path = AnimalItem.adapted_find_shortest_path(AnimalItem.gInfo, source, goal);
            //list of street in this walk.
            List<IdentifiedWeightedEdge> streets = path.getEdgeList();

        return streets;
    }


    public static List<String> detailPath(List<IdentifiedWeightedEdge> path, String start){
        List<String> display = new ArrayList<>();
        String street = path.get(0).getId();
        String source = start;
        String target = "";
        String edgeInfo = "";
        Double distance = 0.0;
        for(IdentifiedWeightedEdge edge : path){
            //target node name
            String edgeSource = AnimalItem.vInfo.get(AnimalItem.gInfo.getEdgeSource(edge)).name;
            Log.d("edgeSource in displayPath",edgeSource);
            Log.d("source in displayPath",source);
            if(edgeSource.equals(source)){
                target = AnimalItem.vInfo.get(AnimalItem.gInfo.getEdgeTarget(edge)).name;
            }else{
                target = AnimalItem.vInfo.get(AnimalItem.gInfo.getEdgeSource(edge)).name;
            }
            //move the source toward node in the path
            source = new String(target);

            //distance that user need to walk on this street.
            distance = AnimalItem.gInfo.getEdgeWeight(edge);
            //If we continues on the same street.
            String nextStreet = AnimalItem.eInfo.get(edge.getId()).street;

            if(street.equals(nextStreet)){
                edgeInfo = "Continue on " + street + " " + distance + " ft towards " + target;
            }else{
                street = nextStreet;
                edgeInfo = "Proceed on " + street + " " + distance + " ft towards " + target;
            }
            display.add(edgeInfo);
        }
        return display;
    }

    public static List<String> briefPath(List<IdentifiedWeightedEdge> path, String startNode){
        List<String> display = new ArrayList<>();
        String street = path.get(0).getId();
        String source = startNode;
        String target;
        String edgeInfo;
        Double totalDistance = 0.0;

        for(IdentifiedWeightedEdge edge : path){
            //target node name
            String edgeSource = AnimalItem.vInfo.get(AnimalItem.gInfo.getEdgeSource(edge)).name;
            /*
            Log.d("edgeSource in displayPath",edgeSource);
            Log.d("source in displayPath",source);
            */
            if(edgeSource.equals(source)){
                target = AnimalItem.vInfo.get(AnimalItem.gInfo.getEdgeTarget(edge)).name;
            }else{
                target = AnimalItem.vInfo.get(AnimalItem.gInfo.getEdgeSource(edge)).name;
            }
            //move the source toward node in the path
            source = new String(target);

            //distance that user need to walk on this street.
            Double distance = AnimalItem.gInfo.getEdgeWeight(edge);
            //If we continues on the same street.
            String nextStreet = AnimalItem.eInfo.get(edge.getId()).street;

            if(street.equals(nextStreet)){
                totalDistance += distance;
                continue;
            }else{
                edgeInfo = "Proceed on " + street + " " + totalDistance + " ft towards " + target;
                street = nextStreet;
            }

            display.add(edgeInfo);
        }
        return display;
    }



    public static double totalDistance(List<IdentifiedWeightedEdge> path){
        double distance = 0.0;
        for(IdentifiedWeightedEdge edge : path){
            distance += AnimalItem.gInfo.getEdgeWeight(edge);
        }
        return distance;
    }

    public static String getNodeName(String id){
        return AnimalItem.vInfo.get(id).name;
    }

    public static void saveDirectionsInformation(Context context, int order, boolean isNext){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Team57", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("currentOrder", Integer.toString(order));
        editor.putString("currentIsNext", String.valueOf(isNext));
        editor.commit();
        editor.apply();
    }

    public static List<String> loadDirectionsInformation(Context context){
        List<String> retainedInfo = new ArrayList<String>();
        SharedPreferences sharedPreferences = context.getSharedPreferences("Team57", Activity.MODE_PRIVATE);
        String currentOrder = sharedPreferences.getString("currentOrder", "0");
        String currentIsNext = sharedPreferences.getString("currentIsNext", "true");
        retainedInfo.add(currentOrder);
        retainedInfo.add(currentIsNext);
        return retainedInfo;
    }

    public static void restoreCurrentOrderAndIsNext(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Team57", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("currentOrder", "0");
        editor.putString("currentIsNext", "true");
        editor.commit();
        editor.apply();
    }
/*
    public static boolean isNext(HashMap<Integer,DirectionData> zooRoute, int order, String source){
        String edgeSource = zooRoute.get(order).getStartExhibit();
        Log.d("edgeSource in displayPath",edgeSource);
        Log.d("source in displayPath",source);
        if(edgeSource.equals(source)){
            return true;
        }else{
            return false;
        }
    }

 */

}
