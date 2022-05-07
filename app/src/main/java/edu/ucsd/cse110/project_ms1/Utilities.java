package edu.ucsd.cse110.project_ms1;

import android.app.Activity;
import android.app.AlertDialog;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;

import java.util.ArrayList;

public class Utilities {
    public static void showAlert(Activity activity, String message){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);

        alertBuilder.setTitle("Alert!")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, id)->{
                    dialog.cancel();
                })
                .setCancelable(true);

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    public static Graph<String,IdentifiedWeightedEdge> completeG (Graph<String,IdentifiedWeightedEdge> g, ArrayList<String> vertex) {
        Graph<String,IdentifiedWeightedEdge> graph = new DefaultUndirectedWeightedGraph<>(IdentifiedWeightedEdge.class);
        // add vertex to graph
        for (String str: vertex){
            graph.addVertex(str);
        }

        //add edge to graph
        for (int i = 0; i<vertex.size();i++){
            for (int j = i+1; j<vertex.size();j++){
                String start = vertex.get(i);
                String goal = vertex.get(j);
                GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(g, start, goal);
                double edge_weight = 0;
                for (IdentifiedWeightedEdge e: path.getEdgeList()){edge_weight+=g.getEdgeWeight(e);}
                IdentifiedWeightedEdge edge = graph.addEdge(start,goal);
                graph.setEdgeWeight(edge,edge_weight);
            }
        }
        return graph;
    }
}
