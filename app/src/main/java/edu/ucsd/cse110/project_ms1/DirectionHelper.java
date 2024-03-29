package edu.ucsd.cse110.project_ms1;

import static org.apache.commons.lang3.math.NumberUtils.max;
import static org.apache.commons.lang3.math.NumberUtils.min;

import static java.lang.Math.abs;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import org.jgrapht.GraphPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import edu.ucsd.cse110.project_ms1.location.Coord;
import edu.ucsd.cse110.project_ms1.location.Coords;

public class DirectionHelper {

    //get the names of all selected animals
    public static List<String> animalInRoute(List<AnimalItem> orderedAnimalList){
        List<String> route = new ArrayList<>();
        orderedAnimalList.forEach(animalItem ->route.add(animalItem.id));
        return route;
    }

    //load AnimalItem from sharedPreferences
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

    //HashMap of edges
    public static HashMap<Integer,List<IdentifiedWeightedEdge>> findRoute(List<route_node> planned_route){
        //  (order,paths)
        HashMap<Integer,List<IdentifiedWeightedEdge>> route = new HashMap<>();

        for(int i = 0;i < planned_route.size() - 1 ;i++){
            String source = planned_route.get(i).exhibit.name;
            String sink = planned_route.get(i+1).exhibit.name;

            GraphPath<String, IdentifiedWeightedEdge> path = AnimalItem.adapted_find_shortest_path(AnimalItem.gInfo, source, sink);
            //list of street in this walk.
            List<IdentifiedWeightedEdge> streets = path.getEdgeList();

            route.put(i,streets);
        }

        return route;
    }
    //find the path between "source" and "goal"
    public static List<IdentifiedWeightedEdge> findPathBetween(String source_id, String goal_id){

            GraphPath<String, IdentifiedWeightedEdge> path =
                    AnimalItem.adapted_find_shortest_path(AnimalItem.gInfo, source_id, goal_id);
            //list of street in this walk.
            List<IdentifiedWeightedEdge> streets = path.getEdgeList();

        return streets;
    }

