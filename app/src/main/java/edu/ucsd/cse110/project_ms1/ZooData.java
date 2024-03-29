package edu.ucsd.cse110.project_ms1;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.nio.json.JSONImporter;

import edu.ucsd.cse110.project_ms1.location.Coord;

public class ZooData {
    public static class VertexInfo {
        public static enum Kind {
            // The SerializedName annotation tells GSON how to convert
            // from the strings in our JSON to this Enum.
            @SerializedName("gate") GATE,
            @SerializedName("exhibit") EXHIBIT,
            @SerializedName("intersection") INTERSECTION,
            @SerializedName("exhibit_group") EXHIBIT_GROUP
        }
        public String id;
        public Kind kind;
        public String name;
        public String group_id;
        public List<String> tags;
        public Double lat, lng;
        VertexInfo(String id, Kind kind, String name, List<String> tags, Double lat, Double lng, String group_id) {
            this.id = id;
            this.kind = kind;
            this.name = name;
            this.tags = tags;
            this.lat = lat;
            this.lng = lng;
            this.group_id = group_id;
        }
    }
    //informatoin of edge
    public static class EdgeInfo {
        public String id;
        public String street;
    }

    //load the vertex information form Json file
    public static Map<String, ZooData.VertexInfo> loadVertexInfoJSON(InputStream inputStream) {

        Reader reader = new InputStreamReader(inputStream);

        Gson gson = new Gson();
        Type type = new TypeToken<List<ZooData.VertexInfo>>(){}.getType();
        List<ZooData.VertexInfo> zooData = gson.fromJson(reader, type);

        // This code is equivalent to:
        //
        // Map<String, ZooData.VertexInfo> indexedZooData = new HashMap();
        // for (ZooData.VertexInfo datum : zooData) {
        //   indexedZooData[datum.id] = datum;
        // }
        //
        Map<String, ZooData.VertexInfo> indexedZooData = zooData
                .stream()
                .collect(Collectors.toMap(v -> v.id, datum -> datum));

        return indexedZooData;
    }
    //load the edge information form Json file
    public static Map<String, ZooData.EdgeInfo> loadEdgeInfoJSON(InputStream inputStream) {
        Reader reader = new InputStreamReader(inputStream);

        Gson gson = new Gson();
        Type type = new TypeToken<List<ZooData.EdgeInfo>>(){}.getType();
        List<ZooData.EdgeInfo> zooData = gson.fromJson(reader, type);

        Map<String, ZooData.EdgeInfo> indexedZooData = zooData
                .stream()
                .collect(Collectors.toMap(v -> v.id, datum -> datum));

        return indexedZooData;
    }
    //load the graph information form Json file
    public static Graph<String, IdentifiedWeightedEdge> loadZooGraphJSON(InputStream inputStream) {
        // Create an empty graph to populate.
        Graph<String, IdentifiedWeightedEdge> g = new DefaultUndirectedWeightedGraph<>(IdentifiedWeightedEdge.class);

        // Create an importer that can be used to populate our empty graph.
        JSONImporter<String, IdentifiedWeightedEdge> importer = new JSONImporter<>();

        // We don't need to convert the vertices in the graph, so we return them as is.
        importer.setVertexFactory(v -> v);

        // We need to make sure we set the IDs on our edges from the 'id' attribute.
        // While this is automatic for vertices, it isn't for edges. We keep the
        // definition of this in the IdentifiedWeightedEdge class for convenience.
        importer.addEdgeAttributeConsumer(IdentifiedWeightedEdge::attributeConsumer);

        // On Android, you would use context.getAssets().open(path) here like in Lab 5.
        Reader reader = new InputStreamReader(inputStream);

        // And now we just import it!
        importer.importGraph(g, reader);

        return g;
    }
    //load the mocking points information form Json file
    public static List<Coord> loadMockingJSON(InputStream inputStream){
        Reader reader = new InputStreamReader(inputStream);
        Gson gson = new Gson();
        Type type = new TypeToken<List<Coord>>(){}.getType();
        List<Coord> Coords = gson.fromJson(reader, type);
        /*
        List<Coord> Coords = zooData
                .stream()
                .collect(Collectors.toMap(v -> v.id, datum -> datum));

         */
        return Coords;
    }
}