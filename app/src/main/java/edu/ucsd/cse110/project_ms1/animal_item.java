package edu.ucsd.cse110.project_ms1;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class animal_item {
    //
    public ZooData.VertexInfo vertex;
    //For List
    public boolean selected;
    public boolean searched;
    public int order;

    //not sure if i will change this constructor
    public animal_item(int order, ZooData.VertexInfo vertex) {
        this.vertex = vertex;
        // temp
        this.order = order;
        this.selected = false;
        this.searched = false;
    }

    public static List<animal_item> loadJSON(Context context, String path) {
//
        //Load the information about our nodes
        Map<String, ZooData.VertexInfo> vInfo = ZooData.loadVertexInfoJSON(context,"sample_node_info.json");
//
        //retVal -return value
        List<animal_item> retVal = new ArrayList<animal_item>();
        int i = 0;
        for (Map.Entry<String, ZooData.VertexInfo> set : vInfo.entrySet()) {
            retVal.add(new animal_item(i, set.getValue()));
            i++;
        }

        return retVal;
    }

    @Override
    public String toString() {
        return "animal_item{" +
                "vertex=" + vertex +
                ", vertex.id=" + vertex.id +
                ", vertex.name=" + vertex.name +
                ", vertex.tags=" + vertex.tags +
                ", selected=" + selected +
                ", searched=" + searched +
                ", order=" + order +
                '}';
    }
}
