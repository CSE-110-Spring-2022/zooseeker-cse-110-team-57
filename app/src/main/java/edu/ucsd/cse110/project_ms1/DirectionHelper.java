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
import java.util.stream.Collectors;

public class DirectionHelper {


    public static List<String> animalInRoute(List<AnimalItem> orderedAnimalList){
        List<String> route = new ArrayList<>();
        orderedAnimalList.forEach(animalItem ->route.add(animalItem.id));
        return route;
    }

    public static List<String> loadAnimalItem(Context context, ArrayList<String> orderedAnimal){
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

        return animalInRoute(orderedAnimalItemList);
    }


    public static HashMap<Integer,List<IdentifiedWeightedEdge>> findRoute(List<String> orderedAnimalList){
        //  (order,paths)
        HashMap<Integer,List<IdentifiedWeightedEdge>> route = new HashMap<>();

        //we need add the front gate into orderedAnimalList, so that route begin at gate
        orderedAnimalList.add(0,"entrance_exit_gate");
        orderedAnimalList.add(orderedAnimalList.size(),"entrance_exit_gate");


        for(int i = 0;i < orderedAnimalList.size() - 1 ;i++){
            String source = orderedAnimalList.get(i);
            String sink = orderedAnimalList.get(i+1);

            GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(AnimalItem.gInfo, source, sink);
            //list of street in this walk.
            List<IdentifiedWeightedEdge> streets = path.getEdgeList();

            route.put(i,streets);
        }

        return route;
    }


    public static List<String> displayPath(List<IdentifiedWeightedEdge> path,String start){
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

    public static void saveDirectionsInformation(Context context, String startName, String goalName, int order){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Team57", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("currentStart", startName);
        editor.putString("currentGoal", goalName);
        editor.putString("currentOrder", Integer.toString(order));
        editor.commit();
        editor.apply();
    }

    public static ArrayList<String> loadDirectionsInformation(Context context){
        ArrayList<String> retainedDirections = new ArrayList<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences("Team57", Activity.MODE_PRIVATE);
        retainedDirections.add(sharedPreferences.getString("currentOrder", "0"));
        retainedDirections.add(sharedPreferences.getString("currentStart", "entrance_exit_gate"));
        retainedDirections.add(sharedPreferences.getString("currentGoal", ""));
        return retainedDirections;
    }

}
