package edu.ucsd.cse110.project_ms1;

import android.content.Context;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;



@Entity(tableName = "animal_items")
@TypeConverters({TagConverter.class,LLConverter.class})
public class AnimalItem {
    @PrimaryKey(autoGenerate = true)
    public long unique_id;
    @NonNull
    public String id;
    public ArrayList<String> tags;
    public String name; //essentially, the name is the tag in json file
    public boolean searched;
    public boolean visited;
    public LatLng position;

    public static Map<String, ZooData.VertexInfo> vInfo;
    public static Map<String, ZooData.EdgeInfo> eInfo;
    public static Graph<String, IdentifiedWeightedEdge> gInfo;
    public static Map<String, String> Latlng_ids_Map;
    public static AnimalItem gate;

    //not sure if i will change this constructor
    public AnimalItem(@NonNull String id, ArrayList<String> tags, String name, LatLng position){
        this.id = id;
        this.name = name;
        this.tags = tags;
        this.searched = false;
        this.visited = false;
        this.position = position;
    }



    // parsing json files to static fields
    public static void loadInfo(Context context,String v_path,String e_path,String g_path)
            throws IOException {

        InputStream input = context.getAssets().open(v_path);
        vInfo = ZooData.loadVertexInfoJSON(input);

        input = context.getAssets().open(e_path);
        eInfo = ZooData.loadEdgeInfoJSON(input);

        input = context.getAssets().open(g_path);
        gInfo = ZooData.loadZooGraphJSON(input);

        Latlng_ids_Map = new HashMap<String, String>();
        for (Map.Entry<String, ZooData.VertexInfo> set : vInfo.entrySet()){
            ZooData.VertexInfo currentVertex = set.getValue();

            // populate the latlng id map
            String id_latlng = currentVertex.group_id;
            if(id_latlng==null)
                id_latlng = currentVertex.id;
            Latlng_ids_Map.put(currentVertex.id, id_latlng);
            // look for gate
            if(currentVertex.kind.name().equals("GATE")){
                gate = new AnimalItem(
                        set.getValue().id,
                        (ArrayList<String>) set.getValue().tags,
                        set.getValue().name,
                        new LatLng(currentVertex.lat,currentVertex.lng)
                );
            }
        }
    }

    @Override
    public String toString() {
        return name;
    }

    //return a list of animals whose tag is contained but the user input tag
    // If animal names match the tag, that also counts
    //List<AnimalItem> -return all animal under the tag
    public static List<AnimalItem> search_by_tag(String tag){

        List<AnimalItem> retVal =  new ArrayList<AnimalItem>();

        int i =0;
        for (Map.Entry<String, ZooData.VertexInfo> set : vInfo.entrySet()) {
            ZooData.VertexInfo currentVertex = set.getValue();
            if(currentVertex.kind.name().equals("EXHIBIT")){
                if (tag==null ||
                       AnimalUtilities.matchByTag(currentVertex.tags, tag.toLowerCase()) ||
                        currentVertex.name.toLowerCase().contains(tag.toLowerCase())){
                    retVal.add(new AnimalItem(
                            set.getValue().id,
                            (ArrayList<String>) set.getValue().tags,
                            set.getValue().name,
                            currentVertex.lat ==null ? null : new LatLng(currentVertex.lat,currentVertex.lng)
                    ));
                }
            }

            i++;
        }
        return  retVal;
    }

    //return a route that has a different order of input route, so it can be a good choice for the user
    public static List<route_node> plan_route(List<AnimalItem> animal_items){
        //begin and end positions
        String start = "entrance_exit_gate";
        String goal;
        ArrayList<route_node> planned_route = new ArrayList<>();
        int num_iter=animal_items.size();
        route_node myRouteNode = null;
        for (int i=0; i < num_iter+1; i++){
            //plus 1 because we need the begin and end of the route
            double min_distance=99999999.9;
            AnimalItem closest_animal=null;
            String address_id = null;
            double distance = 0;

            if (i==num_iter){
                animal_items.add(gate);
            }

            //find next closest exhibit
            closest_animal = AnimalUtilities.getClosestAnimalItem(animal_items, start, min_distance, closest_animal);
            animal_items.remove(closest_animal);
            // find the potential group name, may be null
            String potential_parent_id = vInfo.get( closest_animal.id).group_id;
            // itself is a parent/group exhibit
            if (vInfo.get( closest_animal.id).kind.name() == "exhibit_group")
                potential_parent_id = closest_animal.id;

            if (potential_parent_id!=null &&
                    myRouteNode != null &&
                    potential_parent_id .equals( myRouteNode.exhibit.id)){

                myRouteNode.names.add(closest_animal.name);
                continue;
            }

            //find the address of the goal
            GraphPath<String, IdentifiedWeightedEdge> path = adapted_find_shortest_path(gInfo, start, closest_animal.id);
            int pathSize = path.getEdgeList().size();
            IdentifiedWeightedEdge myEdge = path.getEdgeList().get(pathSize - 1);
            address_id = myEdge.getId();
            String address= eInfo.get(address_id).street;

            //construct parent item for the node
            if (potential_parent_id!=null && potential_parent_id != closest_animal.id){
                for (Map.Entry<String, ZooData.VertexInfo> set : vInfo.entrySet()){
                    ZooData.VertexInfo currentVertex = set.getValue();
                    if (currentVertex.id .equals( potential_parent_id)){
                        closest_animal = new AnimalItem(
                                currentVertex.id,
                                (ArrayList<String>) set.getValue().tags,
                                set.getValue().name,
                                currentVertex.lat ==null ? null : new LatLng(currentVertex.lat,currentVertex.lng)
                        );
                        break;
                    }
                }

            }
            distance = route_length(adapted_find_shortest_path(gInfo, "entrance_exit_gate",closest_animal.id));
            start = closest_animal.id;
            //closest_animal = vInfo.get(potential_parent_id)
            myRouteNode = new route_node(closest_animal, address, distance);
            planned_route.add(myRouteNode);
        }
        return  planned_route;
    }


    // return a path's length
    public static double route_length(GraphPath<String, IdentifiedWeightedEdge> path){
        double retVal=0;
        for (IdentifiedWeightedEdge e : path.getEdgeList()){
            retVal+= gInfo.getEdgeWeight(e);
        }
        return retVal;
    }

    public static GraphPath<String, IdentifiedWeightedEdge> adapted_find_shortest_path (Graph<String, IdentifiedWeightedEdge> gInfo, String source, String sink){
        source = Latlng_ids_Map.get(source);
        sink = Latlng_ids_Map.get(sink);
        return DijkstraShortestPath.findPathBetween(gInfo, source, sink);
    }

//    public String getCoordString() {
//        var coords = getCoords();
//        return String.format(Locale.getDefault(), "%3.6f, %3.6f", coords.first, coords.second);
//    }
//    public Pair<Double, Double> getCoords(AnimalItem landmark) {
//        if (landmark != null) {
//            return Pair.create(landmark.position.latitude, landmark.position.longitude);
//        } else {
//            return Pair.create(exhibit.lat, exhibit.lng);
//        }
//    }
}


class route_node
{
    public route_node(AnimalItem exhibit, String address, double distance) {
        this.exhibit = exhibit;
        this.address = address;
        this.distance = distance;
        names= new ArrayList<>();
        names.add(this.exhibit.name);
    }

    public AnimalItem exhibit;
    public String address;
    public double  distance;
    public List<String> names;
};