    //get the directions in detail version
    public static List<String> detailPath(List<IdentifiedWeightedEdge> path, String source_id){
        List<String> display = new ArrayList<>();
        String street = path.get(0).getId();
        String source = AnimalItem.vInfo.get(source_id).name;
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
                edgeInfo = "Continue on \"" + street + "\" " + distance + " ft towards \"" + target + "\"";
            }else{
                street = nextStreet;
                edgeInfo = "Proceed on \"" + street + "\" " + distance + " ft towards \"" + target + "\"";
            }
            display.add(edgeInfo);
        }
        return display;
    }
    //get the directions in brief version
    public static List<String> briefPath(List<IdentifiedWeightedEdge> path, String source_id){
        List<String> display = new ArrayList<>();
        List<Double> distances = new ArrayList<>();
        List<String> streets = new ArrayList<>();
        List<String> targets = new ArrayList<>();

        DirectionHelper.briefBuild(path,source_id,distances,streets,targets);

        Log.d("briefPathDistance",distances.toString());
        Log.d("briefPathStreets",streets.toString());
        Log.d("briefPathTargets",targets.toString());
        for(int i = 0; i < streets.size();i++){
            String edgeInfo = "Proceed on \"" + streets.get(i) + "\" " + distances.get(i).toString() + " ft towards \"" + targets.get(i) + "\"";
            display.add(edgeInfo);
        }
        return display;
    }

    public static void briefBuild(List<IdentifiedWeightedEdge> path, String source_id, List<Double> distances,
                                      List<String> streets, List<String> targets){
        String source = AnimalItem.vInfo.get(source_id).name;
        for(int i = 0; i < path.size();i++){
            String target;
            //target node name
            String edgeSource = AnimalItem.vInfo.get(AnimalItem.gInfo.getEdgeSource(path.get(i))).name;
            if(edgeSource.equals(source)){
                target = AnimalItem.vInfo.get(AnimalItem.gInfo.getEdgeTarget(path.get(i))).name;
            }else{
                target = AnimalItem.vInfo.get(AnimalItem.gInfo.getEdgeSource(path.get(i))).name;
            }
            //move the source toward node in the path
            source = target;
            /*
            Log.d("edgeSource in displayPath",edgeSource);
            Log.d("source in displayPath",source);
            */

            IdentifiedWeightedEdge edge = path.get(i);
            //If we continues on the same street.
            String street = AnimalItem.eInfo.get(edge.getId()).street;
            //distance that user need to walk on this street.
            double distance = AnimalItem.gInfo.getEdgeWeight(edge);


            if(!streets.contains(street)){
                streets.add(street);
                targets.add(target);
                distances.add(distance);
            }else {
                double newDistance = distances.get(distances.size()-1)+distance;
                distances.set(distances.size()-1, newDistance);
                targets.set(targets.size()-1, target);
            }
        }
    }


    @NonNull
    public static String getSingleEdgeInfo(IdentifiedWeightedEdge singleEdge){
        String street = AnimalItem.eInfo.get(singleEdge.getId()).street;
        List<String> CloserEndpointInfo = getCloserEndpointInfo(singleEdge);
        String target = CloserEndpointInfo.get(0);
        String distance = CloserEndpointInfo.get(1);
        String edgeInfo = "Proceed on \"" + street + "\" " + distance + " ft towards \"" + target + "\"";
        return edgeInfo;
    }

    //get the closer endpoints of an edge and distance based on current location Coord
    public static List<String> getCloserEndpointInfo(IdentifiedWeightedEdge singleEdge) {
        List<String> CloserEndPointList = new ArrayList<>();
        ZooData.VertexInfo singleEdge_source = AnimalItem.vInfo.get(AnimalItem.gInfo.getEdgeSource(singleEdge));
        ZooData.VertexInfo singleEdge_goal = AnimalItem.vInfo.get(AnimalItem.gInfo.getEdgeTarget(singleEdge));
        Coord singleEdge_source_coord = new Coord(singleEdge_source.lat, singleEdge_source.lng);
        Coord singleEdge_goal_coord = new Coord(singleEdge_goal.lat, singleEdge_goal.lng);
        double Source_distance = AnimalItem.distance_between_coords(Coords.currentLocationCoord, singleEdge_source_coord);
        double Goal_distance = AnimalItem.distance_between_coords(Coords.currentLocationCoord, singleEdge_goal_coord);
        if (Source_distance <= Goal_distance){
            CloserEndPointList.add(singleEdge_source.name);
            CloserEndPointList.add(Double.toString(Source_distance));
        }
        else{
            CloserEndPointList.add(singleEdge_goal.name);
            CloserEndPointList.add(Double.toString(Goal_distance));
        }
        return CloserEndPointList;
    }


    public static void showUpdateAlert(Context context, String street) {
        String reminder = "The direction instruction to \"" + street + "\" has been updated.";
        Utilities.showAlert((Activity) context, reminder);
    }


    //get the total distance of path
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

    //save the direction order and isNext
    public static void saveDirectionsInformation(Context context, int order, boolean isNext){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Team57", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("currentOrder", Integer.toString(order));
        editor.putString("currentIsNext", String.valueOf(isNext));
        editor.commit();
        editor.apply();
    }
    //load the direction order and isNext
    public static List<String> loadDirectionsInformation(Context context){
        List<String> retainedInfo = new ArrayList<String>();
        SharedPreferences sharedPreferences = context.getSharedPreferences("Team57", Activity.MODE_PRIVATE);
        String currentOrder = sharedPreferences.getString("currentOrder", "0");
        String currentIsNext = sharedPreferences.getString("currentIsNext", "true");
        retainedInfo.add(currentOrder);
        retainedInfo.add(currentIsNext);
        return retainedInfo;
    }
    //restore the direction order is isNext
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
    //routeNode to directionData
    public static List<DirectionData> routeNode_to_DirectionData(List<route_node> planned_route) {

        List<DirectionData> orderedAnimalList = new ArrayList<>();

        planned_route.stream().forEach(node -> {
            DirectionData nodeData = new DirectionData(node.exhibit.name,
                    node.exhibit.id,
                    node.names);
            orderedAnimalList.add(nodeData);
        });

        //add gate at begin of route
        DirectionData gate = new DirectionData("Entrance and Exit Gate",
                "entrance_exit_gate",
                new ArrayList<>(Arrays.asList("")));
        orderedAnimalList.add(0, gate);

        return orderedAnimalList;
    }

    //Check the LatLng is on range
    public static boolean onRange(LatLng curr, LatLng nodeA, LatLng nodeB){
        boolean x1 = curr.latitude >= min(nodeA.latitude, nodeB.latitude);
        boolean x2 = curr.latitude <= max(nodeA.latitude,nodeB.latitude);
        boolean y1 = curr.longitude >= min(nodeA.longitude, nodeB.longitude);
        boolean y2 = curr.longitude <= max(nodeA.longitude,nodeB.longitude);

        return x1 && x2 && y1 && y2;
    }

    //check if user current location between the two exhibit with 0.000001 threshold
    public static boolean onTrack(LatLng curr, LatLng nodeA, LatLng nodeB){
        //y=mx+b

//        //m = y2-y1/x2-x1
//        double m = findSlope(nodeA,nodeB);
//        //b = y-mx
//        double b = nodeA.longitude - m*nodeA.latitude;

        double x1 = curr.latitude - nodeA.latitude;
        double y1 = curr.longitude - nodeA.longitude;

        double x2 = nodeB.latitude - nodeA.latitude;
        double y2 = nodeB.longitude - nodeA.longitude;

        return Math.abs(x1*y2 - y1*x2) < 0.000001;
    }

    //find the slope of line that between two exhibits
    public static double findSlope( LatLng nodeA, LatLng nodeB){
        return nodeB.longitude- nodeA.longitude / nodeB.latitude - nodeA.latitude;
    }

    //find the current street
    public static IdentifiedWeightedEdge findCurrStreet(String nearestExhibit, LatLng curr){
        //all edge that connect to the nearestExhibit
        Set<IdentifiedWeightedEdge> incomingEdges = AnimalItem.gInfo.incomingEdgesOf(nearestExhibit);
        Log.v("findCurrStreet",incomingEdges.toString());
        IdentifiedWeightedEdge possibleEdge = incomingEdges.stream().findFirst().get();
        for (IdentifiedWeightedEdge edge : incomingEdges){
            String nodeA = AnimalItem.gInfo.getEdgeSource(edge);
            String nodeB = AnimalItem.gInfo.getEdgeTarget(edge);
            LatLng LatLngA = getLatLng(nodeA);
            LatLng LatLngB = getLatLng(nodeB);

            //if current location on rectangle of two nodes as point
            boolean onRange = onRange(curr,LatLngA,LatLngB);
            //if current location on the line
            boolean onLine = onTrack(curr,LatLngA,LatLngB);

            if(onRange){
                possibleEdge = edge;
            }

            if(onRange && onLine){
                return edge;
            }

        }
        return possibleEdge;
    }
    //getter
    public static LatLng getLatLng(String exhibit){
        for(Map.Entry<String, ZooData.VertexInfo> vertexInfo : AnimalItem.vInfo.entrySet()) {
            if(vertexInfo.getKey().equals(exhibit)){
                return new LatLng(vertexInfo.getValue().lat,vertexInfo.getValue().lng);
            }
        }
        return new LatLng(0.0,0.0);
    }

    //get the shortest path distance from current location to destination
    public static double getPathDistanceBetween(Coord current, AnimalItem Destination){
        double first_path, second_path;
        //get the closest landmark
        String closestName_id = AnimalItem.getClosestLandmark(current).get(0);
        String closestName_parent_id = AnimalItem.Latlng_ids_Map.get(closestName_id);

        return AnimalItem.adapted_find_shortest_path(AnimalItem.gInfo,
                closestName_parent_id, Destination.id).getWeight();
    }

}


