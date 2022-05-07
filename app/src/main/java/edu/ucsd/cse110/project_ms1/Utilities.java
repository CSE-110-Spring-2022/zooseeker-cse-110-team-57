package edu.ucsd.cse110.project_ms1;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Pair;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.GraphTests;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
    //findcycle() for detecting cycle
    //DegreeOf() for checking degree


    public static ArrayList<String> TSP(Graph<String,IdentifiedWeightedEdge> g){
        if(!GraphTests.isComplete(g)){
            throw new IllegalArgumentException("not a complete graph");
        }

        int n = g.vertexSet().size();
        Set<String>  vertexSet = g.vertexSet();

        //new graph with vertex only
        Graph<String,IdentifiedWeightedEdge> graph = new DefaultUndirectedWeightedGraph<>(IdentifiedWeightedEdge.class);
        for(String s: vertexSet){
            graph.addVertex(s);
        }



        // sorted EdgeList
        Set<IdentifiedWeightedEdge> edgeSet = graph.edgeSet();
        ArrayList<IdentifiedWeightedEdge> edgeList = new ArrayList<>();
        for (IdentifiedWeightedEdge e: edgeSet){
            edgeList.add(e);
        }
        Collections.sort(edgeList);


        //WIP
        while(g.edgeSet().size() < n){

        }

        ArrayList<String> str = new ArrayList<>();
        return str;
    }
}
