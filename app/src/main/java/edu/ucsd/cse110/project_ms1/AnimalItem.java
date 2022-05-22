package edu.ucsd.cse110.project_ms1;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;


@Entity(tableName = "animal_items")
@TypeConverters({TagConverter.class})
public class AnimalItem {
    @PrimaryKey(autoGenerate = true)
    public long unique_id;
    @NonNull
    public String id;
    public ArrayList<String> tags;
    public String name; //essentially, the name is the tag in json file
    public boolean searched;

    public static Map<String, ZooData.VertexInfo> vInfo;
    public static Map<String, ZooData.EdgeInfo> eInfo;
    public static Graph<String, IdentifiedWeightedEdge> gInfo;
    public static AnimalItem gate;

    //not sure if i will change this constructor
    public AnimalItem(@NonNull String id, ArrayList<String> tags, String name){
        this.id = id;
        this.name = name;
        this.tags = tags;
        this.searched = false;

        //this.exhibit = exhibit;
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

        for (Map.Entry<String, ZooData.VertexInfo> set : vInfo.entrySet()){
            ZooData.VertexInfo currentVertex = set.getValue();
            if(currentVertex.kind.name().equals("GATE")){
                gate = new AnimalItem(set.getValue().id, (ArrayList<String>) set.getValue().tags,set.getValue().name);
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
                if (tag==null || currentVertex.tags.contains(tag.toLowerCase()) ||
                        currentVertex.name.toLowerCase().contains(tag.toLowerCase())){
                    retVal.add(new AnimalItem(set.getValue().id, (ArrayList<String>) set.getValue().tags,set.getValue().name));
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
        for (int i=0; i < num_iter+1; i++){
            //plus 1 because we need the begin and end of the route
            double min_distance=99999999.9;
            AnimalItem closest_animal=null;
            String address_id = null;
            double distance = 0;

            if (i==num_iter){
                animal_items.add(gate);
            }

            //use for loop to find next closet exhibit
            for (AnimalItem item : animal_items){
                goal = item.id;
                GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(gInfo, start, goal);
                double curr_dis = route_length(path);
                if (curr_dis < min_distance){
                    min_distance = curr_dis;
                    closest_animal = item;
                    int pathSize = path.getEdgeList().size();
                    IdentifiedWeightedEdge myEdge = path.getEdgeList().get(pathSize - 1);
                    address_id = myEdge.getId();
                }
            }

            String address = eInfo.get(address_id).street;
            distance = route_length(DijkstraShortestPath.findPathBetween(gInfo, "entrance_exit_gate",closest_animal.id ));
            start = closest_animal.id;
            animal_items.remove(closest_animal);
            route_node myRouteNode = new route_node(closest_animal, address, distance);
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

    //get the closest landmark of current location
    /*
    public ZooData.VertexInfo getClosestLandmark(LatLng current){

    }
    */

}


class route_node
{
    public route_node(AnimalItem animal, String address, double distance) {
        this.animal = animal;
        this.address = address;
        this.distance = distance;
    }

    public AnimalItem animal;
    public String address;
    public double  distance;
};